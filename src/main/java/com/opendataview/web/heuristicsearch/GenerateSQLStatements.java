package com.opendataview.web.heuristicsearch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerateSQLStatements extends MainClass {

	private static final long serialVersionUID = 1L;

public GenerateSQLStatements(PageParameters parameters) throws IOException {
		super(parameters);
		// TODO Auto-generated constructor stub
	}

private final static Logger log = LoggerFactory.getLogger(GenerateSQLStatements.class);

  public static void createSQLInserts(File source, BufferedWriter buffer) throws IOException {

    BufferedReader br =
        new BufferedReader(new InputStreamReader(new FileInputStream(source), "utf-8"));

    int i = 0;
    String line;
    
    log.info("file taken: "+source.getAbsolutePath());
    
    log.info("line "+br.readLine());
    while ((line = br.readLine()) != null) {

      // we do not insert the header
      //if (i != 0) {

        // we split the row by ";" separator
        String[] value = line.split(DELIMITER);
        //log.info("value: "+value[i]);
        // log.info("out!!");
        int j = 0;
        // we create the insert just if we have latitude and longitude
        if (!value[6].contentEquals("null") && !value[7].contentEquals("null")) {
          //log.info("we will insert!! "+value[6]);
          buffer
              .append("INSERT INTO locations(name,type,address,district,postcode,city,latitude,longitude,description,website,phone,date,schedule,email,csvName,population,elevation,otherinfo,rating,nrating) VALUES(");
          j = 0;
          while (j < value.length) {
            value[j] = value[j].replaceAll("\'", "").replaceAll("\"", "");

            // we add just strings with double quotes
            if (!value[j].contentEquals("null") && j != 6 && j != 7) {
              buffer.append("'");
              buffer.append(value[j]);
              buffer.append("'");

              // we add lat and long without quotes
            } else if (j == 6 || j == 7) {
              buffer.append(value[j]);

              // its a null value
            } else {
              buffer.append(null);
            }

            // we insert separator ","
            if (j != value.length - 1) {
              buffer.append(DELIMITER2);
            }
            j++;
          }
          buffer.append(",0,0);");
          buffer.append(NEW_LINE);
        } else {
        	//log.info("we do not insert!! "+value[6]);
          // log.info(source.getAbsolutePath().toString() + ", row [" + i
          // + "]: no lat and long to upload to database.");



          // if (source.getAbsolutePath().contains(dirNutsFiles)) {
          // no_insert_nutsfiles++;
          // } else if (source.getAbsolutePath().contains(dirPossibleFiles)) {
          // no_insert_possiblefiles++;
          // } else if (source.getAbsolutePath().contains(dirPossibleFiles)) {
          // no_insert_possiblefiles++;
          // }
        }
      //}
      i++;
    }
    br.close();
  }
}
