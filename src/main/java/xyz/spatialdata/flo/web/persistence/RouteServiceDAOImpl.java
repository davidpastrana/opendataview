package xyz.spatialdata.flo.web.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import xyz.spatialdata.flo.web.model.RouteModel;

@Service
public class RouteServiceDAOImpl implements RouteServiceDAO {

  private final static Logger log = LoggerFactory.getLogger(RouteServiceDAOImpl.class);

  protected EntityManager entityManager;

  public EntityManager getEntityManager() {
    return entityManager;
  }

  @PersistenceContext(unitName = "spatialdatasearch")
  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  @Transactional
  public void storeRouteModel(List<RouteModel> list) {
    for (RouteModel objModel : list) {
      entityManager.persist(objModel);
      setEntityManager(entityManager);
    }
  }

  @Override
  @Transactional
  public void removeRoute() {
    getEntityManager().createQuery("delete from routes").executeUpdate();
  }

  @Override
  @Transactional
  public List<RouteModel> readRouteModel() throws DataAccessException {
    Query query = getEntityManager().createQuery("select r from routes r", RouteModel.class);
    List<RouteModel> resultList = query.getResultList();
    return resultList;
  }

  @Override
  @Transactional
  public List<RouteModel> readRouteModelName(String nameRouteValue) throws DataAccessException {
    Query query =
        getEntityManager().createQuery(
            "select r from routes r where r.name='" + nameRouteValue + "'", RouteModel.class);
    List<RouteModel> resultList = query.getResultList();
    return resultList;
  }

}
