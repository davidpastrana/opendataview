package com.opendataview.web.heuristicsearch;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;
import org.geonames.InvalidParameterException;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;
import org.slf4j.LoggerFactory;

import com.opendataview.web.model.LocationModel;

public class TestedAPIs {

	private final static org.slf4j.Logger log = LoggerFactory.getLogger(TestedAPIs.class);

	// Google API for Geocoding
	private static final String GEOCODE_API_V3 = "https://maps.googleapis.com/maps/api/geocode/json?address=";

	// NOMINATIM OPEN STREET MAP GET LAT LNG LIMITED TO 2.500 reaquests
	public void NominatimAPI(String name, LocationModel loc) {

		String address = name + ", AT";
		log.info("address is " + address);

		OpenStreetMapGeoCodeJacksonParser latlng = new OpenStreetMapGeoCodeJacksonParser();
		com.opendataview.web.heuristicsearch.TestedAPIs.OpenStreetMapGeoCodeJacksonParser.LatLng coord = latlng
				.parse(address);

		if (coord != null) {
			loc.setLatitude(coord.getLat());
			loc.setLongitude(coord.getLng());
		}
	}

	// GEONAMES API limit 2000 requests per hour

	public void GeonamesAPI(String name, LocationModel loc) throws InvalidParameterException {

		ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
		WebService.setUserName("davisrp");
		searchCriteria.setQ(loc.getCity());
		searchCriteria.setCountryCode("AT");

		try {
			ToponymSearchResult searchResult = WebService.search(searchCriteria);
			if (searchResult != null) {
				loc.setLatitude(searchResult.getToponyms().get(0).getLatitude());
				loc.setLongitude(searchResult.getToponyms().get(0).getLongitude());
				// loc.setPostcode(searchResult.getToponyms().get(0).getPos);
			}
			if (searchResult.toString().contentEquals(
					"the hourly limit of 2000 credits for davisrp has been exceeded. Please throttle your requests or use the commercial service.")) {

				log.info("In one hour we make 2000 more requests ! :)");

			}
			log.info("CITY READ: " + name + "with latlng: ");
			log.info(searchResult.getToponyms().get(0).getLatitude() + ", "
					+ searchResult.getToponyms().get(0).getLongitude());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String setFormatLine(String linea) {
		String copy = "";
		if (linea.indexOf('"') >= 0) {
			copy = new String();
			boolean inQuotes = false;
			boolean inParentesis = false;
			for (int i = 0; i < linea.length(); ++i) {
				if (linea.charAt(i) == '"') {
					inQuotes = !inQuotes;
				}
				if (linea.charAt(i) == '(') {
					inParentesis = !inParentesis;
				}
				char x = linea.charAt(i);
				if ((x == ',' || x == ';') && inQuotes) {
					copy += ' ';
				} else if ((x == '(' || x == ' ' || x == ')') && inParentesis) {
					if (x == '(') {
						copy += ';';
					} else if (x == ')') {
						inParentesis = !inParentesis;
					} else {
						copy += ' ';
					}

				} else {
					copy += x;
				}
			}
			linea = copy.replace("\"", "");
		}
		return linea;
	}

	public class OpenStreetMapGeoCodeJacksonParser {

		private static final String LATITUDE = "lat";
		private static final String LONGITUDE = "lon";

		public LatLng parse(final InputStream jsonStream) {
			LatLng coordinate = null;
			final ObjectMapper mapper = new ObjectMapper();
			try {
				final List<Object> dealData = mapper.readValue(jsonStream, List.class);
				if (dealData != null && dealData.size() == 1) {
//					final Map<String, Object> locationMap = (Map<String, Object>) dealData.get(0);
//					if (locationMap != null && locationMap.containsKey(LATITUDE)
//							&& locationMap.containsKey(LONGITUDE)) {
//						final double lat = Double.parseDouble(locationMap.get(LATITUDE).toString());
//						final double lng = Double.parseDouble(locationMap.get(LONGITUDE).toString());
//						coordinate = new LatLng(lat, lng);
//					}
				} else {
					Logger.getLogger(OpenStreetMapGeoCodeJacksonParser.class.getName()).log(Level.SEVERE, "NO RESULTS",
							"NO RESULTS");
				}
			} catch (Exception ex) {
				Logger.getLogger(OpenStreetMapGeoCodeJacksonParser.class.getName()).log(Level.SEVERE, ex.getMessage(),
						ex);
			}
			return coordinate;
		}

		public LatLng parse(String rawAddress) {
			InputStream stream = null;
			LatLng coords = null;

			if (rawAddress != null && rawAddress.length() > 0) {
				try {
					String address = URLEncoder.encode(rawAddress, "utf-8");
					String geocodeURL = "http://nominatim.openstreetmap.org/search?format=json&limit=1&q=";
					String formattedUrl = geocodeURL + address;
					log.info("url to call: " + formattedUrl);
					URL geocodeUrl = new URL(formattedUrl);
					stream = geocodeUrl.openStream();
					coords = parse(stream);
				} catch (IOException ex) {
					Logger.getLogger(OpenStreetMapGeoCodeJacksonParser.class.getName()).log(Level.SEVERE, null, ex);
				} finally {
					try {
						stream.close();
					} catch (IOException ex) {
						Logger.getLogger(OpenStreetMapGeoCodeJacksonParser.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}
			return coords;
		}

		public class LatLng implements Serializable {

			private static final long serialVersionUID = 16549987563L;

			private double lat;
			private double lng;

			public LatLng(final double lat, final double lng) {
				this.lat = lat;
				this.lng = lng;
			}

			public double getLat() {
				return lat;
			}

			public void setLat(final double lat) {
				this.lat = lat;
			}

			public double getLng() {
				return lng;
			}

			public void setLng(final double lng) {
				this.lng = lng;
			}

			@Override
			public boolean equals(Object obj) {
				if (obj == null) {
					return false;
				}
				if (getClass() != obj.getClass()) {
					return false;
				}
				final LatLng other = (LatLng) obj;
				if (Double.doubleToLongBits(this.lat) != Double.doubleToLongBits(other.lat)) {
					return false;
				}
				if (Double.doubleToLongBits(this.lng) != Double.doubleToLongBits(other.lng)) {
					return false;
				}
				return true;
			}

			@Override
			public int hashCode() {
				int hash = 3;
				hash = 53 * hash
						+ (int) (Double.doubleToLongBits(this.lat) ^ (Double.doubleToLongBits(this.lat) >>> 32));
				hash = 53 * hash
						+ (int) (Double.doubleToLongBits(this.lng) ^ (Double.doubleToLongBits(this.lng) >>> 32));
				return hash;
			}
		}

	}

}
