package com.opendataview.web.heuristicsearch;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.opendataview.web.model.LocationModel;
import com.opendataview.web.model.PropertiesModel;
import com.opendataview.web.pages.properties.SetPropertiesPage;
import com.opendataview.web.persistence.LocationServiceDAO;
import com.opendataview.web.persistence.PropertiesServiceDAO;

public class ReadPropertyValues extends MainClass {
	public ReadPropertyValues(PageParameters parameters) throws IOException {
		super(parameters);
		// TODO Auto-generated constructor stub
	}





	private static final long serialVersionUID = 1L;
	
	



  public static void getPropValues() throws IOException {

    //String log4jConfPath = System.getProperty("user.dir") + "/log4j.properties";
	InputStream log4jConfPath = null;
    

    Properties prop = new Properties();
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
//     	input = ReadPropertyValues.class.getClassLoader().getResourceAsStream("config.properties");
//     	
//     	prop.load(input);

    	if(origList.size() != 0) {
    	
     	 log.info("EL TAMAÑO 2222 ES..."+origList.get(0).getTestmode());
     	
     	log.info("SIZE ORIG LIST=" + origList.size() + " and test mode is "+origList.get(0).getTestmode());
     	
     	testmode = Boolean.valueOf(origList.get(0).getTestmode());

        //testmode = Boolean.valueOf(prop.getProperty("testmode"));
        test_file = origList.get(0).getTestfile();

        geonamesdebugmode = Boolean.valueOf(origList.get(0).getGeonamesdebugmode());
        fieldtypesdebugmode = Boolean.valueOf(origList.get(0).getFieldtypesdebugmode());

        nrowchecks = Integer.valueOf(origList.get(0).getNrowchecks());
        pvalue_nrowchecks = Double.valueOf(origList.get(0).getPvalue_nrowchecks());

        removeExistingBData = Boolean.valueOf(origList.get(0).getRemoveExistingBData());
        executeSQLqueries = Boolean.valueOf(origList.get(0).getExecuteSQLqueries());

        geonames_dbdriver = origList.get(0).getGeonames_dbdriver();
        geonames_dburl = origList.get(0).getGeonames_dburl();
        geonames_dbusr = origList.get(0).getGeonames_dbusr();
        geonames_dbpwd = origList.get(0).getGeonames_dbpwd();

        web_dbdriver = origList.get(0).getWeb_dbdriver();
        web_dburl = origList.get(0).getWeb_dburl();
        web_dbusr = origList.get(0).getWeb_dbusr();
        web_dbpwd = origList.get(0).getWeb_dbpwd();

        //sqlinserts_file = origList.get(0).getSqlinserts_file();
//        csvfiles_dir = origList.get(0).getCsvfiles_dir();
//        tmp_dir = origList.get(0).getTmp_dir();
        active_dictionary = origList.get(0).getProcessed_dir();
//        newformat_dir = origList.get(0).getNewformat_dir();
        dictionary_matches = origList.get(0).getEnriched_dir();
//        missinggeoreference_dir = origList.get(0).getMissinggeoreference_dir();


        country_code = origList.get(0).getCountrycode();

        shapes_file = origList.get(0).getShapes_file();

        st1postcode = origList.get(0).getSt1postcode();
        st2postcode = origList.get(0).getSt2postcode();
        st1city = origList.get(0).getSt1city();
        st2city = origList.get(0).getSt2city();
        st3city = origList.get(0).getSt3city();

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
