package com.opendataview.web.heuristicsearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import com.opendataview.web.model.LocationModel;

public class GeonamesSQLQueries extends MainClass {

	private static final long serialVersionUID = 1L;

	// Google API for Geocoding
	private static final String GEOCODE_API_V3 = "https://maps.googleapis.com/maps/api/geocode/json?address=";

	public GeonamesSQLQueries(PageParameters parameters) throws IOException {
		super(parameters);
	}

	private final static org.slf4j.Logger log = LoggerFactory.getLogger(GeonamesSQLQueries.class);

	public static void setGeonameResult(LocationModel loc, ResultSet rs) throws NumberFormatException, SQLException {

		loc.setLatitude(new Float(rs.getString(3)));
		loc.setLongitude(new Float(rs.getString(4)));
		loc.setPopulation(rs.getString(5));
		loc.setElevation(rs.getString(6));
	}

	public static void DebugInfo(PreparedStatement ps, ResultSet rs) throws SQLException {
		log.info(ps.toString());
		log.info(rs.getString(2) + ",  " + rs.getString(3) + "," + rs.getString(4));
	}

	public static void DebugError(PreparedStatement ps, ResultSet rs) throws SQLException {
		log.info(ps.toString());
		log.info("--------------------------------- NO Resul found ---------------------------------");
	}

	public static ResultSet getPostcodeLatLng(Integer postcode, Connection conn) throws SQLException {

		PreparedStatement ps = null;
		Statement st = conn.createStatement();
		st.setMaxRows(1);
		ResultSet rs = null;

		if (postcode > 0 && postcode < 10000) {
			if (postcode < 100) {
				ps = conn.prepareStatement(st1postcode);
				ps.setString(1, "_" + String.valueOf(postcode) + "_");
				rs = ps.executeQuery();

				if (!rs.next()) {
					if (geonamesdebugmode)
						DebugError(ps, rs);
					return null;
				} else {
					if (geonamesdebugmode)
						DebugInfo(ps, rs);
					return rs;
				}
			} else {
				ps = conn.prepareStatement(st2postcode);
				ps.setString(1, String.valueOf(postcode));
				rs = ps.executeQuery();

				if (!rs.next()) {
					if (geonamesdebugmode)
						DebugError(ps, rs);
					return null;
				} else {
					if (geonamesdebugmode)
						DebugInfo(ps, rs);
					return rs;
				}
			}
		}

		return null;

	}

	public static class LatLng implements Serializable {

		private static final long serialVersionUID = 16549987563L;

		private Float lat;
		private Float lng;

		public LatLng(final Float lat, final Float lng) {
			this.lat = lat;
			this.lng = lng;
		}

		public Float getLat() {
			return lat;
		}

		public void setLat(final Float lat) {
			this.lat = lat;
		}

		public Float getLng() {
			return lng;
		}

		public void setLng(final Float lng) {
			this.lng = lng;
		}
	}

//  public boolean NominatimAPI(String name, LocationModel loc, String country_code) {
//
//	    String address = name;
//	    log.info("address is " + address);
//
//	   OpenStreetMapGeoCodeJacksonParser latlng = new OpenStreetMapGeoCodeJacksonParser();
//	    //LatLng coord = latlng.parse(address);
//
//	    log.info("Nominatim RESULT: LAT "+coord.getLat()+ ", LONG "+ coord.getLng());
//	    
//	    if (coord != null) {
//	        loc.setLatitude(new BigDecimal(coord.getLat()));
//	        loc.setLongitude(new BigDecimal(coord.getLng()));
//	        return true;
//	    }
//		return false;
//	  }

	static LatLng getGoogleCoordinates(boolean printMsg, LocationModel loc, String location, String googleMapsAPI,
			int fil, int col) {

		StringBuilder sb = new StringBuilder();
		sb.append(GEOCODE_API_V3);
		sb.append(location.replace(" ", "+"));
		sb.append("&key=");
		sb.append(googleMapsAPI);
		// sb.append("&sensor=false");

		// log.info("Google URL:" + sb.toString());
		if (printMsg) {
			log.info("GMAP field[" + fil + "," + col + "]: " + sb.toString());
			outputinfo.append("\nGMAP field[" + fil + "," + col + "]: " + sb.toString());
		}
//	    if (nfil % 5 == 0) {
//	      try {
//	        log.info(">>>>>> " + nfil + " requests sent, waiting for next " + nfil + " requests..");
//	        Thread.sleep(100);
//	      } catch (InterruptedException ex) {
//	        Thread.currentThread().interrupt();
//	      }
//	    }
		try {
			URL url = new URL(sb.toString());
			URLConnection urlConnection = url.openConnection();
			InputStreamReader inputResults = new InputStreamReader(urlConnection.getInputStream());
			BufferedReader resultsReader = new BufferedReader(inputResults);
			String inputLine = "";
			StringBuilder jsonFormat = new StringBuilder();

			while ((inputLine = resultsReader.readLine()) != null) {
				jsonFormat.append(inputLine);
			}
			JSONObject jso = new JSONObject(jsonFormat.toString());
			String status = jso.getString("status");
			if (status.equals("OVER_QUERY_LIMIT")) {

				try {
					log.info("Error ! Timeout for location " + location);
					Thread.sleep(500);
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
				}

			} else if (status.equals("ZERO_RESULTS")) {
				log.info("ZERO RESULTS FOR PLACE " + location);

			} else {
				log.info(status.toString());
				JSONArray jsa = jso.getJSONArray("results");
				JSONObject js2 = jsa.getJSONObject(0);

				JSONObject js3 = js2.getJSONObject("geometry");
				JSONObject js4 = js3.getJSONObject("location");

//	        BigDecimal lat = BigDecimal.valueOf(js4.getDouble("lat"));
//	        BigDecimal lng = BigDecimal.valueOf(js4.getDouble("lng"));
				Float lat = js4.getFloat("lat");
				Float lng = js4.getFloat("lng");

				LatLng latlng = new LatLng(lat, lng);

//	        loc.setLatitude(lat);
//	        loc.setLongitude(lng);
				return latlng;
			}
			resultsReader.close();
			inputResults.close();
		} catch (Exception e) {
			log.info("error " + e);
		}
		return null;
	}

	// GOOGLE API GET LAT LNG LIMITED TO 2.500 requests per IP

//	  public static LatLng GoogleAPI(String name, String googleMapsAPI) {
//
//
////	    if (loc.getLatitude() == null) {
////	      Thread.sleep(100);
////	      getGoogleCoordinates(loc, name, row);
////	      log.info("REPEAT SEARCH CITY: " + name + ": " + loc.getLatitude() + "," + loc.getLongitude());
////	    }
//
//	  }

	public static boolean getCityLatLng(boolean printMsg, LocationModel loc, ResultSet rs, String name, Connection conn,
			String[][] column_types, String[][] file_values, String country_code, int fil, int col)
			throws SQLException, URISyntaxException {

		// check extension website url as codes below (to know if we can use EXT code or
		// not!); with executed, we check this runs only once !
		if (!executed) {
			String uri = "";
			// String pname = "";
			String[] country_codes = new String[] { "AF", "AX", "AL", "DZ", "AS", "AD", "AO", "AI", "AQ", "AG", "AR",
					"AM", "AW", "AU", "AT", "AZ", "BS", "BH", "BD", "BB", "BY", "BE", "BZ", "BJ", "BM", "BT", "BO",
					"BQ", "BA", "BW", "BV", "BR", "IO", "BN", "BG", "BF", "BI", "KH", "CM", "CA", "CV", "KY", "CF",
					"TD", "CL", "CN", "CX", "CC", "CO", "KM", "CG", "CD", "CK", "CR", "CI", "HR", "CU", "CW", "CY",
					"CZ", "DK", "DJ", "DM", "DO", "EC", "EG", "SV", "GQ", "ER", "EE", "ET", "FK", "FO", "FJ", "FI",
					"FR", "GF", "PF", "TF", "GA", "GM", "GE", "DE", "GH", "GI", "GR", "GL", "GD", "GP", "GU", "GT",
					"GG", "GN", "GW", "GY", "HT", "HM", "VA", "HN", "HK", "HU", "IS", "IN", "ID", "IR", "IQ", "IE",
					"IM", "IL", "IT", "JM", "JP", "JE", "JO", "KZ", "KE", "KI", "KP", "KR", "KW", "KG", "LA", "LV",
					"LB", "LS", "LR", "LY", "LI", "LT", "LU", "MO", "MK", "MG", "MW", "MY", "MV", "ML", "MT", "MH",
					"MQ", "MR", "MU", "YT", "MX", "FM", "MD", "MC", "MN", "ME", "MS", "MA", "MZ", "MM", "NA", "NR",
					"NP", "NL", "NC", "NZ", "NI", "NE", "NG", "NU", "NF", "MP", "NO", "OM", "PK", "PW", "PS", "PA",
					"PG", "PY", "PE", "PH", "PN", "PL", "PT", "PR", "QA", "RE", "RO", "RU", "RW", "BL", "SH", "KN",
					"LC", "MF", "PM", "VC", "WS", "SM", "ST", "SA", "SN", "RS", "SC", "SL", "SG", "SX", "SK", "SI",
					"SB", "SO", "ZA", "GS", "SS", "ES", "LK", "SD", "SR", "SJ", "SZ", "SE", "CH", "SY", "TW", "TJ",
					"TZ", "TH", "TL", "TG", "TK", "TO", "TT", "TN", "TR", "TM", "TC", "TV", "UG", "UA", "AE", "GB",
					"US", "UM", "UY", "UZ", "VU", "VE", "VN", "VG", "VI", "WF", "EH", "YE", "ZM", "ZW" };
			for (int r = 1; r < nrowchecks; r++) {
				for (int s = 0; s < ncolchecks; s++) {
					// log.info("value "+r+","+s+" has: "+columnTypes[r][s]+" and value
					// "+csv_values[r][s]+" with nrows "+nrowchecks+" and cols "+ncolchecks + " and
					// current row "+current_row);

					// log.info("columnTypes: "+columnTypes[i][j]);
					if (column_types[r][s] != null) {
						if (column_types[r][s].matches("^(nuts1|nuts2|nuts3)$")) {
							log.info("WE FOUND NUTS!! for " + column_types[r][s]);
						}
						if (column_types[r][s].matches("^(urls)$")) {
							if (column_types[r][s] == "urls")
								uri = file_values[r][s];
							// if(column_types[r][s]=="titles") pname = file_values[r][s];

							URI urls = new URI(uri);
							// String hostname = urls.getHost();
							String extension = urls.toString().substring(urls.toString().lastIndexOf(".") + 1)
									.replaceAll("/", "").toUpperCase().trim();

							// log.info("We have urls '"+urls+"' and extension '"+extension+"'");
							for (int t = 0; t < country_codes.length; t++) {
								if (extension.equals(country_codes[t])) {
									log.info("WE FOUND A MATCH!! for " + column_types[r][s] + " urls '" + urls
											+ "' and extension '" + extension + "'");
									code_found = extension;
									break;
								}
							}
							/*
							 * if(found == true) {
							 * 
							 * log.info("We have hostname '"+hostname+"' and pname '"+pname+"'"); if
							 * (hostname != null && hostname == pname) {
							 * log.info("WE FOUND A MATCH!! for "+columnTypes[i][j]+" hostname '"
							 * +hostname+"' and pname '"+pname+"'"); } }
							 * 
							 * found = true;
							 */
						}
					}

				}
			}
		}

		PreparedStatement ps = null;
		Statement st = conn.createStatement();
		st.setMaxRows(1);

		if (geonamesdebugmode)
			log.info("\nchecking > " + name);

		name = name.toLowerCase().replaceAll("\"|(.)?[0-9](.)?|'|^\\s*$", "").trim();

		// log.info("NUMBER OF WORDS: " + name.split(" ").length);

		// we get the first part from / division
		if (name.contains("/")) {
			name = name.split("/")[0];
		}

		// replace anything between parenthesis and replace German characters with _
		name = name.replaceAll("\\(.*?\\) ?", "").replace("ü", "ue").replace("ö", "oe").replace("ä", "ae")
				.replace("ß", "ss").replace("Ü", "ue").replace("Ö", "oe").replace("Ä", "ae").trim();

//try {
//    conn = DriverManager.getConnection(geonames_dburl, geonames_dbusr, geonames_dbpwd);
//  } catch (SQLException e) {
//    log.info("Geonames Connection Failed! Please check again later ..., error:\n"+e);
//    outputinfo.append("Geonames Connection Failed! Please check again later ..., error:\n"+e);
//    e.printStackTrace();
//
//  }
//
//if (conn != null) {
//    //log.info("Connected to DB !");
//  } else {
//    log.info("Failed to make DB connection!");
//  }

		StringBuilder input = null;
		int indexOfWhere = 0;
		if (name != null && !name.isEmpty() && !name.matches("\\d+")) {

			// we only change the statement if we found a country code!!
			if (code_found != "" && !executed) {
				input = new StringBuilder(st1city);
				indexOfWhere = input.indexOf(" where "); // notice the spaces
				input.insert(indexOfWhere + 7, "country = '" + code_found + "' and ");
				st1city = input.toString();
				input = new StringBuilder(st2city);
				indexOfWhere = input.indexOf(" where "); // notice the spaces
				input.insert(indexOfWhere + 7, "country = '" + code_found + "' and ");
				executed = true;
			}

			ps = conn.prepareStatement(st1city);
			ps.setString(1, name);

			if (printMsg) {
				log.info("SQL field[" + fil + "," + col + "]: " + ps.toString());
				outputinfo.append("SQL field[" + fil + "," + col + "]: " + ps.toString());
			}
			rs = ps.executeQuery();

			if (name.length() >= 4) {

				// if there is no resultset
				if (!rs.next()) {

					// we remove dots
					if (name.contains(".")) {
						name = name.replace(".", "");
					}

					// LatLng result = GoogleAPI(name,googleMapsAPI);

					LatLng result = getGoogleCoordinates(printMsg, loc, name, googleMapsAPI, fil, col);
//  	    if (result != null) {
//  	        log.info("FOUND CITY: " + name + ": " + result.getLat() + "," + result.getLng());
//  	    } else {
//  	    	log.info("CITY NULL NOT FOUND "+name);
//  	    }

					// if we are checking for possible geographic types
					if (loc == null) {
						if (result != null)
							return true;
						else
							return false;
					}
					// if we want to find and store the geocoding type found
					if (loc != null & result != null) {
						loc.setLatitude(Float.valueOf(result.getLat()));
						loc.setLongitude(Float.valueOf(result.getLng()));
						return true;
					}

					// boolean result = NominatimAPI(name, loc, country_code);
//          String endpointUrl = "https://nominatim.openstreetmap.org/";
//          Address address = new Address();
//          address.setCity(name);
//          MapPoint mapPoint = NominatimAPI.with(endpointUrl).getMapPointFromAddress(address, 5);

//          // we will query with the last 80% of chars of the city
//          int nchars = (name.length() * 80) / 100;
//          ps = conn.prepareStatement(st2city);
//          ps.setString(1, name.substring(0, 1) + "%" + name.substring(name.length() - nchars));
//          ps.setString(2, name + "%");
//          
//          log.info("st2city: "+ps.toString());
//          outputinfo.append("\nst2city: "+ps.toString());
//          
//          rs = ps.executeQuery();

//          if (!rs.next()) {
//
//            // we get the first part from - division
//            if (name.contains("-")) {
//              name = name.replace("-", " ");
//              name = name.replaceAll("\\s+", " ").trim(); // remove more than one empty space
//            }
//
//            String firstTwoWord = "";
//            String lastTwoWord = "";
//            String[] values = name.split(" ");
//
//            // if it has more than 2 words
//            if (values.length > 2) {
//              firstTwoWord = values[0] + "%" + values[1];
//              lastTwoWord = values[values.length - 2] + "%" + values[values.length - 1];
//            } else if (name.length() > 6) {
//              // we get the first and last characters (60%) of the word
//              nchars = (name.length() * 60) / 100;
//              firstTwoWord = name.substring(0, nchars);
//              lastTwoWord = name.substring(name.length() - nchars);
//            } else {
//              return false;
//            }
//
//            // log.info("first words/chars: " + firstTwoWord);
//            // log.info("last words/chars: " + lastTwoWord);
//
//            ps = conn.prepareStatement(st3city);
//            ps.setString(1, "%" + firstTwoWord + "%");
//            ps.setString(2, "%" + lastTwoWord + "%");
//            log.info("st3city: "+ps.toString());
//            outputinfo.append("\nst3city: "+ps.toString());
//
//            rs = ps.executeQuery();
//
//            if (!rs.next()) {
//              if (geonamesdebugmode)
//                DebugError(ps, rs);
//              return false;
//            } else {
//              if (geonamesdebugmode)
//                DebugInfo(ps, rs);
//              return true;
//            }
//          } else {
//            if (geonamesdebugmode)
//              DebugInfo(ps, rs);
//            return true;
//          }
				} else {
					if (geonamesdebugmode)
						DebugInfo(ps, rs);
					if (loc != null) {
						loc.setLatitude(new Float(rs.getString(3)));
						loc.setLongitude(new Float(rs.getString(4)));
						loc.setPopulation(rs.getString(5));
						loc.setElevation(rs.getString(6));
						return true;
					}

				}
			}
		}
		return false;
	}
}
