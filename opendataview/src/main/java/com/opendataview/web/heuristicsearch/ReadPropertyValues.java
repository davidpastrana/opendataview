package com.opendataview.web.heuristicsearch;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.PropertyConfigurator;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class ReadPropertyValues extends MainClass {

	private static final long serialVersionUID = 1L;

	public ReadPropertyValues(PageParameters parameters) throws IOException {
		super(parameters);
	}

	public static void getPropValues() throws IOException {

		InputStream log4jConfPath = null;

		InputStream input = null;

		try {
			log4jConfPath = ReadPropertyValues.class.getClassLoader().getResourceAsStream("log4j.properties");
			PropertyConfigurator.configure(log4jConfPath);
		} finally {

			if (log4jConfPath != null) {
				try {
					log4jConfPath.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		try {

			if (origList.size() != 0) {

				log.info("EL TAMAÑO 2222 ES..." + origList.get(0).getMapsAPI());

				log.info("SIZE ORIG LIST=" + origList.size() + " and test mode is " + origList.get(0).getMapsAPI());

				googleMapsAPI = origList.get(0).getMapsAPI();

				// testmode = Boolean.valueOf(prop.getProperty("testmode"));
				// test_file = origList.get(0).getTestfile();

				geonamesdebugmode = Boolean.valueOf(origList.get(0).getGeonamesdebugmode());
				fieldtypesdebugmode = Boolean.valueOf(origList.get(0).getFieldtypesdebugmode());

				nrowchecks = Integer.valueOf(origList.get(0).getNrowchecks());
				pvalue_nrowchecks = Double.valueOf(origList.get(0).getPvalue_nrowchecks());

				autodetectSchema = Boolean.valueOf(origList.get(0).getAutoDetectSchema());
				executeSQLqueries = Boolean.valueOf(origList.get(0).getExecuteSQLqueries());

				geonames_dbdriver = origList.get(0).getGeonames_dbdriver();
				geonames_dburl = origList.get(0).getGeonames_dburl();
				geonames_dbusr = origList.get(0).getGeonames_dbusr();
				geonames_dbpwd = origList.get(0).getGeonames_dbpwd();

				web_dbdriver = origList.get(0).getWeb_dbdriver();
				web_dburl = origList.get(0).getWeb_dburl();
				web_dbusr = origList.get(0).getWeb_dbusr();
				web_dbpwd = origList.get(0).getWeb_dbpwd();

				// sqlinserts_file = origList.get(0).getSqlinserts_file();
//        csvfiles_dir = origList.get(0).getCsvfiles_dir();
//        tmp_dir = origList.get(0).getTmp_dir();
				active_dictionary = origList.get(0).getActiveDictionary();
//        newformat_dir = origList.get(0).getNewformat_dir();
				dictionary_matches = origList.get(0).getDictionaryMatches();
//        missinggeoreference_dir = origList.get(0).getMissinggeoreference_dir();

				private_mode = origList.get(0).getPrivate_mode();
				icon_marker = origList.get(0).getIconmarker();
				country_code = origList.get(0).getCountrycode();

				// shapes_file = origList.get(0).getShapes_file();

				st1postcode = origList.get(0).getSt1postcode();
//        st2postcode = origList.get(0).getSt2postcode();
				st1city = origList.get(0).getSt1city();
//        st2city = origList.get(0).getSt2city();
//        st3city = origList.get(0).getSt3city();

				imageRegex = origList.get(0).getImageRegex();
				phoneRegex = origList.get(0).getPhoneRegex();
				cityRegex = origList.get(0).getCityRegex();
				archiveRegex = origList.get(0).getArchiveRegex();
				documentRegex = origList.get(0).getDocumentRegex();
				openinghoursRegex = origList.get(0).getOpeninghoursRegex();
				dateRegex = origList.get(0).getDateRegex();
				yearRegex = origList.get(0).getYearRegex();
				currencyRegex = origList.get(0).getCurrencyRegex();
				percentageRegex = origList.get(0).getPercentageRegex();
				postcodeRegex = origList.get(0).getPostcodeRegex();
				nutsRegex = origList.get(0).getNutsRegex();
				latitudeRegex = origList.get(0).getLatitudeRegex();
				longitudeRegex = origList.get(0).getLongitudeRegex();
				latlngRegex = origList.get(0).getLatlngRegex();
				possiblenameRegex = origList.get(0).getPossiblenameRegex();
				descriptionRegex = origList.get(0).getDescriptionRegex();
			}
		} finally {

			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
