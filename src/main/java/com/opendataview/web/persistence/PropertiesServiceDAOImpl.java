package com.opendataview.web.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.wicket.protocol.http.WebSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opendataview.web.model.LocationModel;
import com.opendataview.web.model.PropertiesModel;
import com.opendataview.web.model.UserModel;

@Service
public class PropertiesServiceDAOImpl implements PropertiesServiceDAO {

	  private final static Logger log = LoggerFactory.getLogger(PropertiesServiceDAOImpl.class);

	  protected EntityManager entityManager;

	  public EntityManager getEntityManager() {
	    return entityManager;
	  }

	  @PersistenceContext(unitName = "spatialdatasearch")
	  public void setEntityManager(EntityManager entityManager) {
	    this.entityManager = entityManager;
	  }
		  
	  @Transactional
	  public List<PropertiesModel> readPropertiesModel(WebSession session) throws DataAccessException {

	    Query query =
	        getEntityManager().createQuery("select a from properties a where a.username='"+session.getAttribute("user_name").toString()+"'",
	            PropertiesModel.class);
	    List<PropertiesModel> resultList = query.getResultList();
	    //log.info("EL TAMAÑO ES..."+resultList.get(0).getTestmode());
	    return resultList;
	  }
	  
	  @Override
	  @Transactional
	  public void updatePropertiesModel(PropertiesModel propModel) {


	    	  log.info("the country code is : "+propModel.getCountrycode());
	    	    PropertiesModel item = entityManager.merge(propModel);
	    	    entityManager.merge(item);
	  }
	  
	  @Override
	  @Transactional
	  public void createPropertiesModel(String user) {
		  PropertiesModel clone = entityManager.find(PropertiesModel.class, (long) 1);
		  entityManager.detach(clone);
		  entityManager.flush();
		  clone.setId((long) 0);
		  clone.setUsername(user);
		  entityManager.merge(clone);
		  setEntityManager(entityManager);
	  }
}
