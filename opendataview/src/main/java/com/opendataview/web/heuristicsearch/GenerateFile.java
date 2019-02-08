package com.opendataview.web.heuristicsearch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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

public class GenerateFile extends MainClass {

	private static final long serialVersionUID = 1L;

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

		// ADD VALUE WHEN WE INSERT SOME NEW FIELD INTO LOCATIONS TABLE - ALWAYS TO ADD
		// AT THE END - MAKE SURE IS IN BOTH APPENDS AND SAME ORDER
		wr.append("Title" + DELIMITER + "Description" + DELIMITER + "Type" + DELIMITER + "Address" + DELIMITER
				+ "Postal code" + DELIMITER + "City" + DELIMITER + "Latitude" + DELIMITER + "Longitude" + DELIMITER
				+ "Website" + DELIMITER + "Phone" + DELIMITER + "Date" + DELIMITER + "Schedule" + DELIMITER + "Email"
				+ DELIMITER + "CsvName" + DELIMITER + "Population" + DELIMITER + "Elevation" + DELIMITER + "Last editor"
				+ DELIMITER + "Data publisher" + DELIMITER + "Published date" + DELIMITER + "Last update" + DELIMITER
				+ "Description" + DELIMITER + "Marker icon" + DELIMITER + "Extra info");
		wr.append(NEW_LINE);

		for (LocationModel loc : new_format) {
			wr.append(loc.getName());
			wr.append(DELIMITER);
			wr.append(loc.getDescription());
			wr.append(DELIMITER);
			wr.append(loc.getType());
			wr.append(DELIMITER);
			wr.append(loc.getAddress());
			wr.append(DELIMITER);
			wr.append(loc.getPostcode());
			wr.append(DELIMITER);
			wr.append(loc.getCity());
			wr.append(DELIMITER);
			wr.append(String.valueOf(loc.getLatitude()));
			wr.append(DELIMITER);
			wr.append(String.valueOf(loc.getLongitude()));
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
			wr.append(loc.getUsername());
			wr.append(DELIMITER);
			wr.append(loc.getSource());
			wr.append(DELIMITER);
			wr.append(loc.getDate_published());
			wr.append(DELIMITER);
			wr.append(loc.getDate_updated());
			wr.append(DELIMITER);
			wr.append(loc.getIconmarker());
			wr.append(DELIMITER);
			wr.append(loc.getOtherInfo());
			wr.append(NEW_LINE);
		}
		wr.close();
	}

	// CREATE NEW CSV FILE IN ENRICHED DIRECTORY (dest)

	public static void CreateEnrichedCSV(File source, File dest, ArrayList<LocationModel> new_format)
			throws IOException {

		PrintWriter wr = new PrintWriter(
				dest.toString().replace("new_format", "enriched").replace(".csv", "-enriched.csv"), "utf-8");

		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(source), "iso-8859-1"));

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

	public GenerateFile(PageParameters parameters) throws IOException {
		super(parameters);
	}

	private final static Logger log = LoggerFactory.getLogger(GenerateFile.class);

	public static void createSQLInserts(File source, BufferedWriter buffer, StringBuilder outputinfo)
			throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(source), "utf-8"));

		String line;

		log.info("file taken: " + source.getAbsolutePath());

		log.info("line " + br.readLine());
		while ((line = br.readLine()) != null) {

			// we do not insert the header
			// if (i != 0) {

			// we split the row by ";" separator
			String[] value = line.split(DELIMITER);
			// log.info("out!!");
			int j = 0;

			int pos_latitude = 6;
			int pos_longitude = 7;
			// we create the insert just if we have latitude and longitude
			if (!value[pos_latitude].contentEquals("null") && !value[pos_longitude].contentEquals("null")) {

				// ADD VALUE WHEN WE INSERT SOME NEW FIELD INTO LOCATIONS TABLE - ALWAYS TO ADD
				// AT THE END - MAKE SURE IS SAME ORDER
				buffer.append(
						"INSERT INTO locations(name,description,type,address,postcode,city,latitude,longitude,website,phone,date,schedule,email,csvName,population,elevation,username,source,date_published,date_updated,iconmarker,otherinfo,rating,nrating) VALUES(");
				j = 0;
				while (j < value.length) {
					value[j] = value[j].replaceAll("\'", "").replaceAll("\"", "").replaceAll(",", ";");

					// we format strings with single quotes except latitude (position 7) and
					// longitude (position 8)
					if (!value[j].contentEquals("null") && j != pos_latitude && j != pos_longitude) {
						buffer.append("'");
						buffer.append(value[j]);
						buffer.append("'");

						// we add lat and long without quotes
					} else if (j == pos_latitude || j == pos_longitude) {
						buffer.append(value[j]);

						// its a null value
					} else {
						buffer.append("''"); // make field empty in database and not null
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
				log.info("We could not find any latitude or longitude value to upload into database.");
				outputinfo.append("\nWe could not find any latitude or longitude value to upload into database.\n");
			}

		}
		br.close();
		buffer.close();
	}
}
