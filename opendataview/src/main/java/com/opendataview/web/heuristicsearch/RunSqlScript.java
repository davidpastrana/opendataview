package com.opendataview.web.heuristicsearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opendataview.web.model.LocationModel;
import com.opendataview.web.persistence.LocationServiceDAO;

public class RunSqlScript {
	private final static Logger log = LoggerFactory.getLogger(RunSqlScript.class);
	private static int new_id = 0;

	public static void runSqlScript(File source, Connection conn, StringBuilder outputinfo, boolean autodetectSchema,
			LocationServiceDAO locationServiceDAO) throws ClassNotFoundException, IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(source), "utf-8"));

		// we return the greatest id or 0 in case is the first insert-id
		String sql = "SELECT coalesce(max(id), 0) FROM locations";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				new_id = Integer.valueOf(rs.getString(1));
			}
			rs.close();

			String line = br.readLine();
			if (line == null) {
				log.info("\nFile empty! No inserts to execute.");
				outputinfo.append("\n\nFile empty! No inserts to execute.");
			}

			boolean hasid = false;
			boolean isupdate = false;
			int countinserts = 0;
			int num_updates = 0;
			int num_inserts = 0;
			while (line != null) {
				new_id++;

				hasid = line.contains("INSERT INTO locations(id");

				List<String> listValues = new ArrayList<String>();
				listValues = Arrays.asList(
						line.split("VALUES\\(")[1].split("\\);$")[0].split(",(?=(?:[^\']*\'[^\']*\')*[^\']*$)"));

				// we clean all single quotes
				for (int i = 0; i < listValues.size(); i++) {
					listValues.set(i, listValues.get(i).replaceAll("'", ""));
				}
				// log.info("\n\nvalues1 " + Arrays.asList(listValues));
				// "INSERT INTO
				// locations(name,description,type,address,postcode,city,latitude,longitude,website,phone,date,schedule,email,filename,population,elevation,username,source,date_published,date_updated,iconmarker,private_mode,data)
				// VALUES(");
				LocationModel o = new LocationModel();
				o.setName(listValues.get(0));
				o.setDescription(listValues.get(1));
				o.setType(listValues.get(2));
				o.setAddress(listValues.get(3));
				o.setPostcode(listValues.get(4));
				o.setCity(listValues.get(5));
				o.setLatitude(listValues.get(6));
				o.setLongitude(listValues.get(7));
				o.setWebsite(listValues.get(8));
				o.setPhone(listValues.get(9));
				o.setDate(listValues.get(10));
				o.setSchedule(listValues.get(11));
				o.setEmail(listValues.get(12));
				o.setFileName(listValues.get(13));
				o.setPopulation(listValues.get(14));
				o.setElevation(listValues.get(15));
				o.setUsername(listValues.get(16));
				o.setSource(listValues.get(17));
				o.setDate_published(listValues.get(18));
				o.setDate_updated(listValues.get(19));
				o.setIconmarker(listValues.get(20));
				o.setPrivate_mode(Boolean.valueOf(listValues.get(21)));
				o.setData(listValues.get(22));

				// check if requires an sql update or not, and execute the corresponding query
				// log.info("query id value is: " + queryid_value);
				if (listValues != null) {
					try {
						if (hasid) {
							// we take the id value from the query
							Pattern pattern = Pattern.compile("\\((.*?)\\,");
							String queryid_value = pattern.matcher(line).group(2);
							isupdate = locationServiceDAO.checkLocationExistanceById(queryid_value);
						} else if (!listValues.get(6).equals("null") && !listValues.get(7).equals("null")) {
							isupdate = locationServiceDAO.checkLocationExistanceByNameLatLng(o);
						}
					} catch (PersistenceException | NullPointerException e) {
						log.error("Error (CheckUpdate): " + e);
						outputinfo.append("\n\nError database (CheckUpdate):  " + e.getLocalizedMessage());
					}
					// log.info("is update? " + isupdate);
					// log.info("hasid? " + hasid);
					if (isupdate == true) {
						// log.info("we update!!");
						try {
							locationServiceDAO.updateQuery(listValues, hasid);
						} catch (PersistenceException e) {
							log.error("Error (CreateUpdate): " + e);
							outputinfo.append("\n\nError database (CreateUpdate):  " + e.getLocalizedMessage());
						}
						num_updates++;
					} else if (isupdate == false) {
						// log.info("we insert!!");
						// if has id is true and is not an update => we create a new id being the last
						// existing id number
						if (hasid == false) {
							line = line.replaceFirst("\\(", "(id,").replace("VALUES('", "VALUES(" + new_id + ",'");
						} else {
							line = line.replace("VALUES('.*,", "VALUES(" + new_id + ",'");
						}
						// log.info("line to exec " + line);

						try {
							locationServiceDAO.executeQuery(line);
						} catch (PersistenceException e) {
							log.error("Error (InsertSQL): " + e);
							outputinfo.append("\n\nError database (InsertSQL):  " + e.getLocalizedMessage());
						}
						num_inserts++;
					}
				}
				line = br.readLine();
				countinserts++;
			}
			br.close();
			ps.close();
			rs.close();

			log.info("\nTotal: " + countinserts + " database executions, being " + num_inserts + " inserts and "
					+ num_updates + " updates.\n");
			outputinfo.append("\n\nTotal: " + countinserts + " database executions, being " + num_inserts
					+ " inserts and " + num_updates + " updates.\n");
		} catch (PersistenceException | NumberFormatException | SQLException e) {
			StringWriter error = new StringWriter();
			outputinfo.append("Error occurred: " + e.getMessage() + "\n\n");
			e.printStackTrace(new PrintWriter(error));
			outputinfo.append(error);
		}

	}
}
