package com.opendataview.web.heuristicsearch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.validator.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.log4j.PropertyConfigurator;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.geonames.InvalidParameterException;
import org.geotools.coverage.processing.operation.Add;
import org.geotools.filter.text.cql2.CQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.opendataview.web.model.LocationModel;
import com.opendataview.web.model.PropertiesModel;
import com.opendataview.web.pages.index.BasePage;
import com.opendataview.web.pages.properties.SetPropertiesPage;
import com.opendataview.web.persistence.PropertiesServiceDAO;


public class MainClass extends SetPropertiesPage {
	
	


	private static final long serialVersionUID = 1L;

	@SpringBean
	  private static PropertiesServiceDAO propertiesServiceDAO;
	
	  
	  public static List<PropertiesModel> origList = null;

  private static Connection conn = null;

  protected final static Logger log = LoggerFactory.getLogger(MainClass.class);

  protected static boolean testmode = true;
  protected static String test_file = "test.csv";

  public static boolean geonamesdebugmode = false;
  protected static boolean fieldtypesdebugmode = false;
  
  public static boolean removeExistingBData = false;
  public static boolean executeSQLqueries = false;

  protected static int nrowchecks = 20;
  protected static double pvalue_nrowchecks = 0.5;

  protected static int nchecks = 21;
  protected static int ncolchecks = 0;
  protected static String resul[] = null;
  protected static String dict_match[] = null;
  
  protected String sqlfile = null;

  // Delimiters used in CSV files
  protected static String DELIMITER = ";";
  protected static final String DELIMITER2 = ",";
  protected static final String NEW_LINE = "\n";

  protected static StringBuilder outputinfo = new StringBuilder("");

  protected static String country_code = "AT";

  protected static String shapes_file = "/NUTS_2013_SHP/data/NUTS_RG_01M_2013.shp";

  // Directories where files are processed
  protected static String csvfiles_dir = "file: config.properties";

  protected static String tmp_dir = "file: config.properties";

  protected static String newformat_dir = "file: config.properties";
  protected static String enriched_dir = "file: config.properties";
  protected static String missinggeoreference_dir = "file: config.properties";
//  protected static String processed_dir = "file: config.properties";
//  protected static String sqlinserts_file = "file: config.properties";


  protected static String geonames_dbdriver = "org.postgresql.Driver";
  protected static String geonames_dburl = "jdbc:postgresql://127.0.0.1:5432/geonames";
  protected static String geonames_dbusr = "postgres";
  protected static String geonames_dbpwd = "postgres";

  protected static String web_dbdriver = "org.postgresql.Driver";
  protected static String web_dburl = "jdbc:postgresql://127.0.0.1:5432/cvienna";
  protected static String web_dbusr = "postgres";
  protected static String web_dbpwd = "postgres";

  protected static String st1postcode =
      "select name,admin3name,code,latitude,longitude from postalcodes where code like ? order by code asc";
  protected static String st2postcode =
      "select name,admin3name,code,latitude,longitude from postalcodes where code = ? order by code asc";
  protected static String st1city =
      "select geonameid,name,latitude,longitude,population,elevation from geoname where asciiname = ? order by population desc";
  protected static String st2city =
      "select geonameid,name,latitude,longitude,population,elevation from geoname where asciiname like ? or asciiname like ? order by population desc";
  protected static String st3city =
      "select geonameid,name,latitude,longitude,population,elevation from geoname where asciiname like ? or asciiname like ? order by population desc";

  protected static String imageRegex = ".*.(jpg|gif|png|bmp|ico)$";
  protected static String phoneRegex = "^\\+?[0-9. ()-]{10,25}$";
  protected static String archiveRegex = ".*.(zip|7z|bzip(2)?|gzip|jar|t(ar|gz)|dmg)$";
  protected static String documentRegex =
      ".*.(doc(x|m)?|pp(t|s|tx)|o(dp|tp)|pub|pdf|csv|xls(x|m)?|r(tf|pt)|info|txt|tex|x(ml|html|ps)|rdf(a|s)?|owl)$";
  protected static String openinghoursRegex =
      "([a-z ]+ )?(mo(n(day)?)?|tu(e(s(day)?)?)?|we(d(nesday)?)?|th(u(r(s(day)‌​?)?)?)?|fr(i(day)?)?‌​|sa(t(urday)?)?|su(n‌​(day)?)?)(-|:| ).*|([a-z ]+ )?(mo(n(tag)?)?|di(e(n(stag)?)?)?|mi(t(woch)?)?|do(n(er(s(tag)‌​?)?)?)?|fr(i(tag)?)?‌​|sa(m(stag)?)?|do(n(erstag)?)?)(-|:| ).*";
  protected static String dateRegex =
      "([0-9]{2})?[0-9]{2}( |-|\\/|.)[0-3]?[0-9]( |-|\\/|.)([0-9]{2})?[0-9]{2}";
  protected static String yearRegex = "^(?:18|20)\\d{2}$";
  protected static String currencyRegex =
      "^(\\d+|\\d+[.,']\\d+)\\p{Sc}|\\p{Sc}(\\d+|\\d+[.,']\\d+)$";
  protected static String percentageRegex = "^(\\d+|\\d+[.,']\\d+)%|%(\\d+|\\d+[.,']\\d+)$";
  protected static String postcodeRegex = "^[0-9]{2}$|^[0-9]{4}$";
  protected static String nutsRegex = "\\w{3,5}";
  protected static String shapeRegex =
      "point\\s*\\(([+-]?\\d+\\.?\\d+)\\s*,?\\s*([+-]?\\d+\\.?\\d+)\\)";
  protected static String latitudeRegex = "/^-?([1-8]?[1-9]|[1-9]0)\\.{1}\\d{4,9}$/";
  protected static String longitudeRegex =
      "^-?([1]?[1-7][1-9]|[1]?[1-8][0]|[1-9]?[0-9])\\.{1}\\d{4,9}$";
  protected static String latlngRegex = "([+-]?\\d+\\.?\\d+)\\s*,\\s*([+-]?\\d+\\.?\\d+)";
  protected static String possiblenameRegex = ".*[0-9]+.*";
  protected static String cityRegex = ".*[a-z]{3,30}.*";



  private static String header[] = null;
  
  private static String columnTypes[][] = null;
  private static String values[][] = null;
  private static int sum[][] = null;

  private static String[] tmp_cities = null;
  private static String[] tmp_possiblenames = null;
  private static boolean[] tmp_postcol = null;

  private static String line;
  private static int comma = 0;
  private static int dotcomma = 0;
  
  protected static String code_found = "";
  protected static boolean executed = false;
  protected static WebSession session = WebSession.get();
  
//  public static void main(String[] args) {
//	  
//	  String log4jConfPath = "/src/main/resources/log4j.properties";
//	  PropertyConfigurator.configure(log4jConfPath);
//	  
//	  log.info("Hello World");
//  }
  
  
	public MainClass(PageParameters parameters) throws IOException {
		super(parameters);
		// TODO Auto-generated constructor stub
	}
	
	public void initialize() throws IOException {
		  origList = new ArrayList<PropertiesModel>();
		  origList = propertiesServiceDAO.readPropertiesModel(session);
		  

		     /////////////// IMPORTANT //////////////////
		     // Sets all property values from database //
		     ReadPropertyValues.getPropValues();	   //
		     ////////////////////////////////////////////
	}
  
  public static void setFormatLatLng(LocationModel obj, String field) {
    String newFormat = "";
    boolean startCopying = false;
    boolean isLatitudeRead = false;

    for (int i = 0; i < field.length(); ++i) {
      char x = field.charAt(i);
      if (x == '(') {
        startCopying = !startCopying;
      }
      if (x != '(' && startCopying && !isLatitudeRead) {
        newFormat += x;
      }
      if (startCopying && x == ' ') {
        obj.setLongitude(new BigDecimal(new Double(newFormat), new MathContext(
            newFormat.length() - 2)));
        newFormat = "";
        isLatitudeRead = true;
      }
      if (x != ' ' && x != ')' && isLatitudeRead) {
        newFormat += x;
      }
      if (x == ')') {
        obj.setLatitude(new BigDecimal(new Double(newFormat), new MathContext(
            newFormat.length() - 2)));
        startCopying = !startCopying;
      }
    }
  }

  public static void printLog(int sum[][], int n, int m, String vartype) {
	  
      if (testmode || fieldtypesdebugmode || geonamesdebugmode) {
    	  outputinfo.append(sum[m][n] + " \""+vartype+"\" in col " + n+"\n");
    	  log.info(sum[m][n] + " \""+vartype+"\" in col " + n);
      } 
  }
  
  public static void findFieldTypes(String dir, String name, Model<String> textResult) throws NumberFormatException,
      CQLException, IOException, NumberParseException, SQLException, URISyntaxException {

    br = new BufferedReader(new InputStreamReader(new FileInputStream(dir + name), "iso-8859-1"));

    double confidence_limit = ((nrowchecks-1) * (100 - pvalue_nrowchecks * 100)) / 100;
    
//	  for(int i=0; i<nrowchecks; i++) {
//		  for(int j=0; j<ncolchecks; j++) {
//			  columnTypes[i][j] = "";
//		  }
//	  }

    int i = 0;
    comma = 0;
    dotcomma = 0;
    while ((line = br.readLine()) != null && i < nrowchecks) {
    	
    	
    	
    //log.info("\n\nLine read: "+line.toString());
    	
      if (i == 0) {
        log.info("\n--------------------------------------------------------------------------------------------------------");
        outputinfo.append("\n--------------------------------------------------------------------------------------------------------");

        
        //File separator detection (between comma or dot-comma)
        for (int j = 0; j < line.length(); j++) {
          if (line.charAt(j) == ',')
            comma++;
          if (line.charAt(j) == ';')
            dotcomma++;
        }
        if (dotcomma > comma) {
          DELIMITER = ";";
        } else {
          DELIMITER = ",";
        }

        // First header row (line 0)
        if (line != null) {

          // split by delimiter with unlimited tokens having empty space
          ncolchecks = line.split("\\" + DELIMITER, -1).length;
          // if (testmode || fieldtypesdebugmode || geonamesdebugmode) {

      	  log.info(">> FILE: " + name);
      	  outputinfo.append("\n>> FILE: " + name);
          log.info(">> NCOLUMNS: " + ncolchecks);
          outputinfo.append("\n>> NCOLUMNS: " + ncolchecks);
          log.info(">> DELIMITER: \"" + DELIMITER + "\"");
          outputinfo.append("\n>> DELIMITER: \"" + DELIMITER + "\"\n\n");
          
          
          
          header = new String[ncolchecks];
          String[]  value = new String[ncolchecks];
          
          value = line.split(DELIMITER);
          
          //Capitalize first letter of each header value
//          for (int j = 0; j < value.length; j++) {
//        	  if(value[j].length()>0) {
//        		  header[j] = value[j].substring(0, 1).toUpperCase() + value[j].substring(1).toLowerCase();
//        	  } else {
//        		  header[j] = value[j];
//        	  }
//              
//          }
          


          // exception for some files with a NONE defined header in the first raw
          if (ncolchecks < 10)
            ncolchecks = 10;


          columnTypes = new String[nrowchecks][ncolchecks];
          values = new String[nrowchecks][ncolchecks];
          

                 
          sum = new int[nchecks][ncolchecks];

          tmp_cities = new String[ncolchecks];
          tmp_possiblenames = new String[ncolchecks];
          tmp_postcol = new boolean[ncolchecks];
          resul = new String[ncolchecks*2]; //we duplicate the number of columns just to be sure we don't end up with a indexoutofbound fault
          dict_match = new String[ncolchecks*2];
          
          String uncap_value = "";
          //Detect header (row 0) via Regex Dictionary
          for (int j = 0; j < value.length; j++) {
        	  if(value[j].length()>0) {
        		  uncap_value = value[j].toLowerCase();
        		  
        		  //capitalize only the first letter of each header value (line 0)
        		  header[j] = value[j].substring(0, 1).toUpperCase() + value[j].substring(1).toLowerCase();

        		  //log.info("VALUE TO CHECK: "+uncap_value);
	        	  if(uncap_value.matches("^(.*nombre.*|.*titulo.*|.*name.*|.*title.*)$")) {
	        		  dict_match[j] = "possible names";
	        		  //log.info("WE HAVE DETECTED NAME: "+resul[j]);
	        	  }
	        	  if(uncap_value.matches("^(lat|latitude)$")) {
	        		  dict_match[j] = "latitudes";
	        		  //log.info("WE HAVE DETECTED LAT: "+resul[j]);
	        	  }
	        	  if(uncap_value.matches("^(lng|lon|long|longitude)$")) {
	        		  dict_match[j] = "longitudes";
	        		  //log.info("WE HAVE DETECTED LONG: "+resul[j]);
	        	  }
	        	  if(uncap_value.matches("^(tel|telf|telefone|telefono)$")) {
	        		  dict_match[j] = "phones";
	        		  //log.info("WE HAVE DETECTED TEL: "+resul[j]);
	        	  }
	        	  if(uncap_value.matches("^(email|mail|correo)$")) {
	        		  dict_match[j] = "emails";
	        		  //log.info("WE HAVE DETECTED EMAIL: "+resul[j]);
	        	  }
	        	  if(uncap_value.matches("^(web|url|site|www)$")) {
	        		  dict_match[j] = "urls";
	        		  //log.info("WE HAVE DETECTED EMAIL: "+resul[j]);
	        	  }
        	  } else {
        		  header[j] = value[j];
        	  }
          }
          log.info(">> HEADER: \"" + java.util.Arrays.toString(header) + "\"");
          outputinfo.append("\n>> HEADER: \"" + java.util.Arrays.toString(header) + "\"\n\n");
        }
        
      } else if (i < nrowchecks) {
    	  
    	 
          //###########################################################################################################################
          // CANDIDATE VALUES (X/Y) ARE GOING TO BE CHECKED FOR A SAME FILE - UNTIL [i] ROWS TO CHECK [nrowchecks]
          // CHECK ARE DONE UNDER X NUMBER OF ROWS [nrowchecks] AND X NUMBER REPETITIONS [pvalue_nrowchecks]
    	  // CONSIDERING confidence_limit = ((nrowchecks-1) * (100 - pvalue_nrowchecks * 100)) / 100;
          //###########################################################################################################################

        // log.info("line: " + i + " -------------------------------------------------");
        // log.info("before.." + line);
        if (DELIMITER.contentEquals(",")) {
          boolean inQuotes = false;

          String str = line;
          String copy = new String();
          for (int k = 0; k < str.length(); ++k) {
            if (str.charAt(k) == '"')
              inQuotes = !inQuotes;
            if (str.charAt(k) == ',' && inQuotes)
              copy += ' ';
            else
              copy += str.charAt(k);
          }

          line = copy;

        } else {
          line = line.replaceAll(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", ",");
        }

        String[] linex = line.split(DELIMITER);



        // ParameterizedSparqlString qs =
        // new ParameterizedSparqlString(""
        // + "prefix rdfs:    <http://www.w3.org/2000/01/rdf-schema#>\n" + "\n"
        // + "select ?resource where {\n" + "  ?resource rdfs:label ?label\n" + "}");

        int j = 0;
        boolean latitude_visited = false;
        
        while (j < linex.length) {



          // Literal val = ResourceFactory.createLangLiteral(value[j], "en");
          // qs.setParam("val1", val);

          // System.out.println(qs);
          // String val = value[j].replaceAll("", "").toLowerCase();

          String val = linex[j].toLowerCase().replaceAll("\"", "").trim();
          values[i][j] = val;

          if (testmode || fieldtypesdebugmode || geonamesdebugmode)
        	  //log.info("check: " + val);


          if (val.matches(phoneRegex)) {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            // try {
            PhoneNumber phone = phoneUtil.parse(val, country_code);

            if (phoneUtil.isValidNumber(phone)) {
            	  log.info("Phone found for val "+val+ " with code "+country_code);
              columnTypes[i][j] = "phones";
            }
            // } catch (NumberParseException e) {
            // // System.err.println("NumberParseException was thrown: " + e.toString());
            // }
          } else if (EmailValidator.getInstance().isValid(val)) {
            // log.info("we have an email ! in fil " + i + " and col " + j);
            columnTypes[i][j] = "emails";
          } else if (UrlValidator.getInstance().isValid(val)
              || UrlValidator.getInstance().isValid("http://" + val)) {
            if (val.matches(imageRegex)) {
              columnTypes[i][j] = "images";
            } else if (val.matches(archiveRegex)) {
              columnTypes[i][j] = "archives";
            } else if (val.matches(documentRegex)) {
              columnTypes[i][j] = "documents";
            } else {
              columnTypes[i][j] = "urls";
            }
          } else if (val.matches(openinghoursRegex)) {
            columnTypes[i][j] = "opening hours";
          } else if (val.matches(dateRegex)) {

            columnTypes[i][j] = "dates";
          } else if (val.matches(yearRegex)) {
            columnTypes[i][j] = "years";
          } else if (val.replaceAll(" ", "").matches(currencyRegex)) {
            columnTypes[i][j] = "currencies";
          } else if (val.replaceAll(" ", "").matches(percentageRegex)) {
            columnTypes[i][j] = "percentages";
          } else if (val.matches(postcodeRegex)) {
        	//  conn = DriverManager.getConnection(geonames_dburl, geonames_dbusr, geonames_dbpwd);
            rs = GeonamesSQLQueries.getPostcodeLatLng(Integer.valueOf(val), conn);
            if (rs != null) {
              // log.info("WE HAVE A POSTCODE!!! with name " + rs.getString(2) + " for[" + val
              // + "] , latitude is " + rs.getString(3) + " and longitude is " + rs.getString(4)
              // + " in line " + i);
              columnTypes[i][j] = "postcodes";
            }
            //rs.close();
            //conn.close();
          } else if (val.matches(nutsRegex)) {
            // log.info("value is  " + val);
            double[] latlng = ReadGISShapes.getNutsLatLng(val.toUpperCase());
            if (latlng != null) {
              // log.info("result is  " + latlng[0]);
              switch (val.length()) {
                case 3:
                  columnTypes[i][j] = "nuts1";
                  break;
                case 4:
                  columnTypes[i][j] = "nuts2";
                  break;
                case 5:
                  columnTypes[i][j] = "nuts3";
                  break;
              }
              // log.info("WE HAVE NUTS!!! with latitude is " + latlng[0] + " and longitude is "
              // + latlng[1] + " in line " + i);
            }

          } else if (val.matches(shapeRegex)) {
            columnTypes[i][j] = "shapes";
          } else if (val.matches(latitudeRegex) && latitude_visited == false) {
            columnTypes[i][j] = "latitudes";
            latitude_visited = true;
          } else if (val.matches(longitudeRegex) && latitude_visited == true) {
            columnTypes[i][j] = "longitudes";
            latitude_visited = false;
          } else if (val.matches(latlngRegex)) {
            columnTypes[i][j] = "latlong";
          //} else if (val.matches(cityRegex) && !val.matches("^(other|city|district|state)$")) {
          } else if (val.matches(cityRegex)) {
              outputinfo.append("\n");
              log.info("\n");
        	//conn = DriverManager.getConnection(geonames_dburl, geonames_dbusr, geonames_dbpwd);
            rs = GeonamesSQLQueries.getCityLatLng(i,val, conn, columnTypes, values, country_code);
            if (rs != null) {
              if (tmp_cities[j] == null) {
            	  log.info("??????????????? we have city in column: "+j);
                columnTypes[i][j] = "cities";
                tmp_cities[j] = val;
//                 log.info("WE HAVE A CITY!!! with name " + rs.getString(2) + " for[" + val
//                 + "] , latitude is " + rs.getString(3) + " and longitude is " + rs.getString(4)
//                 + " in line " + i + " and column "+j);
              } else if (!tmp_cities[j].contentEquals(val)) {
            	  log.info("??? we have city in column: "+j);
                // log.info("for line " + j + " tmp_cities[j] is " + tmp_cities[j] + " with val "
                // + val);
                columnTypes[i][j] = "cities";
                tmp_cities[j] = val;
//                log.info("WE HAVE A CITY!!! with name " + rs.getString(2) + " for[" + val
//                + "] , latitude is " + rs.getString(3) + " and longitude is " + rs.getString(4)
//                + " in line " + i + " and column "+j);
              }
				//rs.close();
				//conn.close();

            }

            // ParameterizedSparqlString qs =
            // new ParameterizedSparqlString(
            // "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> PREFIX dbo: <http://dbpedia.org/ontology/> PREFIX foaf: <http://xmlns.com/foaf/0.1/> select distinct ?C ?Long ?Lat where {?X geo:lat ?Lat ; geo:long ?Long; a ?C . { { ?X foaf:name \""
            // + val + "\"@en. }  } } LIMIT 100 ");
            //
            //
            // QueryExecution exec =
            // QueryExecutionFactory
            // .sparqlService("http://dbpedia.org/sparql", qs.asQuery());
            //
            //
            //
            // ResultSet results = exec.execSelect();
            //
            // // while (results.hasNext()) {
            // // // As RobV pointed out, don't use the `?` in the variable
            // // // name here. Use *just* the name of the variable.
            // // System.out.println(results.next().get("resource"));
            // // }
            //
            // ResultSetFormatter.out(results);
          } else if (!val.matches(possiblenameRegex)) {

              // we check that possible names are not repeated
              if (tmp_possiblenames[j] == null) {
                columnTypes[i][j] = "possible names";
                tmp_possiblenames[j] = val;
              } else if (!tmp_possiblenames[j].contentEquals(val)) {
                // log.info("for line " + j + " tmp_possiblenames[j] is " + tmp_possiblenames[j]
                // + " with val " + val);
                columnTypes[i][j] = "possible names";
                tmp_possiblenames[j] = val;
              }
            }
          j++;
        }
        
        //################################################################################################################
        // SUM-UP ALL CANDIDATE VALUES (X/Y) - UNTIL [i] ROWS TO CHECK [nrowchecks]
        // STORES IN [sum] THE NUMBER OF COLMN (Y) MATCHES OF THE MATCHING LIST (X)
        // i.e. WE COULD HAVE 5 emails AND 5 telephone IN COLUMN 0
        //################################################################################################################

//        for (int k = 1; k < nrowchecks; k++) {
//          for (int l = 0; l < ncolchecks; l++) {
//            // log.info("[" + k + "][" + l + "]:" + columnTypes[k][l] + "; ");
//          }
//        }


        for (int m = 0; m < nchecks; m++) {
          for (int n = 0; n < ncolchecks; n++) {
            sum[m][n] = 0;
          }
        }

        for (int w = 1; w < i; w++) {
            int nmails = 0;
            int nurls = 0;
            int nphones = 0;
            int ncities = 0;
            int npostcodes = 0;
            int nophours = 0;
            int ndates = 0;
            int nyears = 0;
            int nimages = 0;
            int narchives = 0;
            int ndocuments = 0;
            int ncurrencies = 0;
            int npercentages = 0;
            int nshapes = 0;
            int nlatitudes = 0;
            int nlongitudes = 0;
            int nlatlong = 0;
            int nnuts1 = 0;
            int nnuts2 = 0;
            int nnuts3 = 0;
            int npossiblenames = 0;
          for (int z = 0; z < ncolchecks; z++) {

        	 // log.info("for fila "+w+" and columna "+z+" tenemos tipo "+columnTypes[w][z]);
            if (columnTypes[w][z] != null) {
              switch (columnTypes[w][z]) {
                case "emails":
                  nmails++;
                  sum[0][z] += 1;
                  break;
                case "urls":
                  nurls++;
                  sum[1][z] += 1;
                  break;
                case "phones":
                  nphones++;
                  sum[2][z] += 1;
                  break;
                case "cities":
                  ncities += 1;
                  sum[3][z] += 1;
                  //log.info("WE HAVE STORED "+ sum[3][l] + " CITIES IN COLUNN "+l);
                  break;
                case "postcodes":
                  int trueCount = 0;
                  for (int t = 0; t < tmp_postcol.length; t++) {
                    if (tmp_postcol[t] == true)
                      trueCount++;
                    if (trueCount == 3)
                      break;
                  }
                  // avoid columns which have matched more than 3 postcodes
                  if (trueCount != 3) {
                    npostcodes++;
                    sum[4][z] += 1;
                    tmp_postcol[z] = true;
                  }
                  break;
                case "opening hours":
                  nophours++;
                  sum[5][z] += 1;
                  break;
                case "dates":
                  ndates++;
                  sum[6][z] += 1;
                  break;
                case "year":
                  nyears++;
                  sum[7][z] += 1;
                  break;
                case "images":
                  nimages++;
                  sum[8][z] += 1;
                  break;
                case "archives":
                  narchives++;
                  sum[9][z] += 1;
                  break;
                case "documents":
                  ndocuments++;
                  sum[10][z] += 1;
                  break;
                case "currencies":
                  ncurrencies++;
                  sum[11][z] += 1;
                  break;
                case "percentages":
                  npercentages++;
                  sum[12][z] += 1;
                  break;
                case "shapes":
                  nshapes++;
                  sum[13][z] += 1;
                  break;
                case "latitudes":
                  nlatitudes++;
                  sum[14][z] += 1;
                  break;
                case "longitudes":
                  nlongitudes++;
                  sum[15][z] += 1;
                  break;
                case "latlong":
                  nlatlong++;
                  sum[16][z] += 1;
                  break;
                case "nuts1":
                  nnuts1++;
                  sum[17][z] += 1;
                  break;
                case "nuts2":
                  nnuts2++;
                  sum[18][z] += 1;
                  break;
                case "nuts3":
                  nnuts3++;
                  sum[19][z] += 1;
                  break;
                case "possible names":
                  npossiblenames++;
                  sum[20][z] += 1;
                  break;
              }
            }
          }
          
          
          
          
          
          
          
          //################################################################################################################
          // SAME ROW - PRINTS TOTAL NUMBER OF MATCHES DONE IN THE SAME ROW - UNTIL [i] ROWS TO CHECK [nrowchecks]
          //################################################################################################################
          
          if (fieldtypesdebugmode && w==i-1) {
            log.info("\nline["+i+"]["+j+"]: " + line);
            outputinfo.append("\nline["+i+"]["+j+"]: " + line);
            
            
        	//textResult..setObject("epa..............");
            if (testmode || geonamesdebugmode) {
            	log.info(nmails + " emails, " + nurls + " urls, " + nphones
                        + " phones, " + ncities + " cities, " + npostcodes + " postcodes, " + nophours
                        + " opening hours, " + ndates + " dates, " + nyears + " years, " + nimages
                        + " images, " + narchives + " archives, " + ndocuments + " documents, "
                        + ncurrencies + " currencies, " + npercentages + " percentages, " + nshapes
                        + " shapes, " + nlatitudes + " latitudes, " + nlongitudes + " longitudes, "
                        + nlatlong + " latlong, " + nnuts1 + " nuts1, " + nnuts2 + " nuts2, " + nnuts3
                        + " nuts3, " + npossiblenames + " possible names");
            	outputinfo.append("\n" + nmails + " emails, " + nurls + " urls, " + nphones
                        + " phones, " + ncities + " cities, " + npostcodes + " postcodes, " + nophours
                        + " opening hours, " + ndates + " dates, " + nyears + " years, " + nimages
                        + " images, " + narchives + " archives, " + ndocuments + " documents, "
                        + ncurrencies + " currencies, " + npercentages + " percentages, " + nshapes
                        + " shapes, " + nlatitudes + " latitudes, " + nlongitudes + " longitudes, "
                        + nlatlong + " latlong, " + nnuts1 + " nuts1, " + nnuts2 + " nuts2, " + nnuts3
                        + " nuts3, " + npossiblenames + " possible names\n");
            }

            //#############################################################################################################################
            // SAME ROW - PRINTS EACH MATCH [nchecks] DONE FOR EACH COLUMN [ncolchecks] IN SAME ROW - UNTIL [i] ROWS TO CHECK [nrowchecks]
            //#############################################################################################################################
            
            for (int x = 0; x < nchecks; x++) {
            for (int y = 0; y < ncolchecks; y++) {

                if (sum[x][y] > 0) {

                  switch (x) {
                    case 0:
                    	printLog(sum,y,x,"email");
                      break;
                    case 1:
                        printLog(sum,y,x,"urls");
                        break;
                      case 2:
                      	printLog(sum,y,x,"phone");
                        break;
                      case 3:
                      	printLog(sum,y,x,"cities");
                        break;
                      case 4:
                      	printLog(sum,y,x,"postcodes");
                        break;
                      case 5:
                      	printLog(sum,y,x,"opening hours");
                        break;
                      case 6:
                      	printLog(sum,y,x,"dates");
                        break;
                      case 7:
                      	printLog(sum,y,x,"years");
                        break;
                      case 8:
                      	printLog(sum,y,x,"images");
                        break;
                      case 9:
                      	printLog(sum,y,x,"archives");
                        break;
                      case 10:
                      	printLog(sum,y,x,"documents");
                        break;
                      case 11:
                      	printLog(sum,y,x,"currencies");
                        break;
                      case 12:
                      	printLog(sum,y,x,"percentages");
                        break;
                      case 13:
                      	printLog(sum,y,x,"shapes");
                        break;
                      case 14:
                      	printLog(sum,y,x,"latitudes");
                        break;
                      case 15:
                      	printLog(sum,y,x,"longitudes");
                        break;
                      case 16:
                      	printLog(sum,y,x,"latlong");
                        break;
                      case 17:
                      	printLog(sum,y,x,"nuts1");
                        break;
                      case 18:
                      	printLog(sum,y,x,"nuts2");
                        break;
                      case 19:
                      	printLog(sum,y,x,"nuts3");
                        break;
                      case 20:
                      	printLog(sum,y,x,"possible names");
                        break;
                  } //switch (m)

                } //if (sum[m][n] != 0)
              } //for (int n = 0; n < ncolchecks; n++) 
            } //for (int m = 0; m < nchecks; m++)
          } //if (fieldtypesdebugmode && k==1)
          
          
          
          
          
          

        } //for (int k = 1; k < nrowchecks; k++)

      } //if (i < nrowchecks) 
//      if (i == nrowchecks) {
//    	    break;
//      }
      i++;
    }
    
    //################################################################################################################
    // PRINTS THE FINAL RESULT - TOTAL NUMBER OF MATCHES [nchecks] DONE FOR EACH COLUMN [ncolchecks]
    //################################################################################################################
    
    if (testmode || fieldtypesdebugmode || geonamesdebugmode) {
    	log.info("\n\nFINAL RESULT (NRows:"+(nrowchecks-1)+", PVal:"+pvalue_nrowchecks+", Confidence:"+confidence_limit+"):");
    	outputinfo.append("\n\nFINAL RESULT (NRows:"+(nrowchecks-1)+", PVal:"+pvalue_nrowchecks+", Confidence:"+confidence_limit+"):\n");
    }
    

    
    outputinfo.append("\nFields detected without using the Header\n");
	log.info("\nFields detected without using the Header\n");
    for (int m = 0; m < nchecks; m++) {
      for (int n = 0; n < ncolchecks; n++) {

    	 //log.info("==> OF '"+columnTypes[m][n]+"' WE HAVE "+sum[m][n]);
        if (sum[m][n] >= confidence_limit) {

          switch (m) {
            case 0:
              printLog(sum,n,m,"email");
              resul[n] = "emails";
              break;
            case 1:
              printLog(sum,n,m,"urls");
              resul[n] = "urls";
              break;
            case 2:
            	printLog(sum,n,m,"phone");
              resul[n] = "phones";
              break;
            case 3:
            	if (ArrayUtils.contains(resul,"cities")) {
            		int ind_first = ArrayUtils.indexOf(resul, "cities");
            		int first = sum[3][ArrayUtils.indexOf(resul, "cities")];
            		int second = sum[3][n];
            		log.info("first one has: "+first+ " and second has "+second);
            		if (second>first) {
                    	printLog(sum,n,m,"cities");
                        
            		    log.info("----> first city with "+first+" \"cities\" has been removed");
            		    log.info(resul[ind_first]+" for index ["+ind_first+"] has been set to null");
            			resul[ind_first] = null;
            			resul[n] = "cities";
            		} else {
            		    log.info("----> second city with "+second+" \"cities\" for index ["+n+"] has been removed");
            		    log.info("we keep first city with index ["+ind_first+"]");
            			resul[n] = null;
            			resul[ind_first] = "cities";
            		}
            	} else {
                	printLog(sum,n,m,"cities");
                    resul[n] = "cities";
            	}

              break;
            case 4:
            	printLog(sum,n,m,"postcodes");
              resul[n] = "postcodes";
              break;
            case 5:
            	printLog(sum,n,m,"opening hours");
              resul[n] = "openinghours";
              break;
            case 6:
            	printLog(sum,n,m,"dates");
              resul[n] = "dates";
              break;
            case 7:
            	printLog(sum,n,m,"years");
              resul[n] = "years";
              break;
            case 8:
            	printLog(sum,n,m,"images");
              resul[n] = "images";
              break;
            case 9:
            	printLog(sum,n,m,"archives");
              resul[n] = "archives";
              break;
            case 10:
            	printLog(sum,n,m,"documents");
              resul[n] = "documents";
              break;
            case 11:
            	printLog(sum,n,m,"currencies");
              resul[n] = "currencies";
              break;
            case 12:
            	printLog(sum,n,m,"percentages");
              resul[n] = "percentages";
              break;
            case 13:
            	printLog(sum,n,m,"shapes");
              resul[n] = "shapes";
              break;
            case 14:
            	printLog(sum,n,m,"latitudes");
              resul[n] = "latitudes";
              break;
            case 15:
            	printLog(sum,n,m,"longitudes");
              resul[n] = "longitudes";
              break;
            case 16:
            	printLog(sum,n,m,"latlong");
              resul[n] = "latlong";
              break;
            case 17:
            	printLog(sum,n,m,"nuts1");
              resul[n] = "nuts1";
              break;
            case 18:
            	printLog(sum,n,m,"nuts2");
              resul[n] = "nuts2";
              break;
            case 19:
            	printLog(sum,n,m,"nuts3");
              resul[n] = "nuts3";
              break;
            case 20:
            	printLog(sum,n,m,"possible names");
              resul[n] = "possible names";
              break;
          }

        }
      }
    }
    
    outputinfo.append("\nFields detected from the Header\n");
	log.info("\nFields detected from the Header\n");
    for (int n = 0; n < ncolchecks; n++) {
  	  
    	if(dict_match[n] != null) {
       // if ((resul[n] == null && dict_match[n] != null) || dict_match[n] == "latitudes" || dict_match[n] == "longitudes" || dict_match[n] == "latlong" || dict_match[n] == "shapes") {
        	//we replace value giving more importance to the header
        	  
        	//(dict_match[n] == "possible names" && resul[n] != "city") || 
//      	  if (dict_match[n] == "possible names" && ArrayUtils.contains(resul,"possible names")) {
//    		  resul[ArrayUtils.indexOf(resul,"possible names")] = null;
//    	  }
        	
        	

        	for(int k=0; k<resul.length; k++) {
        		if (resul[k] == dict_match[n]) {
        			resul[k] = null;
        		}
        	}
        	resul[n] = dict_match[n];
        	  
            if (testmode || fieldtypesdebugmode || geonamesdebugmode) {
          	  outputinfo.append("\""+dict_match[n]+"\" in col " + n + "\n");
          	  log.info("\""+dict_match[n]+"\" in col " + n + "\n");
            } 
          
          }
        }
    


    textResult.setObject(outputinfo.toString());
    
  }


  public static void generateLocationAndFile(File source, File dest) throws IOException, ParseException,
      CQLException, InterruptedException, InvalidParameterException, SQLException, URISyntaxException {

    BufferedReader br =
        new BufferedReader(new InputStreamReader(new FileInputStream(source), "iso-8859-1"));

    ArrayList<LocationModel> new_format = new ArrayList<LocationModel>();


    Pattern patternCsvName = Pattern.compile("([A-Z].*|[a-zA-Z0-9]{2,20}).csv");
    Matcher matcher = patternCsvName.matcher(name);
    String csvname_match = null;
    if (matcher.find()) {
      // log.info("CSV Type: " + matcher.group());
      csvname_match = matcher.group().split(".csv")[0];
    }

    String line;

    // i is the row position
    int i = 0;

    boolean onlyfirstcity = false;
    while ((line = br.readLine()) != null) {


      if (DELIMITER.contentEquals(",")) {
        boolean inQuotes = false;

        String str = line;
        String copy = new String();
        for (int k = 0; k < str.length(); ++k) {
          if (str.charAt(k) == '"')
            inQuotes = !inQuotes;
          if (str.charAt(k) == ',' && inQuotes)
            copy += ' ';
          else
            copy += str.charAt(k);
        }

        line = copy;
        // System.out.println(line);

      } else {
        line = line.replaceAll(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", ",");
      }

      // we split the row by ";" separator
      String[] value = line.split(DELIMITER);

      // for each row we create an instance of the Model
      LocationModel loc = new LocationModel();
      
      //Defaul values for the location
      loc.setUser(session.getAttribute("user_name").toString());
      String date = new SimpleDateFormat("yyyy-MM-dd HH:mm").toString();
      loc.setDatePublished(date);
      loc.setDateUpdated(date);

      // j is the column position
      int j = 0;


      //log.info("value..." + value[j]);
      //log.info("value length..." + value.length);
      while (j < value.length) {
    	  

	    	  //log.info("WE ARE IN COLUMN..."+j+" HAVING RESUL "+resul[j]);
	    	  
	    	  
	        // type is a regex of 20 chars or any capital letter start from the CSV name
	        if (csvname_match != null) {
	          loc.setType(csvname_match);
	
	        }
	
	        loc.setCsvName(name);

	        if (header[j] != null && !value[j].isEmpty()) {
	            if (loc.getOther() == null) {
	                loc.setOther(header[j]+": " + value[j]);
	              } else {
	                loc.setOther(loc.getOther() + " ## "+header[j]+": " + value[j]);
	              }
	      	}
//	    	} else {
//	    		log.info("valor header es "+header[j]);
//	    		log.info("valor loc.getOther() es "+loc.getOther());
//			      if (loc.getOther() == null) {
//			        loc.setOther("no_match[" + j + "]: " + value[j]);
//			      } else {
//			        loc.setOther(loc.getOther() + " ## no_match[" + j + "]: " + value[j]);
//			      }
//	    	}
	        
	        
	        
	        //log.info("index is "+j+ " with size "+resul.length);
	        if (resul[j] != null && !value[j].trim().isEmpty()) {
	          switch (resul[j]) {
	            case "source":
		              loc.setSource(value[j]);
		              break;
	            case "emails":
	              loc.setEmail(value[j]);
	              break;
	            case "urls":
	            	if(value[j].matches("^(https?|ftp)://.*$")) {
	            		loc.setWebsite(value[j]);
	            	} else {
	            		loc.setWebsite("http://"+value[j]);
	            	}
	              break;
	            case "phones":
	              loc.setPhone(value[j]);
	              break;
	            case "cities":
	            	log.info("WE HAVE A CITY TO ADD!! "+value[j]);
	
	              //boolean haslatlng = false;
	              
	              
	              
	
	              if (!ArrayUtils.contains(resul,"possible names")) {
	            	  loc.setName(value[j]);
	              }
	              
	              
	//              for (int p = 0; p < nrowchecks; p++) {
	//                for (int q = 0; q < ncolchecks; q++) {
	//                  if (columnTypes[p][q] == "latitudes" || columnTypes[p][q] == "latlong"
	//                      || columnTypes[p][q] == "shape") {
	//                    haslatlng = true;
	//                    break;
	//                  }
	//                }
	//              }
	              
	              if (!ArrayUtils.contains(resul,"latitudes") && !ArrayUtils.contains(resul,"latlong") && !ArrayUtils.contains(resul,"shape")) {
	//            	  haslatlng = true;
	//              }
	//              if (!haslatlng) {
	            	log.info("Querying city value..." + value[j]);
	            	//conn = DriverManager.getConnection(geonames_dburl, geonames_dbusr, geonames_dbpwd);
	                rs = GeonamesSQLQueries.getCityLatLng(j,value[j], conn, columnTypes, values, country_code);
	                if (rs != null) {
	                  // if (tmp_cities[j] != null) {
	                  GeonamesSQLQueries.setGeonameResult(loc, rs);
	//                  rs.close();
	//                  conn.close();
	                  // }
	                }
	              }
	              break;
	            case "postcodes":
	              loc.setPostcode(value[j]);
	
	//              haslatlng = false;
	//              for (int p = 0; p < nrowchecks; p++) {
	//                for (int q = 0; q < ncolchecks; q++) {
	//                  if (columnTypes[p][q] == "latitudes" || columnTypes[p][q] == "latlong"
	//                      || columnTypes[p][q] == "shape") {
	//                    haslatlng = true;
	//                    break;
	//                  }
	//                }
	//              }
	              if (value[j].matches(postcodeRegex) &&!ArrayUtils.contains(resul,"latitudes") && !ArrayUtils.contains(resul,"latlong") && !ArrayUtils.contains(resul,"shape")) {
	
	              //if (!haslatlng && value[j].matches(postcodeRegex)) {
	            	  log.info("Querying postcode value..." + value[j]);
	            //	  conn = DriverManager.getConnection(geonames_dburl, geonames_dbusr, geonames_dbpwd);
	                rs = GeonamesSQLQueries.getPostcodeLatLng(Integer.valueOf(value[j]), conn);
	                if (rs != null) {
	                  // if (tmp_cities[j] != null) {
	                  GeonamesSQLQueries.setGeonameResult(loc, rs);
	//                  rs.close();
	//                  conn.close();
	                  // }
	                }
	              }
	              break;
	            case "opening hours":
	              loc.setSchedule(value[j]);
	              break;
	            case "dates":
	              loc.setDate(value[j]);
	              break;
	            case "years":
	              // loc.setYear(value[j]);
	              break;
	            case "images":
	              loc.setImage(value[j]);
	              break;
	            case "archives":
	              // loc.setArchive(value[j]);
	              break;
	            case "documents":
	              // loc.setDocument(value[j]);
	              break;
	            case "currencies":
	              // loc.setCurrency(value[j]);
	              break;
	            case "percentages":
	              // loc.setPercentage(value[j]);
	              break;
	            case "shapes":
	              setFormatLatLng(loc, value[j]);
	              break;
	            case "latitudes":
	              if (NumberUtils.isNumber(value[j].replace(",", "."))) {
	                loc.setLatitude(new BigDecimal(value[j].replace(",", ".")));
	              } else {
	                log.info("NOT NUMBERIC, latitude is " + value[j]);
	              }
	              break;
	            case "longitudes":
	              if (NumberUtils.isNumber(value[j].replace(",", "."))) {
	                loc.setLongitude(new BigDecimal(value[j].replace(",", ".")));
	              } else {
	                log.info("NOT NUMBERIC, longitude is " + value[j]);
	              }
	              break;
	            case "latlong":
	              setFormatLatLng(loc, value[j]);
	              break;
	            case "nuts1":
	              double[] latlng = ReadGISShapes.getNutsLatLng(value[j]);
	              if (latlng != null) {
	                loc.setLatitude(new BigDecimal(latlng[0]));
	                loc.setLongitude(new BigDecimal(latlng[1]));
	              }
	              break;
	            case "nuts2":
	              latlng = ReadGISShapes.getNutsLatLng(value[j]);
	              if (latlng != null) {
	                loc.setLatitude(new BigDecimal(latlng[0]));
	                loc.setLongitude(new BigDecimal(latlng[1]));
	              }
	              break;
	            case "nuts3":
	              latlng = ReadGISShapes.getNutsLatLng(value[j]);
	              if (latlng != null) {
	                loc.setLatitude(new BigDecimal(latlng[0]));
	                loc.setLongitude(new BigDecimal(latlng[1]));
	              }
	              break;
	            case "possible names":
	              loc.setName(value[j]);
	              break;
	          }


          
          
          
        }
	    

        j++;
      }


      if (i != 0) {
        if (loc.getName() == null) {
          loc.setName(csvname_match);
        }

        new_format.add(loc);
      }

      i++;
    }
    br.close();

    if (dest.getAbsolutePath().contains(tmp_dir)) {
      GenerateFile.CreateEnrichedCSV(source, dest, new_format);
    }

    GenerateFile.CreateFormattedCSV(dest, new_format);

  }

  static int no_insert_bestfiles = 0;
  static int no_insert_nutsfiles = 0;
  static int no_insert_possiblefiles = 0;

  private static java.sql.ResultSet rs;

  private static BufferedReader br;

private static EntityManagerFactory entityManagerFactory;



  public static void createDir(String dir) throws IOException {
    File folder = new File(dir);
    if (folder.exists()) {
      FileUtils.forceDelete(folder);
      folder.mkdir();
    } else {
      folder.mkdir();
    }
  }
//  public static void main(String[] args) throws IOException, ParseException, CQLException,
//  InterruptedException, InvalidParameterException, SQLException, ExecutionException,
//  NumberParseException, ClassNotFoundException {
  public void start(String upload_folder, File uploadedFile, FeedbackPanel feedbackPanel, Model<String> textResult) throws Exception {

    

    
    ApplicationContext context = new ClassPathXmlApplicationContext(
            "classpath*:**/applicationContext.xml");
    
    
    
    
    
    
    
    

    //ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

//    PropertiesModel p = new PropertiesModel();
// 	p.setTestmode("false");
// 	p.setTestfile("httpckan.data.ktn.gv.atstoragef20140630T133A123A02.832Zsteuergem12.csv");
// 	
// 	p.setRemoveExistingBData("true");
// 	p.setExecuteSQLqueries("false");
// 	
// 	p.setGeonamesdebugmode("false");
// 	p.setFieldtypesdebugmode("false");
// 	
// 	p.setCsvfiles_dir("/Users/david/Desktop/at_dump_v1/wwdagvat/");
// 	p.setTmp_dir("/Users/david/Desktop/at_dump_v1/wwdagvat/tmp/");
// 	p.setProcessed_dir("/Users/david/Desktop/at_dump_v1/wwdagvat/tmp/processed/");
// 	p.setNewformat_dir("/Users/david/Desktop/at_dump_v1/wwdagvat/tmp/processed/new_format/");
// 	p.setEnriched_dir("/Users/david/Desktop/at_dump_v1/wwdagvat/tmp/processed/enriched/");
// 	p.setMissinggeoreference_dir("/Users/david/Desktop/at_dump_v1/wwdagvat/tmp/processed/discarded_files/");
// 	p.setSqlinserts_file("/Users/david/Desktop/at_dump_v1/wwdagvat/tmp/processed/sql_inserts.sql");
// 	
// 	     	p.setNrowchecks("20");
// 	     	p.setPvalue_nrowchecks("0.3");
// 	     	p.setImageRegex(".*.(jpg|gif|png|bmp|ico)$");
// 	     	p.setPhoneRegex("^\\\\+?[0-9. ()-]{10,25}$");
// 	     	p.setCityRegex(".*[a-z]{3,30}.*");
// 	     	p.setArchiveRegex(".*.(zip|7z|bzip(2)?|gzip|jar|t(ar|gz)|dmg)$");
// 	     	p.setDocumentRegex(".*.(doc(x|m)?|pp(t|s|tx)|o(dp|tp)|pub|pdf|csv|xls(x|m)?|r(tf|pt)|info|txt|tex|x(ml|html|ps)|rdf(a|s)?|owl)$");
// 	     	p.setOpeninghoursRegex("([a-z ]+ )?(mo(n(day)?)?|tu(e(s(day)?)?)?|we(d(nesday)?)?|th(u(r(s(day)\\u200C\\u200B?)?)?)?|fr(i(day)?)?\\u200C\\u200B|sa(t(urday)?)?|su(n\\u200C\\u200B(day)?)?)(-|:| ).*|([a-z ]+ )?(mo(n(tag)?)?|di(e(n(stag)?)?)?|mi(t(woch)?)?|do(n(er(s(tag)\\u200C\\u200B?)?)?)?|fr(i(tag)?)?\\u200C\\u200B|sa(m(stag)?)?|do(n(erstag)?)?)(-|:| ).*");
// 	     	p.setDateRegex("([0-9]{2})?[0-9]{2}( |-|\\\\/|.)[0-3]?[0-9]( |-|\\\\/|.)([0-9]{2})?[0-9]{2}");
// 	     	p.setYearRegex("^(?:18|20)\\\\d{2}$");
// 	     	p.setCurrencyRegex("^(\\\\d+|\\\\d+[.,']\\\\d+)\\\\p{Sc}|\\\\p{Sc}(\\\\d+|\\\\d+[.,']\\\\d+)$");
// 	     	p.setPercentageRegex("^(\\\\d+|\\\\d+[.,']\\\\d+)%|%(\\\\d+|\\\\d+[.,']\\\\d+)$");
// 	     	p.setPostcodeRegex("^[0-9]{2}$|^[0-9]{4}$");
// 	     	p.setNutsRegex("\\\\w{3,5}");
// 	     	
// 	     	p.setShapeRegex("point\\\\s*\\\\(([+-]?\\\\d+\\\\.?\\\\d+)\\\\s*,?\\\\s*([+-]?\\\\d+\\\\.?\\\\d+)\\\\)");
// 	     	p.setLatitudeRegex("^-?([1-8]?[1-9]|[1-9]0)\\\\.{1}\\\\d{4,9}$");
// 	     	p.setLongitudeRegex("^-?([1]?[1-7][1-9]|[1]?[1-8][0]|[1-9]?[0-9])\\\\.{1}\\\\d{4,9}$");
// 	     	p.setLatlngRegex("([+-]?\\\\d+\\\\.?\\\\d+)\\\\s*,\\\\s*([+-]?\\\\d+\\\\.?\\\\d+)");
// 	     	p.setPossiblenameRegex(".*[0-9]+.*");
//
// 	p.setCountrycode("AT");
// 	p.setShapes_file("/NUTS_2013_SHP/data/NUTS_RG_01M_2013.shp");
//
// 	p.setGeonames_dbdriver("org.postgresql.Driver");
// 	p.setGeonames_dburl("jdbc:postgresql://127.0.0.1:5432/geonames");
// 	p.setGeonames_dbusr("postgres");
// 	p.setGeonames_dbpwd("postgres");
// 	
// 	p.setWeb_dbdriver("web_dbdriver=org.postgresql.Driver");
// 	p.setWeb_dburl("jdbc:postgresql://127.0.0.1:5432/spatialdatasearch");
// 	p.setWeb_dbusr("postgres");
// 	p.setWeb_dbpwd("postgres");
//
//p.setSt1postcode("select p.name,admin3name,code,p.latitude,p.longitude,g.population,g.elevation from postalcodes p inner join geoname g on p.admin3 = g.admin3 where code like ? order by code asc;");
//p.setSt2postcode("select p.name,admin3name,code,p.latitude,p.longitude,g.population,g.elevation from postalcodes p inner join geoname g on p.admin3 = g.admin3 where code = ? order by code asc;");
//p.setSt1city("select geonameid,name,latitude,longitude,population,elevation from geoname where asciiname = ? order by population desc;");
//p.setSt2city("select geonameid,name,latitude,longitude,population,elevation from geoname where asciiname like ? or asciiname like ? order by population desc;");
//p.setSt3city("select geonameid,name,latitude,longitude,population,elevation from geoname where asciiname like ? or asciiname like ? order by population desc;");
//
//    
//
//
//try {
//    entityManagerFactory = Persistence.createEntityManagerFactory("spatialdatasearch");
//    EntityManager entityManager = entityManagerFactory.createEntityManager();
//    entityManager.getTransaction().begin();
//    entityManager.persist(p);
//    entityManager.getTransaction().commit();
//    System.out.println("successfull");
//    entityManager.close();
//} catch (Exception e) {
//    e.printStackTrace();
//}



//entityManager.createQuery("delete from routes").executeUpdate();

    
    	
    try {
      Class.forName(geonames_dbdriver);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      return;
    }


    try {
      conn = DriverManager.getConnection(geonames_dburl, geonames_dbusr, geonames_dbpwd);
    } catch (SQLException e) {
      log.info("Connection Failed! Check output console");
      e.printStackTrace();
      return;
    }

    if (conn != null) {
      log.info("Connected to DB !");
    } else {
      log.info("Failed to make DB connection!");
      conn.close();
    }

    
  //  if (!executeSQLqueries) {

    log.info("Creating directories...");




//    createDir(tmp_dir);
//    createDir(processed_dir);
//    createDir(newformat_dir);
//    createDir(enriched_dir);
//    createDir(missinggeoreference_dir);

    File folderToSearch = new File(csvfiles_dir);

    log.info("----------------------"
    		+ "Test mode is........ "+testmode);
    
    log.info("geonames_dbdriver is........ "+geonames_dbdriver);
    


    //log.info(" DIRECTORIO " + upload_folder);
    
    //log.info("directory is........ "+upload_folder);
    
   // ServletContext servletContext = WebApplication.get().getServletContext();
    
    
    
    //log.info(">>FILE IS "+sqlFile.getAbsolutePath());
    

    
    
    

    // PrintWriter out_sql_bestfiles_buffer = new PrintWriter(sql_bestfiles_buffer);
    // PrintWriter out_sql_nutsfiles_buffer = new PrintWriter(sql_nutsfiles_buffer);
    // PrintWriter out_sql_geofiles_buffer = new PrintWriter(sql_geofiles_buffer);

    log.info("Processing files...");

    nrowchecks += 1; // skip header

    log.info(">> TEST MODE: " + testmode);
    outputinfo.append(">> TEST MODE: " + testmode);
    log.info(">> GEONAMES DEBUG MODE: " + geonamesdebugmode);
    outputinfo.append("\n>> GEONAMES DEBUG MODE: " + geonamesdebugmode);

    
    BufferedWriter bw_sql_inserts = null;

    

        if (testmode) {
        	
        	if(uploadedFile != null) {
        	
        	//name = servletContext.getRealPath("/") + "temp_files/"+setPropertiesPage.getDefaultModelObjectAsString("hola").uploadedFile2.getClientFileName();
        	
        		
        		name = uploadedFile.getName();
        	
        	
        	
        	
        	log.info(">> OTHER TEST File name : " + test_file);
        	

        	
         // if (name.contentEquals(test_file)) {
        	  
        	  if (name.contains(".csv")) {

        		  //csvfiles_dir
            findFieldTypes(upload_folder, name, textResult);
            
            //File f = new File(name);
            
//            File csvFile = uploadedFile2.writeToTempFile();
//            File copyFile = new File(upload_folder + "/" + tmp_dir + "/" + name);
//            copyFile.getParentFile().mkdirs();
//            copyFile.createNewFile();
//            GenerateCSVFiles.copyFile(csvFile, copyFile);
            
            log.info(">>>>>>>> upload folder : " + upload_folder);
            log.info(">>>>>>>> tmp folder : " + tmp_dir);
            log.info(">>>>>>>> name file : " + name);
            /*File copiedFile = new File(upload_folder + "/" + tmp_dir + "/" + name);
            copiedFile.getParentFile().mkdirs();
            copiedFile.createNewFile();
            log.info(">>>>>>>> FILE GENERATED name : " + copiedFile.getName());
            log.info(">>>>>>>> FILE GENERATED path : " + copiedFile.getPath());
            log.info(">>>>>>>> FILE GENERATED path : " + copiedFile.getAbsolutePath());
            log.info(">>>>>>>> FILE copied absolute  : " + copiedFile.getAbsoluteFile());
            log.info(">>>>>>>> FILE uploadedFile2 absolute  : " + upload_folder + uploadedFile2.getAbsolutePath());
            uploadedFile2.m.writeTo(copiedFile.getAbsoluteFile());*/
            
            //File processedFile = new File(upload_folder + "/" + processed_dir + "/" + newformat_dir + "/" + name);
            File processedFile = new File(upload_folder + "/processed/structured/" + name);
            processedFile.getParentFile().mkdirs();
            processedFile.createNewFile();
            
            File newFile = new File(upload_folder + "/" + name);
            
            log.info(">>>>>>>> newFile path : " + newFile.getAbsolutePath());
            log.info(">>>>>>>> processedFile path  : " + processedFile.getAbsoluteFile());
            
            if(newFile.exists()) {
            	generateLocationAndFile(newFile, processedFile);
            }
            
            

            
            
            
//            File newFile = new File(upload_folder+"/"+newformat_dir +"/"+ name);
//            newFile.getParentFile().mkdirs();
            //newFile.createNewFile();
            
            
            //File sqlFile = new File(upload_folder + "/" + processed_dir + "/" + sqlinserts_file + "/" + name.replace("csv", "sql"));
            File sqlFile = new File(upload_folder + "/processed/sql/" + name.replace("csv", "sql"));

            log.info(">>>>>>>> newSQLFile path : " + newFile.getAbsolutePath());
            log.info(">>>>>>>> processedSQLFile path  : " + sqlFile.getAbsoluteFile());
            
            sqlFile.getParentFile().mkdirs();
            sqlFile.createNewFile();

            if(sqlFile.exists()) {
                bw_sql_inserts = new BufferedWriter(new FileWriter(sqlFile));
                log.info(">>>>>>>> before processedFile path  : " + processedFile.getAbsoluteFile());
                GenerateFile.createSQLInserts(processedFile,bw_sql_inserts);
                sqlfile = sqlFile.getAbsoluteFile().toString();
                log.info(">>>>>>>> after sqlfile path  : " + sqlFile.getAbsoluteFile());
                
                if (executeSQLqueries) {
                    //sqlinserts_file = sqlfile;

                    Class.forName(web_dbdriver);
                    Connection conn = DriverManager.getConnection(web_dburl, web_dbusr, web_dbpwd);
                    RunSqlScript.runSqlScript(sqlFile.getAbsoluteFile(), conn, removeExistingBData);
                  }
            } else {
            	log.info("No existing SQL file!!!");
            }
            bw_sql_inserts.close();
            
            //String sqlfile = sqlFile.getAbsoluteFile().getPath();
            //bw_sql_inserts.close();
           //DownloadLink downloadFile = new DownloadLink("downloadFile", sqlFile);
            //wmc.add(downloadFile);
            
        	  }
         // }
        	} else {
        		log.info("no test file selected");
        		feedbackPanel.info("No file has been uploaded! Please try again.");
        		feedbackPanel.setVisible(true);
        	}
        	  

        }

        if (!testmode) {
        	
        	log.info("NOT IN TEST MODE");
        	
            for (File file : folderToSearch.listFiles()) {

                name = file.getName();

                if (name.contains(".csv")) {
          findFieldTypes(csvfiles_dir, name, textResult);
          GenerateFile.copyFile(file, new File(tmp_dir + name));
          generateLocationAndFile(file, new File(newformat_dir + name));
          GenerateFile.createSQLInserts(new File(newformat_dir + name),
              bw_sql_inserts);
            }
            }
        }
      

    
 // }


    
    if (rs != null) 
        rs.close(); 
    if (conn != null) 
        conn.close(); 
    log.info("Finished! :)");


  }
}
