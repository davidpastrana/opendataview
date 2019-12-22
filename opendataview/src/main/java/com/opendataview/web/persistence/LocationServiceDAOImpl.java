package com.opendataview.web.persistence;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.jpa.QueryHints;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opendataview.web.api.LocationsRestResource;
import com.opendataview.web.model.LocationModel;

@Service
public class LocationServiceDAOImpl implements LocationServiceDAO {

	private final static Logger log = LoggerFactory.getLogger(LocationServiceDAOImpl.class);

	protected EntityManager entityManager;
	// protected FullTextEntityManager fullTextEntityManager;

	// LocationsRestResource locationsRestResource;

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public FullTextEntityManager getFullTextEntityManager() {
		try {
			log.info("Beginning to index data.");
			Search.getFullTextEntityManager(entityManager).createIndexer().startAndWait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		log.info("Index complete.");
		return Search.getFullTextEntityManager(entityManager);
	}

//	public FullTextEntityManager getFullTextEntityManager() {
//		try {
//			log.info("Beginning to index data.");
//			fullTextEntityManager.createIndexer().startAndWait();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//
//		log.info("Index complete.");
//		return fullTextEntityManager;
//	}

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
	public void removeLocationByName(String filename) {
		Query query = entityManager.createNativeQuery("delete from locations l where l.filename = ?",
				LocationModel.class);
		query.setParameter(1, filename);
		query.executeUpdate();

		// we update the plain static data into the server
		new LocationsRestResource();
	}

	@Override
	@Transactional
	public List<LocationModel> showLocationByName(String filename) {
		List<LocationModel> resultList = getEntityManager()
				.createQuery("select l from locations l where l.filename = '" + filename + "'", LocationModel.class)
				.setHint(QueryHints.HINT_CACHEABLE, true).getResultList();
		return resultList;
	}

	@Override
	@Transactional
	public boolean checkLocationExistanceById(String queryidvalue) {

		// log.info("we check queryid select l from locations l where l.id = " +
		// queryidvalue + "");
		Query query = getEntityManager().createQuery("select l from locations l where l.id = " + queryidvalue,
				LocationModel.class);

		if (!query.setHint(QueryHints.HINT_CACHEABLE, true).getResultList().isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	@Transactional
	public List<LocationModel> getLocationByID(String queryidvalue) {

		List<LocationModel> resultList = getEntityManager()
				.createQuery("select l from locations l where l.id = " + queryidvalue, LocationModel.class)
				.setHint(QueryHints.HINT_CACHEABLE, true).getResultList();
		return resultList;
	}

	@Override
	@Transactional
	public boolean checkLocationExistanceByNameLatLng(LocationModel locationModel) {

//		log.info("from locations where lower(name) = '" + locationModel.getName().toLowerCase() + "' and latitude = '"
//				+ locationModel.getLatitude() + "' and longitude = '" + locationModel.getLongitude() + "'");

		if (locationModel != null) {
			Query query = getEntityManager().createQuery("from locations where lower(name) = '"
					+ locationModel.getName().toLowerCase() + "' and latitude = '" + locationModel.getLatitude()
					+ "' and longitude = '" + locationModel.getLongitude() + "'", LocationModel.class);

			// log.info("return is: " + query.getResultList());
			if (!query.getResultList().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	@Override
	@Transactional
	public void updateQuery(List<String> listValues, boolean hasid) {

		Query query = null;
		if (hasid == true) {
			// id,name,type,address,district,postcode,city,latitude,longitude,description,website,phone,date,schedule,email,filename,population,elevation,username,source,date_published,date_updated,data,rating,nrating
			query = entityManager.createNativeQuery(
					"update locations set name=:name,description=:description,type=:type,address=:address,postcode=:postcode,city=:city,latitude=:latitude,longitude=:longitude,"
							+ "website=:website,phone=:phone,date=:date,schedule=:schedule,email=:email,filename=:filename,population=:population,elevation=:elevation,username=:username,"
							+ "source=:source,date_updated=:date_updated,iconmarker=:iconmarker,private_mode=:private_mode,data=:data where id = :id",
					LocationModel.class);
			query.setParameter("id", listValues.get(0));

		} else {
			String latitude = listValues.get(6);
			String longitude = listValues.get(7);
			String filename = listValues.get(13);

			query = entityManager.createNativeQuery(
					"update locations set name=:name,description=:description,type=:type,address=:address,postcode=:postcode,city=:city,latitude=:latitude,longitude=:longitude,"
							+ "website=:website,phone=:phone,date=:date,schedule=:schedule,email=:email,filename=:filename,population=:population,elevation=:elevation,username=:username,"
							+ "source=:source,date_updated=:date_updated,iconmarker=:iconmarker,private_mode=:private_mode,data=:data where latitude = '"
							+ latitude + "' and longitude = '" + longitude + "' and lower(filename) = '"
							+ filename.toLowerCase() + "'",
					LocationModel.class);
		}
		query.setParameter("name", listValues.get(0));
		query.setParameter("description", listValues.get(1));
		query.setParameter("type", listValues.get(2));
		query.setParameter("address", listValues.get(3));
		query.setParameter("postcode", listValues.get(4));
		query.setParameter("city", listValues.get(5));
		query.setParameter("latitude", new BigDecimal(listValues.get(6)).setScale(6, RoundingMode.FLOOR));
		query.setParameter("longitude", new BigDecimal(listValues.get(7)).setScale(6, RoundingMode.FLOOR));
		query.setParameter("website", listValues.get(8));
		query.setParameter("phone", listValues.get(9));
		query.setParameter("date", listValues.get(10));
		query.setParameter("schedule", listValues.get(11));
		query.setParameter("email", listValues.get(12));
		query.setParameter("filename", listValues.get(13));
		query.setParameter("population", listValues.get(14));
		query.setParameter("elevation", listValues.get(15));
		query.setParameter("username", listValues.get(16));
		query.setParameter("source", listValues.get(17));
		// query.setParameter("date_published", listValues.get(18));
		query.setParameter("date_updated", listValues.get(19));
		query.setParameter("iconmarker", listValues.get(20));
		query.setParameter("private_mode", Boolean.valueOf(listValues.get(21)));
		query.setParameter("data", listValues.get(22));
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

		// we update the plain static data into the server
		new LocationsRestResource();
	}

	@Override
	@Transactional
	public void updateLocationModel(LocationModel locationModel) {
		LocationModel item = entityManager.merge(locationModel);
		entityManager.merge(item);

		// we update the plain static data into the server
		new LocationsRestResource();
	}

	@Override
	@Transactional
	public List<LocationModel> readLocationModel() throws DataAccessException {

		List<LocationModel> resultList = getEntityManager().createQuery("from locations", LocationModel.class)
				.setHint(QueryHints.HINT_READONLY, true).getResultList();
		return resultList;
	}

	@Override
	@Transactional
	public List<LocationModel> searchLocationModel(String location_value) throws DataAccessException {

		List<LocationModel> resultList;
//		log.info("Starting2...");
//		log.info("my way is: select l from locations l where lower(data) like '%## " + location_value.toLowerCase()
//				+ " ##%'");
//		log.info("other: select l from locations l where data like '%## " + location_value + " ##%'");
		if (location_value.startsWith("sql:")) {
			String sqlquery = location_value.split("sql:")[1].trim();
			resultList = getEntityManager()
					.createQuery("select l from locations l where " + sqlquery, LocationModel.class)
					.setHint(QueryHints.HINT_CACHEABLE, true).getResultList();
		} else if (location_value.startsWith("eq:")) {
			String match = location_value.split("eq:")[1].trim();
			resultList = getEntityManager()
					.createQuery("select l from locations l where data like '%## " + match + " ##%'",
							LocationModel.class)
					.setHint(QueryHints.HINT_CACHEABLE, true).getResultList();

		} else {
			resultList = getEntityManager()
					.createQuery("select l from locations l where lower(replace(replace(data,'##',''),' ', '')) like '%"
							+ location_value.replaceAll("\\s+", "").toLowerCase() + "%'", LocationModel.class)
					.setHint(QueryHints.HINT_CACHEABLE, true).getResultList();
		}
		return resultList;
	}

	@Override
	@Transactional
	public List<LocationModel> searchLocationByFileName(String filename) throws DataAccessException {
//		FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search
//				.getFullTextEntityManager(getEntityManager());
//		EntityContext entityContext = fullTextEntityManager.getSearchFactory().buildQueryBuilder()
//				.forEntity(LocationModel.class);
//
//		entityContext = entityContext.overridesForField("filename", "nameAnalyzer");
//
//		// org.apache.lucene.search.Query query =
//		// qb.keyword().onFields("filename").matching("parks.csv").createQuery();
//		// wrap Lucene query in a javax.persistence.Query
//		javax.persistence.Query persistenceQuery = fullTextEntityManager.createFullTextQuery(query,
//				LocationModel.class);
//		// execute search
//		@SuppressWarnings("unchecked")
//		List<LocationModel> result = persistenceQuery.getResultList();

		// log.info("RESULTS ARE:" + result.get(0).getName());

		List<LocationModel> resultList = getEntityManager()
				.createQuery("from locations where filename = :filename", LocationModel.class)
				.setParameter("filename", filename).setHint(QueryHints.HINT_CACHEABLE, true).getResultList();
		return resultList;

	}

}
