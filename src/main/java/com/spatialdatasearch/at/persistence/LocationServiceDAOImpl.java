package com.spatialdatasearch.at.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spatialdatasearch.at.model.LocationModel;
import com.spatialdatasearch.at.model.RatingModel;

@Service
public class LocationServiceDAOImpl implements LocationServiceDAO {

  private final static Logger log = LoggerFactory.getLogger(LocationServiceDAOImpl.class);

  protected EntityManager entityManager;

  public EntityManager getEntityManager() {
    return entityManager;
  }

  @PersistenceContext(unitName = "opendata")
  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  @Transactional
  public void storeLocationModel(List<LocationModel> locationlist) {
    for (LocationModel attractionModel : locationlist) {
      entityManager.persist(attractionModel);
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
  public void updateLocationModel(LocationModel locationModel) {
    LocationModel item = entityManager.merge(locationModel);
    entityManager.merge(item);
  }

  @Override
  @Transactional
  public List<LocationModel> readLocationModel() throws DataAccessException {
    Query query =
        getEntityManager().createQuery("select a from locations a order by rating desc, name asc",
            LocationModel.class);
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
