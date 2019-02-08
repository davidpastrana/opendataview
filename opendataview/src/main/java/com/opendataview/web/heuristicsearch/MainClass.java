package com.opendataview.web.heuristicsearch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.geonames.InvalidParameterException;
import org.geotools.filter.text.cql2.CQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.opendataview.web.model.LocationModel;
import com.opendataview.web.model.PropertiesModel;
import com.opendataview.web.pages.properties.SetPropertiesPage;
import com.opendataview.web.persistence.PropertiesServiceDAO;

public class MainClass extends SetPropertiesPage {

	static PageParameters parameters;

	public MainClass(PageParameters parameters) throws IOException {
		super(parameters);
	}

	private static final long serialVersionUID = 1L;

	public static List<PropertiesModel> origList = null;

	private static Connection conn = null;

	protected final static Logger log = LoggerFactory.getLogger(MainClass.class);

	protected static String googleMapsAPI = "";
	protected static String test_file = "test.csv";

	public static boolean geonamesdebugmode = false;
	protected static boolean fieldtypesdebugmode = false;

	public static boolean autodetectSchema = false;
	public static boolean executeSQLqueries = false;

	protected static int nrowchecks = 20;
	protected static double pvalue_nrowchecks = 0.5;

	protected static int nchecks = 26;
	protected static int ncolchecks = 0;

	protected static String resul[] = null;
	protected static String dict_match[] = null;
	protected static String dict_static[] = null;

	protected static String sqlfile = null;

	// Delimiters used in CSV files
	protected static String DELIMITER = ";";
	protected static final String DELIMITER2 = ",";
	protected static final String NEW_LINE = "\n";

	protected static StringBuilder outputinfo = new StringBuilder("");

	protected static String icon_marker = "01";
	protected static String country_code = "AT";

	// Directories where files are processed
	protected static String csvfiles_dir = "file: config.properties";

	protected static String tmp_dir = "file: config.properties";

	protected static String newformat_dir = "file: config.properties";
	protected static String dictionary_matches = "file: config.properties";
	protected static String missinggeoreference_dir = "file: config.properties";
	protected static String active_dictionary = "file: config.properties";
//  protected static String sqlinserts_file = "file: config.properties";

	protected static String geonames_dbdriver = "org.postgresql.Driver";
	protected static String geonames_dburl = "jdbc:postgresql://127.0.0.1:5432/geonames";
	protected static String geonames_dbusr = "postgres";
	protected static String geonames_dbpwd = "postgres";

	protected static String web_dbdriver = "org.postgresql.Driver";
	protected static String web_dburl = "jdbc:postgresql://127.0.0.1:5432/cvienna";
	protected static String web_dbusr = "postgres";
	protected static String web_dbpwd = "postgres";

	protected static String st1postcode = "select name,admin3name,code,latitude,longitude from postalcodes where code like ? order by code asc";
	protected static String st2postcode = "select name,admin3name,code,latitude,longitude from postalcodes where code = ? order by code asc";
	protected static String st1city = "select geonameid,name,latitude,longitude,population,elevation from geoname where asciiname = ? order by population desc";
	protected static String st2city = "select geonameid,name,latitude,longitude,population,elevation from geoname where asciiname like ? or asciiname like ? order by population desc";
	protected static String st3city = "select geonameid,name,latitude,longitude,population,elevation from geoname where asciiname like ? or asciiname like ? order by population desc";

	protected static String imageRegex = ".*.(jpg|gif|png|bmp|ico)$";
	protected static String phoneRegex = "^\\+?[0-9. ()-]{10,25}$";
	protected static String archiveRegex = ".*.(zip|7z|bzip(2)?|gzip|jar|t(ar|gz)|dmg)$";
	protected static String documentRegex = ".*.(doc(x|m)?|pp(t|s|tx)|o(dp|tp)|pub|pdf|csv|xls(x|m)?|r(tf|pt)|info|txt|tex|x(ml|html|ps)|rdf(a|s)?|owl)$";
	protected static String openinghoursRegex = "([a-z ]+ )?(mo(n(day)?)?|tu(e(s(day)?)?)?|we(d(nesday)?)?|th(u(r(s(day)‌​?)?)?)?|fr(i(day)?)?‌​|sa(t(urday)?)?|su(n‌​(day)?)?)(-|:| ).*|([a-z ]+ )?(mo(n(tag)?)?|di(e(n(stag)?)?)?|mi(t(woch)?)?|do(n(er(s(tag)‌​?)?)?)?|fr(i(tag)?)?‌​|sa(m(stag)?)?|do(n(erstag)?)?)(-|:| ).*";
	protected static String dateRegex = "([0-9]{2})?[0-9]{2}( |-|\\/|.)[0-3]?[0-9]( |-|\\/|.)([0-9]{2})?[0-9]{2}";
	protected static String yearRegex = "^(?:18|20)\\d{2}$";
	protected static String currencyRegex = "^(\\d+|\\d+[.,']\\d+)\\p{Sc}|\\p{Sc}(\\d+|\\d+[.,']\\d+)$";
	protected static String percentageRegex = "^(\\d+|\\d+[.,']\\d+)%|%(\\d+|\\d+[.,']\\d+)$";
	protected static String postcodeRegex = "^[0-9]{2}$|^[0-9]{4}$";
	protected static String nutsRegex = "\\w{3,5}";
	protected static String shapeRegex = "point\\s*\\(([+-]?\\d+\\.?\\d+)\\s*,?\\s*([+-]?\\d+\\.?\\d+)\\)";
	protected static String latitudeRegex = "/^-?([1-8]?[1-9]|[1-9]0)\\.{1}\\d{4,9}$/";
	protected static String longitudeRegex = "^-?([1]?[1-7][1-9]|[1]?[1-8][0]|[1-9]?[0-9])\\.{1}\\d{4,9}$";
	protected static String latlngRegex = "([+-]?\\d+\\.?\\d+)\\s*,\\s*([+-]?\\d+\\.?\\d+)";
	protected static String possiblenameRegex = ".*[0-9]+.*";
	protected static String descriptionRegex = ".*[0-20]+.*";
	protected static String cityRegex = ".*[a-z]{3,30}.*";

	private static String header[] = null;

	private static String column_types[][] = null;
	private static String file_values[][] = null;
	private static int sum[][] = null;

	private static String[] tmp_cities = null;
	private static String[] tmp_possiblenames = null;
	private static boolean[] tmp_postcol = null;

	private static String line;
	private static int comma = 0;
	private static int dotcomma = 0;

	protected static String code_found = "";
	protected static boolean executed = false;

//  public static void main(String[] args) {
//	  
//	  String log4jConfPath = "/src/main/resources/log4j.properties";
//	  PropertyConfigurator.configure(log4jConfPath);
//	  
//	  log.info("Hello World");
//  }

	public static void initialize(String user, PropertiesServiceDAO propertiesServiceDAO) throws IOException {

		log.info("WHEN INITIALIZE USER SESSION IS " + user);
		origList = new ArrayList<PropertiesModel>();
		origList = propertiesServiceDAO.readPropertiesModel(user);

		/////////////// IMPORTANT //////////////////
		// Sets all property values from database //
		ReadPropertyValues.getPropValues(); //
		////////////////////////////////////////////
	}

	public static void setFormatLatLng(LocationModel obj, String field) {
		String newFormat = "";
		boolean startCopying = false;
		boolean isLatitudeRead = false;

		field = field.replaceAll("\"", "");
		for (int i = 0; i < field.length(); ++i) {
			char x = field.charAt(i);
			if (x == '(') {
				startCopying = !startCopying;
			}
			if (x != '(' && startCopying && !isLatitudeRead) {
				newFormat += x;
			}
			if (startCopying && x == ' ') {
				obj.setLongitude(new BigDecimal(new Double(newFormat), new MathContext(newFormat.length() - 2)));
				newFormat = "";
				isLatitudeRead = true;
			}
			if (x != ' ' && x != ')' && isLatitudeRead) {
				newFormat += x;
			}
			if (x == ')') {
				// newFormat = newFormat.replace(")", "");
				obj.setLatitude(new BigDecimal(new Double(newFormat), new MathContext(newFormat.length() - 2)));
				startCopying = !startCopying;
			}
		}
	}

	public static void printLog(int sum[][], int n, int m, double confidence_limit, String vartype) {

		if (sum[m][n] >= confidence_limit) {
			outputinfo.append(sum[m][n] + " \"" + vartype + "\" in col \"" + n + "\" already accepted! " + sum[m][n]
					+ " >= " + confidence_limit + "\n");
			log.info(sum[m][n] + " \"" + vartype + "\" in col \"" + n + "\" already accepted! " + sum[m][n] + " >= "
					+ confidence_limit + "\n");
		} else {
			outputinfo.append(sum[m][n] + " \"" + vartype + "\" in col \"" + n + "\" NOT accepted: " + sum[m][n]
					+ " <= " + confidence_limit + "\n");
			log.info(sum[m][n] + " \"" + vartype + "\" in col \"" + n + "\" NOT accepted: " + sum[m][n] + " <= "
					+ confidence_limit + "\n");
		}
	}

	public static void findFieldTypes(String dir, String name, Model<String> textResult) throws NumberFormatException,
			CQLException, IOException, NumberParseException, SQLException, URISyntaxException {

		// br = new BufferedReader(new InputStreamReader(new FileInputStream(dir +
		// name), "iso-8859-1"));
		br = new BufferedReader(new InputStreamReader(new FileInputStream(dir + name), "ISO-8859-1"));

		BufferedReader reader = new BufferedReader(new FileReader(dir + name));
		int nlines = 0;
		while (reader.readLine() != null) {
			nlines++;
		}
		reader.close();

		double confidence_limit = ((nrowchecks - 1) * (100 - pvalue_nrowchecks * 100)) / 100;

		int i = 0;
		int inh = 0;
		comma = 0;
		dotcomma = 0;

		for (int m = 0; m < nchecks; m++) {
			for (int n = 0; n < ncolchecks; n++) {
				sum[m][n] = 0;

			}
		}

		// we only read the given number of rows (our analysis to go through)
		while ((line = br.readLine()) != null && i < nrowchecks) {

			// log.info("\n\nLine read: "+line.toString());

			if (i == 0) {

				log.info("WE ARE IN HEADER WITH I:=" + i);

				// File separator detection (between comma or dot-comma)
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

					log.info(">> File: " + name);
					outputinfo.append(">> File: " + name);
					log.info(">> Columns: " + ncolchecks);
					outputinfo.append("\n>> Columns: " + ncolchecks);
					log.info(">> Rows: " + nlines);
					outputinfo.append("\n>> Rows: " + nlines);
					log.info(">> Delimiter: [ " + DELIMITER + " ]");
					outputinfo.append("\n>> Delimiter: [ " + DELIMITER + " ]");
					log.info(">> Auto detect field types: " + autodetectSchema);
					outputinfo.append("\n>> Auto detect field types: " + autodetectSchema);
					log.info(">> Schema enabled: " + active_dictionary);
					outputinfo.append("\n>> Schema enabled: " + active_dictionary);
					log.info(">> Schema mappings:\n[" + dictionary_matches + "]");
					outputinfo.append("\n>> Schema mappings:\n[" + dictionary_matches + "]");
					log.info(">> Field types debug: " + fieldtypesdebugmode);
					outputinfo.append("\n>> Field types debug: " + fieldtypesdebugmode);
					log.info(">> Queries debug: " + geonamesdebugmode);
					outputinfo.append("\n>> Queries debug: " + geonamesdebugmode);
					log.info(">> Temp directory: " + dir);
					outputinfo.append("\n>> Temp directory: " + dir);

					header = new String[ncolchecks];
					String[] value = new String[ncolchecks];

					value = line.split(DELIMITER);

					// Capitalize first letter of each header value
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

					column_types = new String[nrowchecks][ncolchecks];
					file_values = new String[nrowchecks][ncolchecks];

					sum = new int[nchecks][ncolchecks];

					tmp_cities = new String[ncolchecks];
					tmp_possiblenames = new String[ncolchecks];
					tmp_postcol = new boolean[ncolchecks];
					resul = new String[ncolchecks * 2]; // we duplicate the number of columns just to be sure we don't
														// end up with a indexoutofbound fault
					dict_match = new String[ncolchecks * 2];
					dict_static = new String[50]; // maximum number of fields to store from the config schema

					// Detect header (row 0) via Regex Dictionary
					String first;
					String second;
					String[] schema_config;
					for (int hfile = 0; hfile < value.length; hfile++) { // each value of the csv header
						if (value[hfile].length() > 0) {

							// capitalize only the first letter of each header value (line 0)
							header[hfile] = value[hfile].substring(0, 1).toUpperCase()
									+ value[hfile].substring(1).toLowerCase();

							if (Boolean.valueOf(active_dictionary) == true && dictionary_matches.contains(":")) {

								// log.info("VALUE TO CHECK!!: "+header_value);

								schema_config = dictionary_matches.trim().split(";");

								for (int hconfig = 0; hconfig < schema_config.length; hconfig++) { // each value of the
																									// config schema
									first = schema_config[hconfig].trim().split(":")[0].trim();
									second = schema_config[hconfig].trim().split(":")[1].trim();
									// in case is a commented line with hashtag, do not make any change
									if (!first.contains("#")) {

										// for static values surrounded by double quotes
										if (first.contains("\"")) {
											// log.info("value first0 is "+second);

											String static_value = first.replaceAll("\"", "");
											// log.info("STATIC VALUE1 added: "+static_value);
//                				  String static_value = first.split("\"")[0].split("\"")[1];
////                				  log.info("ALL VALUE "+tmpsecond);
//                				  log.info("STATIC VALUE2 added: "+static_value);
//                				  first = tmp;
											// if(header_value.contentEquals(first)) {
											dict_static[hconfig] = static_value + "_forcolumn_" + second;
											// log.info("STATIC VALUE SAVED FOR COL '"+hconfig+"':
											// "+dict_static[hconfig]);
											// }

											// for all the rest, could it be one or multiple relations as *:1 where *
											// fields have to be divided by comma
										} else {
											String[] val_first = first.split(",");
											for (int item = 0; item < val_first.length; item++) { // each value of the
																									// key schema config
												// header_config_field = ;

//	            				  log.info(">> Match for? val file: "+header_file_field);
//	            				  log.info("val schema: "+header_config_field);

												// for(int it=0; it<nlocfields; it++) {

												String newformat = val_first[item].replaceAll("%", ".*");

												if (value[hfile].toLowerCase().trim()
														.matches(newformat.toLowerCase().trim())) {

//	                    			  log.info("schema=> "+schema_config[hconfig].trim());
//	                    			  
//	                    			  log.info("first "+first);
//	                    			  log.info("second "+second);

													// log.info("WE HAVE A MATCH!!\nheader file field:
													// "+header_file_field);

													// dict_match[j] = header_config_field;
													switch (second) {
													case "emails":
														dict_match[hfile] = "emails";
														break;
													case "urls":
														dict_match[hfile] = "urls";
														break;
													case "phones":
														dict_match[hfile] = "phones";
														break;
													case "cities":
														dict_match[hfile] = "cities";
														log.info("WE PLACE citie IN POSITION:" + hfile);
														break;
													case "postal_codes":
														dict_match[hfile] = "postal_codes";
														break;
													case "working_hours":
														dict_match[hfile] = "working_hours";
														break;
													case "dates":
														dict_match[hfile] = "dates";
														break;
													case "years":
														dict_match[hfile] = "years";
														break;
													case "images":
														dict_match[hfile] = "images";
														break;
													case "archives":
														dict_match[hfile] = "archives";
														break;
													case "documents":
														dict_match[hfile] = "documents";
														break;
													case "currencies":
														dict_match[hfile] = "currencies";
														break;
													case "percentages":
														dict_match[hfile] = "percentages";
														break;
													case "shapes":
														dict_match[hfile] = "shapes";
														break;
													case "latitudes":
														dict_match[hfile] = "latitudes";
														log.info("WE PLACE LATITUDE IN POSITION:" + hfile);
														break;
													case "longitudes":
														dict_match[hfile] = "longitudes";
														log.info("WE PLACE LONG IN POSITION:" + hfile);
														break;
													case "latlong":
														dict_match[hfile] = "latlong";
														break;
													case "nuts1":
														dict_match[hfile] = "nuts1";
														break;
													case "nuts2":
														dict_match[hfile] = "nuts2";
														break;
													case "nuts3":
														dict_match[hfile] = "nuts3";
														break;
													case "titles":
														dict_match[hfile] = "titles";
														break;
													case "descriptions":
														dict_match[hfile] = "descriptions";
														break;
													case "data_publishers":
														dict_match[hfile] = "data_publishers";
														break;

													}

												}
											}
										}
										// log.info("value second2 issss "+second);

									}
								}
							} else {
								header[hfile] = value[hfile];
							}

						}
					}
					log.info(">> Header: " + java.util.Arrays.toString(header) + "");
					outputinfo.append("\n>> Header: " + java.util.Arrays.toString(header) + "\n");
				}

			} else if (i < nrowchecks && autodetectSchema) {

				log.info("WE ARE IN NOT IN HEADER WITH I:=" + i);

				// ###########################################################################################################################
				// CANDIDATE VALUES (X/Y) ARE GOING TO BE CHECKED FOR A SAME FILE - UNTIL [i]
				// ROWS TO CHECK [nrowchecks]
				// CHECK ARE DONE UNDER X NUMBER OF ROWS [nrowchecks] AND X NUMBER REPETITIONS
				// [pvalue_nrowchecks]
				// CONSIDERING confidence_limit = ((nrowchecks-1) * (100 - pvalue_nrowchecks *
				// 100)) / 100;
				// ###########################################################################################################################

				// log.info("line: " + i + "
				// -------------------------------------------------");
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
				log.info("WE ARE IN NOT IN HEADER WITH LINEX:=" + line);

				// ParameterizedSparqlString qs =
				// new ParameterizedSparqlString(""
				// + "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + "\n"
				// + "select ?resource where {\n" + " ?resource rdfs:label ?label\n" + "}");

				int j = 0;
				boolean latitude_visited = false;

				while (j < linex.length) {

					// Literal val = ResourceFactory.createLangLiteral(value[j], "en");
					// qs.setParam("val1", val);

					// System.out.println(qs);
					// String val = value[j].replaceAll("", "").toLowerCase();

					String val = linex[j].toLowerCase().replaceAll("\"", "").trim();
					file_values[i][j] = val;
					// inh means i with no header, otherwise would have started from i=1
					if (fieldtypesdebugmode || geonamesdebugmode)
						// log.info("check: " + val);

						if (val.matches(phoneRegex)) {
							PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
							// try {
							PhoneNumber phone = phoneUtil.parse(val, country_code);

							if (phoneUtil.isValidNumber(phone)) {
								log.info("Phone found for val " + val + " with code " + country_code);
								column_types[inh][j] = "phones";
								log.info("we are in column type with i:" + i + " and j:" + j + " and we added="
										+ column_types[i][j]);
							}
							// } catch (NumberParseException e) {
							// // System.err.println("NumberParseException was thrown: " + e.toString());
							// }
						} else if (EmailValidator.getInstance().isValid(val)) {
							// log.info("we have an email ! in fil " + i + " and col " + j);
							column_types[inh][j] = "emails";
						} else if (UrlValidator.getInstance().isValid(val)
								|| UrlValidator.getInstance().isValid("http://" + val)) {
							if (val.matches(imageRegex)) {
								column_types[inh][j] = "images";
							} else if (val.matches(archiveRegex)) {
								column_types[inh][j] = "archives";
							} else if (val.matches(documentRegex)) {
								column_types[inh][j] = "documents";
							} else {
								column_types[inh][j] = "urls";
							}
						} else if (val.matches(openinghoursRegex)) {
							column_types[inh][j] = "working_hours";
						} else if (val.matches(dateRegex)) {

							column_types[inh][j] = "dates";
						} else if (val.matches(yearRegex)) {
							column_types[inh][j] = "years";
						} else if (val.replaceAll(" ", "").matches(currencyRegex)) {
							column_types[inh][j] = "currencies";
						} else if (val.replaceAll(" ", "").matches(percentageRegex)) {
							column_types[inh][j] = "percentages";
						} else if (val.matches(postcodeRegex)) {
							// conn = DriverManager.getConnection(geonames_dburl, geonames_dbusr,
							// geonames_dbpwd);
							rs = GeonamesSQLQueries.getPostcodeLatLng(Integer.valueOf(val), conn);
							if (rs != null) {
								// log.info("WE HAVE A POSTCODE!!! with name " + rs.getString(2) + " for[" + val
								// + "] , latitude is " + rs.getString(3) + " and longitude is " +
								// rs.getString(4)
								// + " in line " + i);
								column_types[inh][j] = "postal_codes";
							}
							// rs.close();
							// conn.close();
						} else if (val.matches(nutsRegex)) {
							// log.info("value is " + val);

							double[] latlng = new ReadGISShapes(parameters).getNutsLatLng(val.toUpperCase());
							if (latlng != null) {
								// log.info("result is " + latlng[0]);
								switch (val.length()) {
								case 3:
									column_types[inh][j] = "nuts1";
									break;
								case 4:
									column_types[inh][j] = "nuts2";
									break;
								case 5:
									column_types[inh][j] = "nuts3";
									break;
								}
								// log.info("WE HAVE NUTS!!! with latitude is " + latlng[0] + " and longitude is
								// "
								// + latlng[1] + " in line " + i);
							}

						} else if (val.matches(shapeRegex)) {
							column_types[inh][j] = "shapes";
						} else if (val.matches(latitudeRegex) && latitude_visited == false) {
							column_types[inh][j] = "latitudes";
							latitude_visited = true;
						} else if (val.matches(longitudeRegex) && latitude_visited == true) {
							column_types[inh][j] = "longitudes";
							latitude_visited = false;
						} else if (val.matches(latlngRegex)) {
							column_types[inh][j] = "latlong";
							// } else if (val.matches(cityRegex) &&
							// !val.matches("^(other|city|district|state)$")) {
						} else if (val.matches(descriptionRegex)) {
							column_types[inh][j] = "descriptions";
						} else if (val.matches(cityRegex)) {
							outputinfo.append("\n");
							log.info("\n");
							// conn = DriverManager.getConnection(geonames_dburl, geonames_dbusr,
							// geonames_dbpwd);

							// log.info("We are in line " + linex + " col " + j);

							result = GeonamesSQLQueries.getCityLatLng(true, null, rs, val, conn, column_types,
									file_values, country_code, i, j);
							if (result != false) {
								if (tmp_cities[j] == null) {
									log.info("??????????????? we have city in column: " + j);
									column_types[inh][j] = "cities";
									tmp_cities[j] = val;
//                 log.info("WE HAVE A CITY!!! with name " + rs.getString(2) + " for[" + val
//                 + "] , latitude is " + rs.getString(3) + " and longitude is " + rs.getString(4)
//                 + " in line " + i + " and column "+j);
								} else if (!tmp_cities[j].contentEquals(val)) {
									log.info("??? we have city in column: " + j);
									// log.info("for line " + j + " tmp_cities[j] is " + tmp_cities[j] + " with val
									// "
									// + val);
									column_types[inh][j] = "cities";
									tmp_cities[j] = val;
//                log.info("WE HAVE A CITY!!! with name " + rs.getString(2) + " for[" + val
//                + "] , latitude is " + rs.getString(3) + " and longitude is " + rs.getString(4)
//                + " in line " + i + " and column "+j);
								}
								// rs.close();
								// conn.close();

							}

							// ParameterizedSparqlString qs =
							// new ParameterizedSparqlString(
							// "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> PREFIX dbo:
							// <http://dbpedia.org/ontology/> PREFIX foaf: <http://xmlns.com/foaf/0.1/>
							// select distinct ?C ?Long ?Lat where {?X geo:lat ?Lat ; geo:long ?Long; a ?C .
							// { { ?X foaf:name \""
							// + val + "\"@en. } } } LIMIT 100 ");
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

							// we check that titles are not repeated
							if (tmp_possiblenames[j] == null) {
								column_types[inh][j] = "titles";
								tmp_possiblenames[j] = val;
							} else if (!tmp_possiblenames[j].contentEquals(val)) {
								// log.info("for line " + j + " tmp_possiblenames[j] is " + tmp_possiblenames[j]
								// + " with val " + val);
								column_types[inh][j] = "titles";
								tmp_possiblenames[j] = val;
							}
						}
					j++;
				}

				// ################################################################################################################
				// SUM-UP ALL CANDIDATE VALUES (X/Y) - UNTIL [i] ROWS TO CHECK [nrowchecks]
				// STORES IN [sum] THE NUMBER OF COLMN (Y) MATCHES OF THE MATCHING LIST (X)
				// i.e. WE COULD HAVE 5 emails AND 5 telephone IN COLUMN 0
				// ################################################################################################################

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
				for (int w = 0; w < inh + 1; w++) {
					log.info("our condition to debug is w == i - 1, wher w=" + w + " and i =" + i);
					int nmails = 0;
					int nurls = 0;
					int nphones = 0;
					int ncities = 0;
					int npostal_codes = 0;
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
					int ntitles = 0;
					int ndescriptions = 0;

					for (int z = 0; z < ncolchecks; z++) {

						// log.info("for fila " + w + " and columna " + z + " tenemos tipo " +
						// column_types[w][z]);
//						log.info("after are in column type with i:" + w + " and j:" + z + " and we added="
//								+ column_types[w][z]);
						if (column_types[w][z] != null) {
							switch (column_types[w][z]) {
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

								log.info("WE ADDED A PHONE WITH SUM: " + sum[2][z]);
								break;
							case "cities":
								ncities += 1;
								sum[3][z] += 1;
								// log.info("WE HAVE STORED "+ sum[3][l] + " CITIES IN COLUNN "+l);
								break;
							case "postal_codes":
								int trueCount = 0;
								for (int t = 0; t < tmp_postcol.length; t++) {
									if (tmp_postcol[t] == true)
										trueCount++;
									if (trueCount == 3)
										break;
								}
								// avoid columns which have matched more than 3 postal_codes
								if (trueCount != 3) {
									npostal_codes++;
									sum[4][z] += 1;
									tmp_postcol[z] = true;
								}
								break;
							case "working_hours":
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
							case "titles":
								ntitles++;
								sum[20][z] += 1;
								break;
							case "descriptions":
								ndescriptions++;
								sum[21][z] += 1;
								break;
							}
						}
					}

					// ################################################################################################################
					// SAME ROW - PRINTS TOTAL NUMBER OF MATCHES DONE IN THE SAME ROW - UNTIL [i]
					// ROWS TO CHECK [nrowchecks]
					// ################################################################################################################

					if (fieldtypesdebugmode && w == inh) {
						// if (fieldtypesdebugmode && w==i-1) {
						log.info("\nLine[" + i + "]: " + line);
						outputinfo.append("\nLine[" + i + "]: " + line);

						// textResult..setObject("epa..............");
						// if (googleMapsAPI || geonamesdebugmode) {
						log.info("In a same row: " + nmails + " emails, " + ndescriptions + " descriptions, " + nurls
								+ " urls, " + nphones + " phones, " + ncities + " cities, " + npostal_codes
								+ " postal_codes, " + nophours + " working_hours, " + ndates + " dates, " + nyears
								+ " years, " + nimages + " images, " + narchives + " archives, " + ndocuments
								+ " documents, " + ncurrencies + " currencies, " + npercentages + " percentages, "
								+ nshapes + " shapes, " + nlatitudes + " latitudes, " + nlongitudes + " longitudes, "
								+ nlatlong + " latlong, " + nnuts1 + " nuts1, " + nnuts2 + " nuts2, " + nnuts3
								+ " nuts3, " + ntitles + " titles\nTotal sum:");
						outputinfo.append("\nIn a same row: " + nmails + " emails, " + ndescriptions + " descriptions, "
								+ nurls + " urls, " + nphones + " phones, " + ncities + " cities, " + npostal_codes
								+ " postal_codes, " + nophours + " working_hours, " + ndates + " dates, " + nyears
								+ " years, " + nimages + " images, " + narchives + " archives, " + ndocuments
								+ " documents, " + ncurrencies + " currencies, " + npercentages + " percentages, "
								+ nshapes + " shapes, " + nlatitudes + " latitudes, " + nlongitudes + " longitudes, "
								+ nlatlong + " latlong, " + nnuts1 + " nuts1, " + nnuts2 + " nuts2, " + nnuts3
								+ " nuts3, " + ntitles + " titles\nTotal sum:\n");
						// }

						// #############################################################################################################################
						// SAME ROW - PRINTS EACH MATCH [nchecks] DONE FOR EACH COLUMN [ncolchecks] IN
						// SAME ROW - UNTIL [i] ROWS TO CHECK [nrowchecks]
						// #############################################################################################################################

						for (int x = 0; x < nchecks; x++) {
							for (int y = 0; y < ncolchecks; y++) {

								if (sum[x][y] > 0) {

									switch (x) {
									case 0:
										printLog(sum, y, x, confidence_limit, "emails");
										break;
									case 1:
										printLog(sum, y, x, confidence_limit, "urls");
										break;
									case 2:
										printLog(sum, y, x, confidence_limit, "phones");
										break;
									case 3:
										printLog(sum, y, x, confidence_limit, "cities");
										break;
									case 4:
										printLog(sum, y, x, confidence_limit, "postal_codes");
										break;
									case 5:
										printLog(sum, y, x, confidence_limit, "working_hours");
										break;
									case 6:
										printLog(sum, y, x, confidence_limit, "dates");
										break;
									case 7:
										printLog(sum, y, x, confidence_limit, "years");
										break;
									case 8:
										printLog(sum, y, x, confidence_limit, "images");
										break;
									case 9:
										printLog(sum, y, x, confidence_limit, "archives");
										break;
									case 10:
										printLog(sum, y, x, confidence_limit, "documents");
										break;
									case 11:
										printLog(sum, y, x, confidence_limit, "currencies");
										break;
									case 12:
										printLog(sum, y, x, confidence_limit, "percentages");
										break;
									case 13:
										printLog(sum, y, x, confidence_limit, "shapes");
										break;
									case 14:
										printLog(sum, y, x, confidence_limit, "latitudes");
										break;
									case 15:
										printLog(sum, y, x, confidence_limit, "longitudes");
										break;
									case 16:
										printLog(sum, y, x, confidence_limit, "latlong");
										break;
									case 17:
										printLog(sum, y, x, confidence_limit, "nuts1");
										break;
									case 18:
										printLog(sum, y, x, confidence_limit, "nuts2");
										break;
									case 19:
										printLog(sum, y, x, confidence_limit, "nuts3");
										break;
									case 20:
										printLog(sum, y, x, confidence_limit, "titles");
										break;
									case 21:
										printLog(sum, y, x, confidence_limit, "descriptions");
										break;
									} // switch (m)

								} // if (sum[m][n] != 0)
							} // for (int n = 0; n < ncolchecks; n++)
						} // for (int m = 0; m < nchecks; m++)
					} // if (fieldtypesdebugmode && k==1)

				} // for (int k = 1; k < nrowchecks; k++)

				inh++;// VERY IMPORTANT TO NOT move it from HERE and NOT ignore first row

			} // if (i < nrowchecks)
//      if (i == nrowchecks) {
//    	    break;
//      }
			i++;

		}

		// ################################################################################################################
		// PRINTS THE FINAL RESULT - TOTAL NUMBER OF MATCHES [nchecks] DONE FOR EACH
		// COLUMN [ncolchecks]
		// ################################################################################################################

		if (fieldtypesdebugmode || geonamesdebugmode) {
			log.info("\n\n>> FINAL RESULT <<\nFor probability " + pvalue_nrowchecks + " of " + (nrowchecks - 1)
					+ "rows out of " + nlines + " rows and confidence interval " + confidence_limit + ":");
			outputinfo.append("\n\n>> FINAL RESULT <<\nFor probability " + pvalue_nrowchecks + " of " + (nrowchecks - 1)
					+ "rows out of " + nlines + " rows and confidence interval " + confidence_limit + ":\n");
		}

		boolean fieldsAutoDetected = false;
		boolean fieldsHeaderDetected = false;

		for (int m = 0; m < nchecks; m++) {
			for (int n = 0; n < ncolchecks; n++) {
				if (sum[m][n] >= confidence_limit) {
					fieldsAutoDetected = true;
					break;
				}
			}
		}
		for (int n = 0; n < ncolchecks; n++) {

			if (dict_match[n] != null) {
				fieldsHeaderDetected = true;
				break;
			}
		}
		log.info("RESULT LENGTH " + fieldsAutoDetected);
		log.info("dict_match LENGTH " + fieldsHeaderDetected);
		if (!autodetectSchema) {
			outputinfo.append("\nSchema auto-detection is not enabled.\n");
			log.info("\nSchema auto-detection is not enabled.\n");
		} else {
			outputinfo.append("\nField columns auto detected (without using the header tags):\n");
			log.info("\nField columns auto detected (without using the header tags):\n");
			for (int m = 0; m < nchecks; m++) {
				for (int n = 0; n < ncolchecks; n++) {

					// log.info("==> OF '"+columnTypes[m][n]+"' WE HAVE "+sum[m][n]);
					if (sum[m][n] >= confidence_limit) {

						switch (m) {
						case 0:
							printLog(sum, n, m, confidence_limit, "emails");
							resul[n] = "emails";
							break;
						case 1:
							printLog(sum, n, m, confidence_limit, "urls");
							resul[n] = "urls";
							break;
						case 2:
							printLog(sum, n, m, confidence_limit, "phones");
							resul[n] = "phones";
							break;
						case 3:
							if (ArrayUtils.contains(resul, "cities")) {
								int ind_first = ArrayUtils.indexOf(resul, "cities");
								int first = sum[3][ArrayUtils.indexOf(resul, "cities")];
								int second = sum[3][n];
								log.info("first one has: " + first + " and second has " + second);
								if (second > first) {
									printLog(sum, n, m, confidence_limit, "cities");

									log.info("----> first city with " + first + " \"cities\" has been removed");
									log.info(resul[ind_first] + " for index [" + ind_first + "] has been set to null");
									resul[ind_first] = null;
									resul[n] = "cities";
								} else {
									log.info("----> second city with " + second + " \"cities\" for index [" + n
											+ "] has been removed");
									log.info("we keep first city with index [" + ind_first + "]");
									resul[n] = null;
									resul[ind_first] = "cities";
								}
							} else {
								printLog(sum, n, m, confidence_limit, "cities");
								resul[n] = "cities";
							}

							break;
						case 4:
							printLog(sum, n, m, confidence_limit, "postal_codes");
							resul[n] = "postal_codes";
							break;
						case 5:
							printLog(sum, n, m, confidence_limit, "working_hours");
							resul[n] = "openinghours";
							break;
						case 6:
							printLog(sum, n, m, confidence_limit, "dates");
							resul[n] = "dates";
							break;
						case 7:
							printLog(sum, n, m, confidence_limit, "years");
							resul[n] = "years";
							break;
						case 8:
							printLog(sum, n, m, confidence_limit, "images");
							resul[n] = "images";
							break;
						case 9:
							printLog(sum, n, m, confidence_limit, "archives");
							resul[n] = "archives";
							break;
						case 10:
							printLog(sum, n, m, confidence_limit, "documents");
							resul[n] = "documents";
							break;
						case 11:
							printLog(sum, n, m, confidence_limit, "currencies");
							resul[n] = "currencies";
							break;
						case 12:
							printLog(sum, n, m, confidence_limit, "percentages");
							resul[n] = "percentages";
							break;
						case 13:
							printLog(sum, n, m, confidence_limit, "shapes");
							resul[n] = "shapes";
							break;
						case 14:
							printLog(sum, n, m, confidence_limit, "latitudes");
							resul[n] = "latitudes";
							break;
						case 15:
							printLog(sum, n, m, confidence_limit, "longitudes");
							resul[n] = "longitudes";
							break;
						case 16:
							printLog(sum, n, m, confidence_limit, "latlong");
							resul[n] = "latlong";
							break;
						case 17:
							printLog(sum, n, m, confidence_limit, "nuts1");
							resul[n] = "nuts1";
							break;
						case 18:
							printLog(sum, n, m, confidence_limit, "nuts2");
							resul[n] = "nuts2";
							break;
						case 19:
							printLog(sum, n, m, confidence_limit, "nuts3");
							resul[n] = "nuts3";
							break;
						case 20:
							printLog(sum, n, m, confidence_limit, "titles");
							resul[n] = "titles";
							break;
						case 21:
							printLog(sum, n, m, confidence_limit, "descriptions");
							resul[n] = "descriptions";
							break;
						}

					}
				}
			}
		}

		if (Boolean.valueOf(active_dictionary) == true) {

			if (!fieldsHeaderDetected) {
				outputinfo.append("\nNo fields detected from the Schema mapping.\n");
				log.info("\nNo fields detected from the Schema mapping.\n");
			} else {
				outputinfo.append("\nFields detected from the Schema (by using the header tags):\n");
				log.info("\nFields detected from the Schema (by using the header tags):\n");
				for (int n = 0; n < ncolchecks; n++) {

					// log.info("FIELD MATCH!! WITH :"+dict_match[n]);

					if (dict_match[n] != null) {
						// if ((resul[n] == null && dict_match[n] != null) || dict_match[n] ==
						// "latitudes" || dict_match[n] == "longitudes" || dict_match[n] == "latlong" ||
						// dict_match[n] == "shapes") {
						// we replace value giving more importance to the header

						// (dict_match[n] == "titles" && resul[n] != "city") ||
//      	  if (dict_match[n] == "titles" && ArrayUtils.contains(resul,"titles")) {
//    		  resul[ArrayUtils.indexOf(resul,"titles")] = null;
//    	  }

						for (int k = 0; k < resul.length; k++) {
							if (resul[k] == dict_match[n]) {
								resul[k] = null;
							}
						}
						resul[n] = dict_match[n];

						if (fieldtypesdebugmode || geonamesdebugmode) {
							outputinfo.append("\"" + dict_match[n] + "\" in col \"" + n + "\"\n");
							log.info("\"" + dict_match[n] + "\" in col \"" + n + "\"\n");
						}

					}
				}
				if (dict_static[0] != null) {
					outputinfo.append("\nFields with static values:\n");
					log.info("\nFields with static values:\n");
					for (int k = 0; k < dict_static.length; k++) {
						if (dict_static[k] != null) {
							String location_value = dict_static[k].split("_forcolumn_")[0];
							String location_type = dict_static[k].split("_forcolumn_")[1];
							outputinfo.append("\"" + location_value + "\" in col \"" + location_type + "\"\n");
							log.info("\"" + location_value + "\" in col \"" + location_type + "\"\n");
						}
					}
				}
			}
		} else {
			outputinfo.append("\nCustom Schema is not enabled.\n");
			log.info("\nCustom Schema is not enabled.\n");
		}

		log.info("\nFile \"" + file_name + "\" successfully processed.\n");
		outputinfo.append("\nFile \"" + file_name + "\" successfully processed.\n");

	}

	public static void setFieldTypes(LocationModel loc, String type, String value, int x, int y)
			throws NumberFormatException, SQLException, URISyntaxException, CQLException, IOException {

		switch (type) {
		case "data_publishers":
			loc.setSource(value);
			break;
		case "emails":
			loc.setEmail(value);
			break;
		case "urls":
			if (value.matches("^(https?|ftp)://.*$")) {
				loc.setWebsite(value);
			} else {
				loc.setWebsite("http://" + value);
			}
			break;
		case "phones":
			loc.setPhone(value);
			break;
		case "cities":
			loc.setCity(value);

			// TODO
			if (!ArrayUtils.contains(resul, "latitudes") && !ArrayUtils.contains(resul, "longitudes")
					&& !ArrayUtils.contains(resul, "latlong") && !ArrayUtils.contains(resul, "shapes")) {
				// no need to print all the messages of the geocoding: reason of false
				GeonamesSQLQueries.getCityLatLng(false, loc, rs, value, conn, column_types, file_values, country_code,
						x, y);
			}
			break;
		case "postal_codes":
			loc.setPostcode(value);

			// we only search for coordinates over the whole file if no option was available
			// (hard processing time)
			if (value.matches(postcodeRegex) && !ArrayUtils.contains(resul, "latitudes")
					&& !ArrayUtils.contains(resul, "latlong") && !ArrayUtils.contains(resul, "shapes")) {
				log.info("Querying postcode value..." + value);
				rs = GeonamesSQLQueries.getPostcodeLatLng(Integer.valueOf(value), conn);
				if (rs != null) {
					GeonamesSQLQueries.setGeonameResult(loc, rs);
				}
			}
			break;
		case "working_hours":
			loc.setSchedule(value);
			break;
		case "dates":
			loc.setDate(value);
			break;
		case "years":
			loc.setYear(value);
			break;
		case "images":
			loc.setUrlImage(value);
			break;
		case "archives":
			loc.setArchive(value);
			break;
		case "documents":
			loc.setDocument(value);
			break;
		case "currencies":
			loc.setCurrency(value);
			break;
		case "percentages":
			loc.setPercentage(value);
			break;
		case "shapes":
			setFormatLatLng(loc, value);
			break;
		case "latitudes":
			if (NumberUtils.isNumber(value.replace(",", "."))) {
				loc.setLatitude(new BigDecimal(value.replace(",", ".")));
			} else {
				log.info("NOT NUMBERIC, latitude is " + value);
			}
			break;
		case "longitudes":
			if (NumberUtils.isNumber(value.replace(",", "."))) {
				loc.setLongitude(new BigDecimal(value.replace(",", ".")));
			} else {
				log.info("NOT NUMBERIC, longitude is " + value);
			}
			break;
		case "latlong":
			setFormatLatLng(loc, value);
			break;
		case "nuts1":
			double[] latlng = new ReadGISShapes(parameters).getNutsLatLng(value);
			if (latlng != null) {
				loc.setLatitude(new BigDecimal(latlng[0]));
				loc.setLongitude(new BigDecimal(latlng[1]));
			}
			break;
		case "nuts2":
			latlng = new ReadGISShapes(parameters).getNutsLatLng(value);
			if (latlng != null) {
				loc.setLatitude(new BigDecimal(latlng[0]));
				loc.setLongitude(new BigDecimal(latlng[1]));
			}
			break;
		case "nuts3":
			latlng = new ReadGISShapes(parameters).getNutsLatLng(value);
			if (latlng != null) {
				loc.setLatitude(new BigDecimal(latlng[0]));
				loc.setLongitude(new BigDecimal(latlng[1]));
			}
			break;
		case "titles":
			loc.setName(value);
			break;
		case "descriptions":
			log.info("WE FOUND A DESCRIPTION with value " + value + "!!! in fil " + x + " and col " + y);
			loc.setDescription(value);
			break;

		}
	}

	public static void generateLocationAndFile(File source, File dest, String user) throws IOException, ParseException,
			CQLException, InterruptedException, InvalidParameterException, SQLException, URISyntaxException {

		// BufferedReader br = new BufferedReader(new InputStreamReader(new
		// FileInputStream(source), "iso-8859-1"));

		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(source), "ISO-8859-1"));

		ArrayList<LocationModel> new_format = new ArrayList<LocationModel>();

		Pattern patternCsvName = Pattern.compile("([A-Z].*|[a-zA-Z0-9]{2,20}).csv");
		Matcher matcher = patternCsvName.matcher(file_name);
		String csvname_match = null;
		if (matcher.find()) {
			// log.info("CSV Type: " + matcher.group());
			csvname_match = matcher.group().split(".csv")[0];
		}

		String line;

		// i is the row position
		int i = 0;

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

			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd yyyy");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

			loc.setDate_published(sdf.format(now).toString());
			loc.setDate_updated(sdf.format(now).toString());
			loc.setUsername(user);
			log.info("ICON TO ADD IS:" + icon_marker);
			loc.setIconmarker(icon_marker);

			// j is the column position
			int j = 0;

			for (int x = 0; x < dict_static.length; x++) {
				if (dict_static[x] != null) {
					for (int y = 0; y < nchecks; y++) {
						String location_value = dict_static[x].split("_forcolumn_")[0];
						String location_type = dict_static[x].split("_forcolumn_")[1];
						setFieldTypes(loc, location_type, location_value, x, y);

					}
				}
			}

			while (j < value.length) {

				// type is a regex of 20 chars or any capital letter start from the CSV name
				if (csvname_match != null) {
					loc.setType(csvname_match);

				}

				loc.setCsvName(file_name);

				if (header[j] != null && !value[j].isEmpty()) {
					if (loc.getOtherInfo() == null) {
						loc.setOtherInfo("Filename: " + file_name + " ## Published by: " + loc.getUsername()
								+ " ## Last update: " + loc.getDate_updated() + " ## " + header[j] + ": " + value[j]);
					} else {
						loc.setOtherInfo(loc.getOtherInfo() + " ## " + header[j] + ": " + value[j]);
					}
				}

				// log.info("index is "+j+ " with size "+resul.length);
				if (resul[j] != null && !value[j].trim().isEmpty()) {
					// log.info("going to check harcoded? "+resul[j]);
					setFieldTypes(loc, resul[j], value[j], i, j);

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

	private static java.sql.ResultSet rs = null;
	private static boolean result = false;
	private static BufferedReader br;

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
	public static void start(String upload_folder, File uploadedFile, FeedbackPanel feedbackPanel, boolean clear,
			String user) throws Exception {

		// we clear the output log messages only if it is one file - to display multiple
		// file logged messages
		if (clear)
			outputinfo.setLength(0);

		// ApplicationContext context = new
		// ClassPathXmlApplicationContext("classpath*:**/applicationContext.xml");

		// ApplicationContext context = new
		// ClassPathXmlApplicationContext("applicationContext.xml");

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
			log.info("Geonames Connection Failed! Please check again later ..., error:\n" + e);
			outputinfo.append("Geonames Connection Failed! Please check again later ..., error:\n" + e);
			e.printStackTrace();
			return;
		}

		if (conn != null) {
			log.info("Connected to DB !");
		} else {
			log.info("Failed to make DB connection!");
			conn.close();
		}

		// if (!executeSQLqueries) {

		log.info("Creating directories...");

//    createDir(tmp_dir);
//    createDir(processed_dir);
//    createDir(newformat_dir);
//    createDir(enriched_dir);
//    createDir(missinggeoreference_dir);

//		File folderToSearch = new File(csvfiles_dir);

		log.info("----------------------" + "Test mode is........ " + googleMapsAPI);

		log.info("geonames_dbdriver is........ " + geonames_dbdriver);

		// log.info(" DIRECTORIO " + upload_folder);

		// log.info("directory is........ "+upload_folder);

		// ServletContext servletContext = WebApplication.get().getServletContext();

		// log.info(">>FILE IS "+sqlFile.getAbsolutePath());

		// PrintWriter out_sql_bestfiles_buffer = new PrintWriter(sql_bestfiles_buffer);
		// PrintWriter out_sql_nutsfiles_buffer = new PrintWriter(sql_nutsfiles_buffer);
		// PrintWriter out_sql_geofiles_buffer = new PrintWriter(sql_geofiles_buffer);

		log.info("Processing files...");

		nrowchecks += 1; // skip header

//    log.info(">> TEST MODE: " + testmode);
//    outputinfo.append(">> TEST MODE: " + testmode);

		BufferedWriter bw_sql_inserts = null;

		// if (googleMapsAPI) {

		if (uploadedFile != null) {

			file_name = uploadedFile.getName();

			if (file_name.contains(".sql")) {
				if (executeSQLqueries == true) {
					Class.forName(web_dbdriver);
					Connection conn = DriverManager.getConnection(web_dburl, web_dbusr, web_dbpwd);
					RunSqlScript.runSqlScript(new File(upload_folder + file_name), conn, outputinfo, autodetectSchema,
							locationServiceDAO);
					outputinfo.append("\n\nYou can now check your data going to the menu Locations.");
				} else {
					outputinfo.append(
							"Backup has not been executed. Please enable the option: Execute results found (production mode)");
				}
				textResult.setObject(outputinfo.toString());
			}
			if (file_name.contains(".csv")) {

				// We search for Geographic field types
				findFieldTypes(upload_folder, file_name, textResult);
				// End search for Geographic field types

				// We create location objects and new structured file format
				File processedFile = new File(upload_folder + "/processed/structured/" + file_name);
				processedFile.getParentFile().mkdirs();
				processedFile.createNewFile();
				File newFile = new File(upload_folder + "/" + file_name);
				log.info("Temp files created in the server:");
				outputinfo.append("\nTemp files created in the server:");
				log.info("csv unstructured format: " + newFile.getAbsolutePath());
				outputinfo.append("\ncsv unstructured format: " + newFile.getAbsolutePath());
				log.info("csv structured format: " + processedFile.getAbsoluteFile());
				outputinfo.append("\ncsv structured format: " + processedFile.getAbsoluteFile());
				if (newFile.exists() && executeSQLqueries) {

					// we create a structured CSV file format
					generateLocationAndFile(newFile, processedFile, user);
				}
				// End create location objects and new structured file format

				// We create sql file and we execute in case is required
				if (executeSQLqueries) {
					File sqlFile = new File(upload_folder + "/processed/sql/" + file_name.replace("csv", "sql"));
					sqlFile.getParentFile().mkdirs();
					sqlFile.createNewFile();

					log.info("sql inserts/updates file: " + sqlFile.getAbsoluteFile());
					outputinfo.append("\nsql inserts/updates file: " + sqlFile.getAbsoluteFile());
					if (sqlFile.exists()) {
						bw_sql_inserts = new BufferedWriter(new FileWriter(sqlFile));

						GenerateFile.createSQLInserts(processedFile, bw_sql_inserts, outputinfo);

						sqlfile = sqlFile.getAbsoluteFile().toString();

						Class.forName(web_dbdriver);
						Connection conn = DriverManager.getConnection(web_dburl, web_dbusr, web_dbpwd);
						RunSqlScript.runSqlScript(sqlFile.getAbsoluteFile(), conn, outputinfo, autodetectSchema,
								locationServiceDAO);
					} else {
						log.info("Failure, no existing SQL file on the server!");
						outputinfo.append("\n\nFailure, no existing SQL server!");
					}
					bw_sql_inserts.close();
				} else {
					log.info(
							"You are in Test mode, please enable \"Upload results into database (production mode)\" to upload data.");
					outputinfo.append(
							"\n\nYou are in Test mode, please enable \"Upload results into database (production mode)\" to upload data.");
				}
				// End create sql file and we execute in case is required
			}

		} else {
			log.info("No file has been uploaded! Please try again.");
			feedbackPanel.info("No file has been uploaded! Please try again.");
			feedbackPanel.setVisible(true);
		}
		textResult.setObject(outputinfo.toString());
		// we reload the outputinfo result
		textResult.isPresent();

		if (rs != null)
			rs.close();
		if (conn != null)
			conn.close();

		log.info(
				"\n\n############################################################################################################################################################\n"
						+ "############################################################################################################################################################\n");
		outputinfo.append(
				"\n\n############################################################################################################################################################\n"
						+ "############################################################################################################################################################\n");

	}

}
