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
		DELIMITER = "'" + DELIMITER + "'";

		// ADD VALUE WHEN WE INSERT SOME NEW FIELD INTO LOCATIONS TABLE - ALWAYS TO ADD
		// AT THE END - MAKE SURE IS IN BOTH APPENDS AND SAME ORDER
//		wr.append("'Title" + DELIMITER + "Description" + DELIMITER + "Type" + DELIMITER + "Address" + DELIMITER
//				+ "Postalcode" + DELIMITER + "City" + DELIMITER + "Latitude" + DELIMITER + "Longitude" + DELIMITER
//				+ "Polygon" + DELIMITER + "Website" + DELIMITER + "Phone" + DELIMITER + "Date" + DELIMITER + "Schedule"
//				+ DELIMITER + "Email" + DELIMITER + "Filename" + DELIMITER + "Population" + DELIMITER + "Elevation"
//				+ DELIMITER + "Last editor" + DELIMITER + "Data publisher" + DELIMITER + "Published date" + DELIMITER
//				+ "Last update" + DELIMITER + "Description" + DELIMITER + "Marker icon" + DELIMITER + "Data'");
//		wr.append(NEW_LINE);

		for (LocationModel loc : new_format) {
			// if (loc.getLatitude() != null && loc.getLongitude() != null) {

			wr.append("'");
			wr.append(loc.getName() == null ? "" : loc.getName());
			wr.append(DELIMITER);
			wr.append(loc.getDescription() == null ? "" : loc.getDescription());
			wr.append(DELIMITER);
			wr.append(loc.getType() == null ? "" : loc.getType());
			wr.append(DELIMITER);
			wr.append(loc.getAddress() == null ? "" : loc.getAddress());
			wr.append(DELIMITER);
			wr.append(loc.getPostcode() == null ? "" : loc.getPostcode());
			wr.append(DELIMITER);
			wr.append(loc.getCity() == null ? "" : loc.getCity());
			wr.append(DELIMITER);
			wr.append(loc.getLatitude() == null ? "" : loc.getLatitude().toString().toString());
			wr.append(DELIMITER);
			wr.append(loc.getLongitude() == null ? "" : loc.getLongitude().toString());
			wr.append(DELIMITER);
			wr.append(loc.getWebsite() == null ? "" : loc.getWebsite());
			wr.append(DELIMITER);
			wr.append(loc.getPhone() == null ? "" : loc.getPhone());
			wr.append(DELIMITER);
			wr.append(loc.getDate() == null ? "" : loc.getDate());
			wr.append(DELIMITER);
			wr.append(loc.getSchedule() == null ? "" : loc.getSchedule());
			wr.append(DELIMITER);
			wr.append(loc.getEmail() == null ? "" : loc.getEmail());
			wr.append(DELIMITER);
			wr.append(loc.getFileName() == null ? "" : loc.getFileName());
			wr.append(DELIMITER);
			wr.append(loc.getPopulation() == null ? "" : loc.getPopulation());
			wr.append(DELIMITER);
			wr.append(loc.getElevation() == null ? "" : loc.getElevation());
			wr.append(DELIMITER);
			wr.append(loc.getUsername() == null ? "" : loc.getUsername());
			wr.append(DELIMITER);
			wr.append(loc.getSource() == null ? "" : loc.getSource());
			wr.append(DELIMITER);
			wr.append(loc.getDate_published() == null ? "" : loc.getDate_published());
			wr.append(DELIMITER);
			wr.append(loc.getDate_updated() == null ? "" : loc.getDate_updated());
			wr.append(DELIMITER);
			wr.append(loc.getIconmarker() == null ? "" : loc.getIconmarker());
			wr.append(DELIMITER);
			wr.append(String.valueOf(loc.getPrivate_mode()));
			wr.append(DELIMITER);
			wr.append(loc.getData() == null ? "" : loc.getData());
			wr.append("'");
			wr.append(NEW_LINE);

			// }
		}
		wr.close();
	}

	public GenerateFile(PageParameters parameters) throws IOException {
		super(parameters);
	}

	private final static Logger log = LoggerFactory.getLogger(GenerateFile.class);

	public static void createSQLInserts(File source, BufferedWriter buffer, StringBuilder outputinfo)
			throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(source), "utf-8"));

		String line;

		// log.info("line " + br.readLine());
		int no_inserts = 0;
		while ((line = br.readLine()) != null) {

			// we split the row by ";" separator
			String[] value = line.split(DELIMITER);
			int j = 0;

			int pos_latitude = 6;
			int pos_longitude = 7;
			int pos_polygon = 8;
			// we create the insert just if we have latitude and longitude
			// if (!value[pos_latitude].contentEquals("null") &&
			// !value[pos_longitude].contentEquals("null")) {
			try {
//				log.info("LAT IS " + loc.getLatitude());
//				log.info("LNG IS " + loc.getLongitude());

				if ((!value[pos_latitude].isEmpty() && !value[pos_longitude].isEmpty())
						|| !value[pos_polygon].isEmpty()) {
					// ADD VALUE WHEN WE INSERT SOME NEW FIELD INTO LOCATIONS TABLE - ALWAYS TO ADD
					// AT THE END - MAKE SURE IS SAME ORDER
					buffer.append(
							"INSERT INTO locations(name,description,type,address,postcode,city,latitude,longitude,website,phone,date,schedule,email,filename,population,elevation,username,source,date_published,date_updated,iconmarker,private_mode,data) VALUES(");
					j = 0;
					while (j < value.length) {
						// value[j] = value[j].replaceAll("\'", "").replaceAll("\"", "").replaceAll(",",
						// ";");

						value[j] = value[j].trim().replaceAll("\'", "");
//						if (!value[j].isEmpty()) {
//							buffer.append("'" + value[j] + "'");
//						} else {
//							buffer.append("''"); // make field empty in database and not null
//						}
						if (!value[j].isEmpty() && (j == pos_latitude || j == pos_longitude)) {
							buffer.append(value[j]);
						} else if (!value[j].isEmpty()) {
							buffer.append("'" + value[j] + "'");
						} else {
							buffer.append("''"); // make field empty in database and not null
						}

						// we insert separator ","
						if (j != value.length - 1) {
							buffer.append(DELIMITER2);
						}
						j++;
					}
					buffer.append(");");
					buffer.append(NEW_LINE);
				} else {
					no_inserts++;
				}
			} catch (NumberFormatException e) {
				log.info("We do not insert!!!! latlng are failing: " + e.getMessage());
			}
		}
		if (no_inserts > 0) {
			log.info("\n\n" + no_inserts + " Failed to upload! either latitude or longitude could be missing.");
			outputinfo.append(
					"\n\n" + no_inserts + " Failed to upload! either latitude or longitude could be missing.\n");
		}
		br.close();
		buffer.close();
	}
}
