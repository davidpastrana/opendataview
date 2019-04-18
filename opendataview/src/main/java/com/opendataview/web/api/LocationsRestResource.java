package com.opendataview.web.api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.resource.gson.GsonRestResource;

import com.opendataview.web.model.LocationModel;

public class LocationsRestResource extends GsonRestResource {

	private static final long serialVersionUID = 1L;
	private final static Logger log = LoggerFactory.getLogger(LocationsRestResource.class);

	private List<LocationModel> locations = new ArrayList<LocationModel>();
	private List<String> datasets = new ArrayList<String>();
	private List<LocationModel> datasetLocations = new ArrayList<LocationModel>();

	@SpringBean
	protected EntityManager entityManager;

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public LocationsRestResource() {

		String url = "jdbc:postgresql://localhost:5432/opendataview";
		String user = "postgres";
		String password = "postgres";

		try (Connection con = DriverManager.getConnection(url, user, password);
				// id,address,archive,city,country,filename,currency,date,date_published,date_updated,description,document,elevation,email,iconmarker,latitude,longitude,name,number,data,percentage,phone,population,postcode,private_mode,schedule,source,street,type,image,username,website,year
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery(
						"SELECT id,address,archive,city,country,filename,currency,date,date_published,date_updated,description,document,elevation,email,iconmarker,latitude,longitude,name,number,data,percentage,phone,population,postcode,private_mode,schedule,source,street,type,image,username,website,year FROM locations")) {
			LocationModel l = null;
			while (rs.next()) {
				l = new LocationModel(rs.getLong("id"), rs.getString("address"), rs.getString("archive"),
						rs.getString("city"), rs.getString("country"), rs.getString("filename"),
						rs.getString("currency"), rs.getString("date"), rs.getString("date_published"),
						rs.getString("date_updated"), rs.getString("description"), rs.getString("document"),
						rs.getString("elevation"), rs.getString("email"), rs.getString("iconmarker"),
						rs.getBigDecimal("latitude"), rs.getBigDecimal("longitude"), rs.getString("name"),
						rs.getString("number"), rs.getString("data"), rs.getString("percentage"), rs.getString("phone"),
						rs.getString("population"), rs.getString("postcode"), rs.getBoolean("private_mode"),
						rs.getString("schedule"), rs.getString("source"), rs.getString("street"), rs.getString("type"),
						rs.getString("image"), rs.getString("username"), rs.getString("website"), rs.getString("year"));
				locations.add(l);
			}
			rs.close();
			st.close();
			con.close();

		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
			System.out.println(ex);
		}
	}

	@MethodMapping("/locations")
	public List<LocationModel> getAllLocations() {
		return locations;
	}

	@MethodMapping(value = "/locations/{locationIndex}")
	public LocationModel getLocationIndex(int locationIndex) {
		for (int i = 0; i < locations.size(); i++) {
			if (locations.get(i).getId() == locationIndex) {
				return locations.get(i);
			}
		}
		return null;
	}

	@MethodMapping("/datasets")
	public List<String> getAllDatasets() {
		datasets.clear();
		for (int i = 0; i < locations.size(); i++) {
			String ds = locations.get(i).getFileName();
			if (!datasets.contains(locations.get(i).getFileName())) {
				datasets.add(ds);
			}
		}
		return datasets;
	}

	@MethodMapping("/datasets/{datasetName}")
	public List<LocationModel> getDatasetLocations(String datasetName) {
		datasetLocations.clear();
		for (int i = 0; i < locations.size(); i++) {
			if (locations.get(i).getFileName().equals(datasetName)) {
				datasetLocations.add(locations.get(i));
			}
		}
		return datasetLocations;
	}

}
