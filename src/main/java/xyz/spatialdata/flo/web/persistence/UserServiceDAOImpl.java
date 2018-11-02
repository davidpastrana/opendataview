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

import xyz.spatialdata.flo.web.model.UserModel;

@Service
public class UserServiceDAOImpl implements UserServiceDAO {

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
  public void registerUserModel(UserModel user) {
    entityManager.persist(user);
    setEntityManager(entityManager);
  }

  @Override
  @Transactional
  public List<UserModel> readUserModel(String username, String password) throws DataAccessException {
    log.info("coming user.." + username);
    log.info("coming password..." + password);
    Query query = getEntityManager().createQuery("select u from users u", UserModel.class);
    List<UserModel> resultList = query.getResultList();
    return resultList;
  }
}
