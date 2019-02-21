package com.opendataview.web.pages.locations;

import java.awt.geom.Path2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.opendataview.web.model.LocationModel;
import com.opendataview.web.model.PieChartModel;
import com.opendataview.web.pages.index.BasePage;
import com.opendataview.web.panels.DirectionPanel;
import com.opendataview.web.persistence.LocationServiceDAO;
import com.opendataview.web.persistence.PropertiesServiceDAO;
import com.opendataview.web.persistence.RouteServiceDAO;
import com.opendataview.web.persistence.UserServiceDAO;

import de.adesso.wickedcharts.highcharts.options.ChartOptions;
import de.adesso.wickedcharts.highcharts.options.CreditOptions;
import de.adesso.wickedcharts.highcharts.options.CssStyle;
import de.adesso.wickedcharts.highcharts.options.DataLabels;
import de.adesso.wickedcharts.highcharts.options.Events;
import de.adesso.wickedcharts.highcharts.options.ExportingOptions;
import de.adesso.wickedcharts.highcharts.options.Function;
import de.adesso.wickedcharts.highcharts.options.HorizontalAlignment;
import de.adesso.wickedcharts.highcharts.options.Options;
import de.adesso.wickedcharts.highcharts.options.SeriesType;
import de.adesso.wickedcharts.highcharts.options.Title;
import de.adesso.wickedcharts.highcharts.options.Tooltip;
import de.adesso.wickedcharts.highcharts.options.color.HexColor;
import de.adesso.wickedcharts.highcharts.options.functions.RedirectFunction;
import de.adesso.wickedcharts.highcharts.options.series.Point;
import de.adesso.wickedcharts.highcharts.options.series.PointSeries;
import de.adesso.wickedcharts.highcharts.options.series.Series;
import de.adesso.wickedcharts.wicket8.highcharts.Chart;

//import de.adesso.wickedcharts.highcharts.options.ChartOptions;
//import de.adesso.wickedcharts.highcharts.options.CreditOptions;
//import de.adesso.wickedcharts.highcharts.options.Events;
//import de.adesso.wickedcharts.highcharts.options.ExportingOptions;
//import de.adesso.wickedcharts.highcharts.options.Legend;
//import de.adesso.wickedcharts.highcharts.options.Options;
//import de.adesso.wickedcharts.highcharts.options.SeriesType;
//import de.adesso.wickedcharts.highcharts.options.Title;
//import de.adesso.wickedcharts.highcharts.options.functions.RedirectFunction;
//import de.adesso.wickedcharts.highcharts.options.series.Point;
//import de.adesso.wickedcharts.highcharts.options.series.PointSeries;
//import de.adesso.wickedcharts.highcharts.options.series.Series;
//import de.adesso.wickedcharts.wicket8.highcharts.Chart;

public class LocationServicePage extends BasePage {

	PageParameters pageParameters = new PageParameters();

	private static final long serialVersionUID = 1L;

	@Override
	protected void onInitialize() {
		super.onInitialize();
		visitChildren(new IVisitor<Component, Void>() {
			@Override
			public void component(Component component, IVisit<Void> arg1) {
//				if (!component.isStateless())
//					System.out.println("Component " + component.getId() + " is not stateless");
			}
		});
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);

		response.render(OnDomReadyHeaderItem.forScript("window.onload = function () {}"));

		String value = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("value").toString();
		String coords = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("coords").toString();
		String dist = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("dist").toString();
		String zoom = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("zoom").toString();
		String fullscreen = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("fullscreen")
				.toString();
		String mapType = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("maptype").toString();
//		log.info("param coord is: " + polygonCoordInputValue);
//		log.info("param distance dist is: " + polygonDist);

		if (zoom != null || fullscreen != null || mapType != null || dist != null) {
			log.info("we have a search with zoom " + zoom);
			response.render(OnDomReadyHeaderItem.forScript("$('#mapZoomLevel').val(" + zoom + ");$('#viewPanels').val("
					+ fullscreen + ");$('#mapType').val('" + mapType + "');$('#geoCoordDistance').val(" + dist + ")"));
		}

		if (coords != null) {
			response.render(OnDomReadyHeaderItem.forScript("$('#geoCoordDistance').val(" + dist + ")"));
			String[] att = coords.substring(1, coords.length() - 1).split("\\),\\(");
			if (att.length == 1) {
				response.render(OnDomReadyHeaderItem
						.forScript("window.onload = function () {initMyPosition();$(\".slider_wrapper\").show()}"));
			} else {
				response.render(OnDomReadyHeaderItem
						.forScript("window.onload = function () {polygon = new google.maps.Polygon({paths:["
								+ coords.replaceAll("\\(", "{lat:").replaceAll("\\)", "}").replaceAll("[0-9],", ",lng:")
								+ "],zIndex: 1300, fillColor: 'rgba(255,255,255,.0)', clickable: false, editable: false, strokeWeight: 0, fillOpacity: 0.45, draggable: false, flat: false,});polygon.setMap(map.gmap(\"get\",\"map\"))}"));

//				log.info("Polygon is: polygon = new google.maps.Polygon({paths:["
//						+ polygonCoordInputValue.replaceAll("\\(", "{lat:").replaceAll("\\)", "}").replaceAll("[0-9],",
//								",lng:")
//						+ "],zIndex: 1300, fillColor: 'rgba(255,255,255,.87)', clickable: true, editable: true, strokeWeight: 0, fillOpacity: 0.45, draggable: false, flat: true,});polygon.setMap(map.gmap(\"get\",\"map\"))");

			}
		}
//		if(polygonCoordInputValue != null) {
//
//				response.render(OnDomReadyHeaderItem.forScript("window.onload = function () {}"));
//		}
	}

	private List<LocationModel> list = new ArrayList<LocationModel>();
	private List<LocationModel> list2 = new ArrayList<LocationModel>();
	private List<LocationModel> origList = new ArrayList<LocationModel>();
	private List<String> names = new ArrayList<String>();
	private Form<?> locationsForm = null;

	private ArrayList<String> namesRemoveSelect = new ArrayList<String>();
	private ArrayList<String> namesSelect = new ArrayList<String>();
	private List<LocationModel> temp_list = new ArrayList<LocationModel>();
	private List<LocationModel> temp_list2 = new ArrayList<LocationModel>();
	int count = 0;
	List<String> addlinks = null;

	private static Options chart_options = new Options();
	private Chart pie_chart = new Chart("chart", chart_options);
	private List<PieChartModel> pieChartList = new ArrayList<PieChartModel>();

	private List<String> allAttributes = new ArrayList<String>();
	private List<Integer> allSums = new ArrayList<Integer>();
	private static Series<Point> series = new PointSeries();

	private int max_number_markers_perpage = 1000;

	private final static Logger log = LoggerFactory.getLogger(LocationServicePage.class);

	String selected = null;

	@SpringBean
	private LocationServiceDAO locationServiceDAO;

	@SpringBean
	private RouteServiceDAO routeServiceDAO;

	@SpringBean
	private UserServiceDAO userServiceDAO;

	@SpringBean
	private static PropertiesServiceDAO propertiesServiceDAO;

	public static final double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
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

	final TextField<String> viewPanels = new TextField<String>("viewPanels", Model.of("false"));
	final TextField<String> mapType = new TextField<String>("mapType", Model.of("grayscale"));
	final TextField<String> mapZoomLevel = new TextField<String>("mapZoomLevel", Model.of("8"));
	final TextField<String> geoCoordDistance = new TextField<String>("geoCoordDistance", Model.of("1"));
//	Pie pie = new Pie();
	final PageableListView<LocationModel> lview = displayList("rows", list, max_number_markers_perpage);

	public LocationServicePage(PageParameters parameters) throws IOException {

//		PieChartPanel pieChart = new PieChartPanel("chart", Model.of(pie));
//		pie.getOptions().setResponsive(true);
//		pie.getOptions().setMaintainAspectRatio(true);
//		pie.getOptions().setTooltipTemplate("<%= label %>");

//        LatLng.of(55.0, 1),
//        LatLng.of(50.0, -1)

		setStatelessHint(false);
		setVersioned(false);

		WebSession session = WebSession.get();
		viewPanels.setOutputMarkupId(true);
		mapType.setOutputMarkupId(true);
		mapZoomLevel.setOutputMarkupId(true);
		geoCoordDistance.setOutputMarkupId(true);
//		final TextField<String> mapIconMarker = new TextField<String>("mapIconMarker", Model.of("10"));
//		mapIconMarker.setOutputMarkupId(true);

		String username = null;
//		if (session.getAttribute("user_name") == null) {
//			throw new PageExpiredException("No user available. Page seems to be expired.");
//		}
		if (session.getAttribute("user_name") != null) {
			username = session.getAttribute("user_name").toString();
		}

		final WebMarkupContainer datacontainer = new WebMarkupContainer("data");
		datacontainer.setOutputMarkupId(true);
		add(datacontainer);

		chart_options.setExporting(new ExportingOptions().setEnableImages(false).setEnabled(false));
		chart_options.setChartOptions(new ChartOptions().setType(SeriesType.PIE));
		chart_options.setCredits(new CreditOptions().setEnabled(false));
		chart_options.setTitle(new Title("").setEnabled(true));
		pie_chart.setVisible(false);
		// chart_options.setLegend(new
		// Legend().setReversed(true).setWidth(300).setMaxHeight(200));
		datacontainer.add(pie_chart);

//		datacontainer.add(pieChart);

		final WebMarkupContainer wmc = new WebMarkupContainer("wmc");
		wmc.setVersioned(false);
		wmc.setOutputMarkupId(true);
		add(wmc);

		final WebMarkupContainer wmc2 = new WebMarkupContainer("wmc2");
		// wmc2.setVersioned(false);
		wmc2.setOutputMarkupId(true);
		add(wmc2);

//		final Form<?> showMarkerForm = new Form<Void>("showMarkerForm");
//		// showMarkerInfoList.setOutputMarkupId(true);
//
//		showMarkerForm.setOutputMarkupId(true);
//		wmc2.add(showMarkerForm);
//		wmc.add(showMarkerInfoList);
//		wmc.add(new DirectionPanel("direction2"));
//		wmc.setOutputMarkupId(true);

		final Model<String> modelLat = new Model<String>("");
		Label currentLatitude = new Label("currentLatitude", modelLat);
		currentLatitude.setOutputMarkupId(true);
		wmc.add(currentLatitude);

		final Model<String> modelLng = new Model<String>("");
		Label currentLongitude = new Label("currentLongitude", modelLng);
		currentLongitude.setOutputMarkupId(true);
		wmc.add(currentLongitude);

		origList = locationServiceDAO.readLocationModel();

		// restriction to display 300 locations per page (to not overload the browser)
		lview.setOutputMarkupId(true);
		wmc.add(lview);

		wmc.add(tableNavigator);
		tableNavigator.setVisible(false);

		final TextField<String> idInput2 = new TextField<String>("idInput2", Model.of(""));
		AjaxButton showMarkerInfoBttn = new AjaxButton("showMarkerInfoBttn") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				super.onSubmit(target);

				String idInputValue2 = idInput2.getModelObject();
//				log.info("YOU JUST CLICKED OVER MARKER: " + idInputValue2);
				temp_list2.clear();

				temp_list2.addAll(locationServiceDAO.getLocationByID(idInputValue2));

//				log.info("TEMP2 NAME IS " + temp_list2.get(0).getName());

				target.add(wmc2);
				// target.appendJavaScript("$('.markersTable2').show()");

				// target.add(datacontainer);
				// setResponsePage(getPage());

			}
		};
		final Form<?> showMarkerInfo = new Form<Void>("showMarkerInfo");
		showMarkerInfo.add(idInput2);
		datacontainer.add(showMarkerInfo);
		showMarkerInfo.add(showMarkerInfoBttn);
		// showMarkerInfoBttn.setDefaultFormProcessing(false);

		/*
		 * In case we need to display the current page of the Markers navigation final
		 * Label currentPage = new Label("currentPage", new Model<String>());
		 * currentPage.setOutputMarkupId(true); wmc.add(currentPage);
		 */

		if (!parameters.get("id").isNull()) {
			log.info("we will query id=" + parameters.get("id"));
			int id = parameters.get("id").toInt();
			for (int i = 0; i < origList.size(); i++) {

				if (origList.get(i).getId() == id) {
					list2.add(origList.get(i));
				}

			}

			list.addAll(list2);
		}
		if (!parameters.get("value").isNull()) {
			list2 = locationServiceDAO.searchLocationModel(parameters.get("value").toString().toLowerCase());
			list.addAll(list2);
		}

		if (!parameters.get("coords").isNull()) {

			String[] att = null;

			String geoCoordDistanceValue = "";
			if (parameters.get("dist").isNull()) {
				geoCoordDistanceValue = "1";
			} else {
				geoCoordDistanceValue = parameters.get("dist").toString();
			}

			String polygonCoordInputValue = parameters.get("coords").toString();
			double distanceKm = 0;
			if (geoCoordDistanceValue != null) {
				distanceKm = Double.valueOf(geoCoordDistanceValue);
			}

			if (polygonCoordInputValue == null) {
				list.clear();

			} else if (polygonCoordInputValue != null && polygonCoordInputValue.contains(",")) {
				polygonCoordInputValue = polygonCoordInputValue.replaceAll("\\s+", "");

				Path2D myPolygon = new Path2D.Double();
				att = polygonCoordInputValue.substring(1, polygonCoordInputValue.length() - 1).split("\\),\\(");

				String[] coordXY = null;
				for (int i = 0; i < att.length; i++) {
					coordXY = att[i].split(",");
					if (i == 0) {
						myPolygon.moveTo(Double.valueOf(coordXY[0]), Double.valueOf(coordXY[1]));
					} else {
						myPolygon.lineTo(Double.valueOf(coordXY[0]), Double.valueOf(coordXY[1]));
					}
				}
				myPolygon.closePath();

				if (att.length == 1) {
					modelLat.setObject(coordXY[0]);
					modelLng.setObject(coordXY[1]);
				}

				list.clear();

				for (LocationModel loc : origList) {

					if (!polygonCoordInputValue.isEmpty()) {
						if (att.length == 1) {

							double dist = distance(Double.valueOf(coordXY[0]), Double.valueOf(coordXY[1]),
									loc.getLatitude().doubleValue(), loc.getLongitude().doubleValue(), 'K');

							if (dist < distanceKm) {

								list.add(loc);
							}
						} else {
							if (myPolygon.contains(loc.getLatitude().doubleValue(), loc.getLongitude().doubleValue())) {
								list.add(loc);
							}
						}

					}
				}

			}
//			if (!list.isEmpty()) {
//				saveVisibleLocFile.setVisible(true);
//				saveSqlLocBackup.setVisible(true);
//				target.add(saveVisibleLocFile);
//				target.add(saveSqlLocBackup);
//			}
//			target.add(wmc);

		}

		ListView<LocationModel> showMarkerInfoList = new ListView<LocationModel>("showMarkerInfoList", temp_list2) {

			private static final long serialVersionUID = 1L;

			String zoom = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("zoom").toString();
			String fullscreen = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("fullscreen")
					.toString();
			String maptype = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("maptype")
					.toString();
//			String fullscreen = viewPanels.getModelObject();
//			String maptype = mapType.getModelObject();
//			String zoom = mapZoomLevel.getModelObject();

			@Override
			public void populateItem(final ListItem<LocationModel> item) {

				// log.info("WE DISPLAY THE MARKER LIST WITH " +
				// item.getModelObject().getName());
				final LocationModel obj = item.getModelObject();

				item.add(new Label("id_location2", obj.getId()));

//				log.info("LOCATION ID IS MARKER: " + obj.getId());

				item.add(new Label("icon_marker2", obj.getIconmarker()));

				if (obj.getName() != null && !obj.getName().isEmpty()) {
					item.add(new Label("name2", obj.getName()));
				} else {
					item.add(new Label("name2").setVisible(false));
				}
//				if (username != null) {
//					item.add(new Label("userSession", username));
//				} else {
//					item.add(new Label("userSession", "no active user"));
//				}
				if (obj.getCsvName() != null && !obj.getCsvName().isEmpty()) {
					item.add(new Label("csvName2", obj.getCsvName()));
				} else {
					item.add(new Label("csvName2").setVisible(false));
				}
				if (obj.getType() != null && !obj.getType().isEmpty()) {
					item.add(new Label("typeTag2", "Type: "));
					item.add(new Label("type2", obj.getType()));
				} else {
					item.add(new Label("typeTag2").setVisible(false));
					item.add(new Label("type2").setVisible(false));
				}
				if (obj.getCity() != null && !obj.getCity().isEmpty()) {
					item.add(new Label("cityTag2", "Address: "));
					item.add(new Label("city2", obj.getAddress()));
				} else {
					item.add(new Label("cityTag2").setVisible(false));
					item.add(new Label("city2").setVisible(false));
				}
				if (obj.getPostcode() != null && !obj.getPostcode().isEmpty()) {
					item.add(new Label("postcode2", obj.getPostcode()));
				} else {
					item.add(new Label("postcode2").setVisible(false));
				}
				if (obj.getLatitude() != null) {
					item.add(new Label("coordsTag2", "Coords: "));
					item.add(new Label("latitude2", obj.getLatitude().toString()));
					item.add(new Label("longitude2", obj.getLongitude().toString()));
				} else {
					item.add(new Label("latitude2").setVisible(false));
					item.add(new Label("longitude2").setVisible(false));
				}
				if (obj.getWebsite() != null && !obj.getWebsite().isEmpty()) {
					item.add(new Label("websiteTag2", "Website: "));
					item.add(new ExternalLink("website2", obj.getWebsite(), obj.getWebsite()));
				} else {
					item.add(new Label("websiteTag2").setVisible(false));
					item.add(new Label("website2").setVisible(false));
				}
				if (obj.getUrlImage() != null && !obj.getUrlImage().isEmpty()) {
					item.add(new ContextImage("image2", obj.getUrlImage().toString()));
				} else {
					item.add(new Image("image2", obj.getUrlImage()).setVisible(false));
				}
				if (obj.getDescription() != null && !obj.getDescription().isEmpty()) {
					item.add(new Label("descriptionTag2", "Description: "));
					item.add(new Label("description2", obj.getDescription()));
				} else {
					item.add(new Label("descriptionTag2").setVisible(false));
					item.add(new Label("description2").setVisible(false));
				}
				if (obj.getEmail() != null && !obj.getEmail().isEmpty()) {
					item.add(new Label("emailTag2", "Email: "));
					item.add(new ExternalLink("email2", "mailto:" + obj.getEmail(), obj.getEmail()));
				} else {
					item.add(new Label("emailTag2").setVisible(false));
					item.add(new Label("email2").setVisible(false));
				}
				if (obj.getPhone() != null && !obj.getPhone().isEmpty()) {
					item.add(new Label("phoneTag2", "Tlf.: "));
					item.add(new Label("phone2", obj.getPhone()));
				} else {
					item.add(new Label("phoneTag2").setVisible(false));
					item.add(new Label("phone2").setVisible(false));
				}
				if (obj.getDate() != null && !obj.getDate().isEmpty()) {
					item.add(new Label("dateTag2", "Date: "));
					item.add(new Label("date2", obj.getDate()));
				} else {
					item.add(new Label("dateTag2").setVisible(false));
					item.add(new Label("date2", obj).setVisible(false));
				}
				if (obj.getSchedule() != null && !obj.getSchedule().isEmpty()) {
					item.add(new Label("scheduleTag2", "Schedule: "));
					item.add(new Label("schedule2", obj.getSchedule()));
				} else {
					item.add(new Label("scheduleTag2").setVisible(false));
					item.add(new Label("schedule2").setVisible(false));
				}
				if (obj.getOtherInfo() != null) {
					item.add(new Label("otherTag2", "Searcahable attributes: "));
					addlinks = new ArrayList<>(Arrays.asList(obj.getOtherInfo().split("##")));
					String newinfo = "";

					for (int i = 0; i < addlinks.size(); i++) {
						newinfo += "<a href='search?&value=" + addlinks.get(i).trim().replaceAll(" ", "+") + "&zoom="
								+ zoom + "&fullscreen=" + fullscreen + "&maptype=" + maptype + "'>" + addlinks.get(i)
								+ "</a><br />";
					}
					item.add(new Label("other2", newinfo).setEscapeModelStrings(false));
				} else {
					item.add(new Label("otherTag2").setVisible(false));
					item.add(new Label("other2").setVisible(false));
				}
				if (obj.getDate_updated() != null) {
					item.add(new Label("dateUpdatedTag2", "Last update: "));
					item.add(new Label("dateUpdated2", obj.getDate_updated()));
				} else {
					item.add(new Label("dateUpdatedTag2").setVisible(false));
					item.add(new Label("dateUpdated2").setVisible(false));
				}
				if (obj.getDate_published() != null) {
					item.add(new Label("datePublishedTag2", "First publish: "));
					item.add(new Label("datePublished2", obj.getDate_published()));
				} else {
					item.add(new Label("datePublishedTag2").setVisible(false));
					item.add(new Label("datePublished2").setVisible(false));
				}
				if (obj.getUsername() != null) {
					item.add(new Label("userPublishedTag2", "Last editor: "));
					item.add(new Label("userPublished2", obj.getUsername()));

				} else {
					item.add(new Label("userPublishedTag2").setVisible(false));
					item.add(new Label("userPublished2").setVisible(false));
				}
				if (obj.getSource() != null && !obj.getSource().isEmpty()) {
					item.add(new Label("sourcePublishedTag2", "Data publisher: "));
					item.add(new Label("sourcePublished2", obj.getSource()));
				} else {
					item.add(new Label("sourcePublishedTag2").setVisible(false));
					item.add(new Label("sourcePublished2").setVisible(false));
				}

			}
		};
		wmc2.add(showMarkerInfoList);
		wmc2.add(new DirectionPanel("direction2"));

		final Form<?> formSaveLoc = new Form<Void>("saveLocationsForm");
		datacontainer.add(formSaveLoc);

		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy'_'HH:mm");
		File saveVisibleLoc = new File(Files.createTempDir(), "Visible_Locations_" + sdf.format(now) + ".csv");
		formSaveLoc.add(new DownloadLink("saveVisibleLoc", saveVisibleLoc));

		final AjaxButton saveVisibleLocFile = new AjaxButton("saveVisibleLocFile") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {

				String DELIMITER = ";";
				String NEW_LINE = "\n";

				try {
					PrintWriter wr = new PrintWriter(saveVisibleLoc, "utf-8");
					if (!list.isEmpty()) {
						// IF YOU CHANGE SOMETHING HERE MUST BE ALSO CHANGED IN 3 BELOW APPENDS AND
						// KEEPING THE SAME ORDER - SAME FOR THE INSERT SQL
						wr.append("Id" + DELIMITER + "Title" + DELIMITER + "Description" + DELIMITER + "Type"
								+ DELIMITER + "Address" + DELIMITER + "Postal code" + DELIMITER + "City" + DELIMITER
								+ "Website" + DELIMITER + "Latitude" + DELIMITER + "Longitude" + DELIMITER + "Phone"
								+ DELIMITER + "Date" + DELIMITER + "Schedule" + DELIMITER + "Email" + DELIMITER
								+ "CsvName" + DELIMITER + "Population" + DELIMITER + "Elevation" + DELIMITER
								+ "Last editor" + DELIMITER + "Data publisher" + DELIMITER + "Published date: "
								+ DELIMITER + "Last update" + DELIMITER + "Icon Marker" + DELIMITER + "Extra info");
						wr.append(NEW_LINE);

						for (LocationModel loc : list) {
							wr.append(loc.getId().toString());
							wr.append(DELIMITER);
							wr.append(loc.getName().toString());
							wr.append(DELIMITER);
							wr.append(loc.getDescription());
							wr.append(DELIMITER);
							wr.append(loc.getType());
							wr.append(DELIMITER);
							wr.append(loc.getAddress());
							wr.append(DELIMITER);
							wr.append(loc.getPostcode());
							wr.append(DELIMITER);
							wr.append(loc.getCity());
							wr.append(DELIMITER);
							wr.append(loc.getWebsite());
							wr.append(DELIMITER);
							wr.append(String.valueOf(loc.getLatitude()));
							wr.append(DELIMITER);
							wr.append(String.valueOf(loc.getLongitude()));
							wr.append(DELIMITER);
							wr.append(loc.getPhone());
							wr.append(DELIMITER);
							wr.append(loc.getDate());
							wr.append(DELIMITER);
							wr.append(loc.getSchedule());
							wr.append(DELIMITER);
							wr.append(loc.getEmail());
							wr.append(DELIMITER);
							wr.append(loc.getCsvName());
							wr.append(DELIMITER);
							wr.append(loc.getPopulation());
							wr.append(DELIMITER);
							wr.append(loc.getElevation());
							wr.append(DELIMITER);
							wr.append(loc.getUsername());
							wr.append(DELIMITER);
							wr.append(loc.getSource());
							wr.append(DELIMITER);
							wr.append(loc.getDate_published());
							wr.append(DELIMITER);
							wr.append(loc.getDate_updated());
							wr.append(DELIMITER);
							wr.append(loc.getIconmarker());
							wr.append(DELIMITER);
							wr.append(loc.getOtherInfo());
							wr.append(NEW_LINE);
						}
					} else {
						wr.append("Can only work after making a search over the map.");
						wr.append(NEW_LINE);
						wr.append("Some markers must be displayed.");
					}
					wr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				target.prependJavaScript("$('#linkSaveVisibleLoc').trigger('click');");
			}

		};
		saveVisibleLocFile.setOutputMarkupPlaceholderTag(true);
		saveVisibleLocFile.setVisible(false);
		formSaveLoc.add(saveVisibleLocFile);

		now = new Date();
		sdf = new SimpleDateFormat("dd-MM-yyyy'_'HH:mm");

		File sqlLocBackup = new File(Files.createTempDir(), "Locations_Backup_" + sdf.format(now) + ".sql");
		formSaveLoc.add(new DownloadLink("sqlLocBackup", sqlLocBackup));

		final AjaxButton saveSqlLocBackup = new AjaxButton("saveSqlLocBackup") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {

				String DELIMITER = ";";
				String DELIMITER2 = ",";
				String NEW_LINE = "\n";
				boolean to_backup = false;

				try {
					PrintWriter wr = new PrintWriter(saveVisibleLoc, "utf-8");
					if (!list.isEmpty()) {
						for (LocationModel loc : list) {
							wr.append(loc.getId().toString());
							wr.append(DELIMITER);
							wr.append(loc.getName().toString());
							wr.append(DELIMITER);
							wr.append(loc.getDescription());
							wr.append(DELIMITER);
							wr.append(loc.getType());
							wr.append(DELIMITER);
							wr.append(loc.getAddress());
							wr.append(DELIMITER);
							wr.append(loc.getPostcode());
							wr.append(DELIMITER);
							wr.append(loc.getCity());
							wr.append(DELIMITER);
							wr.append(loc.getWebsite());
							wr.append(DELIMITER);
							wr.append(String.valueOf(loc.getLatitude()));
							wr.append(DELIMITER);
							wr.append(String.valueOf(loc.getLongitude()));
							wr.append(DELIMITER);
							wr.append(loc.getPhone());
							wr.append(DELIMITER);
							wr.append(loc.getDate());
							wr.append(DELIMITER);
							wr.append(loc.getSchedule());
							wr.append(DELIMITER);
							wr.append(loc.getEmail());
							wr.append(DELIMITER);
							wr.append(loc.getCsvName());
							wr.append(DELIMITER);
							wr.append(loc.getPopulation());
							wr.append(DELIMITER);
							wr.append(loc.getElevation());
							wr.append(DELIMITER);
							wr.append(loc.getUsername());
							wr.append(DELIMITER);
							wr.append(loc.getSource());
							wr.append(DELIMITER);
							wr.append(loc.getDate_published());
							wr.append(DELIMITER);
							wr.append(loc.getDate_updated());
							wr.append(DELIMITER);
							wr.append(loc.getIconmarker());
							wr.append(DELIMITER);
							wr.append(loc.getOtherInfo());
							wr.append(NEW_LINE);
							to_backup = true;
						}
					} else {
						to_backup = false;
					}
					wr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				BufferedWriter buffer = null;
				BufferedReader br = null;
				try {
					buffer = new BufferedWriter(new FileWriter(sqlLocBackup));
					br = new BufferedReader(new InputStreamReader(new FileInputStream(saveVisibleLoc), "utf-8"));

					if (to_backup == true) {

						String line;
						while ((line = br.readLine()) != null) {

							String[] value = line.split(DELIMITER);
							int j = 0;
							// we create the insert just if we have latitude and longitude
							if (!value[7].contentEquals("null") && !value[8].contentEquals("null")) {
								buffer.append(
										"INSERT INTO locations(id,name,description,type,address,postcode,city,website,latitude,longitude,phone,date,schedule,email,csvName,population,elevation,username,source,date_published,date_updated,iconmarker,otherinfo,rating,nrating) VALUES(");
								j = 0;
								while (j < value.length) {
									value[j] = value[j].replaceAll("\'", "").replaceAll("\"", "").replaceAll("null",
											"");
									// we add just strings with single quotes except for lat and lng
									if (!value[j].contentEquals("") && j != 8 && j != 9) {
										buffer.append("'");
										buffer.append(value[j]);
										buffer.append("'");

										// we add lat and long without quotes
									} else if (j == 8 || j == 9) {
										buffer.append(value[j]);
									} else {
										buffer.append("''");
									}

									// we insert separator ","
									if (j != value.length - 1) {
										buffer.append(DELIMITER2);
									}
									j++;
								}
								buffer.append(",0,0);");
								buffer.append(NEW_LINE);
							}
						}

					} else {
						buffer.append("Can only work after making a search over the map.");
						buffer.append(NEW_LINE);
						buffer.append("Some markers need to be displayed for a successful backup.");

					}

					br.close();
					buffer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				target.prependJavaScript("$('#linkSqlLocBackup').trigger('click');");
			}
		};
		saveSqlLocBackup.setOutputMarkupPlaceholderTag(true);
		saveSqlLocBackup.setVisible(false);
		formSaveLoc.add(saveSqlLocBackup);

		if (!list.isEmpty()) {
			saveVisibleLocFile.setVisible(true);
			saveSqlLocBackup.setVisible(true);
		}

		/*
		 * Current Navigation page LoadableDetachableModel<Object> model = new
		 * LoadableDetachableModel<Object>() {
		 * 
		 * private static final long serialVersionUID = 1L;
		 * 
		 * @Override public Object load() { return
		 * pagination.getPageable().getCurrentPage() + 1; } };
		 * currentPage.setDefaultModel(model);
		 */

		names.clear();
		namesSelect.clear();

		if (username != null) {

			for (int i = 0; i < origList.size(); i++) {
				if (!names.contains(origList.get(i).getCsvName()) && origList.get(i).getUsername() != null
						&& origList.get(i).getUsername().contentEquals(username)) {
					names.add(origList.get(i).getCsvName());
				}
			}
		}

		initializeLocations(datacontainer);

		final Form<String> formSearch = new Form<String>("formSearch");
		add(formSearch);

		final Form<String> formSearchExtraInfo = new Form<String>("formSearchExtraInfo");
		add(formSearchExtraInfo);

		final AutoCompleteTextField<String> inputField = new AutoCompleteTextField<String>("textSearch",
				new Model<String>()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected Iterator<String> getChoices(String input) {

				List<String> choices = new ArrayList<String>();
				int count = 0;
				for (final LocationModel loc : origList) {

					String name = loc.getOtherInfo();
					if (name != null) {
						if (name.toLowerCase().replaceAll("\\s", "").replaceAll("##", "").matches(
								"(.*)" + input.toLowerCase().replaceAll("\\s", "").replaceAll("##", "") + "(.*)")) {
							choices.add(name.toLowerCase().replaceAll("##", "•").replace(input, input.toUpperCase()));
							if (++count == 5)
								break;
						}
					}
				}
				return choices.iterator();
			}
		};
		formSearch.add(inputField);

		AjaxButton resetInputSubmit = new AjaxButton("resetInputSubmit") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target) {
				super.onSubmit(target);
				inputField.setModelObject(null);
				target.add(inputField);
				list.clear();
				saveVisibleLocFile.setVisible(false);
				saveSqlLocBackup.setVisible(false);
				target.add(wmc);
				target.appendJavaScript("map.gmap('clear', 'markers');");
				PageParameters pageParameters = new PageParameters();
				setResponsePage(LocationServicePage.class, pageParameters);
			}
		};
		resetInputSubmit.setDefaultFormProcessing(false);
		formSearch.add(resetInputSubmit);

		formSearch.add(new AjaxButton("searchLocation") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target) {
				super.onSubmit(target);
				String name = inputField.getModelObject();
				String viewPanelsInputValue = viewPanels.getModelObject();
				String mapTypeInputValue = mapType.getModelObject();
				String zoomLevelInputValue = mapZoomLevel.getModelObject();

				if (name != null) {
					list.clear();
					list2.clear();
					boolean found = false, found2 = false;
					int total = 0;
					for (LocationModel loc : origList) {

						// single search restricted to 5 possible choices coming out from: inputField
						if (loc.getOtherInfo() != null && loc.getOtherInfo().replaceAll("##", "•").toLowerCase()
								.contentEquals(name.toLowerCase())) {
							list.add(loc);
							found = true;
							break;
						}
						total++;

						// there was not attribute found, we search globally
						if (!found && total == origList.size()) {

							// get multiple search of locations by querying database
							list2 = locationServiceDAO.searchLocationModel(name);
							if (!list2.isEmpty()) {
								list.addAll(list2);
								found2 = true;
							}
						}
					}
					PageParameters pageParameters = new PageParameters();
					// used for a single search depending on the chosen location
					if (found) {
						pageParameters.set("name", list.get(0).getName());
						pageParameters.set("id", list.get(0).getId());
						pageParameters.set("lat", list.get(0).getLatitude());
						pageParameters.set("lng", list.get(0).getLongitude());
						pageParameters.set("zoom", zoomLevelInputValue);
						pageParameters.set("fullscreen", viewPanelsInputValue);
						pageParameters.set("maptype", mapTypeInputValue);
					}
					// user for multiple results by querying with a like %otherinfo% statement
					if (found2) {
						String searched_value = inputField.getModelObject();
						pageParameters.set("value", searched_value);
						log.info("ZOOM TO ADD: " + zoomLevelInputValue);
						pageParameters.set("zoom", zoomLevelInputValue);
						pageParameters.set("fullscreen", viewPanelsInputValue);
						pageParameters.set("maptype", mapTypeInputValue);
					}
					setResponsePage(LocationServicePage.class, pageParameters);
				}
				target.add(wmc);
			}

		});

		final TextField<String> polygonCoordInput = new TextField<String>("polygonCoordInput", Model.of(""));
		polygonCoordInput.setOutputMarkupId(true);

//		AjaxButton deletePolygonCoordinates = new AjaxButton("deletePolygonCoordinates") {
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public void onSubmit(AjaxRequestTarget target) {
//				super.onSubmit(target);
//				String polygonCoordInputValue = polygonCoordInput.getModelObject();
//
//				if (polygonCoordInputValue == null) {
//					list.clear();
//
//				} else if (polygonCoordInputValue != null && polygonCoordInputValue.contains(",")) {
//					polygonCoordInputValue = polygonCoordInputValue.replaceAll("\\s+", "");
//
//					Path2D myPolygon = new Path2D.Double();
//					String[] att = polygonCoordInputValue.substring(1, polygonCoordInputValue.length() - 1)
//							.split("\\),\\(");
//
//					String[] coordXY = null;
//					for (int i = 0; i < att.length; i++) {
//						coordXY = att[i].split(",");
//						if (i == 0) {
//							myPolygon.moveTo(Double.valueOf(coordXY[0]), Double.valueOf(coordXY[1]));
//						} else {
//							myPolygon.lineTo(Double.valueOf(coordXY[0]), Double.valueOf(coordXY[1]));
//						}
//					}
//					myPolygon.closePath();
//
//					for (LocationModel loc : origList) {
//
//						if (!polygonCoordInputValue.isEmpty()) {
//							if (myPolygon.contains(loc.getLatitude().doubleValue(), loc.getLongitude().doubleValue())) {
//								list.remove(loc);
//							}
//
//						}
//					}
//
//				}
//				if (list.isEmpty()) {
//					saveVisibleLocFile.setVisible(false);
//					saveSqlLocBackup.setVisible(false);
//					target.add(saveVisibleLocFile);
//					target.add(saveSqlLocBackup);
//				}
//				target.add(wmc);
//				target.appendJavaScript("initMarkers(false);");
//
//			}
//		};

		AjaxButton savePolygonCoordinates = new AjaxButton("savePolygonCoordinates") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target) {
				super.onSubmit(target);

				String[] att = null;
				String viewPanelsInputValue = viewPanels.getModelObject();
				String mapTypeInputValue = mapType.getModelObject();
				String zoomLevelInputValue = mapZoomLevel.getModelObject();
				String geoCoordDistanceValue = geoCoordDistance.getModelObject();

				log.info("DISTANCE READ IS " + geoCoordDistanceValue);

				String polygonCoordInputValue = polygonCoordInput.getModelObject();
				double distanceKm = 0;
				if (geoCoordDistanceValue != null) {
					distanceKm = Double.valueOf(geoCoordDistanceValue);
				} else {
					distanceKm = 1;
					target.appendJavaScript("$('#geoCoordDistance').val(1);");
				}

				if (polygonCoordInputValue == null) {
					list.clear();

				} else if (polygonCoordInputValue != null && polygonCoordInputValue.contains(",")) {
					polygonCoordInputValue = polygonCoordInputValue.replaceAll("\\s+", "");

					Path2D myPolygon = new Path2D.Double();
					att = polygonCoordInputValue.substring(1, polygonCoordInputValue.length() - 1).split("\\),\\(");

					String[] coordXY = null;
					for (int i = 0; i < att.length; i++) {
						coordXY = att[i].split(",");
						if (i == 0) {
							myPolygon.moveTo(Double.valueOf(coordXY[0]), Double.valueOf(coordXY[1]));
						} else {
							myPolygon.lineTo(Double.valueOf(coordXY[0]), Double.valueOf(coordXY[1]));
						}
					}
					myPolygon.closePath();

					if (att.length == 1) {
						modelLat.setObject(coordXY[0]);
						modelLng.setObject(coordXY[1]);
					}

					list.clear();

					for (LocationModel loc : origList) {

						if (!polygonCoordInputValue.isEmpty()) {
							if (att.length == 1) {

								double dist = distance(Double.valueOf(coordXY[0]), Double.valueOf(coordXY[1]),
										loc.getLatitude().doubleValue(), loc.getLongitude().doubleValue(), 'K');

								if (dist < distanceKm) {

									list.add(loc);
								}
							} else {
								if (myPolygon.contains(loc.getLatitude().doubleValue(),
										loc.getLongitude().doubleValue())) {
									list.add(loc);
								}
							}

						}
					}

				}
				if (!list.isEmpty()) {
					saveVisibleLocFile.setVisible(true);
					saveSqlLocBackup.setVisible(true);
					target.add(saveVisibleLocFile);
					target.add(saveSqlLocBackup);
				}
				target.add(wmc);

				PageParameters pageParameters = new PageParameters();

//				if (att.length == 1) {
//					setResponsePage(LocationServicePage.class, pageParameters);
////					target.appendJavaScript(
////							"initIds();initMarkers(false);showLocationTableIfNavigatorChange();initMyPosition();");
//				} else {
//
//					setResponsePage(LocationServicePage.class, pageParameters);
////					target.appendJavaScript("initIds();initMarkers(false);showLocationTableIfNavigatorChange();");
//				}
				if (polygonCoordInputValue == null) {
					pageParameters.set("zoom", zoomLevelInputValue);
					pageParameters.set("fullscreen", viewPanelsInputValue);
					pageParameters.set("maptype", mapTypeInputValue);
					pageParameters.set("dist", geoCoordDistanceValue);
					pageParameters.set("coords", parameters.get("coords"));
					// log.info("coords2 from input fied are:" + polygonCoordInputValue);
				} else {
					pageParameters.set("zoom", zoomLevelInputValue);
					pageParameters.set("fullscreen", viewPanelsInputValue);
					pageParameters.set("maptype", mapTypeInputValue);
					pageParameters.set("dist", geoCoordDistanceValue);
					pageParameters.set("coords", polygonCoordInputValue);
//					int zoom = target.appendJavaScript("map.gmap(\"get\",\"map\").getZoom()");
					// log.info("coords1 from input fied are:" + polygonCoordInputValue);
				}
				// setResponsePage(getPage().getClass(), getPage().getPageParameters());
				setResponsePage(LocationServicePage.class, pageParameters);

			}

		};

		formSearch.add(viewPanels);
		formSearch.add(mapType);
		formSearch.add(mapZoomLevel);
		formSearch.add(geoCoordDistance);
		formSearch.add(polygonCoordInput);
		formSearch.add(savePolygonCoordinates);
		// formSearch.add(deletePolygonCoordinates);
		add(formSearch);
	}

	final AjaxPagingNavigator tableNavigator = new AjaxPagingNavigator("tableNavigator", lview) {
		private static final long serialVersionUID = 1L;

		@Override
		protected void onAjaxEvent(AjaxRequestTarget target) {
			super.onAjaxEvent(target);
			// target.add(currentPage);

			target.appendJavaScript("initMarkers(true);showLocationTableIfNavigatorChange();");
		}
	};

	public PageableListView<LocationModel> displayList(String id, List<LocationModel> list, int num) {

		String zoom = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("zoom").toString();
		String fullscreen = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("fullscreen")
				.toString();
		String maptype = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("maptype").toString();

		PageableListView<LocationModel> listView = new PageableListView<LocationModel>(id, list, num) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onBeforeRender() {
				if (series.getData() != null) {
					series.getData().clear();
				}
				allAttributes.clear();
				allSums.clear();

				super.onBeforeRender();
			}

			@Override
			public void populateItem(final ListItem<LocationModel> item) {
				final LocationModel obj = item.getModelObject();

				item.add(new Label("id_location", obj.getId()));
				item.add(new Label("icon_marker", obj.getIconmarker()));
				item.add(new Label("latitude", obj.getLatitude().toString()));
				item.add(new Label("longitude", obj.getLongitude().toString()));
				item.add(new Label("name", obj.getName()));
				item.add(new Label("list_size", list.size()));

//				if (obj.getOtherInfo() != null) {
				addlinks = new ArrayList<>(Arrays.asList(obj.getOtherInfo().split("##")));
				String newinfo = "";

				for (int i = 0; i < addlinks.size(); i++) {

					newinfo += "<a href='search?&value=" + addlinks.get(i).trim().replaceAll(" ", "+") + "&zoom=" + zoom
							+ "&fullscreen=" + fullscreen + "'>" + addlinks.get(i) + "</a><br />";

//						if (allAttributes.contains(addlinks[i])) {
//							int indexOfWord = allAttributes.indexOf(addlinks[i]);
//							allSums.set(indexOfWord, allSums.get(indexOfWord) + 1);
//
//							// log.info("Existing word! for " + addlinks[i] + " we have a sum of: "
////									+ allSums.get(indexOfWord));
//						} else {
//							allAttributes.add(addlinks[i]);
//							allSums.add(0);
//
//							// log.info("Adding first time " + addlinks[i] + " with position: " + i);
//						}

					PieChartModel pc = new PieChartModel();
					boolean found = false;
					int pos = 0;

					if (!addlinks.get(i).contains("Published by") && !addlinks.get(i).contains("Last update")) {
						for (PieChartModel pie : pieChartList) {
							if (pie.getAttribute() != null && pie.getAttribute().equals(addlinks.get(i))) {
								found = true;
								pos = pieChartList.indexOf(pie);
//								log.info("we found existing val for pos "
//										+ pie.getAttribute().indexOf(pie.getAttribute()) + " and val "
//										+ pie.getAttribute());
							}
						}
						if (found) {
							// int indexOfWord = pieChartList.indexOf(addlinks[i]);
							pieChartList.get(pos).setRepetitions(pieChartList.get(pos).getRepetitions() + 1);

//							log.info("Existing word! for " + pieChartList.get(pos).getAttribute()
//									+ " we have a sum of: " + pieChartList.get(pos).getRepetitions());
						} else if (addlinks.get(i) != null) {
							pc.setAttribute(addlinks.get(i));
							pc.setRepetitions(1);

//							log.info("Adding first time " + pc.getAttribute() + " with position: " + i);
							pieChartList.add(pc);
						}
					}

				}
				item.add(new Label("other", newinfo).setEscapeModelStrings(false));
//				} else {
//					item.add(new Label("otherTag").setVisible(false));
//					item.add(new Label("other").setVisible(false));
//				}

				// list size of the pie chart must be greater than the values of a one location
				// (otherwise we hide the chart)
				// log.info("WE HAVE " + (list.size() - 1) + ", " + count + ", " + (num - 1));
				if ((count == list.size() - 1 || count == num - 1) && !pieChartList.isEmpty()
						&& pieChartList.size() > addlinks.size()) {

					log.info("count SIZE2 " + count + " and MAX MARKERS IS " + num);
					if (count == num - 1) {
						tableNavigator.setVisible(true);
					}

					log.info("size is " + pieChartList.size());
					log.info("size2 is " + addlinks.size());

					series = new PointSeries();
					pie_chart.setVisible(true);

					for (int i = 0; i < pieChartList.size(); i++)
//						log.info("val is " + pieChartList.get(i).getAttribute() + ", "
//								+ pieChartList.get(i).getRepetitions());

						Collections.sort(pieChartList, Collections.reverseOrder());

//					pieChartList.sort(Comparator.comparing(PieChartModel::getRepetitions,
//							Comparator.nullsLast(Comparator.naturalOrder())));

					for (int i = 0; i < pieChartList.size(); i++) {

						if (pieChartList.get(i).getRepetitions() != null && pieChartList.get(i).getRepetitions() > 1) {

							series.addPoint(
									new Point(pieChartList.get(i).getAttribute(), pieChartList.get(i).getRepetitions())
											.setEvents(
													new Events().setClick(new RedirectFunction("search?&value="
															+ pieChartList.get(i).getAttribute().trim().replaceAll(" ",
																	"")
															+ "&zoom=" + zoom + "&fullscreen=" + fullscreen
															+ "&maptype=" + maptype))));
						}
					}
//					log.info("WE HAVE " + pieChartList.get(0).getAttribute() + " WITH REP IS "
//							+ pieChartList.get(0).getRepetitions());
//					log.info("WE HAVE2 " + pieChartList.get(1).getAttribute() + " WITH REP IS "
//							+ pieChartList.get(1).getRepetitions());
					series.setShowInLegend(false);
					series.setDataLabels(new DataLabels().setEnabled(true).setBackgroundColor(new HexColor("#364654"))
							.setColor(new HexColor("#FFFFFF"))
//							.setFormatter(new Function().setFunction("if('" + pieChartList.get(0).getRepetitions()
//									+ "' == this.y || '" + pieChartList.get(1).getRepetitions()
//									+ "' == this.y){ return '' + this.point.name +': '+ this.y + ' rep';}"))
							.setStyle(new CssStyle().setProperty("font-weight", "normal")
									.setProperty("font-size", "11px").setProperty("font-family", "sans-serif, verdana")
									.setProperty("width", "60px"))
							.setAlign(HorizontalAlignment.RIGHT));
					chart_options.setTooltip(new Tooltip().setFormatter(
							new Function().setFunction(" return ''+ this.point.name +': '+ this.y +' rep';")));
					chart_options.addSeries(series);
				} else {
					pie_chart.setVisible(false);
				}
				count++;
			}
		};

		return listView;
	}

	public void initializeLocations(final WebMarkupContainer datacontainer) {

		final TextField<String> idInput = new TextField<String>("idInput", Model.of(""));
		final Form<?> editFormTriggerClick = new Form<Void>("editFormTriggerClick") {

			private static final long serialVersionUID = 1L;

		};
		editFormTriggerClick.add(idInput);
		datacontainer.add(editFormTriggerClick);

		final Form<?> editForm = new Form<Void>("editForm");
		editForm.setVisible(false);
		editForm.setOutputMarkupId(true);
		datacontainer.add(editForm);

		locationsForm = new Form<Void>("locationsForm");
		locationsForm.setOutputMarkupId(true);
		add(locationsForm);

		final TextField<String> testinput = new TextField<String>("testinput", Model.of(""));
		testinput.setOutputMarkupId(true);
		locationsForm.add(testinput);

		WebSession session = WebSession.get();

		String username = null;
		if (WebSession.get().getAttribute("user_name") != null) {
			username = session.getAttribute("user_name").toString();
		}
		if (username != null) {
			if (username.contentEquals("admin")) {
				locationsForm.setVisible(true);
			}
		} else {
			locationsForm.setVisible(false);
		}

		final CheckBoxMultipleChoice<String> listRemoveLocations = new CheckBoxMultipleChoice<String>("removeLocations",
				new Model<ArrayList<String>>(namesRemoveSelect), names);
		listRemoveLocations.setSuffix("<br />");

		locationsForm.add(listRemoveLocations);

		final Button removeSelected = new Button("removeSelected") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				super.onSubmit();
				for (int i = 0; i < namesRemoveSelect.size(); i++) {
					locationServiceDAO.removeLocationByName(namesRemoveSelect.get(i));
				}
				setResponsePage(getPage().getClass(), getPage().getPageParameters());
			}
		};
		locationsForm.add(removeSelected);

		Label importedFilesLabel = new Label("importedFilesLabel", "Imported datasets:");
		Label removeSelectedLabel = new Label("removeSelectedLabel", "Files to remove:");
		if (names.isEmpty()) {
			importedFilesLabel.setVisible(false);
			removeSelectedLabel.setVisible(false);
			removeSelected.setVisible(false);
		} else {
			importedFilesLabel.setVisible(true);
			removeSelectedLabel.setVisible(true);
			removeSelected.setVisible(true);
		}
		locationsForm.add(importedFilesLabel);
		locationsForm.add(removeSelectedLabel);

		CheckBoxMultipleChoice<String> listLocations = new CheckBoxMultipleChoice<String>("listLocations",
				new Model<ArrayList<String>>(namesSelect), names);
		listLocations.setSuffix("<br />");
		AjaxFormChoiceComponentUpdatingBehavior check = new AjaxFormChoiceComponentUpdatingBehavior() {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
//				list2.clear();
//				for (int i = 0; i < origList.size(); i++) {
//					for (int j = 0; j < namesSelect.size(); j++) {
//						if (origList.get(i).getCsvName().contentEquals(namesSelect.get(j).toString())) {
//							list2.add(origList.get(i));
//						}
//					}
//				}
//				list.clear();
//				list.addAll(list2);

				// setResponsePage(getPage());
//				String viewPanelsInputValue = viewPanels.getModelObject();
//				String mapTypeInputValue = mapType.getModelObject();
//				String zoomLevelInputValue = mapZoomLevel.getModelObject();

//				String zoom = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("zoom")
//						.toString();
//				String fullscreen = RequestCycle.get().getRequest().getRequestParameters()
//						.getParameterValue("fullscreen").toString();
//				String maptype = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("maptype")
//						.toString();
				String testinput2 = testinput.getModelObject();
				log.info("TEST THE INPUT FIELD:" + testinput2);
//
//				PageParameters pageParameters = new PageParameters();
				pageParameters.set("value", namesSelect.get(0));
				pageParameters.set("zoom", 12);
				// pageParameters.set("fullscreen", false);
				// pageParameters.set("maptype", maptype);
				// setResponsePage(getPage().getClass(), getPage().getPageParameters());
				setResponsePage(LocationServicePage.class, pageParameters);
			}

		};
		listLocations.add(check);
		locationsForm.add(listLocations);

		ListView<LocationModel> editRowList = new ListView<LocationModel>("editRow", temp_list) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<LocationModel> item) {
				item.add(new TextField<String>("name", new PropertyModel<String>(item.getModelObject(), "name")));
				item.add(new TextField<String>("csvName", new PropertyModel<String>(item.getModelObject(), "csvName")));
				item.add(new TextField<String>("type", new PropertyModel<String>(item.getModelObject(), "type")));
				item.add(new TextField<String>("address", new PropertyModel<String>(item.getModelObject(), "address")));
				item.add(new TextField<String>("street", new PropertyModel<String>(item.getModelObject(), "street")));
				item.add(new TextField<String>("number", new PropertyModel<String>(item.getModelObject(), "number")));

				item.add(new TextField<String>("latitude",
						new PropertyModel<String>(item.getModelObject(), "latitude")));
				item.add(new TextField<String>("longitude",
						new PropertyModel<String>(item.getModelObject(), "longitude")));
				item.add(new TextField<String>("website", new PropertyModel<String>(item.getModelObject(), "website")));
				item.add(new TextField<String>("image", new PropertyModel<String>(item.getModelObject(), "urlImage")));
				item.add(new TextField<String>("description",
						new PropertyModel<String>(item.getModelObject(), "description")));
				item.add(new TextField<String>("email", new PropertyModel<String>(item.getModelObject(), "email")));
				item.add(new TextField<String>("phone", new PropertyModel<String>(item.getModelObject(), "phone")));
				item.add(new TextField<String>("date", new PropertyModel<String>(item.getModelObject(), "date")));
				item.add(new TextField<String>("schedule",
						new PropertyModel<String>(item.getModelObject(), "schedule")));
				item.add(new TextArea<String>("otherInfo",
						new PropertyModel<String>(item.getModelObject(), "otherInfo")));
			}
		};
		editForm.add(editRowList);

		final Button saveLocation = new Button("saveLocation") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				super.onSubmit();

				locationServiceDAO.updateLocationModel(temp_list.get(0));
				editForm.setVisible(false);
			}
		};
		saveLocation.setVisible(false);
		editForm.add(saveLocation);

		final AjaxButton closeEdit = new AjaxButton("closeEdit") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				super.onSubmit(target);

				target.appendJavaScript("$('#editInfo').hide();");
				editForm.setVisible(false);
			}
		};
		closeEdit.setVisible(false);
		editForm.add(closeEdit);

		AjaxButton editInfoButton = new AjaxButton("editInfoButton") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				super.onSubmit(target);
				editForm.setVisible(true);

				final String idInputValue = idInput.getModelObject();
				log.info("YOU JUST CLICKED OVER MARKER EDIT: " + idInputValue);

//				int i = 0;
//				boolean find = false;
//				listEdit.clear();
//
//				while (find == false) {
//					if (origList.get(i).getId().toString().contentEquals(idInputValue)) {
//						listEdit.add(origList.get(i));
//						find = true;
//					}
//					i++;
//				}
//				target.add(datacontainer);
//				saveLocation.setVisible(true);
//				setResponsePage(getPage());
//				closeEdit.setVisible(true);
			}
		};
		editFormTriggerClick.add(editInfoButton);

		final Button displayLocations = new Button("displayLocations") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {

				names.clear();
				namesSelect.clear();

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
				locationsForm.setVisible(true);

				editForm.setVisible(true);

				setResponsePage(LocationServicePage.class);
			}
		};
		displayLocations.setOutputMarkupId(true);
		displayLocations.setMarkupId("initDisplayLocations");
	}

	@Override
	protected String getMessage() {
		return null;
	}
}
