package com.opendataview.web.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opendataview.web.model.UserModel;

@Service
public class UserServiceDAOImpl implements UserServiceDAO {

	private final static Logger log = LoggerFactory.getLogger(UserServiceDAOImpl.class);

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

	@Override
	@Transactional
	public UserModel getUser(String username, String password) throws DataAccessException {
		Query query = getEntityManager().createQuery(
				"select u from users u where u.username='" + username + "' and u.password='" + password + "'",
				UserModel.class);
		List<UserModel> resultList = query.getResultList();
		if (!query.getResultList().isEmpty()) {
			return resultList.get(0);
		} else {
			return null;
		}
	}

	@Override
	@Transactional
	public Boolean isUsernameRegistered(String username) throws DataAccessException {
		Query query = getEntityManager().createQuery("select u from users u where u.username='" + username + "'",
				UserModel.class);
		if (!query.getResultList().isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	@Transactional
	public Boolean isEmailRegistered(String email) throws DataAccessException {
		Query query = getEntityManager().createQuery("select u from users u where u.email='" + email + "'",
				UserModel.class);
		if (!query.getResultList().isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

}
