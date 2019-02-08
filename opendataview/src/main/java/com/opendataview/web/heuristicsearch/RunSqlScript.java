package com.opendataview.web.heuristicsearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opendataview.web.persistence.LocationServiceDAO;

public class RunSqlScript {
	private final static Logger log = LoggerFactory.getLogger(RunSqlScript.class);
	private static int new_id = 0;

	public static void runSqlScript(File source, Connection conn, StringBuilder outputinfo, boolean autodetectSchema,
			LocationServiceDAO locationServiceDAO) throws ClassNotFoundException, SQLException, IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(source), "utf-8"));

		// we return the greatest id or 0 as the starting id
		String sql = "SELECT coalesce(max(id), 0) FROM locations";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			new_id = Integer.valueOf(rs.getString(1));
		}
		rs.close();

//		log.info("new id to include: " + new_id);
//		log.info("file taken: " + source.getAbsolutePath());

		String line = br.readLine();

		if (line == null) {
			log.info("File empty! No inserts to execute.");
		}

		boolean hasid = false;
		boolean isupdate = false;
		int countinserts = 0;

		int num_updates = 0;
		int num_inserts = 0;
		while (line != null) {
			new_id++;

			String queryid_field = line.split(",")[0].split("\\(")[1].toLowerCase();

			// splits on colon and gets the first part being "INSERT INTO locations(id"
			String queryid_value = line.replaceAll("'", "").split("VALUES\\(")[1].split(",")[0];

			// splits on colon and gets values out from query INSERT VALUES (__);
			String[] listValues = line.replaceAll("'", "").split("VALUES\\(")[1].split("\\);$")[0].split(",");

			log.info("values " + Arrays.toString(listValues));

			// check if requires an sql update or not, by executing the inster query

			log.info("query id value is: " + queryid_value);
			if (listValues != null) {
				if (queryid_field.contentEquals("id")) {
					hasid = true;
					isupdate = locationServiceDAO.checkLocationExistanceByID(queryid_value);
				} else {

					isupdate = locationServiceDAO.checkLocationExistanceByOtherName(listValues);

				}

				// line = line.replaceAll(";", ",");

				log.info("is update? " + isupdate);
				log.info("hasid? " + hasid);
				if (isupdate) {
					locationServiceDAO.updateQuery(listValues, hasid);
					num_updates++;
				} else {
					// if has id is true and is not an update => we create a new id being the last
					// existing id number
					if (hasid == false) {
						line = line.replaceFirst("\\(", "(id,").replace("VALUES(", "VALUES(" + new_id + ",");
					} else {
						line = line.replace("VALUES(.*,", "VALUES(" + new_id + ",");
					}
					line = line.replaceAll(",,", ",'',");
					log.info("line to execute: " + line);

					locationServiceDAO.executeQuery(line);
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
		outputinfo.append("\n\nTotal: " + countinserts + " database executions, being " + num_inserts + " inserts and "
				+ num_updates + " updates.\n");

	}
}
