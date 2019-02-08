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
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
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
import org.apache.wicket.model.LoadableDetachableModel;
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
import com.opendataview.web.pages.index.BasePage;
import com.opendataview.web.panels.DirectionPanel;
import com.opendataview.web.persistence.LocationServiceDAO;
import com.opendataview.web.persistence.PropertiesServiceDAO;
import com.opendataview.web.persistence.RouteServiceDAO;
import com.opendataview.web.persistence.UserServiceDAO;

import de.adesso.wickedcharts.highcharts.options.ChartOptions;
import de.adesso.wickedcharts.highcharts.options.CreditOptions;
import de.adesso.wickedcharts.highcharts.options.Events;
import de.adesso.wickedcharts.highcharts.options.ExportingOptions;
import de.adesso.wickedcharts.highcharts.options.Legend;
import de.adesso.wickedcharts.highcharts.options.Options;
import de.adesso.wickedcharts.highcharts.options.SeriesType;
import de.adesso.wickedcharts.highcharts.options.Title;
import de.adesso.wickedcharts.highcharts.options.functions.RedirectFunction;
import de.adesso.wickedcharts.highcharts.options.series.Point;
import de.adesso.wickedcharts.highcharts.options.series.PointSeries;
import de.adesso.wickedcharts.highcharts.options.series.Series;
import de.adesso.wickedcharts.wicket8.highcharts.Chart;

public class LocationServicePage extends BasePage {

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

		String searchValue = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("value")
				.toString();
		String polygonCoordInputValue = RequestCycle.get().getRequest().getRequestParameters()
				.getParameterValue("coords").toString();
		String polygonDist = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("dist")
				.toString();
		String mapZoomLevel = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("zoom")
				.toString();
		String viewPanels = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("fullscreen")
				.toString();
		log.info("param coord is: " + polygonCoordInputValue);
		log.info("param distance dist is: " + polygonDist);

		if (searchValue != null) {
			log.info("we have a search with zoom " + mapZoomLevel);
			response.render(OnDomReadyHeaderItem.forScript(
					"$('#mapZoomLevel').val(" + mapZoomLevel + ");$('#viewPanels').val(" + viewPanels + ")"));
		}

		if (polygonCoordInputValue != null) {
			response.render(OnDomReadyHeaderItem.forScript("$('#geoCoordDistance').val(" + polygonDist
					+ ");$('#mapZoomLevel').val(" + mapZoomLevel + ");$('#viewPanels').val(" + viewPanels + ")"));
			String[] att = polygonCoordInputValue.substring(1, polygonCoordInputValue.length() - 1).split("\\),\\(");
			if (att.length == 1) {
				response.render(OnDomReadyHeaderItem
						.forScript("window.onload = function () {initMyPosition();$(\".slider_wrapper\").show()}"));
			} else {
				response.render(OnDomReadyHeaderItem
						.forScript("window.onload = function () {polygon = new google.maps.Polygon({paths:["
								+ polygonCoordInputValue.replaceAll("\\(", "{lat:").replaceAll("\\)", "}")
										.replaceAll("[0-9],", ",lng:")
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
	private List<LocationModel> listEdit = new ArrayList<LocationModel>();

	private static Options chart_options = new Options();
	private List<String> allAttributes = new ArrayList<String>();
	private List<Integer> allSums = new ArrayList<Integer>();
	private static Series<Point> series = new PointSeries();

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

	final TextField<String> mapZoomLevel = new TextField<String>("mapZoomLevel", Model.of("8"));

	public LocationServicePage(PageParameters parameters) throws IOException {

		setStatelessHint(false);
		setVersioned(false);

		WebSession session = WebSession.get();
		viewPanels.setOutputMarkupId(true);
		mapZoomLevel.setOutputMarkupId(true);
//		final TextField<String> mapIconMarker = new TextField<String>("mapIconMarker", Model.of("10"));
//		mapIconMarker.setOutputMarkupId(true);
		final TextField<String> geoCoordDistance = new TextField<String>("geoCoordDistance", Model.of("10"));
		geoCoordDistance.setOutputMarkupId(true);

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
//		chart_options.setLabels(new Labels().setEnabled(true));
		chart_options.setLegend(new Legend().setReversed(true).setWidth(300).setMaxHeight(200));
//		chart_options.getLegend().setEnabled(true);

		// log.info("Page is stateless because: "+setStatelessHint(boolean state));

		// chart_options.setSubtitle(new .setEnabled(false));
		// chart_options.setTitle(new Title("Fields Percentage").setEnabled(false));
		datacontainer.add(new Chart("chart", chart_options));

		final WebMarkupContainer wmc = new WebMarkupContainer("wmc");
		wmc.setVersioned(false);
		wmc.setOutputMarkupId(true);
		add(wmc);

		Label currentPosition = new Label("currentPosition", "Your position");
		currentPosition.setOutputMarkupId(true);
		wmc.add(currentPosition);

		final Model<String> modelLat = new Model<String>("0");
		Label currentLatitude = new Label("currentLatitude", modelLat);
		currentLatitude.setOutputMarkupId(true);
		wmc.add(currentLatitude);

		final Model<String> modelLng = new Model<String>("0");
		Label currentLongitude = new Label("currentLongitude", modelLng);
		currentLongitude.setOutputMarkupId(true);
		wmc.add(currentLongitude);

		origList = locationServiceDAO.readLocationModel();

		// restriction to display 300 locations per page (to not overload the browser)
		final PageableListView<LocationModel> lview = displayList("rows", list, 200, username);
		lview.setOutputMarkupId(true);
		wmc.add(lview);

		final Label currentPage = new Label("currentPage", new Model<String>());
		currentPage.setOutputMarkupId(true);
		wmc.add(currentPage);

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
				geoCoordDistanceValue = "10";
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

		final Form<?> formSaveLoc = new Form<Void>("saveLocationsForm") {

			private static final long serialVersionUID = 1L;

		};
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

		final AjaxPagingNavigator pagination = new AjaxPagingNavigator("navigator", lview) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onAjaxEvent(AjaxRequestTarget target) {
				super.onAjaxEvent(target);
				target.add(currentPage);
				target.appendJavaScript("initIds();initMarkers(false);showLocationTableIfNavigatorChange();");
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

		if (username != null) {

			for (int i = 0; i < origList.size(); i++) {
				if (!names.contains(origList.get(i).getCsvName()) && origList.get(i).getUsername() != null
						&& origList.get(i).getUsername().contentEquals(username)) {
					names.add(origList.get(i).getCsvName());
				}
			}
		}

		initializeLocations(datacontainer);
		wmc.add(new DirectionPanel("direction"));

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
							choices.add(name.replaceAll("##", "•").replace(input, "'''" + input + "'''"));
							if (++count == 5)
								break;
						}
					}
				}
				return choices.iterator();
			}
		};
		formSearch.add(inputField);

		IndicatingAjaxButton clearSearch = new IndicatingAjaxButton("clearSearch") {

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
				setResponsePage(getPage());

			}
		};
		clearSearch.setDefaultFormProcessing(false);
		formSearch.add(clearSearch);

		formSearch.add(new IndicatingAjaxButton("searchLocation") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target) {
				super.onSubmit(target);
				String name = inputField.getModelObject();
				String viewPanelsInputValue = viewPanels.getModelObject();
				String zoomLevelInputValue = mapZoomLevel.getModelObject();

				if (name != null) {
					list.clear();
					list2.clear();
					boolean found = false, found2 = false;
					int total = 0;
					for (LocationModel loc : origList) {

						// single search restricted to 5 possible choices coming out from: inputField
						if (loc.getOtherInfo() != null
								&& loc.getOtherInfo().replaceAll("##", "•").contentEquals(name.replaceAll("'''", ""))) {
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
					}
					// user for multiple results by querying with a like %otherinfo% statement
					if (found2) {
						String searched_value = inputField.getModelObject();
						pageParameters.set("value", searched_value);
						log.info("ZOOM TO ADD: " + zoomLevelInputValue);
						pageParameters.set("zoom", zoomLevelInputValue);
						pageParameters.set("fullscreen", viewPanelsInputValue);
					}
					setResponsePage(LocationServicePage.class, pageParameters);
				}
				target.add(wmc);
			}

		});

//		final TextField<String> geoCoordDistance = new TextField<String>("geoCoordDistance", Model.of("20"));
//		geoCoordDistance.setOutputMarkupId(true);

		final TextField<String> polygonCoordInput = new TextField<String>("polygonCoordInput", Model.of(""));
		polygonCoordInput.setOutputMarkupId(true);

		IndicatingAjaxButton btnDeletePolygon = new IndicatingAjaxButton("btnDeletePolygon") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target) {
				super.onSubmit(target);
				String polygonCoordInputValue = polygonCoordInput.getModelObject();

				if (polygonCoordInputValue == null) {
					list.clear();

				} else if (polygonCoordInputValue != null && polygonCoordInputValue.contains(",")) {
					polygonCoordInputValue = polygonCoordInputValue.replaceAll("\\s+", "");

					Path2D myPolygon = new Path2D.Double();
					String[] att = polygonCoordInputValue.substring(1, polygonCoordInputValue.length() - 1)
							.split("\\),\\(");

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

					for (LocationModel loc : origList) {

						if (!polygonCoordInputValue.isEmpty()) {
							if (myPolygon.contains(loc.getLatitude().doubleValue(), loc.getLongitude().doubleValue())) {
								list.remove(loc);
							}

						}
					}

				}
				if (list.isEmpty()) {
					saveVisibleLocFile.setVisible(false);
					saveSqlLocBackup.setVisible(false);
					target.add(saveVisibleLocFile);
					target.add(saveSqlLocBackup);
				}
				target.add(wmc);
				target.appendJavaScript("initIds();initMarkers(false);");

			}
		};

		IndicatingAjaxButton savePolygonCoordinates = new IndicatingAjaxButton("savePolygonCoordinates") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target) {
				super.onSubmit(target);

				String[] att = null;
				String viewPanelsInputValue = viewPanels.getModelObject();
				String zoomLevelInputValue = mapZoomLevel.getModelObject();
				String geoCoordDistanceValue = geoCoordDistance.getModelObject();
				String polygonCoordInputValue = polygonCoordInput.getModelObject();
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
					pageParameters.set("dist", geoCoordDistanceValue);
					pageParameters.set("coords", parameters.get("coords"));
					log.info("coords2 from input fied are:" + polygonCoordInputValue);
				} else {
					pageParameters.set("zoom", zoomLevelInputValue);
					pageParameters.set("fullscreen", viewPanelsInputValue);
					pageParameters.set("dist", geoCoordDistanceValue);
					pageParameters.set("coords", polygonCoordInputValue);
//					int zoom = target.appendJavaScript("map.gmap(\"get\",\"map\").getZoom()");
					log.info("coords1 from input fied are:" + polygonCoordInputValue);
				}
				// setResponsePage(getPage().getClass(), getPage().getPageParameters());
				setResponsePage(LocationServicePage.class, pageParameters);

			}

		};

//		final Form<?> coordGeoForm = new Form<Void>("coordGeoForm") {
//
//			private static final long serialVersionUID = 1L;
//
//		};
		// datacontainer.add(coordGeoForm);
		formSearch.add(viewPanels);
		formSearch.add(mapZoomLevel);
		// coordGeoForm.add(mapIconMarker);
		formSearch.add(geoCoordDistance);
		formSearch.add(polygonCoordInput);
		formSearch.add(savePolygonCoordinates);
		formSearch.add(btnDeletePolygon);
		add(formSearch);
	}

	int count = 0;

	public PageableListView<LocationModel> displayList(String id, List<LocationModel> list, int num, String username) {

		String zoom = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("zoom").toString();
		String fullscreen = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("fullscreen")
				.toString();
		PageableListView<LocationModel> listView = new PageableListView<LocationModel>(id, list, num) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onBeforeRender() {
				if (series.getData() != null) {
					series.getData().clear();
					log.info("series chart is null");
				} else {
					log.info("series chart is NOT null");
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

				if (obj.getName() != null && !obj.getName().isEmpty()) {
					item.add(new Label("name", obj.getName()));
				} else {
					item.add(new Label("name").setVisible(false));
				}
				if (username != null) {
					item.add(new Label("userSession", username));
				} else {
					item.add(new Label("userSession", "no active user"));
				}
				if (obj.getCsvName() != null && !obj.getCsvName().isEmpty()) {
					item.add(new Label("csvName", obj.getCsvName()));
				} else {
					item.add(new Label("csvName").setVisible(false));
				}
				if (obj.getType() != null && !obj.getType().isEmpty()) {
					item.add(new Label("typeTag", "Type: "));
					item.add(new Label("type", obj.getType()));
				} else {
					item.add(new Label("typeTag").setVisible(false));
					item.add(new Label("type").setVisible(false));
				}
				if (obj.getCity() != null && !obj.getCity().isEmpty()) {
					item.add(new Label("cityTag", "Address: "));
					item.add(new Label("city", obj.getAddress()));
				} else {
					item.add(new Label("cityTag").setVisible(false));
					item.add(new Label("city").setVisible(false));
				}
				if (obj.getPostcode() != null && !obj.getPostcode().isEmpty()) {
					item.add(new Label("postcode", obj.getPostcode()));
				} else {
					item.add(new Label("postcode").setVisible(false));
				}
//				if (obj.getAddress() != null && !obj.getAddress().isEmpty()) {
//					item.add(new Label("addressTag", "Address: "));
//					item.add(new Label("address", obj.getAddress()));
//				} else {
//					item.add(new Label("addressTag").setVisible(false));
//					item.add(new Label("address").setVisible(false));
//				}
//				if (obj.getStreet() != null && !obj.getStreet().isEmpty()) {
//					item.add(new Label("street", obj.getStreet()));
//				} else {
//					item.add(new Label("street").setVisible(false));
//				}
//				if (obj.getNumber() != null && !obj.getNumber().isEmpty()) {
//					item.add(new Label("number", obj.getNumber()));
//				} else {
//					item.add(new Label("number").setVisible(false));
//				}
//				if (obj.getDistrict() != null && !obj.getDistrict().isEmpty()) {
//					item.add(new Label("district", obj.getDistrict()));
//				} else {
//					item.add(new Label("district").setVisible(false));
//				}
				if (obj.getLatitude() != null) {
					item.add(new Label("coordsTag", "Coords: "));
					item.add(new Label("latitude", obj.getLatitude().toString()));
					item.add(new Label("longitude", obj.getLongitude().toString()));
				} else {
					item.add(new Label("latitude").setVisible(false));
					item.add(new Label("longitude").setVisible(false));
				}
				if (obj.getWebsite() != null && !obj.getWebsite().isEmpty()) {
					item.add(new Label("websiteTag", "Website: "));
					item.add(new ExternalLink("website", obj.getWebsite(), obj.getWebsite()));
				} else {
					item.add(new Label("websiteTag").setVisible(false));
					item.add(new Label("website").setVisible(false));
				}
				if (obj.getUrlImage() != null && !obj.getUrlImage().isEmpty()) {
					item.add(new ContextImage("image", obj.getUrlImage().toString()));
				} else {
					item.add(new Image("image", obj.getUrlImage()).setVisible(false));
				}
				if (obj.getDescription() != null && !obj.getDescription().isEmpty()) {
					item.add(new Label("descriptionTag", "Description: "));
					item.add(new Label("description", obj.getDescription()));
				} else {
					item.add(new Label("descriptionTag").setVisible(false));
					item.add(new Label("description").setVisible(false));
				}
				if (obj.getEmail() != null && !obj.getEmail().isEmpty()) {
					item.add(new Label("emailTag", "Email: "));
					item.add(new ExternalLink("email", "mailto:" + obj.getEmail(), obj.getEmail()));
				} else {
					item.add(new Label("emailTag").setVisible(false));
					item.add(new Label("email").setVisible(false));
				}
				if (obj.getPhone() != null && !obj.getPhone().isEmpty()) {
					item.add(new Label("phoneTag", "Tlf.: "));
					item.add(new Label("phone", obj.getPhone()));
				} else {
					item.add(new Label("phoneTag").setVisible(false));
					item.add(new Label("phone").setVisible(false));
				}
				if (obj.getDate() != null && !obj.getDate().isEmpty()) {
					item.add(new Label("dateTag", "Date: "));
					item.add(new Label("date", obj.getDate()));
				} else {
					item.add(new Label("dateTag").setVisible(false));
					item.add(new Label("date", obj).setVisible(false));
				}
				if (obj.getSchedule() != null && !obj.getSchedule().isEmpty()) {
					item.add(new Label("scheduleTag", "Schedule: "));
					item.add(new Label("schedule", obj.getSchedule()));
				} else {
					item.add(new Label("scheduleTag").setVisible(false));
					item.add(new Label("schedule").setVisible(false));
				}
				if (obj.getOtherInfo() != null) {
					item.add(new Label("otherTag", "Linked data: "));
					String[] addlinks = obj.getOtherInfo().split("##");
					String newinfo = "";
					for (int i = 0; i < addlinks.length; i++) {

						newinfo += "<a href='search?&value=" + addlinks[i].trim().replaceAll(" ", "+") + "&zoom=" + zoom
								+ "&fullscreen=" + fullscreen + "'>" + addlinks[i] + "</a><br />";

						if (allAttributes.contains(addlinks[i])) {
							int indexOfWord = allAttributes.indexOf(addlinks[i]);
							allSums.set(indexOfWord, allSums.get(indexOfWord) + 1);
							// log.info("Existing word! for " + addlinks[i] + " we have a sum of: "
//									+ allSums.get(indexOfWord));
						} else {
							allAttributes.add(addlinks[i]);
							allSums.add(0);
							// log.info("Adding first time " + addlinks[i] + " with position: " + i);
						}

					}
					item.add(new Label("other", newinfo).setEscapeModelStrings(false));
				} else {
					item.add(new Label("otherTag").setVisible(false));
					item.add(new Label("other").setVisible(false));
				}
				if (obj.getDate_updated() != null) {
					item.add(new Label("dateUpdatedTag", "Last update: "));
					item.add(new Label("dateUpdated", obj.getDate_updated()));
				} else {
					item.add(new Label("dateUpdatedTag").setVisible(false));
					item.add(new Label("dateUpdated").setVisible(false));
				}
				if (obj.getDate_published() != null) {
					item.add(new Label("datePublishedTag", "First publish: "));
					item.add(new Label("datePublished", obj.getDate_published()));
				} else {
					item.add(new Label("datePublishedTag").setVisible(false));
					item.add(new Label("datePublished").setVisible(false));
				}
				if (obj.getUsername() != null) {
					item.add(new Label("userPublishedTag", "Last editor: "));
					item.add(new Label("userPublished", obj.getUsername()));

				} else {
					item.add(new Label("userPublishedTag").setVisible(false));
					item.add(new Label("userPublished").setVisible(false));
				}
				if (obj.getSource() != null && !obj.getSource().isEmpty()) {
					item.add(new Label("sourcePublishedTag", "Data publisher: "));
					item.add(new Label("sourcePublished", obj.getSource()));
				} else {
					item.add(new Label("sourcePublishedTag").setVisible(false));
					item.add(new Label("sourcePublished").setVisible(false));
				}
				if (count == list.size() - 1 || count == num - 1) {
					series = new PointSeries();
					for (int i = 0; i < allAttributes.size(); i++) {

						if (!allAttributes.isEmpty() && !allSums.isEmpty() && allSums.get(i) > 1
								&& !allAttributes.get(i).contains("Last update")
								&& !allAttributes.get(i).contains("Published by")) {

							String category = allAttributes.get(i) + " [" + allSums.get(i) + " reps]";

							series.addPoint(new Point(category, Math.round(allSums.get(i) * 100) / 100.0)
									.setEvents(new Events().setClick(new RedirectFunction(
											"search?&value=" + allAttributes.get(i).trim().replaceAll(" ", "")
													+ "&zoom=" + zoom + "&fullscreen=" + fullscreen))));

						}
					}
					series.setShowInLegend(true);
					chart_options.addSeries(series);
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

		final Form<?> editForm = new Form<Void>("editForm") {

			private static final long serialVersionUID = 1L;

		};
		editForm.setVisible(false);

		locationsForm = new Form<Void>("locationsForm");
		datacontainer.add(locationsForm);

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
				PageParameters pageParameters = new PageParameters();
				String viewPanelsInputValue = viewPanels.getModelObject();
				String zoomLevelInputValue = mapZoomLevel.getModelObject();
				pageParameters.set("value", namesSelect.get(0));
				pageParameters.set("zoom", zoomLevelInputValue);
				pageParameters.set("fullscreen", viewPanelsInputValue);
				// setResponsePage(getPage().getClass(), getPage().getPageParameters());
				setResponsePage(LocationServicePage.class, pageParameters);
			}

		};
		listLocations.add(check);
		locationsForm.add(listLocations);

		ListView<LocationModel> editRowList = new ListView<LocationModel>("editRow", listEdit) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<LocationModel> item) {
				item.add(new TextField<String>("name", new PropertyModel<String>(item.getModelObject(), "name")));
				item.add(new TextField<String>("csvName", new PropertyModel<String>(item.getModelObject(), "csvName")));
				item.add(new TextField<String>("type", new PropertyModel<String>(item.getModelObject(), "type")));
				item.add(new TextField<String>("address", new PropertyModel<String>(item.getModelObject(), "address")));
				item.add(new TextField<String>("street", new PropertyModel<String>(item.getModelObject(), "street")));
				item.add(new TextField<String>("number", new PropertyModel<String>(item.getModelObject(), "number")));
				item.add(new TextField<String>("district",
						new PropertyModel<String>(item.getModelObject(), "district")));
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

				locationServiceDAO.updateLocationModel(listEdit.get(0));
				editForm.setVisible(false);
			}
		};
		saveLocation.setVisible(false);
		editForm.add(saveLocation);

		final IndicatingAjaxButton closeEdit = new IndicatingAjaxButton("closeEdit") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				super.onSubmit(target);

				target.appendJavaScript("$('#editInfo').hide();");
				editForm.setVisible(false);
			}
		};
		closeEdit.setVisible(false);

		IndicatingAjaxButton editInfoButton = new IndicatingAjaxButton("editInfoButton") {

			private static final long serialVersionUID = 1L;

			@Override
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
