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

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
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
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.file.File;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.opendataview.web.model.LocationModel;
import com.opendataview.web.model.PieChartModel;
import com.opendataview.web.pages.index.BasePage;
import com.opendataview.web.persistence.LocationServiceDAO;
import com.opendataview.web.persistence.PropertiesServiceDAO;
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
import de.adesso.wickedcharts.highcharts.options.series.SimpleSeries;
import de.adesso.wickedcharts.wicket8.highcharts.Chart;

public class LocationServicePage extends BasePage {

	PageParameters pageParameters = new PageParameters();

	private static final long serialVersionUID = 1L;
	final Form<String> formSearch = new Form<String>("formSearch");

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);

		String coords = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("coords").toString();
		String dist = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("dist").toString();
		String zoom = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("zoom").toString();
		boolean fullscreen = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("fullscreen")
				.toBoolean();
		String mapType = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("map").toString();
		boolean showGraph = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("graph")
				.toBoolean();
		String total = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("total").toString();
		String group = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("group").toString();
		String showPrivateMap = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("user")
				.toString();

		if (showPrivateMap != null) {
			config_names_select.add(privatemaps_label);
			response.render(OnDomReadyHeaderItem.forScript("$('.header').show();$('#viewHidePanels').val('false');"));
		}
		if (fullscreen) {
			config_names_select.add(fullscreen_label);
			response.render(OnDomReadyHeaderItem.forScript("$('.header').hide();$('#viewHidePanels').val('true');"));
		} else {
			response.render(OnDomReadyHeaderItem.forScript("$('.header').show();$('#viewHidePanels').val('false');"));
		}
		if (showGraph) {
			config_names_select.add(graphs_label);
			response.render(OnDomReadyHeaderItem.forScript("$('#showGraph').val('true');"));
		} else {
			response.render(OnDomReadyHeaderItem.forScript("$('#showGraph').val('false');$('#demo-panel').hide()"));
		}
		response.render(OnDomReadyHeaderItem.forScript("$('#mapZoomLevel').val(" + zoom + ");$('#viewPanels').val("
				+ fullscreen + ");$('#mapType').val('" + mapType + "');$('#showGraph').val('" + showGraph
				+ "');$('#chart_total').val('" + total + "');$('#chart_group').val('" + group
				+ "');$('#showPrivateMap').val('" + showPrivateMap + "');"));
		if (dist != null) {
			response.render(OnDomReadyHeaderItem.forScript("$('#geoCoordDistance').val(" + dist + ")"));
		}
		if (coords != null) {
			String[] att = coords.substring(1, coords.length() - 1).split("\\),\\(");
			if (att.length == 1) {
				response.render(OnDomReadyHeaderItem
						.forScript("window.onload = function () {map2.locate({setView: true, maxZoom: 15})}"));
			} else {
				String coords_format = "[" + coords.toString().replaceAll("\\(", "new L.LatLng(") + "]";
				response.render(OnDomReadyHeaderItem
						.forScript("$(window).on('load',function(){ var shape = new L.Polygon(" + coords_format
								+ ");shape.setStyle({fillColor:'transparent',color:'#c4d6e6',weight:3});shape.addTo(editableLayers)});"));
			}
		}
	}

	private TextField<String> viewPanels = new TextField<String>("viewPanels", Model.of(""));
	private TextField<String> mapType = new TextField<String>("mapType", Model.of(""));
	private TextField<String> mapZoomLevel = new TextField<String>("mapZoomLevel", Model.of("15"));
	private TextField<String> geoCoordDistance = new TextField<String>("geoCoordDistance", Model.of("1"));
	private TextField<String> showGraph = new TextField<String>("showGraph", Model.of("false"));
	private TextField<String> showChart_total = new TextField<String>("chart_total", Model.of(""));
	private TextField<String> showChart_group = new TextField<String>("chart_group", Model.of(""));
	private TextField<String> showPrivateMap = new TextField<String>("showPrivateMap", Model.of(""));
	private String fullscreen_label = "Enable Fullscreen";
	private String privatemaps_label = "Enable Private mode";
	private String graphs_label = "Enable Graphs";
	private List<LocationModel> list = new ArrayList<LocationModel>();
	private List<LocationModel> list2 = new ArrayList<LocationModel>();
	private List<LocationModel> origList = new ArrayList<LocationModel>();
	private List<String> names = new ArrayList<String>();
	private Form<?> locationsForm = null;
	private ArrayList<String> namesRemoveSelect = new ArrayList<String>();
	private ArrayList<String> namesSelect = new ArrayList<String>();
	private ArrayList<String> config_names_select = new ArrayList<String>();
	private List<LocationModel> temp_list = new ArrayList<LocationModel>();
	private List<LocationModel> temp_list2 = new ArrayList<LocationModel>();

	private String username = null;
	private List<String> addlinks = null;

	private static Options chart_options = new Options();
	private static Options chart_options2 = new Options();
	private Chart pie_chart = new Chart("chart", chart_options);
	private List<PieChartModel> pieChartList = new ArrayList<PieChartModel>();
	private Chart column_chart = new Chart("chart2", chart_options2);
	private List<String> allAttributes = new ArrayList<String>();
	private List<Integer> allSums = new ArrayList<Integer>();
	private static Series<Point> series = new PointSeries();

	private final static Logger log = LoggerFactory.getLogger(LocationServicePage.class);

	@SpringBean
	private LocationServiceDAO locationServiceDAO;

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

	final Label locations_counter = new Label("locations_counter", Model.of(""));
	final ListView<LocationModel> lview = displayList("rows", list);

	public LocationServicePage(PageParameters parameters) throws IOException {
		setStatelessHint(false);
		setVersioned(false);
		WebSession session = WebSession.get();

		add(lview);
		add(formSearch);

		String username = null;
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
		datacontainer.add(pie_chart);
		chart_options2.setExporting(new ExportingOptions().setEnableImages(false).setEnabled(false));
		chart_options2.setChartOptions(new ChartOptions().setType(SeriesType.COLUMN));
		chart_options2.setCredits(new CreditOptions().setEnabled(false));
		chart_options2.setTitle(new Title("").setEnabled(true));
		column_chart.setVisible(false);
		datacontainer.add(column_chart);

		final WebMarkupContainer wmc2 = new WebMarkupContainer("wmc2");
		wmc2.setOutputMarkupId(true);
		add(wmc2);

		origList = locationServiceDAO.readLocationModel();
		wmc2.add(locations_counter);

		final TextField<String> idInput2 = new TextField<String>("idInput2", Model.of(""));
		AjaxButton showMarkerInfoBttn = new AjaxButton("showMarkerInfoBttn") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				super.onSubmit(target);
				String idInputValue2 = idInput2.getModelObject();
				temp_list2.clear();
				temp_list2.addAll(locationServiceDAO.getLocationByID(idInputValue2));
				target.add(wmc2);
			}
		};
		final Form<?> showMarkerInfo = new Form<Void>("showMarkerInfo");
		showMarkerInfo.add(idInput2);
		datacontainer.add(showMarkerInfo);
		showMarkerInfo.add(showMarkerInfoBttn);

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
		}

		ListView<LocationModel> showMarkerInfoList = new ListView<LocationModel>("showMarkerInfoList", temp_list2) {

			private static final long serialVersionUID = 1L;

			boolean fullscreen = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("fullscreen")
					.toBoolean();
			String maptype = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("map").toString();

			@Override
			public void populateItem(final ListItem<LocationModel> item) {

				final LocationModel obj = item.getModelObject();

				item.add(new Label("id_location2", obj.getId()));
				item.add(new Label("icon_marker2", obj.getIconmarker()));

				if (obj.getName() != null && !obj.getName().isEmpty()) {
					item.add(new Label("name2", obj.getName()));
				} else {
					item.add(new Label("name2").setVisible(false));
				}
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
					item.add(new Label("phoneTag2", "Telephone: "));
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
						newinfo += "<a href='search?&value=" + addlinks.get(i).trim().replaceAll(" ", "+")
								+ "&fullscreen=" + fullscreen + "&map=" + maptype + "'>" + addlinks.get(i)
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

		locationsForm = new Form<Void>("locationsForm");
		locationsForm.setOutputMarkupId(true);
		add(locationsForm);

		Label downloadLabel = new Label("downloadLabel", "Download search:");
		downloadLabel.setEscapeModelStrings(false);
		downloadLabel.setVisible(false);
		locationsForm.add(downloadLabel);

		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy'-'HHmm");
		File saveVisibleLoc = new File(Files.createTempDir(), "view-" + sdf.format(now) + ".csv");
		locationsForm.add(new DownloadLink("saveVisibleLoc", saveVisibleLoc));

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
								+ "Latitude" + DELIMITER + "Longitude" + DELIMITER + "Website" + DELIMITER + "Phone"
								+ DELIMITER + "Date" + DELIMITER + "Schedule" + DELIMITER + "Email" + DELIMITER
								+ "CsvName" + DELIMITER + "Population" + DELIMITER + "Elevation" + DELIMITER
								+ "Last editor" + DELIMITER + "Data publisher" + DELIMITER + "Published date: "
								+ DELIMITER + "Last update" + DELIMITER + "Icon Marker" + DELIMITER + "Private Mode"
								+ DELIMITER + "Extra info");
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
							wr.append(String.valueOf(loc.getLatitude()));
							wr.append(DELIMITER);
							wr.append(String.valueOf(loc.getLongitude()));
							wr.append(DELIMITER);
							wr.append(loc.getWebsite());
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
							wr.append(String.valueOf(loc.getPrivate_mode()));
							wr.append(DELIMITER);
							wr.append(loc.getOtherInfo().replaceAll(";", ","));
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
		saveVisibleLocFile.setVisible(false);
		locationsForm.add(saveVisibleLocFile);

		now = new Date();
		sdf = new SimpleDateFormat("ddMMyyyy'-'HHmm");

		File sqlLocBackup = new File(Files.createTempDir(), "backup-" + sdf.format(now) + ".sql");
		locationsForm.add(new DownloadLink("sqlLocBackup", sqlLocBackup));

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
							wr.append(String.valueOf(loc.getLatitude()));
							wr.append(DELIMITER);
							wr.append(String.valueOf(loc.getLongitude()));
							wr.append(DELIMITER);
							wr.append(loc.getWebsite());
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
							wr.append(String.valueOf(loc.getPrivate_mode()));
							wr.append(DELIMITER);
							wr.append(loc.getOtherInfo().replaceAll(";", ","));
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
						int fil_lat = 6;
						int fil_lng = 7;
						while ((line = br.readLine()) != null) {

							String[] value = line.split(DELIMITER);
							int j = 0;
							// we create the insert just if we have latitude and longitude
							if (!value[fil_lat].contentEquals("null") && !value[fil_lng].contentEquals("null")) {
								buffer.append(
										"INSERT INTO locations(name,description,type,address,postcode,city,latitude,longitude,website,phone,date,schedule,email,csvName,population,elevation,username,source,date_published,date_updated,iconmarker,private_mode,otherinfo) VALUES(");
								j = 0;
								while (j < value.length) {
									value[j] = value[j].replaceAll(",", ";").replaceAll("\'", "").replaceAll("\"", "")
											.replaceAll("null", "");
									// we add just strings with single quotes except for lat and lng
//									if (!value[j].contentEquals("") && j != fil_lat && j != fil_lng) {
//										buffer.append("'");
//										buffer.append(value[j]);
//										buffer.append("'");
//
//										// we add lat and long without quotes
//									} else if (j == fil_lat || j == fil_lng) {
//										buffer.append(value[j]);
									if (!value[j].isEmpty()) {
										buffer.append("'" + value[j] + "'");
									} else {
										buffer.append("''");
									}

									// we insert separator ","
									if (j != value.length - 1) {
										buffer.append(DELIMITER2);
									}
									j++;
								}
								buffer.append(");");
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
		// saveSqlLocBackup.setOutputMarkupPlaceholderTag(true);
		saveSqlLocBackup.setVisible(false);
		locationsForm.add(saveSqlLocBackup);

		if (!list.isEmpty()) {
			downloadLabel.setVisible(true);
			saveVisibleLocFile.setVisible(true);
			saveSqlLocBackup.setVisible(true);
		}

		Label active_user = new Label("active_user", "");
		locationsForm.add(active_user);

		if (WebSession.get().getAttribute("user_name") != null) {
			active_user.setDefaultModelObject("Active user: " + WebSession.get().getAttribute("user_name"));
		}

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

		final Form<String> formSearchExtraInfo = new Form<String>("formSearchExtraInfo");
		add(formSearchExtraInfo);

		final AutoCompleteTextField<String> inputField = new AutoCompleteTextField<String>("textSearch",
				new Model<String>()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected Iterator<String> getChoices(String input) {

				List<String> choices = new ArrayList<String>();
				int count = 0;
				String tmp_duplicate = "no match";
				for (final LocationModel loc : origList) {

					String name = loc.getOtherInfo();
					if (name != null) {
						name = name.replaceAll(" ## ", " ").toLowerCase();

						if (name.matches("(.*)" + input.toLowerCase() + "(.*)")) {

							String[] val = name.toLowerCase().split(input.toLowerCase());
							tmp_duplicate = input + StringUtils.left(val[1], 10);

							if (!choices.contains(tmp_duplicate))
								choices.add(tmp_duplicate);
							if (++count == 10)
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
				target.add(wmc2);
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
				String showGraphInput = showGraph.getModelObject();
				String chart_total = showChart_total.getModelObject();
				String chart_group = showChart_group.getModelObject();
				String showPrivateMapInput = showPrivateMap.getModelObject();

				if (name != null) {
					list.clear();
					list2.clear();
					boolean found2 = false;
					/*
					 * boolean found = false; int total = 0; for (LocationModel loc : origList) {
					 * 
					 * // single search restricted to 5 possible choices coming out from: inputField
					 * if (loc.getOtherInfo() != null && loc.getOtherInfo().replaceAll("##",
					 * "").toLowerCase() .contentEquals(name.toLowerCase())) { list.add(loc); found
					 * = true; break; } total++;
					 * 
					 * // there was not attribute found, we search globally if (!found && total ==
					 * origList.size()) {
					 * 
					 * // get multiple search of locations by querying database list2 =
					 * locationServiceDAO.searchLocationModel(name); if (!list2.isEmpty()) {
					 * list.addAll(list2); found2 = true; } } }
					 */
					list2 = locationServiceDAO.searchLocationModel(name);
					if (!list2.isEmpty()) {
						list.addAll(list2);
						found2 = true;
					}
					PageParameters pageParameters = new PageParameters();
					// used for a single search depending on the chosen location
					/*
					 * if (found) { pageParameters.set("name", list.get(0).getName());
					 * pageParameters.set("id", list.get(0).getId()); pageParameters.set("lat",
					 * list.get(0).getLatitude()); pageParameters.set("lng",
					 * list.get(0).getLongitude()); pageParameters.set("zoom", zoomLevelInputValue);
					 * pageParameters.set("fullscreen", viewPanelsInputValue);
					 * pageParameters.set("map", mapTypeInputValue); }
					 */
					// user for multiple results by querying with a like %otherinfo% statement
					if (found2) {
						String searched_value = inputField.getModelObject();
						pageParameters.set("value", searched_value);
						pageParameters.set("zoom", zoomLevelInputValue);

						if (viewPanelsInputValue != null)
							pageParameters.set("fullscreen", viewPanelsInputValue);
						if (!mapTypeInputValue.equals("null"))
							pageParameters.set("map", mapTypeInputValue);
						if (showGraphInput != null)
							pageParameters.set("graph", showGraphInput);
						if (!chart_total.equals("null"))
							pageParameters.set("total", chart_total);
						if (!chart_group.equals("null"))
							pageParameters.set("group", chart_group);
						if (!showPrivateMapInput.equals("null"))
							pageParameters.set("user", showPrivateMapInput);
					}
					setResponsePage(LocationServicePage.class, pageParameters);
				}
				target.add(wmc2);
			}

		});

		final TextField<String> polygonCoordInput = new TextField<String>("polygonCoordInput", Model.of(""));
		polygonCoordInput.setOutputMarkupId(true);

//		QueryBuilder builder = fullTextSession.getSearchFactory()
//				   .buildQueryBuilder().forEntity( POI.class ).get();
//		Query luceneQuery = builder.spatial()
//			      .onCoordinates("location")
//			      .within(100, Unit.KM)
//			      .ofLatitude(centerLatitude)
//			      .andLongitude(centerLongitude)
//			   .createQuery();

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
				String showGraphValue = showGraph.getModelObject();
				String chart_total = showChart_total.getModelObject();
				String chart_group = showChart_group.getModelObject();
				String showPrivateMapValue = showPrivateMap.getModelObject();

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

								// log.info("My polygon is " + myPolygon);
								if (myPolygon.contains(loc.getLatitude().doubleValue(),
										loc.getLongitude().doubleValue())) {
									list.add(loc);

//									Query query = builder.spatial().onCoordinates("location").within(51, Unit.KM)
//											.ofCoordinates(coordinates).createQuery();
									// log.info("we add location being: " + loc);
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
				target.add(wmc2);

				PageParameters pageParameters = new PageParameters();
				pageParameters.set("zoom", zoomLevelInputValue);
				if (att.length == 1) {
					pageParameters.set("dist", geoCoordDistanceValue);
				}
				if (viewPanelsInputValue != null)
					pageParameters.set("fullscreen", viewPanelsInputValue);
				if (!mapTypeInputValue.equals("null"))
					pageParameters.set("map", mapTypeInputValue);
				if (!showGraphValue.equals("null"))
					pageParameters.set("graph", showGraphValue);
				if (!chart_total.equals("null"))
					pageParameters.set("total", chart_total);
				if (!chart_group.equals("null"))
					pageParameters.set("group", chart_group);
				if (!showPrivateMapValue.equals("null"))
					pageParameters.set("user", showPrivateMapValue);
				if (polygonCoordInputValue == null) { // used when using check my current location
					pageParameters.set("coords", parameters.get("coords"));
				} else {
					pageParameters.set("coords", polygonCoordInputValue);
				}
				setResponsePage(getPage().getClass(), pageParameters);
			}
		};

		formSearch.add(viewPanels);
		formSearch.add(mapType);
		formSearch.add(mapZoomLevel);
		formSearch.add(geoCoordDistance);
		formSearch.add(polygonCoordInput);
		formSearch.add(showGraph);
		formSearch.add(showChart_total);
		formSearch.add(showChart_group);
		formSearch.add(showPrivateMap);
		formSearch.add(savePolygonCoordinates);
		add(formSearch);
	}

	JSONArray json = new JSONArray();

	public ListView<LocationModel> displayList(String id, List<LocationModel> list) throws IOException {

		String zoom = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("zoom").toString();
		boolean fullscreen = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("fullscreen")
				.toBoolean();
		String maptype = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("map").toString();
		boolean showGraph = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("graph")
				.toBoolean();
		String user = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("user").toString();
		String chart = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("total").toString();
		String chart2 = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("group").toString();

		List<String> visited = new ArrayList<String>();
		List<Integer> visited_id = new ArrayList<Integer>();
		List<Integer> visited_sum = new ArrayList<Integer>();
		List<Integer> valuerep = new ArrayList<Integer>();

		ListView<LocationModel> listView = new ListView<LocationModel>(id, list) {
			private static final long serialVersionUID = 1L;

			@Override
			public void renderHead(IHeaderResponse response) {
				if (!json.isEmpty())
					response.render(OnDomReadyHeaderItem.forScript("jsonObj = JSON.stringify(" + json + ")"));
				super.renderHead(response);
			}

			int count = 0;
			List<String> listchart = null;

			@Override
			protected void onBeforeRender() {
				count = 0;
				chart_options.clearSeries();
				chart_options2.clearSeries();
				if (showGraph) {
					if (series.getData() != null) {
						series.getData().clear();
					}
					allAttributes.clear();
					allSums.clear();
				}

				super.onBeforeRender();
			}

			JSONObject o;
			LocationModel obj;

			Series<Number> serie4 = null;

			@Override
			@SuppressWarnings("unchecked")
			public void populateItem(final ListItem<LocationModel> item) {
				obj = item.getModelObject();
				if (obj.getPrivate_mode() == false || obj.getUsername().equals(user)) {
					o = new JSONObject();
					o.put("id", obj.getId());
					o.put("name", obj.getName());
					o.put("icon", obj.getIconmarker().split("#")[0]);
					o.put("lat", String.format("%.5f", obj.getLatitude()));
					o.put("lng", String.format("%.5f", obj.getLongitude()));
					json.add(o);
				}
				if (count == list.size() - 1) {
					locations_counter.setMarkupId("locations_counter");
					locations_counter.setDefaultModelObject("Total Markers: " + list.size());
					locations_counter.modelChanged();
				}

				if (chart != null) {

					listchart = new ArrayList<String>(Arrays.asList(chart.split(",")));
					addlinks = new ArrayList<>(Arrays.asList(obj.getOtherInfo().split("##")));
					for (int i = 0; i < listchart.size(); i++) {
						for (int j = 0; j < addlinks.size(); j++) {
							String[] values = addlinks.get(j).split(":");
							if (values[0].toLowerCase().trim().equals(listchart.get(i).toLowerCase())) {

								String val = values[1].trim();

								if (StringUtils.isNumeric(val)) {
									Integer val_rep = Integer.valueOf(val);
									String visit = values[0].toLowerCase().trim();

									if (visited.contains(listchart.get(i))) {
										int k = visited.indexOf(listchart.get(i));
										valuerep.set(k, valuerep.get(k) + val_rep);
									} else if (!visited.contains(listchart.get(i))) {
										valuerep.add(i, val_rep);
										visited.add(i, visit);
									}
								}
							}
						}
					}
					if (count == list.size() - 1 && !listchart.isEmpty() && !valuerep.isEmpty()) {
						for (int i = 0; i < listchart.size(); i++) {
							serie4 = new SimpleSeries();
							serie4.setName(listchart.get(i));
							serie4.setData(valuerep.get(i));
							chart_options2.setTooltip(new Tooltip().setFormatter(
									new Function().setFunction("return ''+ this.series.name +': '+ this.y +' rep';")));
							chart_options2.addSeries(serie4);
						}
					}
					column_chart.setVisible(true);
				}
				if (chart2 != null) {
					listchart = new ArrayList<String>(Arrays.asList(chart2.split(",")));
					addlinks = new ArrayList<>(Arrays.asList(obj.getOtherInfo().split("##")));
					int id = Integer.valueOf(obj.getId().toString());

					for (int i = 0; i < listchart.size(); i++) {
						for (int j = 0; j < addlinks.size(); j++) {

							String[] values = addlinks.get(j).split(":");
							if (values[0].toLowerCase().trim().equals(listchart.get(i).toLowerCase())) {

								String val = values[1].trim();

								if (StringUtils.isNumeric(val)) {
									Integer val_rep = Integer.valueOf(val);
									String visit = values[0].toLowerCase().trim();

									if (!visited_id.contains(id)) {
										if (valuerep.contains(val_rep)) {
											int k = valuerep.indexOf(val_rep);
											visited_sum.set(k, visited_sum.get(k) + 1);
											visited_id.add(id);
										} else if (!valuerep.contains(val_rep)) {

											valuerep.add(val_rep);
											visited.add(i, visit);
											visited_sum.add(1);
											visited_id.add(id);
										}
									}
								}
							}
						}
					}
					if (count == list.size() - 1 && !listchart.isEmpty()) {
						for (int i = 0; i < valuerep.size(); i++) {
							serie4 = new SimpleSeries();

							serie4.setName(valuerep.get(i).toString());
							serie4.setData(visited_sum.get(i));
							chart_options2.setTooltip(new Tooltip().setFormatter(
									new Function().setFunction("return ''+ this.series.name +': '+ this.y +' rep';")));
							chart_options2.addSeries(serie4);
						}
						column_chart.setVisible(true);
					} else {
						column_chart.setVisible(false);
					}

				}

				if (showGraph) {
					addlinks = new ArrayList<>(Arrays.asList(obj.getOtherInfo().split("##")));

					for (int i = 0; i < addlinks.size(); i++) {

						PieChartModel pc = new PieChartModel();
						boolean found = false;
						int pos = 0;

						if (!addlinks.get(i).contains("Published by") && !addlinks.get(i).contains("Last update")) {
							for (PieChartModel pie : pieChartList) {
								if (pie.getAttribute() != null && pie.getAttribute().equals(addlinks.get(i))) {
									found = true;
									pos = pieChartList.indexOf(pie);
								}
							}
							if (found) {
								pieChartList.get(pos).setRepetitions(pieChartList.get(pos).getRepetitions() + 1);

							} else if (addlinks.get(i) != null && pieChartList.size() < 500) {
								pc.setAttribute(addlinks.get(i));
								pc.setRepetitions(1);
								pieChartList.add(pc);
							}
						}
					}

					if (count == list.size() - 1) {
						series = new PointSeries();

						for (int i = 0; i < pieChartList.size(); i++)
							Collections.sort(pieChartList, Collections.reverseOrder());

						for (int i = 0; i < pieChartList.size(); i++) {
							if (pieChartList.get(i).getRepetitions() != null
									&& pieChartList.get(i).getRepetitions() > 1) {
								series.addPoint(new Point(pieChartList.get(i).getAttribute(),
										pieChartList.get(i).getRepetitions())
												.setEvents(new Events().setClick(new RedirectFunction("search?&value="
														+ pieChartList.get(i).getAttribute().trim().replaceAll(" ", "")
														+ "&zoom=" + zoom + "&fullscreen=" + fullscreen + "&map="
														+ maptype + "&graph=" + showGraph))));
							}
						}
						series.setShowInLegend(false);
						series.setDataLabels(new DataLabels().setEnabled(true)
								.setBackgroundColor(new HexColor("#F8F9F9")).setColor(new HexColor("#444444"))
								.setFormatter(new Function().setFunction(
										"if(this.point.name.length>20){return this.point.name.substr(0,20)+'...'}else{return this.point.name}"))
								.setStyle(new CssStyle().setProperty("font-weight", "normal")
										.setProperty("font-size", "11px")
										.setProperty("font-family", "sans-serif, verdana").setProperty("width", "60px"))
								.setAlign(HorizontalAlignment.RIGHT));
						chart_options.setTooltip(new Tooltip().setFormatter(
								new Function().setFunction("return ''+ this.point.name +': '+ this.y +' rep';")));
						chart_options.addSeries(series);
						pie_chart.setVisible(true);

					} else if (list.isEmpty()) {
						pie_chart.setVisible(false);
					}
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

		WebSession session = WebSession.get();

		if (WebSession.get().getAttribute("user_name") != null) {
			username = session.getAttribute("user_name").toString();
		}

		// TODO
		/*
		 * if (username != null) { if (username.contentEquals("admin")) {
		 * locationsForm.setVisible(true); } } else { locationsForm.setVisible(false); }
		 */

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

		Label configLabel = new Label("configLabel", "Configuration:");
		Label importedFilesLabel = new Label("importedFilesLabel", "Imported datasets:");
		Label removeSelectedLabel = new Label("removeSelectedLabel", "Remove files:");

		if (names.isEmpty()) {
			importedFilesLabel.setVisible(false);
			removeSelectedLabel.setVisible(false);
			removeSelected.setVisible(false);
		} else {
			importedFilesLabel.setVisible(true);
			removeSelectedLabel.setVisible(true);
			removeSelected.setVisible(true);
		}
		locationsForm.add(configLabel);
		locationsForm.add(importedFilesLabel);
		locationsForm.add(removeSelectedLabel);

		final List<String> config_names = new ArrayList<String>();
		config_names.add(fullscreen_label);
		config_names.add(privatemaps_label);
		config_names.add(graphs_label);

		CheckBoxMultipleChoice<String> listConfig = new CheckBoxMultipleChoice<String>("listConfig",
				new Model<ArrayList<String>>(config_names_select), config_names);
		listConfig.setSuffix("<br />");
		AjaxFormChoiceComponentUpdatingBehavior check2 = new AjaxFormChoiceComponentUpdatingBehavior() {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {

				PageParameters pageParameters = new PageParameters();

				for (int j = 0; j < config_names_select.size(); j++) {
					if (config_names_select.get(j).equals(fullscreen_label)) {
						pageParameters.set("fullscreen", true);
					}
					if (config_names_select.get(j).equals(privatemaps_label)) {
						if (username == null) {
							target.appendJavaScript("alert('Sorry, but you must be first logged in!')");
						} else {
							target.appendJavaScript("$('#showPrivateMap').val('" + username + "')");
							log.info("username is " + username);
							log.info("username2 is " + session.getAttribute("user_name"));
							pageParameters.set("user", username);
						}
					}
					if (config_names_select.get(j).equals(graphs_label)) {
						pageParameters.set("graph", true);
						setResponsePage(getPage().getClass(), pageParameters);
					}
				}
				if (username != null) {
					setResponsePage(getPage().getClass(), pageParameters);
				}

			}
		};
		listConfig.add(check2);
		locationsForm.add(listConfig);

		CheckBoxMultipleChoice<String> listLocations = new CheckBoxMultipleChoice<String>("listLocations",
				new Model<ArrayList<String>>(namesSelect), names);
		listLocations.setSuffix("<br />");
		AjaxFormChoiceComponentUpdatingBehavior check = new AjaxFormChoiceComponentUpdatingBehavior() {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {

				json.clear();
				list.clear();
				list2.clear();

				for (int j = 0; j < namesSelect.size(); j++) {
					list2.addAll(locationServiceDAO.searchLocationByFileName(namesSelect.get(j).toString()));
				}
				if (list2.isEmpty()) {
					target.appendJavaScript("point_markers.remove()");

				} else {
					list.addAll(list2);
					setResponsePage(getPage());
				}
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

				int i = 0;
				boolean find = false;
				temp_list.clear();

				while (find == false) {
					if (origList.get(i).getId().toString().contentEquals(idInputValue)) {
						temp_list.add(origList.get(i));
						find = true;
					}
					i++;
				}
				saveLocation.setVisible(true);
				setResponsePage(getPage());
				closeEdit.setVisible(true);
			}
		};
		editFormTriggerClick.add(editInfoButton);

		final Button displayLocations = new Button("displayLocations") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {

				names.clear();
				namesSelect.clear();

				// we refresh the list to add all the locations that belong to the selected name
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
	}

	@Override
	protected String getMessage() {
		return null;
	}
}
