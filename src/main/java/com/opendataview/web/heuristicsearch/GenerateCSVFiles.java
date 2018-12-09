package com.opendataview.web.heuristicsearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opendataview.web.model.LocationModel;

public class GenerateCSVFiles extends MainClass {

	private static final long serialVersionUID = 1L;

public GenerateCSVFiles(PageParameters parameters) throws IOException {
		super(parameters);
		// TODO Auto-generated constructor stub
	}

private final static Logger log = LoggerFactory.getLogger(GenerateCSVFiles.class);

  public static void copyFile(File source, File dest) throws IOException {
    OutputStream out = new FileOutputStream(dest);
    byte[] buffer = new byte[(int) source.length()];
    FileInputStream in = new FileInputStream(source);

    int n;
    int count = 0;
    while ((n = in.read(buffer)) > 0) {
      for (int i = 0; i < n; i++) {
        if (buffer[i] == '\n')
          count++;
      }
    }
    if (count < nrowchecks)
      log.info("we have less than " + nrowchecks);

    in.read(buffer);

    try {
      out.write(buffer);
    } finally {
      out.close();
      in.close();
    }
  }

  // CREATE NEW CSV FILE IN PROCESSED DIRECTORY (dest)

  public static void CreateFormattedCSV(File dest, ArrayList<LocationModel> new_format)
      throws FileNotFoundException, UnsupportedEncodingException {


    PrintWriter wr = new PrintWriter(dest, "utf-8");

    wr.append("Name" + DELIMITER + "Type" + DELIMITER + "Address" + DELIMITER + "District"
        + DELIMITER + "Postcode" + DELIMITER + "City" + DELIMITER + "Latitude" + DELIMITER
        + "Longitude" + DELIMITER + "Description" + DELIMITER + "Website" + DELIMITER + "Phone"
        + DELIMITER + "Date" + DELIMITER + "Schedule" + DELIMITER + "Email" + DELIMITER + "CsvName"
        + DELIMITER + "Population" + DELIMITER + "Elevation" + DELIMITER + "Other");
    wr.append(NEW_LINE);

    for (LocationModel loc : new_format) {
      wr.append(loc.getName());
      wr.append(DELIMITER);
      wr.append(loc.getType());
      wr.append(DELIMITER);
      wr.append(loc.getAddress());
      wr.append(DELIMITER);
      wr.append(loc.getDistrict());
      wr.append(DELIMITER);
      wr.append(loc.getPostcode());
      wr.append(DELIMITER);
      wr.append(loc.getCity());
      wr.append(DELIMITER);
      wr.append(String.valueOf(loc.getLatitude()));
      wr.append(DELIMITER);
      wr.append(String.valueOf(loc.getLongitude()));
      wr.append(DELIMITER);
      wr.append(loc.getDescription());
      wr.append(DELIMITER);
      wr.append(loc.getWebsite());
      wr.append(DELIMITER);
      wr.append(loc.getPhone());
      wr.append(DELIMITER);
      wr.append(loc.getDate());
      wr.append(DELIMITER);
      wr.append(loc.getSchedule());
      wr.append(DELIMITER);
      wr.append(loc.getEmail());
      wr.append(DELIMITER);
      wr.append(loc.getCsvName());
      wr.append(DELIMITER);
      wr.append(loc.getPopulation());
      wr.append(DELIMITER);
      wr.append(loc.getElevation());
      wr.append(DELIMITER);
      wr.append(loc.getDateUpdated());
      wr.append(loc.getDatePublished());
      wr.append(loc.getOther());
      wr.append(NEW_LINE);
    }
    wr.close();
  }

  // CREATE NEW CSV FILE IN ENRICHED DIRECTORY (dest)

  public static void CreateEnrichedCSV(File source, File dest, ArrayList<LocationModel> new_format)
      throws IOException {

    PrintWriter wr =
        new PrintWriter(dest.toString().replace("new_format", "enriched")
            .replace(".csv", "-enriched.csv"), "utf-8");

    BufferedReader br =
        new BufferedReader(new InputStreamReader(new FileInputStream(source), "iso-8859-1"));

    int i = 0;

    String line;
    while ((line = br.readLine()) != null) {
      wr.append(line);
      if (i == 0) {
        wr.append(DELIMITER);
        wr.append("Latitude");
        wr.append(DELIMITER);
        wr.append("Longitude");
      } else if (i < new_format.size()) {
        wr.append(DELIMITER);
        wr.append(String.valueOf(new_format.get(i - 1).getLatitude()));
        wr.append(DELIMITER);
        wr.append(String.valueOf(new_format.get(i - 1).getLongitude()));
      }
      wr.append(NEW_LINE);
      i++;
    }
    wr.close();
    br.close();

  }

}
