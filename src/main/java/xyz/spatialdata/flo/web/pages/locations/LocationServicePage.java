package xyz.spatialdata.flo.web.pages.locations;

import java.awt.Point;
import java.awt.geom.Path2D;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletContext;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.spatialdata.flo.web.heuristicsearch.ReadPropertyValues;
import xyz.spatialdata.flo.web.model.LocationModel;
import xyz.spatialdata.flo.web.model.PropertiesModel;
import xyz.spatialdata.flo.web.model.RatingModel;
import xyz.spatialdata.flo.web.model.RouteModel;
import xyz.spatialdata.flo.web.model.SuggestRouteModel;
import xyz.spatialdata.flo.web.pages.index.BasePage;
import xyz.spatialdata.flo.web.panels.DirectionPanel;
import xyz.spatialdata.flo.web.panels.RatingsPanel;
import xyz.spatialdata.flo.web.persistence.LocationServiceDAO;
import xyz.spatialdata.flo.web.persistence.PropertiesServiceDAO;
import xyz.spatialdata.flo.web.persistence.RouteServiceDAO;

public class LocationServicePage extends BasePage {

  private static final long serialVersionUID = 1L;

  private List<LocationModel> list = new ArrayList<LocationModel>();
  private List<LocationModel> list2 = new ArrayList<LocationModel>();
  private List<LocationModel> origList = new ArrayList<LocationModel>();
  private List<RouteModel> origRoutes = new ArrayList<RouteModel>();
  private List<RouteModel> listRoute = new ArrayList<RouteModel>();
  private String price = "";
  private String day = "";
  private String selectedPriceType = "Any";
  private String selectedDaysType = "Any";
  private boolean displayToursConfirm = false;
  private boolean displayLocationsConfirm = false;
  private List<String> names = new ArrayList<String>();
  private List<SuggestRouteModel> suggested = new ArrayList<SuggestRouteModel>();
  private String myLatitude = "";
  private String myLongitude = "";
  private String result = "";
  
  private ArrayList<String> namesRemoveSelect = new ArrayList<String>();
  private ArrayList<String> namesSelect = new ArrayList<String>();
  private List<LocationModel> listEdit = new ArrayList<LocationModel>();
  
  private List<PropertiesModel> origPropList = null;

  private final static Logger log = LoggerFactory.getLogger(LocationServicePage.class);

  String selected = null;

  @SpringBean
  private LocationServiceDAO locationServiceDAO;

  @SpringBean
  private RouteServiceDAO routeServiceDAO;
  
	@SpringBean
	  private static PropertiesServiceDAO propertiesServiceDAO;


  private WebSession session = WebSession.get();

  public static final double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
    double theta = lon1 - lon2;
    double dist =
        Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))
            * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
    dist = Math.acos(dist);
    dist = rad2deg(dist);
    dist = dist * 60 * 1.1515;

    if (unit == 'K') {
      dist = dist * 1.609344;
    } else if (unit == 'N') {
      dist = dist * 0.8684;
    }

    return (dist);
  }

  private static final double deg2rad(double deg) {
    return (deg * Math.PI / 180.0);
  }

  private static final double rad2deg(double rad) {
    return (rad * 180 / Math.PI);
  }



  public LocationServicePage() throws IOException {
	  
	  origPropList = new ArrayList<PropertiesModel>();
	  origPropList = propertiesServiceDAO.readPropertiesModel();
	  
	  
    final WebMarkupContainer datacontainer = new WebMarkupContainer("data");
    datacontainer.setOutputMarkupId(true);
    add(datacontainer);

    final WebMarkupContainer wmc = new WebMarkupContainer("wmc");
    wmc.setVersioned(false);
    wmc.setOutputMarkupId(true);
    add(wmc);

    Label currentPosition = new Label("currentPosition", "My position");
    currentPosition.setOutputMarkupId(true);
    wmc.add(currentPosition);

    final Model modelLat = new Model("0");
    Label currentLatitude = new Label("currentLatitude", modelLat);
    currentLatitude.setOutputMarkupId(true);
    wmc.add(currentLatitude);

    final Model modelLng = new Model("0");
    Label currentLongitude = new Label("currentLongitude", modelLng);
    currentLongitude.setOutputMarkupId(true);
    wmc.add(currentLongitude);

    origList = locationServiceDAO.readLocationModel();
    //final PageableListView<LocationModel> lview = displayList("rows", list, 50);
    final PageableListView<LocationModel> lview = displayList("rows", list, 1000);
    lview.setOutputMarkupId(true);
    wmc.add(lview);

    final Label currentPage = new Label("currentPage", new Model());
    currentPage.setOutputMarkupId(true);
    wmc.add(currentPage);

    final AjaxPagingNavigator pagination = new AjaxPagingNavigator("navigator", lview) {
      private static final long serialVersionUID = 1L;


      @Override
      protected void onAjaxEvent(AjaxRequestTarget target) {
        super.onAjaxEvent(target);
        // if (lview.contains()) {
        //
        // }
        // LocationModel myLoc = new LocationModel();
        // myLoc.setName("My position");
        // myLoc.setLatitude(new BigDecimal(48.209206));
        // myLoc.setLongitude(new BigDecimal(16.372778));
        // myLoc.setCsvName("myPosition");
        // currentPage.add(myLoc);
        // target.add(lview);
        target.add(currentPage);
        target
            .appendJavaScript("initIds();initMarkers();initMyPosition();$('.tableNavigator').show();showLocationTableIfNavigatorChange();");
      }
    };
    wmc.add(pagination);

    LoadableDetachableModel<Object> model = new LoadableDetachableModel<Object>() {

      private static final long serialVersionUID = 1L;

      @Override
      public Object load() {
        return pagination.getPageable().getCurrentPage() + 1;
      }
    };
    currentPage.setDefaultModel(model);

    names.clear();
    namesSelect.clear();

    for (int i = 0; i < origList.size(); i++) {
      if (!names.contains(origList.get(i).getCsvName())) {
        names.add(origList.get(i).getCsvName());
//        if (origList.get(i).getCsvName().contentEquals("Top places")) {
//          namesSelect.add(origList.get(i).getCsvName());
//        }
      }
    }
    //
    // list2.clear();
    // for (int i = 0; i < origList.size(); i++) {
    // for (int j = 0; j < namesSelect.size(); j++) {
    // if (origList.get(i).getCsvName().contentEquals(namesSelect.get(j).toString())) {
    // list2.add(origList.get(i));
    // }
    // }
    // }
    // list.clear();
    // list.addAll(list2);

    saveTour(datacontainer);
    origRoutes = routeServiceDAO.readRouteModel();
    wmc.add(new DirectionPanel("direction"));



    final Form<String> formSearch = new Form<String>("formSearch");
    add(formSearch);
    
    final Form<String> formSearchExtraInfo = new Form<String>("formSearchExtraInfo");
    add(formSearchExtraInfo);

    final AutoCompleteTextField<String> inputField =
        new AutoCompleteTextField<String>("textSearch", new Model<String>()) {

          private static final long serialVersionUID = 1L;

          @Override
          protected Iterator<String> getChoices(String input) {
            if (Strings.isEmpty(input)) {
              List<String> emptyList = Collections.emptyList();
              return emptyList.iterator();
            }

            List<String> choices = new ArrayList<String>(10);

            for (final LocationModel loc : origList) {
              final String name = loc.getName();
              if (name.intern().toUpperCase().contains(input.toUpperCase().intern())) {
                choices.add(name);
                if (choices.size() == 10) {
                  break;
                }
              }
            }

            return choices.iterator();
          }

        };
    formSearch.add(inputField);
    
    final AutoCompleteTextField<String> inputField2 =
            new AutoCompleteTextField<String>("textSearch2", new Model<String>()) {

              private static final long serialVersionUID = 1L;

              @Override
              protected Iterator<String> getChoices(String input) {
                if (Strings.isEmpty(input)) {
                  List<String> emptyList = Collections.emptyList();
                  return emptyList.iterator();
                }

                List<String> choices = new ArrayList<String>(10);

                for (final LocationModel loc : origList) {
                  final String name = loc.getOther();
                  if (name != null) {
                  if (name.intern().toUpperCase().contains(input.toUpperCase().intern())) {
                    choices.add(name);
                    if (choices.size() == 10) {
                      break;
                    }
                  }
                  }
                }

                return choices.iterator();
              }

            };
        formSearchExtraInfo.add(inputField2);

    // final AjaxButton closeEdit = new AjaxButton("closeEdit") {
    // private static final long serialVersionUID = 1L;
    //
    // @Override
    // protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
    // super.onSubmit(target, form);
    //
    // target.appendJavaScript("$('#editInfo').hide();");
    // editForm.setVisible(false);
    // }
    // };
    // closeEdit.setVisible(false);

     IndicatingAjaxButton clearSearch = new IndicatingAjaxButton("clearSearch") {

      private static final long serialVersionUID = 1L;

      
      public void onSubmit(AjaxRequestTarget target) {
        super.onSubmit(target);
        list.clear();
        target.add(wmc);
        log.info("the name is and the input is222 ");
        target.appendJavaScript("map.gmap('clear', 'markers');");
        setResponsePage(getPage());

      }
    };
    clearSearch.setDefaultFormProcessing(false);
    formSearch.add(clearSearch);

    // .setDefaultFormProcessing(false)

    formSearch.add(new IndicatingAjaxButton("searchLocation") {

      private static final long serialVersionUID = 1L;

      public void onSubmit(AjaxRequestTarget target) {
        super.onSubmit(target);


        String name = inputField.getModelObject();
        log.debug("the name isssss " + name);
        if (name != null) {
          for (LocationModel loc : origList) {
            // log.debug("the name isssss " + loc.getName() + " and the input is " + name);
            if (loc.getName().trim().contentEquals(name)) {
              list.clear();
              list.add(loc);
              String pos = loc.getLatitude() + "," + loc.getLongitude();
              log.info("the panto is aaaaaaaa " + pos);
              // setResponsePage(getPage());
              target
                  .appendJavaScript("map.gmap('get', 'map').panTo(new google.maps.LatLng("
                      + pos
                      + "));google.maps.event.trigger(map, 'resize');map.gmap('get', 'map').setZoom(17);initIds();initMarkers();");
            }
          }
        }
        target.add(wmc);
      }

    });
    
    formSearchExtraInfo.add(new IndicatingAjaxButton("searchLocation2") {

        private static final long serialVersionUID = 1L;

        public void onSubmit(AjaxRequestTarget target) {
          super.onSubmit(target);


          String other = inputField2.getModelObject();
          log.debug("the name isssss " + other);
          if (other != null) {
            for (LocationModel loc : origList) {
              // log.debug("the name isssss " + loc.getName() + " and the input is " + name);
              if (loc.getOther().trim().contentEquals(other)) {
                list.clear();
                list.add(loc);
                String pos = loc.getLatitude() + "," + loc.getLongitude();
                log.info("the panto is aaaaaaaa " + pos);
                // setResponsePage(getPage());
                target
                    .appendJavaScript("map.gmap('get', 'map').panTo(new google.maps.LatLng("
                        + pos
                        + "));google.maps.event.trigger(map, 'resize');map.gmap('get', 'map').setZoom(17);initIds();initMarkers();");
              }
            }
          }
          target.add(wmc);
        }

      });

    final TextField<String> geoCoordDistance =
        new TextField<String>("geoCoordDistance", Model.of("20"));
    geoCoordDistance.setOutputMarkupId(true);
    
//    final TextField<String> polygonCoordDistance =
//            new TextField<String>("polygonCoordDistance", Model.of(""));
//    polygonCoordDistance.setOutputMarkupId(true);

    final TextField<String> geoCoordInput = new TextField<String>("geoCoordInput", Model.of(""));
    geoCoordInput.setOutputMarkupId(true);

    final TextField<String> polygonCoordInput = new TextField<String>("polygonCoordInput", Model.of(""));
    polygonCoordInput.setOutputMarkupId(true);

    IndicatingAjaxButton btnGetMyGeo = new IndicatingAjaxButton("btnGetMyGeo") {

      private static final long serialVersionUID = 1L;

      public void onSubmit(AjaxRequestTarget target) {
        super.onSubmit(target);

        String geoCoordInputValue = geoCoordInput.getModelObject();
        final String geoCoordDistanceValue = geoCoordDistance.getModelObject();
        int distanceKm = 0;
        if (geoCoordDistanceValue != null) {
          distanceKm = Integer.valueOf(geoCoordDistanceValue);
        }

        if (geoCoordInputValue != null) {
          geoCoordInputValue = geoCoordInputValue.substring(1, geoCoordInputValue.length() - 1);
          log.info("the coord are " + geoCoordInputValue);


          String[] att = geoCoordInputValue.split(", ");

          log.info("the coord are after " + att[0] + " and " + att[1]);

          // if (name != null) {

          list.clear();
          String pos = "";


          // LocationModel myLoc = new LocationModel();
          // myLoc.setName("My position");
          // myLoc.setLatitude(new BigDecimal(att[0]));
          // myLoc.setLongitude(new BigDecimal(att[1]));
          // myLoc.setCsvName("myPosition");

          // Label currentPosition = new Label("currentPosition", "My position");
          // currentPosition.setOutputMarkupId(true);
          // wmc.add(currentPosition);
          // Label currentLatitude = new Label("currentLatitude", new BigDecimal(att[0]));
          // currentLatitude.setOutputMarkupId(true);
          // wmc.add(currentLatitude);
          // Label currentLongitude = new Label("currentLongitude", new BigDecimal(att[1]));
          // currentLongitude.setOutputMarkupId(true);
          // wmc.add(currentLongitude);

          modelLat.setObject(att[0]);
          modelLng.setObject(att[1]);
          
          
          
          
          // wmc.add(modelLat);



          // target.appendJavaScript("getGeolocation();");
          for (LocationModel loc : origList) {

            if (!geoCoordInputValue.isEmpty()) {

              double dist =
                  distance(Double.valueOf(att[0]), Double.valueOf(Double.valueOf(att[1])), loc
                      .getLatitude().doubleValue(), loc.getLongitude().doubleValue(), 'K');

              if (dist < distanceKm) {
                list.add(loc);
                //pos = att[0] + "," + att[1];
              }

            }
          }
          // list.add(0, myLoc);
          // target.add(datacontainer);
          target.add(wmc);

          // target
          // .appendJavaScript("map.gmap('get', 'map').panTo(new google.maps.LatLng("
          // + pos
          // +
          // "));google.maps.event.trigger(map, 'resize');map.gmap('get', 'map').setZoom(15);initIds();initMarkers();");

          target.appendJavaScript("google.maps.event.trigger(map, 'resize');initIds();initMarkers();initMyPosition();");


          // setResponsePage(LocationServicePage.class);
          // target.appendJavaScript("initMarkers2();");
        }
      }
    };
    
    IndicatingAjaxButton btnGetPolygon = new IndicatingAjaxButton("btnGetPolygon") {

        private static final long serialVersionUID = 1L;

        public void onSubmit(AjaxRequestTarget target) {
          super.onSubmit(target);

           String geoCoordDistanceValue = geoCoordDistance.getModelObject();
            String polygonCoordInputValue = polygonCoordInput.getModelObject();
            int distanceKm = 0;
            if (geoCoordDistanceValue != null) {
              distanceKm = Integer.valueOf(geoCoordDistanceValue);
            }
            
//            String coordsArray[] = polygonCoordInputValue.split("\\s+") // Results "0,0","0,1","5,2","7,4","10,5"
//            		List<Point> points = new ArrayList<Point>();
//            		for(String s : coordsArray)
//            		{
//            		    String coordXY[] = s.split(","); 
//            		    int x = Integer.parseInt(coordXY[0]);
//            		    int y = Integer.parseInt(coordXY[1]);
//            		    points.add(new Point(x,y));
//            		}
            
            
//          final String polygonCoordDistanceValue = polygonCoordDistance.getModelObject();
//          int distanceKm = 1;
//          if (polygonCoordDistanceValue != null) {
//            distanceKm = Integer.valueOf(polygonCoordDistanceValue);
//          }

          if (polygonCoordInputValue != null) {
        	  polygonCoordInputValue = polygonCoordInputValue.replaceAll("\\s+","");
            log.info("the polygon marker coord are " + polygonCoordInputValue.replaceAll("\\s+",""));

log.info("FIRST TWO CHARS ARE "+polygonCoordInputValue.substring(0,2));
            
            //myPolygon.moveTo(Double.valueOf(att[0].substring(1)), Double.valueOf(att[1].length()-1));

            Path2D myPolygon = new Path2D.Double();
//            if(polygonCoordInputValue.substring(0,2).contentEquals("((")) {
//                String[] att = polygonCoordInputValue.substring(2,polygonCoordInputValue.length()-2).split("\\),\\(");
//                String coordXYA[] = att[0].split(",");
//                String coordXYB[] = att[1].split(",");
//                log.info("coordxNewA is " + coordXYA[0]);
//    			log.info("coordyNewA is " + coordXYA[1]);
//                log.info("coordxNewB is " + coordXYB[0]);
//    			log.info("coordyNewB is " + coordXYB[1]);
//    			myPolygon.moveTo(Double.valueOf(coordXYA[0]), Double.valueOf(coordXYA[1]));
//    			myPolygon.lineTo(Double.valueOf(coordXYB[0]), Double.valueOf(coordXYB[1]));
//    			myPolygon.closePath();
//            } else {
                String[] att = polygonCoordInputValue.substring(1,polygonCoordInputValue.length()-1).split("\\),\\(");
                
                String[] coordXY = null;
    		for(int i=0; i<att.length; i++){
    		 coordXY = att[i].split(",");
    			log.info("coordx is " + att[i]);
    			
    			if(i==0) {
    				myPolygon.moveTo(Double.valueOf(coordXY[0]), Double.valueOf(coordXY[1]));
    			} else {
    				myPolygon.lineTo(Double.valueOf(coordXY[0]), Double.valueOf(coordXY[1]));
    			}
    			
    			log.info("coordxNew is " + coordXY[0]);
    			log.info("coordyNew is " + coordXY[1]);
                //myPolygon.moveTo(Double.valueOf(att[i].substring(1)), Double.valueOf(att[i+1].length()-1));
    		}
    		myPolygon.closePath();
            

            
            //log.info("the polygon marker coord are after " + att[0].substring(1) + " and " + (att[1].length()-1) + " and " + att[2].substring(1) + " and " + (att[3].length()-1));

            // if (name != null) {

            list.clear();


            // LocationModel myLoc = new LocationModel();
            // myLoc.setName("My position");
            // myLoc.setLatitude(new BigDecimal(att[0]));
            // myLoc.setLongitude(new BigDecimal(att[1]));
            // myLoc.setCsvName("myPosition");

            // Label currentPosition = new Label("currentPosition", "My position");
            // currentPosition.setOutputMarkupId(true);
            // wmc.add(currentPosition);
            // Label currentLatitude = new Label("currentLatitude", new BigDecimal(att[0]));
            // currentLatitude.setOutputMarkupId(true);
            // wmc.add(currentLatitude);
            // Label currentLongitude = new Label("currentLongitude", new BigDecimal(att[1]));
            // currentLongitude.setOutputMarkupId(true);
            // wmc.add(currentLongitude);

//            modelLat.setObject(att[0]);
//            modelLng.setObject(att[1]);
            
            
            
            
            // wmc.add(modelLat);



            // target.appendJavaScript("getGeolocation();");
            for (LocationModel loc : origList) {

              if (!polygonCoordInputValue.isEmpty()) {
                
//                double dist =
//                    distance(Double.valueOf(att[0]), Double.valueOf(Double.valueOf(att[1])), loc
//                        .getLatitude().doubleValue(), loc.getLongitude().doubleValue(), 'K');
//                 log.info("La distancia es : " + dist);
//                if (dist < 20) {
//                  
//                  list.add(loc);
//
//                  pos = att[0] + "," + att[1];
//                }
            	  

                  if(att.length == 1) {
                      
					double dist =
                              distance(Double.valueOf(coordXY[0]), Double.valueOf(coordXY[1]), loc
                                  .getLatitude().doubleValue(), loc.getLongitude().doubleValue(), 'K');

                          if (dist < distanceKm) {

                            list.add(loc);
                            //pos = att[0] + "," + att[1];
                          }
                  } else {
        		if(myPolygon.contains(loc.getLatitude().doubleValue(),loc.getLongitude().doubleValue())) {
        			//log.info("Coordinate to show: " + loc.getLatitude().doubleValue() + " , " + loc.getLongitude().doubleValue());
        			list.add(loc);
        		}
                  }

              }
            }
            // list.add(0, myLoc);
            // target.add(datacontainer);
            target.add(wmc);

            // target
            // .appendJavaScript("map.gmap('get', 'map').panTo(new google.maps.LatLng("
            // + pos
            // +
            // "));google.maps.event.trigger(map, 'resize');map.gmap('get', 'map').setZoom(15);initIds();initMarkers();");

            target.appendJavaScript("google.maps.event.trigger(map, 'resize');initIds();initMarkers();initMyPosition();");


            // setResponsePage(LocationServicePage.class);
            // target.appendJavaScript("initMarkers2();");
          }
        }
      };

    final Form<?> coordGeoForm = new Form<Void>("coordGeoForm") {

      private static final long serialVersionUID = 1L;

    };
    datacontainer.add(coordGeoForm);
    coordGeoForm.add(geoCoordInput);
    coordGeoForm.add(geoCoordDistance);
    coordGeoForm.add(btnGetMyGeo);
    coordGeoForm.add(polygonCoordInput);
//    geoCoordInput.add(polygonCoordDistance);
    coordGeoForm.add(btnGetPolygon);


    inputField.add(new AjaxFormSubmitBehavior(formSearch, "click") {

      private static final long serialVersionUID = 1L;

      @Override
      protected void onSubmit(AjaxRequestTarget target) {
        String name = inputField.getModelObject();
        log.debug("the name isssss " + name);
        list.clear();
        if (name != null) {
          for (LocationModel loc : origList) {
            // log.debug("the name isssss " + loc.getName() + " and the input is " + name);
            if (loc.getName().trim().contentEquals(name)) {
              list.add(loc);
            }
          }
        }

        // } else {
        // list.addAll(origList);
        // }
      }
    });
    add(formSearch);
  }

  public PageableListView<LocationModel> displayList(String id, List<LocationModel> list, int num) {

    PageableListView<LocationModel> listView = new PageableListView<LocationModel>(id, list, num) {
      private static final long serialVersionUID = 1L;

      @Override
      public void populateItem(final ListItem<LocationModel> item) {
        final LocationModel obj = item.getModelObject();

        item.add(new Label("id_location", obj.getId()));

        if (obj.getName() != null) {
          item.add(new Label("name", obj.getName()));
        } else {
          item.add(new Label("name", obj.getName()).setVisible(false));
        }
        if (session.getAttribute("user_name") != null) {
          item.add(new Label("userName", session.getAttribute("user_name")));
        } else {
          item.add(new Label("userName", obj.getName()).setVisible(false));
        }
        if (obj.getCsvName() != null) {
          item.add(new Label("csvName", obj.getCsvName()));
        } else {
          item.add(new Label("csvName", obj.getCsvName()).setVisible(false));
        }
        if (obj.getType() != null) {
          item.add(new Label("typeTag", "Type: "));
          item.add(new Label("type", obj.getType()));
        } else {
          item.add(new Label("typeTag").setVisible(false));
          item.add(new Label("type", obj.getType()).setVisible(false));
        }
        if (obj.getAddress() != null || obj.getStreet() != null) {
          item.add(new Label("addressTag", "Address: "));
        } else {
          item.add(new Label("addressTag").setVisible(false));
        }
        if (obj.getAddress() != null) {
          item.add(new Label("address", obj.getAddress()));
        } else {
          item.add(new Label("address", obj.getAddress()).setVisible(false));
        }
        if (obj.getStreet() != null) {
          item.add(new Label("street", obj.getStreet()));
        } else {
          item.add(new Label("street", obj.getStreet()).setVisible(false));
        }
        if (obj.getNumber() != null) {
          item.add(new Label("number", obj.getNumber()));
        } else {
          item.add(new Label("number", obj.getNumber()).setVisible(false));
        }
        if (obj.getDistrict() != null) {
          item.add(new Label("district", obj.getDistrict()));
        } else {
          item.add(new Label("district", obj.getDistrict()).setVisible(false));
        }
        if (obj.getLatitude() != null) {
          item.add(new Label("latitude", obj.getLatitude().toString()));
        } else {
          item.add(new Label("latitude", obj.getLatitude()).setVisible(false));
        }
        if (obj.getLongitude() != null) {
          item.add(new Label("longitude", obj.getLongitude().toString()));
        } else {
          item.add(new Label("longitude", obj.getLongitude()).setVisible(false));
        }
        if (obj.getWebsite() != null) {
      	  item.add(new Label("websiteTag", "Web: "));
          item.add(new ExternalLink("website", obj.getWebsite(), obj.getWebsite()));
        } else {
          item.add(new Label("websiteTag").setVisible(false));
          item.add(new Label("website", obj.getWebsite()).setVisible(false));
        }
        if (obj.getImage() != null) {
          item.add(new ContextImage("image", obj.getImage().toString()));
        } else {
          item.add(new Image("image", obj.getImage()).setVisible(false));
        }
        if (obj.getDescription() != null) {
          item.add(new Label("descriptionTag", "Description: "));
          item.add(new Label("description", obj.getDescription()));
        } else {
          item.add(new Label("descriptionTag").setVisible(false));
          item.add(new Label("description", obj.getDescription()).setVisible(false));
        }
        if (obj.getEmail() != null) {
          item.add(new Label("emailTag", "Email: "));
          item.add(new ExternalLink("email", "mailto:" + obj.getEmail(), obj.getEmail()));
        } else {
          item.add(new Label("emailTag").setVisible(false));
          item.add(new Label("email", obj.getEmail()).setVisible(false));
        }
        if (obj.getPhone() != null) {
          item.add(new Label("phoneTag", "Tlf.: "));
          item.add(new Label("phone", obj.getPhone()));
        } else {
          item.add(new Label("phoneTag").setVisible(false));
          item.add(new Label("phone", obj.getPhone()).setVisible(false));
        }
        if (obj.getDate() != null) {
          item.add(new Label("dateTag", "Date: "));
          item.add(new Label("date", obj.getDate()));
        } else {
          item.add(new Label("dateTag").setVisible(false));
          item.add(new Label("date", obj.getDate()).setVisible(false));
        }
        if (obj.getSchedule() != null) {
          item.add(new Label("scheduleTag", "Schedule: "));
          item.add(new Label("schedule", obj.getSchedule()));
        } else {
          item.add(new Label("scheduleTag").setVisible(false));
          item.add(new Label("schedule", obj.getSchedule()).setVisible(false));
        }
        if (obj.getOther() != null) {
          item.add(new Label("otherTag", "Extra info: "));
          item.add(new Label("other", obj.getOther()));
        } else {
          item.add(new Label("otherTag").setVisible(false));
          item.add(new Label("other", obj.getSchedule()).setVisible(false));
        }
        // String webAppPath = System.getProperty("catalina.base") + "/webapp";
        // log.info("WEB PATH IS" + webAppPath);
        if (obj.getCsvName() != null) {
          item.add(new Label("downloadCsvTag", "Download file: "));
          ServletContext servletContext = WebApplication.get().getServletContext();
          String path = servletContext.getRealPath("/") + "files/";
          item.add(new DownloadLink("downloadOrigCsv", new File(path + obj.getCsvName())));
//          String enriched = path + origPropList.get(0).getProcessed_dir()  + "/" + obj.getCsvName();
//          item.add(new DownloadLink("downloadEnrichCsv",
//        		  new File(enriched)));
          String newFormat = path + origPropList.get(0).getProcessed_dir()  + "/" + origPropList.get(0).getNewformat_dir() + "/" + obj.getCsvName();
          item.add(new DownloadLink("downloadFormatCsv",
        		  new File(newFormat)));
          String sqlFile = path + origPropList.get(0).getProcessed_dir()  + "/" + origPropList.get(0).getSqlinserts_file() + "/" + obj.getCsvName().replace("csv", "sql");
          //log.info("SQL PATH IS" + sqlFile);
          item.add(new DownloadLink("downloadFormatSql",
        		  new File(sqlFile)));
        } else {
          item.add(new Label("downloadCsvTag").setVisible(false));
        }

        final RatingModel rating = new RatingModel();
        final Label ratingValue =
            new Label("ratingValue", new PropertyModel<LocationModel>(obj, "rating"));
        ratingValue.setOutputMarkupId(true);
        final Label totalVotes =
            new Label("totalVotes", new PropertyModel<LocationModel>(obj, "nrating"));
        totalVotes.setOutputMarkupId(true);
        item.add(ratingValue);
        item.add(totalVotes);
        rating.updateRating((int) Math.ceil(obj.getRating()));
        item.add(new RatingsPanel("ratingPanel", new PropertyModel<Integer>(rating, "ratingPanel"),
            5, false) {

          private static final long serialVersionUID = 1L;
          boolean isVoted = false;

          @Override
          public boolean onIsStarActive(int star) {
            return rating.isActive(star);
          }

          double rating_value_aux = 0;

          public void onRated(int newRating, AjaxRequestTarget target) {
            int nratings = 0;
            double rating_value = 0;
            nratings = obj.getNrating();
            if (!isVoted) {
              List<RatingModel> ratings = new ArrayList<RatingModel>();
              ratings = locationServiceDAO.readRatingModel();
              for (RatingModel r : ratings) {
                if (r.getId().equals(obj.getId())) {
                  rating_value_aux += r.getRating();
                }
              }
            }
            if (!isVoted) {
              rating.updateRating(newRating);
              nratings++;
              List<RatingModel> ratings = new ArrayList<RatingModel>();
              ratings = locationServiceDAO.readRatingModel();
              for (RatingModel r : ratings) {
                if (r.getId().equals(obj.getId())) {
                  rating_value += r.getRating();
                }
              }
              rating_value += newRating;
              rating_value /= nratings;
              obj.setRating(rating_value);
              obj.setNrating(nratings);
              rating.setLocation(obj);
            } else {
              rating.updateRating(newRating);
              rating_value = rating_value_aux;
              rating_value += newRating;
              rating_value /= nratings;
              obj.setRating(rating_value);
            }
            if (isVoted) {
              locationServiceDAO.updateRatingModel(rating);
              locationServiceDAO.updateLocationModel(obj);
            } else {
              locationServiceDAO.storeRatingModel(rating);
              locationServiceDAO.updateLocationModel(obj);
              isVoted = true;
            }
            target.add(ratingValue);
            target.add(totalVotes);
            rating.updateRating((int) Math.round(obj.getRating()));
            rating.updateRating(newRating);
          }

		@Override
		protected void onRated(int arg0, Optional<AjaxRequestTarget> arg1) {
			// TODO Auto-generated method stub
			
		}
        });
      }
    };
    // if (listView) {
    //
    // }
    return listView;
  }

  public void saveTour(final WebMarkupContainer datacontainer) {

    CheckBoxMultipleChoice<String> listRoutes =
        new CheckBoxMultipleChoice<String>("routes", new Model<ArrayList<String>>(namesSelect),
            names);

    final Label labelSuggested = new Label("labelSuggested", "Suggested:");
    labelSuggested.setVisible(false);

    final ListView<SuggestRouteModel> listSuggests =
        new ListView<SuggestRouteModel>("listSuggests", new PropertyModel<List<SuggestRouteModel>>(
            this, "suggested")) {

          private static final long serialVersionUID = 1L;

          @Override
          protected void populateItem(ListItem<SuggestRouteModel> item) {

            log.info("tam list " + suggested.size());
            if (!suggested.isEmpty()) {
              labelSuggested.setVisible(true);
            }
            final SuggestRouteModel obj = item.getModelObject();
            item.add(new Label("suggest", obj.getName()));
            item.add(new Label("labelRecommend", " , Score: " + obj.getScore()));
            // + " (" + obj.getRating() + "/" + obj.getNrating() + " rates)"));
          }
        };

    AjaxFormChoiceComponentUpdatingBehavior checkRoutes =
        new AjaxFormChoiceComponentUpdatingBehavior() {

          private static final long serialVersionUID = 1L;

          @Override
          protected void onUpdate(AjaxRequestTarget target) {

            list2.clear();
            for (int i = 0; i < origList.size(); i++) {
              for (int j = 0; j < origRoutes.size(); j++) {
                for (int k = 0; k < namesSelect.size(); k++) {

                  if (origList.get(i).getId().equals(origRoutes.get(j).getId())
                      && origRoutes.get(j).getName().contentEquals(namesSelect.get(k).toString())) {
                    list2.add(origList.get(i));
                  }
                }
              }

            }
            list.clear();
            list.addAll(list2);
            setResponsePage(getPage());
          }
        };
    listRoutes.setOutputMarkupId(true);
    listRoutes.add(checkRoutes);

    final Form<?> routesForm = new Form<Void>("routesForm");
    routesForm.add(listRoutes);
    routesForm.add(labelSuggested);
    routesForm.add(listSuggests);
    routesForm.setVisible(false);
    datacontainer.add(routesForm);




    final TextField<String> nameRoute = new TextField<String>("nameRoute", Model.of(""));

    final TextField<String> coordInput = new TextField<String>("coordInput", Model.of(""));

    List<String> prices = new ArrayList<String>();
    prices.add("Any");
    prices.add("Free");
    prices.add("< 20 €");
    prices.add("20-100 €");
    prices.add("> 100 €");

    final DropDownChoice<String> pricesTypes =
        new DropDownChoice<String>("priceRoute", new PropertyModel<String>(this,
            "selectedPriceType"), prices) {

          private static final long serialVersionUID = 1L;

          protected void onSelectionChanged(String newSelection) {
            price = newSelection;
          }
        };

    List<String> days = new ArrayList<String>();
    days.add("Any");
    days.add("1/2");
    days.add("1");
    days.add("2");
    days.add(">= 3");

    final DropDownChoice<String> daysTypes =
        new DropDownChoice<String>("daysRoute",
            new PropertyModel<String>(this, "selectedDaysType"), days) {

          private static final long serialVersionUID = 1L;

          protected void onSelectionChanged(String newSelection) {
            day = newSelection;
          }
        };

    final Button buttonSave = new Button("saveRoute") {

      private static final long serialVersionUID = 1L;

      @Override
      public void onSubmit() {
        super.onSubmit();

        final String nameRouteValue = nameRoute.getModelObject();
        final String priceRouteValue = pricesTypes.getModelObject();
        final String daysRouteValue = daysTypes.getModelObject();
        final String coordInputValue = coordInput.getModelObject();

        String[] att = coordInputValue.split(";");
        int ncol = 0;
        String field = "";
        listRoute.clear();
        while (ncol < att.length) {
          RouteModel obj = new RouteModel();
          field = att[ncol];
          String[] latlng = field.split(",");
          obj.setId(Long.valueOf(latlng[0]));
          obj.setRating(Double.valueOf(latlng[1]));
          obj.setNrating(Integer.valueOf(latlng[2]));
          obj.setName(nameRouteValue);
          obj.setPrice(priceRouteValue);
          obj.setDays(daysRouteValue);
          obj.setLatitude(new BigDecimal(new Double(latlng[3]), new MathContext(
              latlng[3].length() - 1)));
          obj.setLongitude(new BigDecimal(new Double(latlng[4]), new MathContext(
              latlng[4].length() - 1)));
          listRoute.add(obj);
          ncol++;
        }
        routeServiceDAO.storeRouteModel(listRoute);
        listRoute = routeServiceDAO.readRouteModelName(nameRouteValue);

        list2.clear();
        for (RouteModel r : listRoute) {
          int el = 0;
          while (el < list.size()) {
            LocationModel a = list.get(el);
            // log.info("lat = " + r.getLatitude());
            // log.info("lat2 = " + a.getLatitude());
            // log.info("lng = " + r.getLongitude());
            // log.info("lng2 = " + a.getLongitude());
            if (r.getLatitude().compareTo(a.getLatitude()) == 0
                && r.getLongitude().compareTo(a.getLongitude()) == 0) {
              list2.add(a);
              el = list.size();
            }
            el++;
          }
        }

        list.clear();
        list.addAll(list2);
        nameRoute.setDefaultModel(null);
        pricesTypes.setDefaultModel(Model.of(""));
        daysTypes.setDefaultModel(Model.of(""));
        coordInput.setDefaultModel(null);
        this.updateModel();
        getPage().setVersioned(true);
      }

    };

    final IndicatingAjaxButton buttonClear = new IndicatingAjaxButton("clearRoute") {

      private static final long serialVersionUID = 1L;

      protected void onSubmit(AjaxRequestTarget target) {
        super.onSubmit(target);
        target.appendJavaScript("$('#coordInput,#nameRoute').val('')");
      }
    };
    buttonClear.setDefaultFormProcessing(false);

    final Form<?> coordForm = new Form<Void>("coordForm") {

      private static final long serialVersionUID = 1L;

    };
    datacontainer.add(coordForm);
    coordForm.add(daysTypes);
    coordForm.add(pricesTypes);
    coordForm.add(nameRoute);
    coordForm.add(coordInput);
    coordForm.add(buttonSave);
    coordForm.add(buttonClear);

    FeedbackPanel statusTour = new FeedbackPanel("statusCreateTour");
    statusTour.setOutputMarkupId(true);
    coordForm.add(statusTour);

    final DropDownChoice<String> pricesFilterTypes =
        new DropDownChoice<String>("priceFilterRoute", new PropertyModel<String>(this,
            "selectedPriceType"), prices) {

          private static final long serialVersionUID = 1L;

          protected void onSelectionChanged(String newSelection) {
            price = newSelection;
          }
        };

    final DropDownChoice<String> daysFilterTypes =
        new DropDownChoice<String>("daysFilterRoute", new PropertyModel<String>(this,
            "selectedDaysType"), days) {

          private static final long serialVersionUID = 1L;

          protected void onSelectionChanged(String newSelection) {
            day = newSelection;
          }
        };

    final Button filterTours = new Button("filterTours") {

      private static final long serialVersionUID = 1L;

      @Override
      public void onSubmit() {
        super.onSubmit();

        final String priceRouteValue = pricesFilterTypes.getModelObject();
        final String daysRouteValue = daysFilterTypes.getModelObject();

        names.clear();

        list.clear();
        for (int i = 0; i < origList.size(); i++) {
          for (int j = 0; j < origRoutes.size(); j++) {

            if (origList.get(i).getId().equals(origRoutes.get(j).getId())) {
              if (origRoutes.get(j).getPrice().contentEquals(priceRouteValue)
                  && origRoutes.get(j).getDays().contentEquals(daysRouteValue)) {

                if (!names.contains(origRoutes.get(j).getName())) {
                  names.add(origRoutes.get(j).getName());
                }
              }
            }
          }
        }

        log.info("NUMBER OF NAMES" + names);

        double total_rating = 0;
        int total_nrating = 0;

        suggested.clear();

        // we count the total rate and total number of rates from each route
        for (int j = 0; j < names.size(); j++) {
          for (int i = 0; i < origRoutes.size(); i++) {
            if (origRoutes.get(i).getName().contentEquals(names.get(j).toString())) {
              total_rating = total_rating + origRoutes.get(i).getRating();
              total_nrating = total_nrating + origRoutes.get(i).getNrating();
            }
          }
          // if the route has existing rates we add it as suggested
          if (total_nrating > 0) {
            SuggestRouteModel srm = new SuggestRouteModel();
            srm.setName(names.get(j).toString());
            total_rating =
                new BigDecimal(total_rating).setScale(2, RoundingMode.HALF_UP).doubleValue();
            srm.setRating(total_rating);
            srm.setNrating(total_nrating);
            suggested.add(srm);
          }

          total_rating = 0;
          total_nrating = 0;
        }

        double avg_tour = 0.0;

        // now we calculate the average score for each route with existing votes
        for (int i = 0; i < suggested.size(); i++) {

          avg_tour = (suggested.get(i).getRating() + suggested.get(i).getNrating()) / 2;
          avg_tour = new BigDecimal(avg_tour).setScale(2, RoundingMode.HALF_UP).doubleValue();
          suggested.get(i).setScore(avg_tour);
        }

        // we order the average score in descending
        Collections.sort(suggested, new Comparator<SuggestRouteModel>() {
          @Override
          public int compare(SuggestRouteModel s1, SuggestRouteModel s2) {
            return Double.valueOf(s2.getScore()).compareTo(s1.getScore());
          }
        });

        namesSelect.clear();

        // we mark the top score as marked
        if (!suggested.isEmpty()) {
          namesSelect.add(suggested.get(0).getName());
        }

        if (suggested.isEmpty()) {
          labelSuggested.setVisible(false);
        }

        // we display the tour we have marked from the menu panel
        list2.clear();
        for (int i = 0; i < origList.size(); i++) {
          for (int j = 0; j < origRoutes.size(); j++) {
            for (int k = 0; k < namesSelect.size(); k++) {
              if (origList.get(i).getId().equals(origRoutes.get(j).getId())
                  && origRoutes.get(j).getName().contentEquals(namesSelect.get(k).toString())) {
                list2.add(origList.get(i));
              }
            }
          }
        }
        list.clear();
        list.addAll(list2);

        setResponsePage(getPage());
      }
    };

    final Form<?> filterForm = new Form<Void>("filterForm") {

      private static final long serialVersionUID = 1L;

    };
    datacontainer.add(filterForm);
    filterForm.setVisible(false);
    filterForm.add(daysFilterTypes);
    filterForm.add(pricesFilterTypes);
    filterForm.add(filterTours);


    final TextField<String> idInput = new TextField<String>("idInput", Model.of(""));


    final Form<?> editFormTriggerClick = new Form<Void>("editFormTriggerClick") {

      private static final long serialVersionUID = 1L;

    };
    editFormTriggerClick.add(idInput);
    datacontainer.add(editFormTriggerClick);


    final Form<?> editForm = new Form<Void>("editForm") {

      private static final long serialVersionUID = 1L;

    };
    editForm.setVisible(false);
    
    
    final Form<?> locationsForm = new Form<Void>("locationsForm") {
		@Override
		protected void onSubmit() {


	        for (int i = 0; i < origList.size(); i++) {
	          for (int j = 0; j < namesRemoveSelect.size(); j++) {
	            if (origList.get(i).getCsvName().contentEquals(namesRemoveSelect.get(j).toString())) {
	              //list2.add(origList.get(i));
	            	log.info("WE REMOVE>>>: "+origList.get(i).getName());
	              locationServiceDAO.removeLocationModel(origList.get(i));
	            }
	          }
	        }
	        setResponsePage(getPage());
		}
    };
    // locationsForm.setVisible(false);
    datacontainer.add(locationsForm);
    
    final CheckBoxMultipleChoice<String> listRemoveLocations = 
    		new CheckBoxMultipleChoice<String>("removeLocations", new Model<ArrayList<String>>(namesRemoveSelect),
    	            names);
    
    locationsForm.add(listRemoveLocations);
    
    CheckBoxMultipleChoice<String> listLocations =
        new CheckBoxMultipleChoice<String>("locations", new Model<ArrayList<String>>(namesSelect),
            names); 
    
    //add(new FeedbackPanel("feedbackCheckboxes"));
    
    AjaxFormChoiceComponentUpdatingBehavior check = new AjaxFormChoiceComponentUpdatingBehavior() {

      private static final long serialVersionUID = 1L;

      @Override
      protected void onUpdate(AjaxRequestTarget target) {

        list2.clear();
        for (int i = 0; i < origList.size(); i++) {
          for (int j = 0; j < namesSelect.size(); j++) {
            if (origList.get(i).getCsvName().contentEquals(namesSelect.get(j).toString())) {
              list2.add(origList.get(i));
            }
          }
        }
        list.clear();
        list.addAll(list2);
        target.appendJavaScript("$('#editInfo').hide();");
        setResponsePage(getPage());
      }

      
    };
    listLocations.add(check);
    locationsForm.add(listLocations);

    ListView<LocationModel> editRowList = new ListView<LocationModel>("editRow", listEdit) {

      private static final long serialVersionUID = 1L;

      @Override
      protected void populateItem(final ListItem<LocationModel> item) {

        item.add(new TextField<String>("name", new PropertyModel<String>(item.getModelObject(),
            "name")));
        item.add(new TextField<String>("csvName", new PropertyModel<String>(item.getModelObject(),
            "csvName")));
        item.add(new TextField<String>("type", new PropertyModel<String>(item.getModelObject(),
            "type")));
        item.add(new TextField<String>("address", new PropertyModel<String>(item.getModelObject(),
            "address")));
        item.add(new TextField<String>("street", new PropertyModel<String>(item.getModelObject(),
            "street")));
        item.add(new TextField<String>("number", new PropertyModel<String>(item.getModelObject(),
            "number")));
        item.add(new TextField<String>("district", new PropertyModel<String>(item.getModelObject(),
            "district")));
        item.add(new TextField<String>("latitude", new PropertyModel<String>(item.getModelObject(),
            "latitude")));
        item.add(new TextField<String>("longitude", new PropertyModel<String>(
            item.getModelObject(), "longitude")));
        item.add(new TextField<String>("website", new PropertyModel<String>(item.getModelObject(),
            "website")));
        item.add(new TextField<String>("image", new PropertyModel<String>(item.getModelObject(),
            "image")));
        item.add(new TextField<String>("description", new PropertyModel<String>(item
            .getModelObject(), "description")));
        item.add(new TextField<String>("email", new PropertyModel<String>(item.getModelObject(),
            "email")));
        item.add(new TextField<String>("phone", new PropertyModel<String>(item.getModelObject(),
            "phone")));
        item.add(new TextField<String>("date", new PropertyModel<String>(item.getModelObject(),
            "date")));
        item.add(new TextField<String>("schedule", new PropertyModel<String>(item.getModelObject(),
            "schedule")));
        item.add(new TextField<String>("otherInfo", new PropertyModel<String>(item.getModelObject(),
                "otherInfo")));
      }
    };
    editForm.add(editRowList);

    final Button saveLocation = new Button("saveLocation") {

      private static final long serialVersionUID = 1L;

      @Override
      public void onSubmit() {
        super.onSubmit();

        locationServiceDAO.updateLocationModel(listEdit.get(0));
        editForm.setVisible(false);
      }
    };
    saveLocation.setVisible(false);
    editForm.add(saveLocation);

    final IndicatingAjaxButton closeEdit = new IndicatingAjaxButton("closeEdit") {
      private static final long serialVersionUID = 1L;

      protected void onSubmit(AjaxRequestTarget target) {
        super.onSubmit(target);

        target.appendJavaScript("$('#editInfo').hide();");
        editForm.setVisible(false);
      }
    };
    closeEdit.setVisible(false);

    IndicatingAjaxButton editInfoButton = new IndicatingAjaxButton("editInfoButton") {

      private static final long serialVersionUID = 1L;

      protected void onSubmit(AjaxRequestTarget target) {
        editForm.setVisible(true);
        super.onSubmit(target);
        final String idInputValue = idInput.getModelObject();

        int i = 0;
        boolean find = false;
        listEdit.clear();

        while (find == false) {
          if (origList.get(i).getId().toString().contentEquals(idInputValue)) {
            listEdit.add(origList.get(i));
            // log.info(origList.get(i).getName() + " has been founded");
            find = true;

          }
          i++;
        }
        target.add(datacontainer);
        saveLocation.setVisible(true);
        setResponsePage(getPage());
        closeEdit.setVisible(true);
      }
    };
    editFormTriggerClick.add(editInfoButton);

    editForm.setOutputMarkupId(true);
    editForm.add(closeEdit);
    datacontainer.add(editForm);


    final Button displayTours = new Button("displayTours") {

      private static final long serialVersionUID = 1L;

      @Override
      public void onSubmit() {

        names.clear();
        suggested.clear();
        namesSelect.clear();
        origRoutes.clear();
        origRoutes = routeServiceDAO.readRouteModel();


        for (int i = 0; i < origRoutes.size(); i++) {

          // we add only once the name of the existing routes
          if (!names.contains(origRoutes.get(i).getName())) {
            names.add(origRoutes.get(i).getName());
          }
        }
        double total_rating = 0;
        int total_nrating = 0;

        // we count the total rate and total number of rates from each route
        for (int j = 0; j < names.size(); j++) {
          for (int i = 0; i < origRoutes.size(); i++) {
            if (origRoutes.get(i).getName().contentEquals(names.get(j).toString())) {
              total_rating = total_rating + origRoutes.get(i).getRating();
              total_nrating = total_nrating + origRoutes.get(i).getNrating();
            }
          }
          // if the route has existing rates we add it as suggested
          if (total_nrating > 0) {
            SuggestRouteModel srm = new SuggestRouteModel();
            srm.setName(names.get(j).toString());
            total_rating =
                new BigDecimal(total_rating).setScale(2, RoundingMode.HALF_UP).doubleValue();
            srm.setRating(total_rating);
            srm.setNrating(total_nrating);
            suggested.add(srm);
          }

          total_rating = 0;
          total_nrating = 0;
        }

        double avg_tour = 0.0;

        // now we calculate the average score for each route with existing votes
        for (int i = 0; i < suggested.size(); i++) {

          avg_tour = (suggested.get(i).getRating() + suggested.get(i).getNrating()) / 2;
          avg_tour = new BigDecimal(avg_tour).setScale(2, RoundingMode.HALF_UP).doubleValue();
          suggested.get(i).setScore(avg_tour);
        }

        // we order the average score in descending
        Collections.sort(suggested, new Comparator<SuggestRouteModel>() {
          @Override
          public int compare(SuggestRouteModel s1, SuggestRouteModel s2) {
            return Double.valueOf(s2.getScore()).compareTo(s1.getScore());
          }
        });

        namesSelect.clear();

        // we mark the top score as marked
        if (!suggested.isEmpty()) {
          namesSelect.add(suggested.get(0).getName());
        }

        // we display the tour we have marked from the menu panel
        list2.clear();
        for (int i = 0; i < origList.size(); i++) {
          for (int j = 0; j < origRoutes.size(); j++) {
            for (int k = 0; k < namesSelect.size(); k++) {
              if (origList.get(i).getId().equals(origRoutes.get(j).getId())
                  && origRoutes.get(j).getName().contentEquals(namesSelect.get(k).toString())) {
                list2.add(origList.get(i));
              }
            }
          }
        }
        list.clear();
        list.addAll(list2);

        displayToursConfirm = true;
        displayLocationsConfirm = false;

        routesForm.setVisible(true);
        filterForm.setVisible(true);
        locationsForm.setVisible(false);
        coordForm.setVisible(false);
        editForm.setVisible(false);
      }
    };
    displayTours.setOutputMarkupId(true);
    displayTours.setMarkupId("initDisplayTours");

    displayLocationsConfirm = true;

    final Button displayLocations = new Button("displayLocations") {

      private static final long serialVersionUID = 1L;

      @Override
      public void onSubmit() {

        names.clear();
        namesSelect.clear();

        // we display by default the top places when we click View Locations
        for (int i = 0; i < origList.size(); i++) {
          if (!names.contains(origList.get(i).getCsvName())) {
            names.add(origList.get(i).getCsvName());
            if (origList.get(i).getCsvName().contentEquals("Top places")) {
              namesSelect.add(origList.get(i).getCsvName());
            }
          }
        }

        // we refresh the list to add all the locations that belong to name selected
        list2.clear();
        for (int i = 0; i < origList.size(); i++) {
          for (int j = 0; j < namesSelect.size(); j++) {
            if (origList.get(i).getCsvName().contentEquals(namesSelect.get(j).toString())) {
              list2.add(origList.get(i));
            }
          }
        }
        list.clear();
        list.addAll(list2);

        displayLocationsConfirm = true;
        displayToursConfirm = false;

        routesForm.setVisible(false);
        filterForm.setVisible(false);
        locationsForm.setVisible(true);
        coordForm.setVisible(true);
        editForm.setVisible(true);
        setResponsePage(LocationServicePage.class);
        // nameRoute.setDefaultModel(null);
        // pricesTypes.setDefaultModel(Model.of(""));
        // daysTypes.setDefaultModel(Model.of(""));
        // coordInput.setDefaultModel(null);
      }
    };
    displayLocations.setOutputMarkupId(true);
    displayLocations.setMarkupId("initDisplayLocations");

    final Form<?> displayOpt = new Form<Void>("displayOption") {

      private static final long serialVersionUID = 1L;

      @Override
      protected void onBeforeRender() {
        super.onBeforeRender();

        if (displayToursConfirm) {
          displayTours.add(new AttributeModifier("class", "activateTours"));
          displayLocations.add(new AttributeModifier("class", "desactiveLocations"));
        }

        if (displayLocationsConfirm) {
          displayLocations.add(new AttributeModifier("class", "activateLocations"));
          displayTours.add(new AttributeModifier("class", "desactiveTours"));
        }
      }
    };
    datacontainer.add(displayOpt);
    displayOpt.add(displayLocations);
    displayOpt.add(displayTours);
  }
}
