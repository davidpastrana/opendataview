package com.opendataview.web.pages.properties;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.protocol.http.PageExpiredException;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.opendataview.web.heuristicsearch.MainClass;
import com.opendataview.web.model.LocationModel;
import com.opendataview.web.model.PropertiesModel;
import com.opendataview.web.pages.index.BasePage;
import com.opendataview.web.persistence.LocationServiceDAO;
import com.opendataview.web.persistence.PropertiesServiceDAO;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.fileinput.BootstrapFileInput;

public class SetPropertiesPage extends BasePage {

	private static final long serialVersionUID = 1L;

	FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		boolean logs = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("logs").toBoolean();
		boolean uploads = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("uploads")
				.toBoolean();
		boolean config = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("config").toBoolean();
		if (logs) {
			response.render(OnDomReadyHeaderItem.forScript("$('.nav-tabs a[href=\"#nav-logs\"]').tab('show');"));
		}
		if (uploads) {
			response.render(OnDomReadyHeaderItem.forScript("$('.nav-tabs a[href=\"#nav-uploads\"]').tab('show');"));
		}
		if (config) {
			response.render(OnDomReadyHeaderItem.forScript("$('.nav-tabs a[href=\"#nav-config\"]').tab('show');"));
		}
		response.render(OnDomReadyHeaderItem.forScript(
				"$('.fileinput-remove-button,.fileinput-cancel-button').hide();$('button.fileinput-upload-button').text('Run');$('button.fileinput-upload-button');"));
	}

	private final static Logger log = LoggerFactory.getLogger(SetPropertiesPage.class);

	// private FileUploadField fileUpload1,fileUpload2,fileUpload3;

	protected static StringBuilder outputinfo = new StringBuilder("");
	public static TextArea<String> resultsBox = null;
	protected static Model<String> textResult = Model.of("");

	private String upload_folder = null;
	// private FileUpload uploadedFile1,uploadedFile2,uploadedFile3 = null;
	protected File newFile1;
	protected File newFile = null;
	public PropertiesModel p1 = null;
	private final List<String> sep = new ArrayList<String>();
	List<LocationModel> list = new ArrayList<LocationModel>();
	// protected static String csvfiles_dir = "file: config.properties";
	// protected static String processed_dir = "file: config.properties";
	// protected static String sqlinserts_file = "file: config.properties";
	protected static File sqlFile = null;
	// csv file name
	protected static String file_name = "";

	@SpringBean
	private PropertiesServiceDAO propertiesServiceDAO;

	@SpringBean
	protected static LocationServiceDAO locationServiceDAO;

	public List<PropertiesModel> propList = new ArrayList<PropertiesModel>();

	private Integer col = 0;

	public void prepareComboBoxes() {
		sep.add(",");
		sep.add(";");
	}

//	public List<String> showSampleCSV(FileUpload uploadedFile) {
//		List<String> sampleRows = null;
//		try {
//			BufferedReader csvFile = null;
//			String datasetFile = upload_folder + uploadedFile.getClientFileName();
//
//			csvFile = new BufferedReader(
//					new InputStreamReader(new FileInputStream(datasetFile), StandardCharsets.UTF_8));
//			String line1 = csvFile.readLine();
//			String line2 = csvFile.readLine();
//			String line3 = csvFile.readLine();
//			csvFile.close();
//			sampleRows = Arrays.asList(line1, line2, line3);
//
//		} catch (IOException e) {
//			log.error("Error while reading from csv " + e.getMessage());
//		}
//		return sampleRows;
//	}

	String user = null;

	public SetPropertiesPage(final PageParameters parameters) throws IOException {

		final WebSession session = WebSession.get();

		if (!Session.exists()) {
			session.info("message");
		}

		if (session.getAttribute("user_name") == null) {
			throw new PageExpiredException("No user available. Page seems to be expired.");
		} else {
			user = session.getAttribute("user_name").toString();
		}

		if (session.getAttribute("user_name") != null) {
			propList = propertiesServiceDAO.readPropertiesModel(user);

			List<LocationModel> origList = new ArrayList<LocationModel>();
			origList = locationServiceDAO.readLocationModel();
			List<String> names = new ArrayList<String>();
			names.clear();

			for (int i = 0; i < origList.size(); i++) {
				if (!names.contains(origList.get(i).getFileName()) && origList.get(i).getUsername() != null
						&& origList.get(i).getUsername().contentEquals(session.getAttribute("user_name").toString())) {
					names.add(origList.get(i).getFileName());
				}
			}

			ServletContext servletContext = WebApplication.get().getServletContext();

			upload_folder = servletContext.getRealPath("/") + "files/";
			final WebMarkupContainer wmc = new WebMarkupContainer("wmc");
			wmc.setVersioned(false);
			add(wmc);

			wmc.add(new Label("userSigned", session.getAttribute("user_name")));

			FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
			feedbackPanel.setOutputMarkupId(true);
			feedbackPanel.setOutputMarkupPlaceholderTag(true);

			if (RequestCycle.get().getRequest().getRequestParameters().getParameterValue("logs").toString() != null) {
				feedbackPanel.setVisible(true);
			} else {
				feedbackPanel.setVisible(false);
			}
			wmc.add(feedbackPanel);

			final Form<?> formTypes = new Form<Void>("formTypes");
			wmc.add(formTypes);
			wmc.setOutputMarkupId(true);

			final Button saveType = new Button("saveType") {

				private static final long serialVersionUID = 1L;

				@Override
				public void onSubmit() {
					col++;
					log.info("COLUMN " + col);
					// colId.setObject("Column \"" + (col + 1) + "\" : ");
					log.info("SAVETYPE IS  " + col);
				}
			};
			formTypes.add(saveType);

			ArrayList<String> namesRemoveSelect = new ArrayList<String>();
			final Form<?> form = new Form<Void>("propertiesForm");
			wmc.add(form);

			Label admin_user_visibility = new Label("admin_user_visibility");

			ListView<PropertiesModel> editPropList = new ListView<PropertiesModel>("editProperties", propList) {

				private static final long serialVersionUID = 1L;

				@Override
				protected void populateItem(final ListItem<PropertiesModel> item) {

					item.add(new TextField<String>("mapsAPI",
							new PropertyModel<String>(item.getModelObject(), "mapsAPI")));
					item.add(new DropDownChoice<Boolean>("private_mode",
							new PropertyModel<Boolean>(item.getModelObject(), "private_mode"),
							Arrays.asList(true, false)));
					item.add(new DropDownChoice<Boolean>("executeSQLqueries",
							new PropertyModel<Boolean>(item.getModelObject(), "executeSQLqueries"),
							Arrays.asList(true, false)));
					item.add(new DropDownChoice<Boolean>("autodetectSchema",
							new PropertyModel<Boolean>(item.getModelObject(), "autodetectSchema"),
							Arrays.asList(true, false)));
					item.add(new DropDownChoice<Boolean>("geonamesdebugmode",
							new PropertyModel<Boolean>(item.getModelObject(), "geonamesdebugmode"),
							Arrays.asList(true, false)));

					item.add(new DropDownChoice<Boolean>("fieldtypesdebugmode",
							new PropertyModel<Boolean>(item.getModelObject(), "fieldtypesdebugmode"),
							Arrays.asList(true, false)));
					item.add(new DropDownChoice<Boolean>("random_color",
							new PropertyModel<Boolean>(item.getModelObject(), "random_color"),
							Arrays.asList(true, false)));
					item.add(new DropDownChoice<Boolean>("active_dictionary",
							new PropertyModel<Boolean>(item.getModelObject(), "active_dictionary"),
							Arrays.asList(true, false)));

					item.add(new TextArea<String>("dictionary_matches",
							new PropertyModel<String>(item.getModelObject(), "dictionary_matches")));
					item.add(new DropDownChoice<String>("nrowchecks",
							new PropertyModel<String>(item.getModelObject(), "nrowchecks"), Arrays.asList("2", "3", "4",
									"5", "6", "7", "8", "9", "10", "15", "20", "25", "30", "35", "40", "45", "50")));
					item.add(new DropDownChoice<String>("pvalue_nrowchecks",
							new PropertyModel<String>(item.getModelObject(), "pvalue_nrowchecks"), Arrays.asList("0.01",
									"0.05", "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "1")));

					item.add(new TextField<String>("imageRegex",
							new PropertyModel<String>(item.getModelObject(), "imageRegex")));
					item.add(new TextField<String>("phoneRegex",
							new PropertyModel<String>(item.getModelObject(), "phoneRegex")));
					item.add(new TextField<String>("cityRegex",
							new PropertyModel<String>(item.getModelObject(), "cityRegex")));
					item.add(new TextField<String>("postcodeRegex",
							new PropertyModel<String>(item.getModelObject(), "postcodeRegex")));
					item.add(new TextField<String>("archiveRegex",
							new PropertyModel<String>(item.getModelObject(), "archiveRegex")));
					item.add(new TextField<String>("documentRegex",
							new PropertyModel<String>(item.getModelObject(), "documentRegex")));
					item.add(new TextField<String>("openinghoursRegex",
							new PropertyModel<String>(item.getModelObject(), "openinghoursRegex")));
					item.add(new TextField<String>("dateRegex",
							new PropertyModel<String>(item.getModelObject(), "dateRegex")));
					item.add(new TextField<String>("yearRegex",
							new PropertyModel<String>(item.getModelObject(), "yearRegex")));
					item.add(new TextField<String>("currencyRegex",
							new PropertyModel<String>(item.getModelObject(), "currencyRegex")));
					item.add(new TextField<String>("percentageRegex",
							new PropertyModel<String>(item.getModelObject(), "percentageRegex")));
					item.add(new TextField<String>("nutsRegex",
							new PropertyModel<String>(item.getModelObject(), "nutsRegex")));
					item.add(new TextField<String>("shapeRegex",
							new PropertyModel<String>(item.getModelObject(), "shapeRegex")));
					item.add(new TextField<String>("latitudeRegex",
							new PropertyModel<String>(item.getModelObject(), "latitudeRegex")));
					item.add(new TextField<String>("longitudeRegex",
							new PropertyModel<String>(item.getModelObject(), "longitudeRegex")));
					item.add(new TextField<String>("latlngRegex",
							new PropertyModel<String>(item.getModelObject(), "latlngRegex")));
					item.add(new TextField<String>("possiblenameRegex",
							new PropertyModel<String>(item.getModelObject(), "possiblenameRegex")));
					item.add(new TextField<String>("descriptionRegex",
							new PropertyModel<String>(item.getModelObject(), "descriptionRegex")));
					item.add(new TextField<String>("iconmarker",
							new PropertyModel<String>(item.getModelObject(), "iconmarker")));
					item.add(new DropDownChoice<String>("countrycode",
							new PropertyModel<String>(item.getModelObject(), "countrycode"),
							Arrays.asList("AF", "AX", "AL", "DZ", "AS", "AD", "AO", "AI", "AQ", "AG", "AR", "AM", "AW",
									"AU", "AT", "AZ", "BS", "BH", "BD", "BB", "BY", "BE", "BZ", "BJ", "BM", "BT", "BO",
									"BQ", "BA", "BW", "BV", "BR", "IO", "BN", "BG", "BF", "BI", "KH", "CM", "CA", "CV",
									"KY", "CF", "TD", "CL", "CN", "CX", "CC", "CO", "KM", "CG", "CD", "CK", "CR", "CI",
									"HR", "CU", "CW", "CY", "CZ", "DK", "DJ", "DM", "DO", "EC", "EG", "SV", "GQ", "ER",
									"EE", "ET", "FK", "FO", "FJ", "FI", "FR", "GF", "PF", "TF", "GA", "GM", "GE", "DE",
									"GH", "GI", "GR", "GL", "GD", "GP", "GU", "GT", "GG", "GN", "GW", "GY", "HT", "HM",
									"VA", "HN", "HK", "HU", "IS", "IN", "ID", "IR", "IQ", "IE", "IM", "IL", "IT", "JM",
									"JP", "JE", "JO", "KZ", "KE", "KI", "KP", "KR", "KW", "KG", "LA", "LV", "LB", "LS",
									"LR", "LY", "LI", "LT", "LU", "MO", "MK", "MG", "MW", "MY", "MV", "ML", "MT", "MH",
									"MQ", "MR", "MU", "YT", "MX", "FM", "MD", "MC", "MN", "ME", "MS", "MA", "MZ", "MM",
									"NA", "NR", "NP", "NL", "NC", "NZ", "NI", "NE", "NG", "NU", "NF", "MP", "NO", "OM",
									"PK", "PW", "PS", "PA", "PG", "PY", "PE", "PH", "PN", "PL", "PT", "PR", "QA", "RE",
									"RO", "RU", "RW", "BL", "SH", "KN", "LC", "MF", "PM", "VC", "WS", "SM", "ST", "SA",
									"SN", "RS", "SC", "SL", "SG", "SX", "SK", "SI", "SB", "SO", "ZA", "GS", "SS", "ES",
									"LK", "SD", "SR", "SJ", "SZ", "SE", "CH", "SY", "TW", "TJ", "TZ", "TH", "TL", "TG",
									"TK", "TO", "TT", "TN", "TR", "TM", "TC", "TV", "UG", "UA", "AE", "GB", "US", "UM",
									"UY", "UZ", "VU", "VE", "VN", "VG", "VI", "WF", "EH", "YE", "ZM", "ZW")));
					item.add(new TextField<String>("geonames_dbdriver",
							new PropertyModel<String>(item.getModelObject(), "geonames_dbdriver")));
					item.add(new TextField<String>("geonames_dburl",
							new PropertyModel<String>(item.getModelObject(), "geonames_dburl")));
					item.add(new TextField<String>("geonames_dbusr",
							new PropertyModel<String>(item.getModelObject(), "geonames_dbusr")));
					item.add(new TextField<String>("geonames_dbpwd",
							new PropertyModel<String>(item.getModelObject(), "geonames_dbpwd")));
					item.add(admin_user_visibility);
					item.add(new TextField<String>("web_dbdriver",
							new PropertyModel<String>(item.getModelObject(), "web_dbdriver")));
					item.add(new TextField<String>("web_dburl",
							new PropertyModel<String>(item.getModelObject(), "web_dburl")));
					item.add(new TextField<String>("web_dbusr",
							new PropertyModel<String>(item.getModelObject(), "web_dbusr")));
					item.add(new TextField<String>("web_dbpwd",
							new PropertyModel<String>(item.getModelObject(), "web_dbpwd")));
					item.add(new TextField<String>("st1postcode",
							new PropertyModel<String>(item.getModelObject(), "st1postcode")));
					item.add(new TextField<String>("st1city",
							new PropertyModel<String>(item.getModelObject(), "st1city")));
				}
			};
			form.add(editPropList);

			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy'_'HH:mm");
			File downloadLog = new File(Files.createTempDir(), "log_opendataview_"
					+ file_name.toLowerCase().replaceAll(".csv", "") + "_" + sdf.format(now) + ".log");

			form.add(new DownloadLink("outputDownload", downloadLog));

			final AjaxButton downloadLogDetails = new AjaxButton("downloadLogDetails") {

				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					try {
						String info = textResult.getObject();
						FileWriter writer = new FileWriter(downloadLog);
						writer.write(info);
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					target.prependJavaScript("$('#linkSaveLogDetails').trigger('click');");
				}

			};
			if (RequestCycle.get().getRequest().getRequestParameters().getParameterValue("logs").toString() != null) {
				downloadLogDetails.setVisible(true);
			} else {
				downloadLogDetails.setVisible(false);
			}
			downloadLogDetails.setOutputMarkupId(true);
			form.add(downloadLogDetails);

			final Button saveProperties = new Button("saveProperties") {

				private static final long serialVersionUID = 1L;

				@Override
				public void onSubmit() {
					super.onSubmit();
					if (propertiesServiceDAO.readPropertiesModel(user).size() != 0) {
						propertiesServiceDAO.updatePropertiesModel(propList.get(0));
						feedbackPanel.setVisible(false);
					} else {
						info("Error. No properties defined in database table.");
						feedbackPanel.setVisible(true);
					}
					PageParameters params = new PageParameters();
					params.add("config", true);
					setResponsePage(getPage().getClass(), params);

				}
			};
			form.add(saveProperties);

			final Button deleteAllUploads = new Button("deleteAllUploads") {

				private static final long serialVersionUID = 1L;

				@Override
				public void onSubmit() {
					super.onSubmit();
					if (propertiesServiceDAO.readPropertiesModel(user).size() != 0) {
						locationServiceDAO.removeAllLocations(user);
						feedbackPanel.setVisible(false);
					}
					setResponsePage(getPage().getClass(), getPage().getPageParameters());
				}
			};
			form.add(deleteAllUploads);

			final Button removeSelectedFiles = new Button("removeSelectedFiles") {

				private static final long serialVersionUID = 1L;

				@Override
				public void onSubmit() {
					super.onSubmit();
					for (int i = 0; i < namesRemoveSelect.size(); i++) {
						if (namesRemoveSelect.get(i) != null) {
							locationServiceDAO.removeLocationByName(namesRemoveSelect.get(i));
						}
					}

					PageParameters params = new PageParameters();
					params.add("uploads", true);
					setResponsePage(getPage().getClass(), params);
				}
			};
			form.add(removeSelectedFiles);

			Label showFilesUpload = new Label("showFilesUpload", "Your uploads:");

			form.add(showFilesUpload);

			final CheckBoxMultipleChoice<String> listRemoveLocations = new CheckBoxMultipleChoice<String>(
					"removeLocations", new Model<ArrayList<String>>(namesRemoveSelect), names);
			listRemoveLocations.setSuffix("<br />");
			form.add(listRemoveLocations);

			if (names.isEmpty()) {
				showFilesUpload.setVisible(false);
				listRemoveLocations.setVisible(false);
				removeSelectedFiles.setVisible(false);
				deleteAllUploads.setVisible(false);
			} else {
				showFilesUpload.setVisible(true);
				listRemoveLocations.setVisible(true);
				removeSelectedFiles.setVisible(true);
				deleteAllUploads.setVisible(true);
			}

			resultsBox = new TextArea<String>("resultsBox", textResult);
			resultsBox.setOutputMarkupPlaceholderTag(true);
			form.add(resultsBox);

			if (session.getAttribute("user_name").toString().contentEquals("admin")) {
				admin_user_visibility.setVisible(true);
			} else {
				admin_user_visibility.setVisible(false);
			}

			sqlFile = new File(upload_folder + "/processed/sql/" + file_name);
			sqlFile.getParentFile().mkdirs();

			final IModel<List<FileUpload>> model = new ListModel<FileUpload>();

			BootstrapFileInput bootstrapFileInput = new BootstrapFileInput("bootstrapFileinput", model) {

				private static final long serialVersionUID = 1L;
				// private boolean clear = false;

				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					super.onSubmit(target);

					List<FileUpload> fileUploads = model.getObject();
					if (fileUploads != null) {

						int countfile = 1;
						int countfile2 = 1;

						StringBuilder message = new StringBuilder();
						for (FileUpload upload : fileUploads) {

							try {
								MainClass.initialize(user, propertiesServiceDAO);
							} catch (IOException e) {
								e.printStackTrace();
							}
//							log.info("file type:" + upload.getContentType());
//							log.info("file size:" + upload.getSize());
//
//							log.info("file to execute is: " + upload.getClientFileName());
//							log.info("file directory: " + upload.toString());

							if (upload.getContentType().contentEquals("text/csv")) {

								File folder = new File(upload_folder + "/uploads/");
								if (!folder.exists()) {
									folder.mkdir();
								}

								File newFile = new File(folder + "/" + upload.getClientFileName());
								if (newFile.exists()) {
									newFile.delete();
								}
								try {
									upload.writeTo(newFile);

									// clear only if its first file
//									if (countfile == 1) {
//										clear = true;
//									} else {
//										clear = false;
//										
//									}
									MainClass.start(upload_folder + "uploads/", newFile, feedbackPanel,
											fileUploads.size(), user);

								} catch (Exception ex) {
									StringWriter errors = new StringWriter();
									errors.append(
											"Some error occurred, please send us an email with the error message and test file to: david@dpastrana.com.\n\n Thanks for your understanding!\n\nTimeStamp: "
													+ DateTime.now() + "\nErrorDetails:\n");
									ex.printStackTrace(new PrintWriter(errors));

									// Opens the email desktop with the email already prepared
//									try {
//										String uriStr = String.format("mailto:%s?subject=%s&body=%s",
//												join(",",
//														Arrays.asList("david@dpastrana.com",
//																"alu0100508031@student.tuwien.at")),
//												urlEncode("Error during transaction [opendataview.com]"),
//												urlEncode(errors.toString()));
//										Desktop.getDesktop().browse(new URI(uriStr));// Outlook
//									} catch (IOException | URISyntaxException e) {
//										e.printStackTrace();
//									}

									error(errors.toString());
									log.error("\n\nError: " + errors.toString());
									break;
								}
								if (countfile == 1) {
									message.append("Processed files:");
								}
								message.append("\nFile " + countfile + ": " + upload.getClientFileName());
								countfile++;
							}
							if (upload.getContentType().contentEquals("application/octet-stream")) {
								File folder = new File(upload_folder + "/uploads/");
								if (!folder.exists()) {
									folder.mkdir();
								}

								File newFile = new File(folder + "/" + upload.getClientFileName());
								if (newFile.exists()) {
									newFile.delete();
								}
								try {
									upload.writeTo(newFile);

									// clear only if its first file
									/*
									 * if (countfile2 == 1) { clear = true; } else { clear = false; }
									 */
									MainClass.start(upload_folder + "/uploads/", newFile, feedbackPanel,
											fileUploads.size(), user);
								} catch (Exception e) {
									e.printStackTrace();
								}
								message.append("\nBackup " + countfile2 + ": " + upload.getClientFileName());
								countfile2++;
							}

						}
						success(message);

						downloadLogDetails.setVisible(true);
						feedbackPanel.setVisible(true);

						wmc.modelChanged();
						resultsBox.modelChanged();
						listRemoveLocations.modelChanged();

						showFilesUpload.setVisible(true);
						listRemoveLocations.setVisible(true);
						removeSelectedFiles.setVisible(true);
						deleteAllUploads.setVisible(true);

						// We reload page after printing the result

						PageParameters params = new PageParameters();
						params.add("logs", true);
						setResponsePage(getPage().getClass(), params);

					} else {
						info("You must upload a file with CSV format.");
						feedbackPanel.setVisible(true);
					}

				}
			};
			bootstrapFileInput.getConfig().allowedFileExtensions(Arrays.asList("csv", "sql")).showUpload(true)
					.showRemove(true).showRemove(true);
			bootstrapFileInput.setRenderBodyOnly(true);
			bootstrapFileInput.setOutputMarkupId(true);
			form.add(bootstrapFileInput);

		} else {
			info("Session Timeout. Please login again.");
			feedbackPanel.setVisible(true);
		}
	}

//	private static final String urlEncode(String str) {
//		try {
//			return URLEncoder.encode(str, "UTF-8").replace("+", "%20");
//		} catch (UnsupportedEncodingException e) {
//			throw new RuntimeException(e);
//		}
//	}

	public static final String join(String sep, Iterable<?> objs) {
		StringBuilder sb = new StringBuilder();
		for (Object obj : objs) {
			if (sb.length() > 0)
				sb.append(sep);
			sb.append(obj);
		}
		return sb.toString();
	}

}
