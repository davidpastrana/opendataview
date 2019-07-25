package com.opendataview.web.heuristicsearch;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.tika.detect.AutoDetectReader;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaMetadataKeys;
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
	protected static boolean private_mode;
	protected static String icon_marker;
	protected static String country_code;

	protected static String csvfiles_dir;
	protected static String tmp_dir;
	protected static String newformat_dir;
	protected static String dictionary_matches;
	protected static String missinggeoreference_dir;
	protected static boolean active_dictionary = false;
	protected static boolean random_color = false;

	protected static String geonames_dbdriver;
	protected static String geonames_dburl;
	protected static String geonames_dbusr;
	protected static String geonames_dbpwd;

	protected static String web_dbdriver;
	protected static String web_dburl;
	protected static String web_dbusr;
	protected static String web_dbpwd;

	protected static String st1postcode;
	protected static String st1city;

	protected static String imageRegex;
	protected static String phoneRegex;
	protected static String archiveRegex;
	protected static String documentRegex;
	protected static String openinghoursRegex;
	protected static String dateRegex;
	protected static String yearRegex;
	protected static String currencyRegex;
	protected static String percentageRegex;
	protected static String postcodeRegex;
	protected static String nutsRegex;
	protected static String shapeRegex;
	protected static String latitudeRegex;
	protected static String longitudeRegex;
	protected static String latlngRegex;
	protected static String possiblenameRegex;
	protected static String descriptionRegex;
	protected static String cityRegex;

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

	public static void initialize(String user, PropertiesServiceDAO propertiesServiceDAO) throws IOException {

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
				obj.setLongitude(newFormat);
				newFormat = "";
				isLatitudeRead = true;
			}
			if (x != ' ' && x != ')' && isLatitudeRead) {
				newFormat += x;
			}
			if (x == ')') {
				obj.setLatitude(newFormat);
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
			outputinfo.append(sum[m][n] + " \"" + vartype + "\" in col \"" + n + "\" (not yet accepted): " + sum[m][n]
					+ " <= " + confidence_limit + "\n");
			log.info(sum[m][n] + " \"" + vartype + "\" in col \"" + n + "\" (not yet accepted): " + sum[m][n] + " <= "
					+ confidence_limit + "\n");
		}
	}

	public static void findFieldTypes(String dir, String name, Model<String> textResult) throws NumberFormatException,
			CQLException, IOException, NumberParseException, SQLException, URISyntaxException {
		String file = dir + name;
		Charset charset = null;
		final Metadata metadata = new Metadata();
		// Try to parse the character set from the content-encoding.
		String orig = metadata.get(Metadata.CONTENT_ENCODING);

		// Set the file name. This provides some level of type-hinting.
		metadata.add(TikaMetadataKeys.RESOURCE_NAME_KEY, new File(file).getName());

		// Try to detect the character set.
		if (null != orig && Charset.isSupported(orig)) {
			charset = Charset.forName(orig);
		}

		try (final BufferedInputStream input = new BufferedInputStream(new FileInputStream(dir + name));
				final AutoDetectReader detector = new AutoDetectReader(input, metadata)) {
			charset = detector.getCharset();
		} catch (TikaException e) {
			throw new IOException("Unable to detect charset.", e);
		}

		br = new BufferedReader(new InputStreamReader(new FileInputStream(dir + name), charset));

		BufferedReader reader = new BufferedReader(new FileReader(file));
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
					// if (fieldtypesdebugmode || geonamesdebugmode) {

					log.info(">> File: " + name);
					outputinfo.append(">> File: " + name);
					log.info(">> Charset: " + charset);
					outputinfo.append("\n>> Charset: " + charset);
					log.info(">> Columns: " + ncolchecks);
					outputinfo.append("\n>> Columns: " + ncolchecks);
					log.info(">> Rows: " + nlines);
					outputinfo.append("\n>> Rows: " + nlines);
					log.info(">> Delimiter: [ " + DELIMITER + " ]");
					outputinfo.append("\n>> Delimiter: [ " + DELIMITER + " ]");
					log.info(">> Auto detect field types: " + autodetectSchema);
					outputinfo.append("\n>> Auto detect field types: " + autodetectSchema);
					log.info(">> Field types debug: " + fieldtypesdebugmode);
					outputinfo.append("\n>> Field types debug: " + fieldtypesdebugmode);
					log.info(">> Queries debug: " + geonamesdebugmode);
					outputinfo.append("\n>> Queries debug: " + geonamesdebugmode);
					log.info(">> Temp directory: " + dir);
					outputinfo.append("\n>> Temp directory: " + dir);
					log.info(">> Schema enabled: " + active_dictionary);
					outputinfo.append("\n>> Schema enabled: " + active_dictionary);
					log.info(">> Random color (markers): " + random_color);
					outputinfo.append("\n>> Random color (markers): " + random_color);
					log.info(">> Schema mappings:\n[" + dictionary_matches + "]");
					outputinfo.append("\n>> Schema mappings:\n[" + dictionary_matches + "]");

					header = new String[ncolchecks * 2];
					String[] value = new String[ncolchecks];

					value = line.split(DELIMITER);

					// exception for some files with a NONE defined header in the first raw
					if (ncolchecks < 10)
						ncolchecks = 10;

					column_types = new String[nrowchecks * 2][ncolchecks * 2];
					file_values = new String[nrowchecks * 2][ncolchecks * 2];

					sum = new int[nchecks][ncolchecks];

					tmp_cities = new String[ncolchecks * 2];
					tmp_possiblenames = new String[ncolchecks * 2];
					tmp_postcol = new boolean[ncolchecks * 2];
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

							// capitalize only the first letter of each header value (line 0) and clean any
							// double quotes
							value[hfile] = value[hfile].replaceAll("\"", "");
							header[hfile] = value[hfile].substring(0, 1).toUpperCase()
									+ value[hfile].substring(1).toLowerCase();

							if (Boolean.valueOf(active_dictionary) == true && dictionary_matches.contains(":")) {

								// log.info("VALUE TO CHECK!!: "+header_value);

								schema_config = dictionary_matches.trim().split(";");

								for (int hconfig = 0; hconfig < schema_config.length; hconfig++) { // each value of the
																									// config schema
									// split out from double quotes
									first = schema_config[hconfig].trim().split(":(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")[0]
											.trim();
									second = schema_config[hconfig].trim().split(":(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")[1]
											.trim();
									// in case is a commented line with hashtag, do not make any change
									if (!first.contains("#")) {

										// for static values surrounded by double quotes
										if (first.contains("\"")) {
											String static_value = first.replaceAll("\"", "");

											// In case of a hardcoded value, left side is the static value and right
											// side is the dictionary word
											dict_static[hconfig] = "'" + static_value + "'_forcolumn_" + second;

											// for all the rest, could it be one or multiple relations as *:1 where *
											// fields have to be divided by comma
										} else {
											String[] val_first = first.split(",");
											for (int item = 0; item < val_first.length; item++) { // each value of the
																									// key schema config

												String newformat = val_first[item].replaceAll("%", ".*");

												if (value[hfile].toLowerCase().trim()
														.matches(newformat.toLowerCase().trim())) {

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
														break;
													case "longitudes":
														dict_match[hfile] = "longitudes";
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
													case "source":
														dict_match[hfile] = "source";
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

				// ###########################################################################################################################
				// CANDIDATE VALUES (X/Y) ARE GOING TO BE CHECKED FOR A SAME FILE - UNTIL [i]
				// ROWS TO CHECK [nrowchecks]
				// CHECK ARE DONE UNDER X NUMBER OF ROWS [nrowchecks] AND X NUMBER REPETITIONS
				// [pvalue_nrowchecks]
				// CONSIDERING confidence_limit = ((nrowchecks-1) * (100 - pvalue_nrowchecks *
				// 100)) / 100;
				// ###########################################################################################################################

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

				int j = 0;
				boolean latitude_visited = false;

				while (j < linex.length) {

					String val = linex[j].toLowerCase().replaceAll("\"", "").trim();
					file_values[i][j] = val;

					if (val.matches(phoneRegex)) {
						PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
						PhoneNumber phone = phoneUtil.parse(val, country_code);
						if (phoneUtil.isValidNumber(phone)) {
							column_types[inh][j] = "phones";
						}
					} else if (EmailValidator.getInstance().isValid(val)) {
						// log.info("we have an email ! in fil " + i + " and col " + j);
						column_types[inh][j] = "emails";
					} else if (UrlValidator.getInstance().isValid(val)) {
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
						rs = GeonamesSQLQueries.getPostcodeLatLng(Integer.valueOf(val), conn);
						if (rs != null) {
							column_types[inh][j] = "postal_codes";
						}
					} else if (val.matches(nutsRegex)) {

						double[] latlng = new ReadGISShapes(parameters).getNutsLatLng(val.toUpperCase());
						if (latlng != null) {
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
						}

					} else if (val.matches(shapeRegex.toLowerCase())) {
						column_types[inh][j] = "shapes";
					} else if (val.matches(latitudeRegex) && latitude_visited == false) {
						column_types[inh][j] = "latitudes";
						latitude_visited = true;
					} else if (val.matches(longitudeRegex) && latitude_visited == true) {
						column_types[inh][j] = "longitudes";
						latitude_visited = false;
					} else if (val.matches(latlngRegex)) {
						column_types[inh][j] = "latlong";
					} else if (val.matches(descriptionRegex)) {
						column_types[inh][j] = "descriptions";
					} else if (val.matches(cityRegex)) {
						outputinfo.append("\n");
						log.info("\n");
						result = GeonamesSQLQueries.getCityLatLng(geonamesdebugmode, null, rs, val, conn, column_types,
								file_values, country_code, i, j);
						if (result != false) {
							if (tmp_cities[j] == null) {
								log.info("??????????????? we have city in column: " + j);
								column_types[inh][j] = "cities";
								tmp_cities[j] = val;

							} else if (!tmp_cities[j].contentEquals(val)) {
								log.info("??? we have city in column: " + j);

								column_types[inh][j] = "cities";
								tmp_cities[j] = val;
							}

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

				for (int m = 0; m < nchecks; m++) {
					for (int n = 0; n < ncolchecks; n++) {
						sum[m][n] = 0;

					}
				}
				for (int w = 0; w < inh + 1; w++) {
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
						log.info("\nLine[" + i + "]: " + line);
						outputinfo.append("\nLine[" + i + "]: " + line);

						log.info("In a same row: " + nmails + " emails, " + ndescriptions + " descriptions, " + nurls
								+ " urls, " + nphones + " phones, " + ncities + " cities, " + npostal_codes
								+ " postal_codes, " + nophours + " working_hours, " + ndates + " dates, " + nyears
								+ " years, " + nimages + " images, " + narchives + " archives, " + ndocuments
								+ " documents, " + ncurrencies + " currencies, " + npercentages + " percentages, "
								+ nshapes + " shapes, " + nlatitudes + " latitudes, " + nlongitudes + " longitudes, "
								+ nlatlong + " latlong, " + nnuts1 + " nuts1, " + nnuts2 + " nuts2, " + nnuts3
								+ " nuts3, " + ntitles + " titles\nTotal sum until this row");
						outputinfo.append("\nIn a same row: " + nmails + " emails, " + ndescriptions + " descriptions, "
								+ nurls + " urls, " + nphones + " phones, " + ncities + " cities, " + npostal_codes
								+ " postal_codes, " + nophours + " working_hours, " + ndates + " dates, " + nyears
								+ " years, " + nimages + " images, " + narchives + " archives, " + ndocuments
								+ " documents, " + ncurrencies + " currencies, " + npercentages + " percentages, "
								+ nshapes + " shapes, " + nlatitudes + " latitudes, " + nlongitudes + " longitudes, "
								+ nlatlong + " latlong, " + nnuts1 + " nuts1, " + nnuts2 + " nuts2, " + nnuts3
								+ " nuts3, " + ntitles + " titles\nTotal sum until this row\n");

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

					if (dict_match[n] != null) {

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
							// We have a hardcoded value, then we split into the value (left hardcoded
							// value) and dictionary word (right side)
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
		case "source":
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
			if (!ArrayUtils.contains(resul, "latitudes") && !ArrayUtils.contains(resul, "longitudes")
					&& !ArrayUtils.contains(resul, "latlong") && !ArrayUtils.contains(resul, "shapes")) {
				// no need to print all the messages of the geocoding: reason of false

				log.info("we search for a lat and long because of city!!");
				// IN CASE THERE IS NO LATITUDE AND LONGITUDE, WE GEOCODE ALL FILE CITIES
//				GeonamesSQLQueries.getCityLatLng(geonamesdebugmode, loc, rs, value, conn, column_types, file_values,
//						country_code, x, y);
			}
			break;
		case "postal_codes":
			loc.setPostcode(value);

			// we only search for coordinates over the whole file if no option was available
			// (hard processing time)
			/*
			 * if (value.matches(postcodeRegex) && !ArrayUtils.contains(resul, "latitudes")
			 * && !ArrayUtils.contains(resul, "latlong") && !ArrayUtils.contains(resul,
			 * "shapes")) { if (fieldtypesdebugmode) log.info("Querying postcode value..." +
			 * value); rs = GeonamesSQLQueries.getPostcodeLatLng(Integer.valueOf(value),
			 * conn); if (rs != null) { GeonamesSQLQueries.setGeonameResult(loc, rs); } }
			 */
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
			loc.setImage(value);
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
			value = value.toLowerCase().trim();
//			if (value.matches(".*(multipoint|linestring|multilinestring|polygon|multipolygon|geometrycollection).*")) {
//
//				log.info("value to add is " + value);
//				if (value.matches("^polygon.*")) {
//					try {
//						loc.setGeometry(new GeometryFactory()
//								.createPolygon(new WKTReader().read(value.toString()).getCoordinates()));
//					} catch (com.vividsolutions.jts.io.ParseException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
			if (value.toLowerCase().matches("point\\s*\\(([+-]?\\d+\\.?\\d+)\\s*,?\\s*([+-]?\\d+\\.?\\d+)\\)")) {
				setFormatLatLng(loc, value);
			}
			break;
		case "latitudes":
			try {
				if (Double.valueOf(value.replace(",", ".")) != null)
					loc.setLatitude(value.replace(",", "."));
			} catch (NumberFormatException e) {
				log.info("Input not a latitude: " + e.getMessage());
				outputinfo.append("\nInput not a latitude: " + e.getMessage());
			}
			break;
		case "longitudes":
			try {
				if (Double.valueOf(value.replace(",", ".")) != null)
					loc.setLongitude(value.replace(",", "."));
			} catch (NumberFormatException e) {
				log.info("Input not a longitude: " + e.getMessage());
				outputinfo.append("\nInput not a longitude: " + e.getMessage());
			}
			break;
		case "latlong":
			setFormatLatLng(loc, value);
			break;
		case "nuts1":
			double[] latlng = new ReadGISShapes(parameters).getNutsLatLng(value);
			if (latlng != null) {
				loc.setLatitude(latlng[0]);
				loc.setLongitude(latlng[1]);
			}
			break;
		case "nuts2":
			latlng = new ReadGISShapes(parameters).getNutsLatLng(value);
			if (latlng != null) {
				loc.setLatitude(latlng[0]);
				loc.setLongitude(latlng[1]);
			}
			break;
		case "nuts3":
			latlng = new ReadGISShapes(parameters).getNutsLatLng(value);
			if (latlng != null) {
				loc.setLatitude(latlng[0]);
				loc.setLongitude(latlng[1]);
			}
			break;
		case "titles": // "#"is used to not create conflict with the iconmarker in the JS as it
						// contains 3 hash simbols
			loc.setName(value.replaceAll("#", ""));
			break;
		case "descriptions":
			loc.setDescription(value);
			break;
		}
	}

	public static void generateLocationAndFile(File source, File dest, int nfiles, String user)
			throws IOException, ParseException, CQLException, InterruptedException, InvalidParameterException,
			SQLException, URISyntaxException {

		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(source), "UTF-8"));

		ArrayList<LocationModel> locations_list = new ArrayList<LocationModel>();

		Pattern patternFileName = Pattern.compile("([A-Z].*|[a-zA-Z0-9]{2,20}).csv");
		Matcher matcher = patternFileName.matcher(file_name);
		String filename_match = null;
		if (matcher.find()) {
			filename_match = matcher.group().split(".csv")[0];
		}

		String line;

		// i is the row position
		int i = 0;

		// random colour when we have multiple markers
		Random random = new Random();
		StringBuilder sb = new StringBuilder();
		sb.append(Integer.toHexString(random.nextInt()));

		while ((line = br.readLine()) != null) {

			// we split the delimiter comma or semicolon, and we ignore the delimiters
			// between/inside quotes
			String[] value = line.split(DELIMITER + "(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

			// for each row we create an instance of the Model
			LocationModel loc = new LocationModel();

			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy' - 'HH:mm");
			loc.setDate_published(sdf.format(now).toString());
			loc.setDate_updated(sdf.format(now).toString());
			loc.setUsername(user);

			// if clear==true we only have one file, if clear==false we have multiple file
			// and we set random colors to markers
			if (!random_color) {
				loc.setIconmarker(icon_marker.split("#")[0].toString());
			} else {
				loc.setIconmarker(sb.substring(0, 6));
			}

			loc.setPrivate_mode(private_mode);

			// j is the column position
			int j = 0;

			// In case we have defined Hardcoded values in the Schema
			for (int x = 0; x < dict_static.length; x++) {
				if (dict_static[x] != null) {
					for (int y = 0; y < nchecks; y++) {
						// We have a hardcoded value, then we split into the value (left hardcoded
						// value) and dictionary word (right side)
						String location_value = dict_static[x].split("_forcolumn_")[0];
						String location_type = dict_static[x].split("_forcolumn_")[1];
						setFieldTypes(loc, location_type, location_value, x, y);

					}
				}
			}

			while (j < value.length) {

				// We set the type from the CSV file name or any other detected field
				if (filename_match != null) {
					loc.setType(filename_match);
				}

				// We set the CSV file name
				loc.setFileName(file_name);

				// We always place all attributes inside a same data column (Important to allow
				// attributes search and graphs on any unrecognized field)
				if (header[j] != null && !value[j].isEmpty()) {
					if (loc.getData() == null) {
						loc.setData("Filename: " + file_name + " ## Published by: " + loc.getUsername() + " ## "
								+ header[j] + ": " + value[j]);
					} else {
						loc.setData(loc.getData() + " ## " + header[j] + ": " + value[j].replaceAll("\"", ""));
					}
				}

				// We clean the value from any possible unwanted character
				value[j] = value[j].replaceAll("\"", "").trim();

				if (resul[j] != null && !value[j].isEmpty() && i > 0) {
					// log.info("going to check harcoded? "+resul[j]);

					setFieldTypes(loc, resul[j], value[j], i, j);
				}

				j++;
			}

			if (i != 0) {
				if (loc.getName() == null) {
					loc.setName(filename_match);
				}
				try {
					if ((loc.getLatitude() != null && loc.getLongitude() != null)) {
						locations_list.add(loc);
					}
				} catch (NullPointerException e) {
					log.info("We do not insert!!!! latlng are failing: " + e.getMessage());
				}
			}

			i++;
		}
		br.close();

		GenerateFile.CreateFormattedCSV(dest, locations_list);
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

	public static void start(String upload_folder, File uploadedFile, FeedbackPanel feedbackPanel, int nfiles,
			String user) throws Exception {

		// we clear the output log messages only if it is one file - to display multiple
		// file logged messages
		if (nfiles == 1)
			outputinfo.setLength(0);

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

		log.info("Processing files ...");

		nrowchecks += 1; // skip header
		BufferedWriter bw_sql_inserts = null;

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
							"Backup has not been executed. Please enable the option: Upload results into database (production mode)");
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
					generateLocationAndFile(newFile, processedFile, nfiles, user);
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
							"You are in Test mode, enable \"Upload results into database (production mode)\" to upload data.");
					outputinfo.append(
							"\n\nYou are in Test mode, enable \"Upload results into database (production mode)\" to upload data.");
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
				"\n\n########################################################################################################\n"
						+ "########################################################################################################\n");
		outputinfo.append(
				"\n\n########################################################################################################\n"
						+ "########################################################################################################\n");

	}

}
