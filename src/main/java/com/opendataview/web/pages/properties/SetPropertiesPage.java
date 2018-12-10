package com.opendataview.web.pages.properties;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.persistence.Column;
import javax.servlet.ServletContext;

import org.apache.commons.fileupload.FileItem;
import org.apache.tools.ant.taskdefs.email.EmailTask.Encoding;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;
import org.eclipse.jetty.server.Server;
import org.geonames.InvalidParameterException;
import org.geotools.filter.text.cql2.CQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.i18n.phonenumbers.NumberParseException;
import com.opendataview.web.heuristicsearch.MainClass;
import com.opendataview.web.heuristicsearch.ReadPropertyValues;
import com.opendataview.web.heuristicsearch.RunSqlScript;
import com.opendataview.web.model.LocationModel;
import com.opendataview.web.model.PropertiesModel;
import com.opendataview.web.pages.index.BasePage;
import com.opendataview.web.persistence.LocationServiceDAO;
import com.opendataview.web.persistence.PropertiesServiceDAO;

import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.fileinput.BootstrapFileInput;

public class SetPropertiesPage extends BasePage {

  private static final long serialVersionUID = 1L;


  



  private final static Logger log = LoggerFactory.getLogger(SetPropertiesPage.class);
  
  private FileUploadField fileUpload1,fileUpload2,fileUpload3;


  public static TextArea<String> resultsBox = null;
  private String upload_folder = null;
  private FileUpload uploadedFile1,uploadedFile2,uploadedFile3 = null;
  protected File newFile1;
  protected File newFile = null;
  public PropertiesModel p1 = null;
  private final List<String> sep = new ArrayList<String>();
  List<LocationModel> list = new ArrayList<LocationModel>();
  //protected static String csvfiles_dir = "file: config.properties";
  //protected static String processed_dir = "file: config.properties";
  //protected static String sqlinserts_file = "file: config.properties";
  protected static File sqlFile = null;
  // csv file name
  protected static String name = "";
  
  @SpringBean
  private PropertiesServiceDAO propertiesServiceDAO;
  
  public List<PropertiesModel> propList = new ArrayList<PropertiesModel>();

  private Integer col = 0;


 // final TextField<String> testmode = new TextField<String>("testmode", Model.of(""));
//  final TextField<String> testfile = new TextField<String>("username", Model.of(""));
//  final TextField<String> executeSQLqueries = new TextField<String>("username", Model.of(""));
//  final TextField<String> removeExistingBData = new TextField<String>("username", Model.of(""));
//  final TextField<String> geonamesdebugmode = new TextField<String>("username", Model.of(""));
//  final TextField<String> fieldtypesdebugmode = new TextField<String>("username", Model.of(""));
//  final TextField<String> csvfiles_dir = new TextField<String>("username", Model.of(""));
//  final TextField<String> tmp_dir = new TextField<String>("username", Model.of(""));
//  final TextField<String> processed_dir = new TextField<String>("username", Model.of(""));
//  final TextField<String> missinggeoreference_dir = new TextField<String>("username", Model.of(""));
//  final TextField<String> sqlinserts_file = new TextField<String>("username", Model.of(""));
//  final TextField<String> newformat_dir = new TextField<String>("username", Model.of(""));
//  final TextField<String> enriched_dir = new TextField<String>("username", Model.of(""));
//  final TextField<String> nrowchecks = new TextField<String>("username", Model.of(""));
//  final TextField<String> pvalue_nrowchecks = new TextField<String>("username", Model.of(""));
//  final TextField<String> imageRegex = new TextField<String>("username", Model.of(""));
//  final TextField<String> phoneRegex = new TextField<String>("username", Model.of(""));
//  final TextField<String> cityRegex = new TextField<String>("username", Model.of(""));
//  final TextField<String> archiveRegex = new TextField<String>("username", Model.of(""));
//  final TextField<String> documentRegex = new TextField<String>("username", Model.of(""));
//  final TextField<String> openinghoursRegex = new TextField<String>("username", Model.of(""));
//  final TextField<String> dateRegex = new TextField<String>("username", Model.of(""));
//  final TextField<String> yearRegex = new TextField<String>("username", Model.of(""));
//  final TextField<String> currencyRegex = new TextField<String>("username", Model.of(""));
//  final TextField<String> percentageRegex = new TextField<String>("username", Model.of(""));
//  final TextField<String> nutsRegex = new TextField<String>("username", Model.of(""));
//  final TextField<String> shapeRegex = new TextField<String>("username", Model.of(""));
//  final TextField<String> latitudeRegex = new TextField<String>("username", Model.of(""));
//  final TextField<String> longitudeRegex = new TextField<String>("username", Model.of(""));
//  final TextField<String> latlngRegex = new TextField<String>("username", Model.of(""));
//  final TextField<String> possiblenameRegex = new TextField<String>("username", Model.of(""));
//  final TextField<String> countrycode = new TextField<String>("username", Model.of(""));
//  final TextField<String> shapes_file = new TextField<String>("username", Model.of(""));
//  final TextField<String> geonames_dbdriver = new TextField<String>("username", Model.of(""));
//  final TextField<String> geonames_dburl = new TextField<String>("username", Model.of(""));
//  final TextField<String> geonames_dbusr = new TextField<String>("username", Model.of(""));
//  final TextField<String> geonames_dbpwd = new TextField<String>("username", Model.of(""));
//  final TextField<String> web_dbdriver = new TextField<String>("username", Model.of(""));
//  final TextField<String> web_dburl = new TextField<String>("username", Model.of(""));
//  final TextField<String> web_dbusr = new TextField<String>("username", Model.of(""));
//  final TextField<String> web_dbpwd = new TextField<String>("username", Model.of(""));
//  final TextField<String> st1postcode = new TextField<String>("username", Model.of(""));
//  final TextField<String> st2postcode = new TextField<String>("username", Model.of(""));
//  final TextField<String> st1city = new TextField<String>("username", Model.of(""));
//  final TextField<String> st2city = new TextField<String>("username", Model.of(""));
//  final TextField<String> st3city = new TextField<String>("username", Model.of(""));

  public void prepareComboBoxes() {
    sep.add(",");
    sep.add(";");
  }


  public List<String> showSampleCSV(FileUpload uploadedFile) {
    List<String> sampleRows = null;
    try {
      BufferedReader csvFile = null;
      String datasetFile = upload_folder + uploadedFile.getClientFileName();

      csvFile =
          new BufferedReader(new InputStreamReader(new FileInputStream(datasetFile),
              StandardCharsets.ISO_8859_1));
      String line1 = csvFile.readLine();
      String line2 = csvFile.readLine();
      String line3 = csvFile.readLine();
      csvFile.close();
      sampleRows = Arrays.asList(line1, line2, line3);

    } catch (IOException e) {
      log.error("Error while reading from csv " + e.getMessage());
    }
    return sampleRows;
  }
  


  public SetPropertiesPage(final PageParameters parameters) throws IOException {
	  ServletContext servletContext = WebApplication.get().getServletContext();

	  
	  upload_folder = servletContext.getRealPath("/") + "files/";
	    final WebMarkupContainer wmc = new WebMarkupContainer("wmc");
	    wmc.setVersioned(false);
	    add(wmc);
	  
	  FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
	  feedbackPanel.setOutputMarkupId(true);
	  feedbackPanel.setOutputMarkupPlaceholderTag(true);
	  feedbackPanel.setVisible(false);
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
        //colId.setObject("Column \"" + (col + 1) + "\" : ");
        log.info("SAVETYPE IS  " + col);
      }
    };
    formTypes.add(saveType);
    //saveType.setVisible(false);
    

    

/*

    final Button saveCSV = new Button("saveCSV") {
      private static final long serialVersionUID = 1L;

      @Override
      public void onSubmit() {
        if (uploadedFile1 != null) {

          String field = "";
          String line = null;
          String datasetFile = upload_folder + uploadedFile1.getClientFileName();
          BufferedReader csvFile = null;
          try {
            csvFile =
                new BufferedReader(new InputStreamReader(new FileInputStream(datasetFile),
                    StandardCharsets.ISO_8859_1));
            csvFile.readLine();

            while ((line = csvFile.readLine()) != null) {
              log.info("old format " + line);
              
            }
            csvFile.close();

          } catch (FileNotFoundException e) {

            e.printStackTrace();

          } catch (IOException e) {

            e.printStackTrace();
          }
        }
        
        if (!list.isEmpty()) {
          
          locationServiceDAO.storeLocationModel(list);
          list.clear();
          list = locationServiceDAO.readLocationModel();
          info("Thanks! Your CSV file \"" + uploadedFile1.getClientFileName()
              + "\" was correctly uploaded.");

          formTypes.setVisible(false);
        }
      }
    };
    saveCSV.setVisible(false);
    formTypes.add(saveCSV);
*/


    List<?> sampleRows = Arrays.asList();
    @SuppressWarnings("rawtypes")
    final PropertyListView rowsCsv = new PropertyListView("listview", sampleRows) {

      private static final long serialVersionUID = 1L;

      @Override
      protected void populateItem(ListItem item) {
        item.add(new Label("rowNum", item.getIndex()));
        item.add(new Label("data", item.getModel()));
      }
    };
    rowsCsv.setVisible(false);
    wmc.add(rowsCsv);

    final Label exampleRowsLabel =
        new Label("exampleRowsLabel", "First 3 rows of the CSV file: ");
    exampleRowsLabel.setVisible(false);
    wmc.add(exampleRowsLabel);

    final Form<?> form = new Form<Void>("propertiesForm") {

      private static final long serialVersionUID = 1L;

    };
    wmc.add(form);
    
    form.setMultiPart(true); // need for uploads
    form.setMaxSize(Bytes.megabytes(10)); // max upload size 10mb
    form.add(fileUpload1 = new FileUploadField("fileUpload1"));
    form.add(fileUpload2 = new FileUploadField("fileUpload2"));
    form.add(fileUpload3 = new FileUploadField("fileUpload3"));
    
    final WebSession session = WebSession.get();
    propList = propertiesServiceDAO.readPropertiesModel(session);

    Label admin_user_visibility = new Label("admin_user_visibility");

    ListView<PropertiesModel> editPropList = new ListView<PropertiesModel>("editProperties", propList) {

        private static final long serialVersionUID = 1L;

        @Override
        protected void populateItem(final ListItem<PropertiesModel> item) {


        	item.add(new DropDownChoice<String>("testmode",new PropertyModel<String>(item.getModelObject(), "testmode"), Arrays.asList("true","false")));
            item.add(new TextField<String>("testfile",new PropertyModel<String>(item.getModelObject(),
                    "testfile")));
            item.add(new DropDownChoice<String>("executeSQLqueries",new PropertyModel<String>(item.getModelObject(), "executeSQLqueries"), Arrays.asList("true","false")));
            item.add(new DropDownChoice<String>("removeExistingBData",new PropertyModel<String>(item.getModelObject(), "removeExistingBData"), Arrays.asList("true","false")));
            item.add(new DropDownChoice<String>("geonamesdebugmode",new PropertyModel<String>(item.getModelObject(), "geonamesdebugmode"), Arrays.asList("true","false")));

            item.add(new DropDownChoice<String>("fieldtypesdebugmode",new PropertyModel<String>(item.getModelObject(), "fieldtypesdebugmode"), Arrays.asList("true","false")));

//            item.add(new TextField<String>("csvfiles_dir",new PropertyModel<String>(item.getModelObject(),
//                    "csvfiles_dir")));
//            item.add(new TextField<String>("tmp_dir",new PropertyModel<String>(item.getModelObject(),
//                    "tmp_dir")));
//            item.add(new TextField<String>("processed_dir",new PropertyModel<String>(item.getModelObject(),
//                    "processed_dir")));
//            item.add(new TextField<String>("missinggeoreference_dir",new PropertyModel<String>(item.getModelObject(),
//                    "missinggeoreference_dir")));
//    item.add(new TextField<String>("sqlinserts_file",new PropertyModel<String>(item.getModelObject(),
//                    "sqlinserts_file")));
//    item.add(new TextField<String>("newformat_dir",new PropertyModel<String>(item.getModelObject(),
//                    "newformat_dir")));
    item.add(new TextArea<String>("enriched_dir",new PropertyModel<String>(item.getModelObject(),
                    "enriched_dir")));
    item.add(new DropDownChoice<String>("nrowchecks",new PropertyModel<String>(item.getModelObject(), "nrowchecks"), Arrays.asList("2","3","4","5","6","7","8","9","10","15","20","25","30","35","40","45","50")));
    item.add(new DropDownChoice<String>("pvalue_nrowchecks",new PropertyModel<String>(item.getModelObject(), "pvalue_nrowchecks"), Arrays.asList("0.01","0.05","0.1","0.2","0.3","0.4","0.5","0.6","0.7","0.8","0.9","1")));

    item.add(new TextField<String>("imageRegex",new PropertyModel<String>(item.getModelObject(),
                    "imageRegex")));
    item.add(new TextField<String>("phoneRegex",new PropertyModel<String>(item.getModelObject(),
                    "phoneRegex")));
    item.add(new TextField<String>("cityRegex",new PropertyModel<String>(item.getModelObject(),
                    "cityRegex")));
    item.add(new TextField<String>("archiveRegex",new PropertyModel<String>(item.getModelObject(),
                    "archiveRegex")));
    item.add(new TextField<String>("documentRegex",new PropertyModel<String>(item.getModelObject(),
                    "documentRegex")));
    item.add(new TextField<String>("openinghoursRegex",new PropertyModel<String>(item.getModelObject(),
                    "openinghoursRegex")));
    item.add(new TextField<String>("dateRegex",new PropertyModel<String>(item.getModelObject(),
                    "dateRegex")));
    item.add(new TextField<String>("yearRegex",new PropertyModel<String>(item.getModelObject(),
                    "yearRegex")));
    item.add(new TextField<String>("currencyRegex",new PropertyModel<String>(item.getModelObject(),
                    "currencyRegex")));
    item.add(new TextField<String>("percentageRegex",new PropertyModel<String>(item.getModelObject(),
                    "percentageRegex")));
    item.add(new TextField<String>("nutsRegex",new PropertyModel<String>(item.getModelObject(),
                    "nutsRegex")));
    item.add(new TextField<String>("shapeRegex",new PropertyModel<String>(item.getModelObject(),
                    "shapeRegex")));
    item.add(new TextField<String>("latitudeRegex",new PropertyModel<String>(item.getModelObject(),
                    "latitudeRegex")));
    item.add(new TextField<String>("longitudeRegex",new PropertyModel<String>(item.getModelObject(),
                    "longitudeRegex")));
    item.add(new TextField<String>("latlngRegex",new PropertyModel<String>(item.getModelObject(),
                    "latlngRegex")));
    item.add(new TextField<String>("possiblenameRegex",new PropertyModel<String>(item.getModelObject(),
                    "possiblenameRegex")));
    item.add(new DropDownChoice<String>("countrycode",new PropertyModel<String>(item.getModelObject(), "countrycode"), Arrays.asList(
    		"AF","AX","AL","DZ","AS","AD","AO","AI","AQ","AG","AR","AM","AW","AU","AT","AZ","BS","BH","BD","BB","BY","BE","BZ","BJ","BM","BT","BO","BQ","BA","BW","BV","BR","IO","BN","BG","BF","BI","KH","CM","CA","CV","KY","CF","TD","CL","CN","CX","CC","CO","KM","CG","CD","CK","CR","CI","HR","CU","CW","CY","CZ","DK","DJ","DM","DO","EC","EG","SV","GQ","ER","EE","ET","FK","FO","FJ","FI","FR","GF","PF","TF","GA","GM","GE","DE","GH","GI","GR","GL","GD","GP","GU","GT","GG","GN","GW","GY","HT","HM","VA","HN","HK","HU","IS","IN","ID","IR","IQ","IE","IM","IL","IT","JM","JP","JE","JO","KZ","KE","KI","KP","KR","KW","KG","LA","LV","LB","LS","LR","LY","LI","LT","LU","MO","MK","MG","MW","MY","MV","ML","MT","MH","MQ","MR","MU","YT","MX","FM","MD","MC","MN","ME","MS","MA","MZ","MM","NA","NR","NP","NL","NC","NZ","NI","NE","NG","NU","NF","MP","NO","OM","PK","PW","PS","PA","PG","PY","PE","PH","PN","PL","PT","PR","QA","RE","RO","RU","RW","BL","SH","KN","LC","MF","PM","VC","WS","SM","ST","SA","SN","RS","SC","SL","SG","SX","SK","SI","SB","SO","ZA","GS","SS","ES","LK","SD","SR","SJ","SZ","SE","CH","SY","TW","TJ","TZ","TH","TL","TG","TK","TO","TT","TN","TR","TM","TC","TV","UG","UA","AE","GB","US","UM","UY","UZ","VU","VE","VN","VG","VI","WF","EH","YE","ZM","ZW"
    )));

    item.add(new TextField<String>("shapes_file",new PropertyModel<String>(item.getModelObject(),
                    "shapes_file")));
    
    item.add(admin_user_visibility);
    item.add(new TextField<String>("geonames_dbdriver",new PropertyModel<String>(item.getModelObject(),
                    "geonames_dbdriver")));
    item.add(new TextField<String>("geonames_dburl",new PropertyModel<String>(item.getModelObject(),
                    "geonames_dburl")));
    item.add(new TextField<String>("geonames_dbusr",new PropertyModel<String>(item.getModelObject(),
                    "geonames_dbusr")));
    item.add(new TextField<String>("geonames_dbpwd",new PropertyModel<String>(item.getModelObject(),
                    "geonames_dbpwd")));
    item.add(new TextField<String>("web_dbdriver",new PropertyModel<String>(item.getModelObject(),
                    "web_dbdriver")));
    item.add(new TextField<String>("web_dburl",new PropertyModel<String>(item.getModelObject(),
                    "web_dburl")));
    item.add(new TextField<String>("web_dbusr",new PropertyModel<String>(item.getModelObject(),
                    "web_dbusr")));
    item.add(new TextField<String>("web_dbpwd",new PropertyModel<String>(item.getModelObject(),
                    "web_dbpwd")));
    item.add(new TextField<String>("st1postcode",new PropertyModel<String>(item.getModelObject(),
                    "st1postcode")));
    item.add(new TextField<String>("st2postcode",new PropertyModel<String>(item.getModelObject(),
                    "st2postcode")));
    item.add(new TextField<String>("st1city",new PropertyModel<String>(item.getModelObject(),
                    "st1city")));
    item.add(new TextField<String>("st2city",new PropertyModel<String>(item.getModelObject(),
                    "st2city")));
    item.add(new TextField<String>("st3city",new PropertyModel<String>(item.getModelObject(),
            "st3city")));

        }
      };
      form.add(editPropList);
    
    final Button saveProperties = new Button("saveProperties") {

        private static final long serialVersionUID = 1L;

        @Override
        public void onSubmit() {
          super.onSubmit();
	   	   if(propertiesServiceDAO.readPropertiesModel(session).size() != 0) {
	   		   propertiesServiceDAO.updatePropertiesModel(propList.get(0));
			} else {
				info("Error. No properties defined in database table.");
				feedbackPanel.setVisible(true);
			}
          //editForm.setVisible(false);
        }
      };
     // saveLocation.setVisible(false);
     form.add(saveProperties);
     
     final Button retrieveCKAN = new Button("retrieveCKAN") {

         private static final long serialVersionUID = 1L;

         @Override
         public void onSubmit() {
           super.onSubmit();
           ClassLoader classLoader = getClass().getClassLoader();
           //File file = new File(classLoader.getResource("api_exec.sh").getFile());
           String[] cmd = new String[]{"/bin/sh", classLoader.getResource("api_exec.sh").getFile()};
	       try {
				Process pr = Runtime.getRuntime().exec(cmd);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
         }
       };
       form.add(retrieveCKAN);
       

       
       Model<String> textResult = Model.of("...");
       resultsBox = new TextArea<String>("resultsBox",textResult);
       resultsBox.setOutputMarkupId(true);
       wmc.add(resultsBox);
       final Button runCKAN = new Button("runCKAN") {
    	   

           private static final long serialVersionUID = 1L;

           @Override
           public void onSubmit() {
             try {
          	   
          	   if(propertiesServiceDAO.readPropertiesModel(session).size() != 0) {
          			  MainClass mc = new MainClass(null);
          			  mc.initialize();
  			mc.start(upload_folder, newFile, feedbackPanel, textResult);
  			} else {
  				info("Error. No properties defined in database table.");
  				feedbackPanel.setVisible(true);
  			}
  		} catch (CQLException | ClassNotFoundException | IOException | ParseException | InterruptedException
  				| InvalidParameterException | SQLException | ExecutionException | NumberParseException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		} catch (Exception e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
           }

//           private static final long serialVersionUID = 1L;
//
//           @Override
//           public void onSubmit() {
//             super.onSubmit();
//
//       	   if(propertiesServiceDAO.readPropertiesModel().size() != 0) {
//     		   
//      			  MainClass mc = null;
//				try {
//					mc = new MainClass(null);
//					mc.initialize();
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//  	    			  
//  	    			  
//  	    			ClassLoader loader = Thread.currentThread().getContextClassLoader();
//  	    		    URL url = loader.getResource("ckan_dump");
//  	    		    String path = url.getPath();
//  	    		    File resource_folder = new File(path);
//
//       		    File list[] = resource_folder.listFiles();
//       		   long fileSize = resource_folder.length();
//       		  System.out.println("Directory size in MB is :" + (double)fileSize/(1024*1024));
//       		    for(int i=0; i<list.length; i++){
//       		        //String substring = list[i].getName().substring(0, list[i].getName().indexOf("."));
//       		    	log.info("file type:"+list[i].getName().substring(list[i].getName().length() - 4));
//       		        if(list[i].isFile() && list[i].getName().substring(list[i].getName().length() - 4).contentEquals(".csv")){
//       		                
//       		        	 
//       		             log.info("file to execute is: " + list[i].getName());
//       		            log.info("file directory: " + list[i].getAbsolutePath());
//
//      
//       		   		try {
//						mc.start(path,list[i].getAbsoluteFile(), feedbackPanel, textResult);
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//       		                
//
//       		         }
//       		    }
//       	   
//             
//           }
//         }
       };
       form.add(runCKAN);
       
       if (session.getAttribute("user_name").toString().contentEquals("admin")) {
    	   retrieveCKAN.setVisible(true);
    	   runCKAN.setVisible(true);
    	    admin_user_visibility.setVisible(true);
       } else {
    	   retrieveCKAN.setVisible(false);
    	   runCKAN.setVisible(false);
    	    admin_user_visibility.setVisible(false);
       }
     
     //sqlFile = new File(upload_folder + "/" + processed_dir + "/" + sqlinserts_file);
     //sqlFile = new File(upload_folder + "/" + processed_dir + "/" + sqlinserts_file + "/" + name);// sqlinserts_file + "/" + "name");
       sqlFile = new File(upload_folder + "/processed/sql/" + name);// sqlinserts_file + "/" + "name");

     sqlFile.getParentFile().mkdirs();
     //sqlFile.createNewFile();
     DownloadLink downloadFile = new DownloadLink("downloadFile", sqlFile);
     wmc.add(downloadFile);
     

     final Button executeSearch = new Button("executeSearch") {

         private static final long serialVersionUID = 1L;

         @Override
         public void onSubmit() {
           try {
        	   
        	   if(propertiesServiceDAO.readPropertiesModel(session).size() != 0) {
        			  MainClass mc = new MainClass(null);
        			  mc.initialize();
			mc.start(upload_folder, newFile, feedbackPanel, textResult);
			} else {
				info("Error. No properties defined in database table.");
				feedbackPanel.setVisible(true);
			}
		} catch (CQLException | ClassNotFoundException | IOException | ParseException | InterruptedException
				| InvalidParameterException | SQLException | ExecutionException | NumberParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         }
       };
       form.add(executeSearch);
      // executeSearch.setVisible(true);
    

    final Label selFile1 = new Label("selFile1", "Select Zip file (with all csv files inside): ");
    form.add(selFile1);



    
    

    
    Button uploadFile1 = new Button("uploadFile1") {

      private static final long serialVersionUID = 1L;

      @Override
      public void onSubmit() {
        
        log.info("DIRECTORIO1 " + upload_folder);
        


        uploadedFile1 = fileUpload1.getFileUpload();
        
        
        if (uploadedFile1 != null) {


        	File folder = new File(upload_folder + "/compressed_files/");
        	if(!folder.exists()){
        		folder.mkdir();
        	}
        	
			newFile1 = new File(folder + uploadedFile1.getClientFileName());

			
			
			//newFileTmp = new File(upload_folder + "/compressed_files/" + uploadedFile1.getClientFileName());

          //newFile1.getParentFile().mkdirs();
          //destiantion..getParentFile().mkdirs();


          log.info(">> File is (test mode): " + uploadedFile1.getClientFileName());
          

//          if (newFile1.exists()) {
//            newFile1.delete();
//          }

          try {
//            newFile1.createNewFile();
            
            log.info("were here ");
           // String zipFile = upload_folder + "/compressed_files/" + uploadedFile1.getClientFileName();
            
            //ZipFile zipFile = null;

           // String outputFolder = upload_folder + "/compressed_files/" + uploadedFile1.getClientFileName().substring(0, uploadedFile1.getClientFileName().length() - 4);
            //zipFile = fileUpload1.getFileUpload();
			//newFile1.extractAll(destination);

            
            
            uploadedFile1.writeTo(new File(folder + "/" + uploadedFile1.getClientFileName()));
            
            
            byte[] buffer = new byte[1024];
            
            
          //get the zip file content
        	ZipInputStream zis = 
        		new ZipInputStream(new FileInputStream(new File(folder + "/" + uploadedFile1.getClientFileName())));
        	//get the zipped file list entry
        	ZipEntry ze = zis.getNextEntry();
        		
        	while(ze!=null){
        			
        	   String fileName = ze.getName();
               File newFile = new File(folder + File.separator + fileName);
                    
               System.out.println("file unzip : "+ newFile.getAbsoluteFile());
                    
                //create all non exists folders
                //else you will hit FileNotFoundException for compressed folder
                new File(newFile.getParent()).mkdirs();
                  
                FileOutputStream fos = new FileOutputStream(newFile);             

                int len;
                while ((len = zis.read(buffer)) > 0) {
           		fos.write(buffer, 0, len);
                }
            		
                fos.close();   
                ze = zis.getNextEntry();
        	}
        	
            zis.closeEntry();
        	zis.close();
            log.info("files have been extracted ");
            
            
            
            
            
            
            
            //uploadedFile1.writeTo(newFile1);
            info("Saved file: \"" + folder + "/" + uploadedFile1.getClientFileName() + "\"");
            feedbackPanel.add(AttributeModifier.append("class", "success"));
            feedbackPanel.setVisible(true);
            
     	   if(propertiesServiceDAO.readPropertiesModel(session).size() != 0) {
     		   
    			  MainClass mc = new MainClass(null);
	    			  mc.initialize();

     		    File list[] = folder.listFiles();
     		   long fileSize = folder.length();
     		  System.out.println("Directory size in MB is :" + (double)fileSize/(1024*1024));
     		    for(int i=0; i<list.length; i++){
     		        //String substring = list[i].getName().substring(0, list[i].getName().indexOf("."));
     		    	log.info("file type:"+list[i].getName().substring(list[i].getName().length() - 4));
     		        if(list[i].isFile() && list[i].getName().substring(list[i].getName().length() - 4).contentEquals(".csv")){
     		                
     		        	 
     		             log.info("file to execute is: " + list[i].getName());
     		            log.info("file directory: " + list[i].getAbsolutePath());

    
     		   		mc.start(upload_folder + "/compressed_files/",list[i].getAbsoluteFile(), feedbackPanel, textResult);
     		                

     		         }
     		    }
     		   
     		   

		
		
		} else {
			info("Error. No properties defined in database table.");
			feedbackPanel.setVisible(true);
		}
     	   
     	   
     	   
          } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }


//          List<String> sampleRows = showSampleCSV(uploadedFile1);
//          if (sampleRows != null) {
//            exampleRowsLabel.setVisible(true);
//            rowsCsv.setList(sampleRows);
//            rowsCsv.setVisible(true);
//            fileUpload1.setVisible(true);
//            selFile1.setVisible(true);
//            //Component setVisible = this.setVisible(false);
//          }


        } else {
        	info("You must upload a file with ZIP format.");
        	feedbackPanel.setVisible(true);
        }
      }
    };

    form.add(uploadFile1);
    
    final Label selFile2 = new Label("selFile2", "Select CSV file to test: ");
    form.add(selFile2);

    Button uploadFile2 = new Button("uploadFile2") {

      private static final long serialVersionUID = 1L;

      @Override
      public void onSubmit() {

          log.info("DIRECTORIO2 " + upload_folder);
//          new BufferedReader(new InputStreamReader(new FileInputStream(datasetFile),
//                  StandardCharsets.ISO_8859_1));

        uploadedFile2 = fileUpload2.getFileUpload();
       

        if (uploadedFile2 != null) {
        	
        	File folder = new File(upload_folder);
        	if(!folder.exists()){
        		folder.mkdir();
        	}
        	
        	
          // write to a new file
          newFile = new File(folder +"/"+ uploadedFile2.getClientFileName());
          //newFile2.getParentFile().mkdirs();
          
          log.info(">> File nam222 (test mode): " +  uploadedFile2.getClientFileName());

          
          if (newFile.exists()) {
            newFile.delete();
          }
          try {
            newFile.createNewFile();
            uploadedFile2.writeTo(newFile);
            log.info("Saved file: \"" + uploadedFile2.getClientFileName() + "\"");
            log.info("Saved newFile2: \"" + newFile.getAbsolutePath() + "\"");
          } catch (IOException e) {
            e.printStackTrace();
          } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          
          List<String> sampleRows = showSampleCSV(uploadedFile2);
          if (sampleRows != null) {
            exampleRowsLabel.setVisible(true);
            feedbackPanel.setVisible(false);
            rowsCsv.setList(sampleRows);
            rowsCsv.setVisible(true);
            fileUpload2.setVisible(true);
            selFile2.setVisible(true);
            //Component setVisible = this.setVisible(false);
          }

        } else {
        	info("You must upload a file with CSV format.");
        	feedbackPanel.setVisible(true);
        }
      }
    };
    form.add(uploadFile2);
//    FileUploadPanel fileUpload2 = new FileUploadPanel("fileUpload");
//    form.add(fileUpload2);
    
    
    final IModel<List<FileUpload>> model = new ListModel<FileUpload>();
  
  BootstrapFileInput bootstrapFileInput = new BootstrapFileInput("bootstrapFileinput", model) {

	private static final long serialVersionUID = 1L;
	



	@Override
      protected void onSubmit(AjaxRequestTarget target) {
          super.onSubmit(target);
          
     	  //log.info("Uploaded0: " );
     	 //info("Uploaded1: "  );
//           success("Files successfully processed: " );
//           feedbackPanel.add(AttributeModifier.append("class", "success"));
//            feedbackPanel.setVisible(true);
//            
//            target.add(feedbackPanel);
            
          List<FileUpload> fileUploads = model.getObject();
          if (fileUploads != null) {
              for (FileUpload upload : fileUploads) {

                  
                  
    			  MainClass mc = null;
				try {
					mc = new MainClass(null);
					mc.initialize();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
 		    	log.info("file type:"+upload.getContentType());
 		    	log.info("file size:"+upload.getSize()); 		                
 		        	 
 		             log.info("file to execute is: " + upload.getClientFileName());
 		            log.info("file directory: " + upload.toString());
 		            
 		        	File folder = new File(upload_folder + "/uploads/");
 		        	if(!folder.exists()){
 		        		folder.mkdir();
 		        	}
 		        	
 		            File newFile = new File(folder + "/" + upload.getClientFileName());
 		        	if(newFile.exists()){
 		        		newFile.delete();
 		        	}
 		            try {
						upload.writeTo(newFile);
						mc.start(upload_folder + "/uploads/",newFile, feedbackPanel, textResult);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

 		
// 	            	  log.info("Uploaded: " + upload.getClientFileName());
// 	            	 info("Uploaded1: " + upload.getClientFileName());
 	                  success("Uploaded: " + upload.getClientFileName());
  	                 feedbackPanel.setVisible(true);

 	                 feedbackPanel.add(AttributeModifier.append("class", "success"));
 	                //target.add(feedbackPanel);
 	                wmc.modelChanged();
                  
              }
          } else {
          	info("You must upload a file with CSV format.");
          	feedbackPanel.setVisible(true);
          }
          
          

      }
  };
  bootstrapFileInput.getConfig()
  .allowedFileExtensions(Arrays.asList("csv"))
  //.maxFileCount(5)
  .showUpload(true)
  .showRemove(true);
  bootstrapFileInput.setRenderBodyOnly(true);
  bootstrapFileInput.setOutputMarkupId(true);
  wmc.add(bootstrapFileInput);
  
  
  
//  final Label label = new Label("dropzonelabel","This is a label changed by dropzone");
//  label.setOutputMarkupId(true);
//  wmc.add(label);
//
//  DropZoneFileUpload dropZoneFileUpload = new DropZoneFileUpload("dropzone") {
//      private static final long serialVersionUID = 1L;
//
//	@Override
//	protected void onUpload(AjaxRequestTarget target, Map<String, List<FileItem>> fileMap) {
//        // Handle files
//        System.out.println("Successfully Uploaded: " + fileMap);
//        label.setDefaultModelObject("This is a label changed by dropzone - file uploaded");
//        target.add(label);
//
//	}
//  };
//  dropZoneFileUpload.getConfig()
//      .withMaxFileSize(1)
//      .withAcceptedFiles(".csv,.zip")
//      .withThumbnailHeight(80)
//      .withThumbnailWidth(80)
//      .withPreviewsContainer(".dropzone-previews")
//      .withParallelUploads(2);
//  wmc.add(dropZoneFileUpload);
//  
  
  
  
  
  
    
    final Label selFile3 = new Label("selFile3", "Select SQL file: ");
    form.add(selFile3);

    Button uploadFile3 = new Button("uploadFile3") {

      private static final long serialVersionUID = 1L;

      @Override
      public void onSubmit() {

        log.info("DIRECTORIO3 " + upload_folder);
		  MainClass mc = null;
		try {
			mc = new MainClass(null);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		  try {
			mc.initialize();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		  
		  

        uploadedFile3 = fileUpload3.getFileUpload();
        if (uploadedFile3 != null) {
        	
        	
        	//File folder = new File(upload_folder + "/"+ sqlinserts_file +"/");
        	File folder = new File(upload_folder + "/sql/");
        	if(!folder.exists()){
        		folder.mkdir();
        	}
        	

          // write to a new file
          newFile = new File(folder + uploadedFile3.getClientFileName());
          //newFile3.getParentFile().mkdirs();
          
          log.info(">> File name333 (test mode): " +  uploadedFile3.getClientFileName());
          try {
			uploadedFile3.writeTo(newFile);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
//          //sqlinserts_file = newFile.getAbsolutePath();
//          try {
//			RunSqlScript.runSqlScript(sqlFile);
//		} catch (ClassNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (SQLException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
          if (newFile.exists()) {
              newFile.delete();
            }


//          try {
//            newFile3.createNewFile();
//            uploadedFile3.writeTo(newFile3);
//            log.info("Saved file: \"" + uploadedFile3.getClientFileName() + "\"");
//          } catch (IOException e) {
//            e.printStackTrace();
//          } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//          }


          List<String> sampleRows = showSampleCSV(uploadedFile3);
          if (sampleRows != null) {
            exampleRowsLabel.setVisible(true);
            feedbackPanel.setVisible(false);
            rowsCsv.setList(sampleRows);
            rowsCsv.setVisible(true);
            fileUpload3.setVisible(true);
            selFile3.setVisible(true);
            //Component setVisible = this.setVisible(false);
          }


        } else {
        	info("You must upload a file with CSV format.");
        	feedbackPanel.setVisible(true);
        }
      }
    };

    form.add(uploadFile3);
  }
}
