package com.opendataview.web.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.jpa.QueryHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opendataview.web.model.LocationModel;
import com.vividsolutions.jts.geom.GeometryFactory;

@Service
public class LocationServiceDAOImpl implements LocationServiceDAO {

	private final static Logger log = LoggerFactory.getLogger(LocationServiceDAOImpl.class);

	protected EntityManager entityManager;

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@PersistenceContext(unitName = "opendataview")
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	@Transactional
	public void storeLocationModel(List<LocationModel> locationlist) {
		for (LocationModel locationModel : locationlist) {
			entityManager.persist(locationModel);
			setEntityManager(entityManager);
		}
	}

	@Override
	@Transactional
	public void removeLocationModel(LocationModel locationModel) {
		LocationModel item = entityManager.merge(locationModel);
		entityManager.remove(item);
	}

	@Override
	@Transactional
	public void removeLocationByName(String csvName) {
		Query query = entityManager.createNativeQuery("delete from locations l where l.csvName = ?",
				LocationModel.class);
		query.setParameter(1, csvName);
		query.executeUpdate();
	}

	@Override
	@Transactional
	public List<LocationModel> showLocationByName(String csvName) {
		List<LocationModel> resultList = getEntityManager()
				.createQuery("select l from locations l where l.csvName = '" + csvName + "'", LocationModel.class)
				.getResultList();
		return resultList;
	}

	;

	@Override
	@Transactional
	public boolean checkLocationExistanceByID(String queryidvalue) {

		Query query = getEntityManager().createQuery("select l from locations l where l.id = " + queryidvalue + "",
				LocationModel.class);

		if (!query.getResultList().isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	@Transactional
	public List<LocationModel> getLocationByID(String queryidvalue) {

		List<LocationModel> resultList = getEntityManager()
				.createQuery("select l from locations l where l.id = " + queryidvalue + "", LocationModel.class)
				.getResultList();
		return resultList;
	}

	@Override
	@Transactional
	public boolean checkLocationExistanceByOtherName(LocationModel locationModel) {

//		log.info("QUERY TO CHECK: select l from locations l where l.latitude = '" + locationModel.getLatitude()
//				+ "' and l.longitude = '" + locationModel.getLongitude() + "' and lower(csvname) = '"
//				+ locationModel.getCsvName().toLowerCase() + "'");

		if (locationModel != null) {
			Query query = getEntityManager().createQuery("select l from locations l where l.latitude = '"
					+ locationModel.getLatitude() + "' and l.longitude = '" + locationModel.getLongitude()
					+ "' and lower(csvname) = '" + locationModel.getCsvName().toLowerCase() + "'", LocationModel.class);

			if (query.getResultList() == null || query.getResultList().isEmpty()) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	@Override
	@Transactional
	public void updateQuery(List<String> listValues, boolean hasid) {

		// Not needed anymore
//		for (int i = 0; i < listValues.size(); i++) {
//			log.info("val2: " + listValues.get(i));
//		}

		// log.info("values to update" + Arrays.toString(listValues));
		// ADD VALUE WHEN WE INSERT SOME NEW FIELD INTO LOCATIONS TABLE - ALWAYS TO ADD
		// AT THE END - MAKE SURE IS IN THE SAME ORDER

		Query query = null;

		// int pos = 0;

		if (hasid == true) {

			// id,name,type,address,district,postcode,city,latitude,longitude,description,website,phone,date,schedule,email,csvName,population,elevation,username,source,date_published,date_updated,otherinfo,rating,nrating
			query = entityManager.createNativeQuery(
					"update locations set name=:name,description=:description,type=:type,address=:address,postcode=:postcode,city=:city,latitude=:latitude,longitude=:longitude,"
							+ "website=:website,phone=:phone,date=:date,schedule=:schedule,email=:email,csvName=:csvName,population=:population,elevation=:elevation,username=:username,"
							+ "source=:source,date_updated=:date_updated,iconmarker=:iconmarker,private_mode=:private_mode,otherinfo=:otherinfo where id = :id",
					LocationModel.class);
			// USED ONLY FOR THE BACKUPS TO NOT CHANGE
			query.setParameter("id", new Float(listValues.get(0)));

		} else {
			// ADD VALUE WHEN WE INSERT SOME NEW FIELD INTO LOCATIONS TABLE - ALWAYS TO ADD
			// AT THE END - MAKE SURE IS IN THE SAME ORDER
			// Make sure the update selected values appear to be the correct numbers (spent
			// 2h for that)
			// pos = -1;
			String latitude = listValues.get(6);
			String longitude = listValues.get(7);
			String csvname = listValues.get(13);

//			log.info(
//					"QUERY TO UPDATE: update locations set name=:name,description=:description,type=:type,address=:address,postcode=:postcode,city=:city,latitude=:latitude,longitude=:longitude,"
//							+ "website=:website,phone=:phone,date=:date,schedule=:schedule,email=:email,csvName=:csvName,population=:population,elevation=:elevation,username=:username,"
//							+ "source=:source,date_updated=:date_updated,iconmarker=:iconmarker,private_mode=:private_mode,otherinfo=:otherinfo where latitude = '"
//							+ latitude + "' and longitude = '" + longitude + "' and lower(csvname) = '"
//							+ csvname.toLowerCase() + "'");

			query = entityManager.createNativeQuery(
					"update locations set name=:name,description=:description,type=:type,address=:address,postcode=:postcode,city=:city,latitude=:latitude,longitude=:longitude,"
							+ "website=:website,phone=:phone,date=:date,schedule=:schedule,email=:email,csvName=:csvName,population=:population,elevation=:elevation,username=:username,"
							+ "source=:source,date_updated=:date_updated,iconmarker=:iconmarker,private_mode=:private_mode,otherinfo=:otherinfo where latitude = '"
							+ latitude + "' and longitude = '" + longitude + "' and lower(csvname) = '"
							+ csvname.toLowerCase() + "'",
					LocationModel.class);
		}
		query.setParameter("name", listValues.get(0));
		query.setParameter("description", listValues.get(1));
		query.setParameter("type", listValues.get(2));
		query.setParameter("address", listValues.get(3));
		query.setParameter("postcode", listValues.get(4));
		query.setParameter("city", listValues.get(5));
		query.setParameter("latitude", new Float(listValues.get(6)));
		query.setParameter("longitude", new Float(listValues.get(7)));
		query.setParameter("website", listValues.get(8));
		query.setParameter("phone", listValues.get(9));
		query.setParameter("date", listValues.get(10));
		query.setParameter("schedule", listValues.get(11));
		query.setParameter("email", listValues.get(12));
		query.setParameter("csvName", listValues.get(13));
		query.setParameter("population", listValues.get(14));
		query.setParameter("elevation", listValues.get(15));
		query.setParameter("username", listValues.get(16));
		query.setParameter("source", listValues.get(17));
		// query.setParameter("date_published", listValues.get(18));
		query.setParameter("date_updated", listValues.get(19));
		query.setParameter("iconmarker", listValues.get(20));
		query.setParameter("private_mode", Boolean.valueOf(listValues.get(21)));
		query.setParameter("otherinfo", listValues.get(22));
		query.executeUpdate();
	}

	@Override
	@Transactional
	public void executeQuery(String query) {
		getEntityManager().createNativeQuery(query, LocationModel.class).executeUpdate();
	}

	@Override
	@Transactional
	public void removeAllLocations(String username) {
		Query query = entityManager.createNativeQuery("delete from locations where username = :admin",
				LocationModel.class);
		query.setParameter("admin", username);
		query.executeUpdate();
	}

	@Override
	@Transactional
	public void updateLocationModel(LocationModel locationModel) {
		LocationModel item = entityManager.merge(locationModel);
		entityManager.merge(item);
	}

	GeometryFactory geometryFactory = new GeometryFactory();

//	FullTextEntityManager fullTextEntityManager 
//	  = Search.getFullTextEntityManager(entityManager);
	@Override
	@Transactional
	public List<LocationModel> readLocationModel() throws DataAccessException {
		List<LocationModel> resultList = getEntityManager().createQuery("from locations", LocationModel.class)
				.setHint(QueryHints.HINT_CACHEABLE, true).getResultList();
		return resultList;
	}

	@Override
	@Transactional
	public List<LocationModel> searchLocationModel(String location_value) throws DataAccessException {

//		log.info(
//				"HOUR QUERY IS: select l from locations l where lower(replace(replace(otherinfo,'##',''),' ', '')) like '%"
//						+ location_value.replaceAll("\\s+", "").toLowerCase() + "%'");

		List<LocationModel> resultList = null;
		if (location_value.contains("q:")) {
			String sqlquery = location_value.split("q:")[1].trim();
			// log.info("query: select l from locations l where " + sqlquery);
			resultList = getEntityManager()
					.createQuery("select l from locations l where " + sqlquery, LocationModel.class).getResultList();
//		}
//		else if (location_value.contains("q2:")) {
//			String sqlquery = location_value.split("q2:")[1].trim().toLowerCase();
//			log.info("query: select l from locations l where lower(otherinfo) like '%" + sqlquery + "%'");
//			resultList = getEntityManager()
//					.createQuery("select l from locations l where lower(otherinfo) like '%" + sqlquery + "%'",
//							LocationModel.class)
//					.getResultList();
		} else {

			resultList = getEntityManager()
					.createQuery(
							"select l from locations l where lower(replace(replace(otherinfo,'##',''),' ', '')) like '%"
									+ location_value.replaceAll("\\s+", "").toLowerCase() + "%'",
							LocationModel.class)
					.getResultList();
			// query.setParameter(1, "%"+location_value+"%")
		}
		return resultList;
	}

	@Override
	@Transactional
	public List<LocationModel> searchLocationByFileName(String filename) throws DataAccessException {

		// log.info("Query: select l from locations l where csvname=" + filename);
		List<LocationModel> resultList = getEntityManager()
				.createQuery("from locations where csvname = :filename", LocationModel.class)
				.setParameter("filename", filename).getResultList();
		// query.setParameter(1, "%"+location_value+"%");
		return resultList;
	}

}
