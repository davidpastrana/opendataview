package com.opendataview.web.persistence;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opendataview.web.model.LocationModel;
import com.opendataview.web.model.RatingModel;

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
		Query query = getEntityManager().createQuery("select l from locations l where l.csvName = '" + csvName + "'",
				LocationModel.class);
		List<LocationModel> resultList = query.getResultList();
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

		Query query = getEntityManager().createQuery("select l from locations l where l.id = " + queryidvalue + "",
				LocationModel.class);
		List<LocationModel> resultList = query.getResultList();
		return resultList;
	}

	@Override
	@Transactional
	public boolean checkLocationExistanceByOtherName(List<String> listValues) {

		String latitude = listValues.get(6);
		String longitude = listValues.get(7);
		String csvname = listValues.get(13);

		log.info("values to check" + Arrays.asList(listValues));

		log.info("QUERY TO CHECK: select l from locations l where l.latitude = '" + latitude + "' and l.longitude = '"
				+ longitude + "' and lower(csvname) = '" + csvname.toLowerCase() + "'");

		if (listValues != null) {

//		 //we get the desired info to check if is an update, being after the third double-hash symbol
//		  String value = listValues[21].substring(listValues[21].indexOf(" ## ", 1 + listValues[21].indexOf(" ## ", 1 + listValues[21].indexOf(" ## ")))+4);
////		  log.info("DESIRED INFO "+value);
			log.info("select l from locations l where l.latitude = '" + latitude + "' and l.longitude = '" + longitude
					+ "' and lower(csvname) = '" + csvname.toLowerCase() + "'");
//		  Query query = getEntityManager().createQuery("select l from locations l where lower(otherinfo) like '%"+value.toLowerCase()+"'",

			Query query = getEntityManager()
					.createQuery(
							"select l from locations l where l.latitude = '" + latitude + "' and l.longitude = '"
									+ longitude + "' and lower(csvname) = '" + csvname.toLowerCase() + "'",
							LocationModel.class);

			if (query.getResultList() == null || query.getResultList().isEmpty()) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	@Transactional
	public void updateQuery(List<String> listValues, boolean hasid) {

		// Not needed anymore
		for (int i = 0; i < listValues.size(); i++) {
			log.info("val2: " + listValues.get(i));
		}

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
							+ "source=:source,date_updated=:date_updated,iconmarker=:iconmarker,otherinfo=:otherinfo,rating=:rating,nrating=:nrating,iconmarker=:iconmarker where id = :id",
					LocationModel.class);
			query.setParameter("id", new Float(listValues.get(0)));

			// USED ONLY FOR THE BACKUPS TO NOT CHANGE

//			query.setParameter("name", listValues[pos + 1]);
//			query.setParameter("description", listValues[pos + 2]);
//			query.setParameter("type", listValues[pos + 3]);
//			query.setParameter("address", listValues[pos + 4]);
//			query.setParameter("postcode", listValues[pos + 5]);
//			query.setParameter("city", listValues[pos + 6]);
//			query.setParameter("latitude", new BigDecimal(listValues[pos + 7].toString()));
//			query.setParameter("longitude", new BigDecimal(listValues[pos + 8].toString()));
//			query.setParameter("website", listValues[pos + 9]);
//			query.setParameter("phone", listValues[pos + 10]);
//			query.setParameter("date", listValues[pos + 11]);
//			query.setParameter("schedule", listValues[pos + 12]);
//			query.setParameter("email", listValues[pos + 13]);
//			query.setParameter("csvName", listValues[pos + 14]);
//			query.setParameter("population", listValues[pos + 15]);
//			query.setParameter("elevation", listValues[pos + 16]);
//			query.setParameter("username", listValues[pos + 17]);
//			query.setParameter("source", listValues[pos + 18]);
//			// query.setParameter("date_published", listValues[pos+19]);
//			query.setParameter("date_updated", listValues[pos + 20]);
//			query.setParameter("iconmarker", listValues[pos + 21]);
//			query.setParameter("otherinfo", listValues[pos + 22]);
//			query.setParameter("rating", Double.parseDouble(listValues[pos + 23]));
//			query.setParameter("nrating", Integer.parseInt(listValues[pos + 24]));
//			query.executeUpdate();
		} else {
			// ADD VALUE WHEN WE INSERT SOME NEW FIELD INTO LOCATIONS TABLE - ALWAYS TO ADD
			// AT THE END - MAKE SURE IS IN THE SAME ORDER
			// Make sure the update selected values appear to be the correct numbers (spent
			// 2h for that)
			// pos = -1;
			String latitude = listValues.get(6);
			String longitude = listValues.get(7);
			String csvname = listValues.get(13);

			log.info(
					"QUERY TO UPDATE: update locations set name=:name,description=:description,type=:type,address=:address,postcode=:postcode,city=:city,latitude=:latitude,longitude=:longitude,"
							+ "website=:website,phone=:phone,date=:date,schedule=:schedule,email=:email,csvName=:csvName,population=:population,elevation=:elevation,username=:username,"
							+ "source=:source,date_updated=:date_updated,iconmarker=:iconmarker,otherinfo=:otherinfo,rating=:rating,nrating=:nrating where latitude = '"
							+ latitude + "' and longitude = '" + longitude + "' and lower(csvname) = '"
							+ csvname.toLowerCase() + "'");

			query = entityManager.createNativeQuery(
					"update locations set name=:name,description=:description,type=:type,address=:address,postcode=:postcode,city=:city,latitude=:latitude,longitude=:longitude,"
							+ "website=:website,phone=:phone,date=:date,schedule=:schedule,email=:email,csvName=:csvName,population=:population,elevation=:elevation,username=:username,"
							+ "source=:source,date_updated=:date_updated,iconmarker=:iconmarker,otherinfo=:otherinfo,rating=:rating,nrating=:nrating where latitude = '"
							+ latitude + "' and longitude = '" + longitude + "' and lower(csvname) = '"
							+ csvname.toLowerCase() + "'",
					LocationModel.class);

//			log.info("long issss " + listValues[pos + 8]);
//			log.info("web to add " + listValues[pos + 9]);

			// query.setParameter("id", new BigInteger(listValues[pos]));

//			query.setParameter("name", listValues[0]);
//			query.setParameter("description", listValues[1]);
//			query.setParameter("type", listValues[2]);
//			query.setParameter("address", listValues[3]);
//			query.setParameter("postcode", listValues[4]);
//			query.setParameter("city", listValues[5]);
//			query.setParameter("latitude", new BigDecimal(listValues[7]));
//			query.setParameter("longitude", new BigDecimal(listValues[8]));
//			query.setParameter("website", listValues[9]);
//			query.setParameter("phone", listValues[10]);
//			query.setParameter("date", listValues[11]);
//			query.setParameter("schedule", listValues[12]);
//			query.setParameter("email", listValues[13]);
//			query.setParameter("csvName", listValues[14]);
//			query.setParameter("population", listValues[15]);
//			query.setParameter("elevation", listValues[16]);
//			query.setParameter("username", listValues[17]);
//			query.setParameter("source", listValues[18]);
//			// query.setParameter("date_published", listValues[19]);
//			query.setParameter("date_updated", listValues[20]);
//			query.setParameter("iconmarker", listValues[21]);
//			query.setParameter("otherinfo", listValues[22]);
//			query.setParameter("rating", Double.parseDouble(listValues[23]));
//			query.setParameter("nrating", Integer.parseInt(listValues[24]));

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
		query.setParameter("otherinfo", listValues.get(21));
		query.setParameter("rating", Double.parseDouble(listValues.get(22)));
		query.setParameter("nrating", Integer.parseInt(listValues.get(23)));
		query.executeUpdate();
	}

	@Override
	@Transactional
	public void executeQuery(String query) {
		getEntityManager().createNativeQuery(query, LocationModel.class).executeUpdate();

//	    Query query = entityManager.createNativeQuery("insert into locations (id,name,type,address,district,postcode,city,latitude,longitude,description,website,phone,date,schedule,email,csvName,population,elevation,username,source,date_published,date_updated,otherinfo,rating,nrating)"
//	    		+ " values (:id,:name,:type,:address,:district,:postcode,:city,:latitude,:longitude,:description,:website,:phone,:date,:schedule,:email,:csvName,:population,:elevation,:username,:source,:date_published,:date_updated,:otherinfo,:rating,:nrating)",
//              LocationModel.class);

//	    for (int i=0; i<listValues.length; i++) {
//	    	listValues[i] = listValues[i].replaceAll("'", "");
//	    }
//	    
//	    query.setParameter("id", new BigInteger(listValues[0]));
//	    query.setParameter("name", listValues[1]);
//	    query.setParameter("type", listValues[2]);
//	    query.setParameter("address", listValues[3]);
//	    query.setParameter("district", listValues[4]);
//	    query.setParameter("postcode", listValues[5]);
//	    query.setParameter("city", listValues[6]);
//	    query.setParameter("latitude", new BigDecimal(listValues[7]));
//	    query.setParameter("longitude", new BigDecimal(listValues[8]));
//	    query.setParameter("description", listValues[9]);
//	    query.setParameter("website", listValues[10]);
//	    query.setParameter("phone", listValues[11]);
//	    query.setParameter("date", listValues[12]);
//	    query.setParameter("schedule", listValues[13]);
//	    query.setParameter("email", listValues[14]);
//	    query.setParameter("csvName", listValues[15]);
//	    query.setParameter("population", listValues[16]);
//	    query.setParameter("elevation", listValues[17]);
//	    query.setParameter("username", listValues[18]);
//	    query.setParameter("source", listValues[19]);
//	    query.setParameter("date_published", listValues[20]);
//	    query.setParameter("date_updated", listValues[21]);
//	    query.setParameter("otherinfo", listValues[22]);
//	    query.setParameter("rating", Double.parseDouble(listValues[23]));
//	    query.setParameter("nrating", Integer.parseInt(listValues[24]));
//	    query.executeUpdate();
//	    log.info("sql insert success!!");
	}

	@Override
	@Transactional
	public void removeAllLocations(String username) {
		Query query = entityManager.createNativeQuery("delete from locations where username = :admin",
				LocationModel.class);
		query.setParameter("admin", username);
		query.executeUpdate();
		log.info("removal success");
	}

	@Override
	@Transactional
	public void updateLocationModel(LocationModel locationModel) {
		LocationModel item = entityManager.merge(locationModel);
		entityManager.merge(item);
	}

	@Override
	@Transactional
	public List<LocationModel> readLocationModel() throws DataAccessException {
		Query query = getEntityManager().createQuery("select a from locations a order by name asc",
				LocationModel.class);
		List<LocationModel> resultList = query.getResultList();
		return resultList;
	}

	@Override
	@Transactional
	public List<LocationModel> searchLocationModel(String location_value) throws DataAccessException {

//		log.info(
//				"HOUR QUERY IS: select l from locations l where lower(replace(replace(otherinfo,'##',''),' ', '')) like '%"
//						+ location_value.replaceAll("\\s+", "").toLowerCase() + "%'");
		Query query = getEntityManager().createQuery(
				"select l from locations l where lower(replace(replace(otherinfo,'##',''),' ', '')) like '%"
						+ location_value.replaceAll("\\s+", "").toLowerCase() + "%'",
				LocationModel.class);
		// query.setParameter(1, "%"+location_value+"%");
		List<LocationModel> resultList = query.getResultList();
		return resultList;
	}

	@Override
	@Transactional
	public List<LocationModel> searchLocationByFileName(String filename) throws DataAccessException {

		// log.info("Query: select l from locations l where csvname=" + filename);
		Query query = getEntityManager().createQuery("from locations where csvname = :filename", LocationModel.class);
		query.setParameter("filename", filename);
		// query.setParameter(1, "%"+location_value+"%");
		List<LocationModel> resultList = query.getResultList();
		return resultList;
	}

	@Override
	@Transactional
	public List<RatingModel> readRatingModel() throws DataAccessException {
		Query query = getEntityManager().createQuery("select r from ratings r", RatingModel.class);
		List<RatingModel> resultList = query.getResultList();
		return resultList;
	}

	@Override
	@Transactional
	public void updateRatingModel(RatingModel ratingModel) {
		RatingModel item = entityManager.merge(ratingModel);
		entityManager.merge(item);
	}

	@Override
	@Transactional
	public void storeRatingModel(RatingModel rating) {
		entityManager.persist(rating);
	}

}
