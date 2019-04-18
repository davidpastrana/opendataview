package com.opendataview.web.persistence;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opendataview.web.model.LocationModel;

@Service
public class LocationServiceDAOLuceneIdxImpl implements LocationServiceDAOLuceneIdx {

	private final static Logger log = LoggerFactory.getLogger(LocationServiceDAOLuceneIdxImpl.class);

	protected EntityManager entityManager;
	FullTextEntityManager fullTextEntityManager;
	org.hibernate.search.query.dsl.QueryBuilder queryBuilder;

	/**
	 * Call at the startup of the server.
	 *
	 * - Enables Hibernate's FullTextEntityManager for Full-text searching. -
	 * Initializes roles in database
	 */
	@Override
	public void initialize() {
		log.info("Linking entityManager.");
		fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		log.info(String.format("entityManager: %s linked.", entityManager));

		try {
			log.info("Beginning to index data.");
			fullTextEntityManager.createIndexer().startAndWait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		log.info("Index complete.");

		queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(LocationModel.class)
				.get();

		// Initialize user roles in database
//		Role publicRole = createRole(1, "ROLE_PUBLIC");
//		roleRepository.save(publicRole);
//		Role adminRole = createRole(2, "ROLE_ADMIN");
//		roleRepository.save(adminRole);
	}

	@Override
	@Transactional
	public List<LocationModel> readLocationModel() throws DataAccessException {

		org.apache.lucene.search.Query luceneQuery = queryBuilder.keyword().onField("username").matching("admin")
				.createQuery();
		javax.persistence.Query fullTextQuery = fullTextEntityManager.createFullTextQuery(luceneQuery);
		List<LocationModel> resultList = fullTextQuery.getResultList();

//		List<LocationModel> resultList = getEntityManager().createQuery("from locations", LocationModel.class)
//				.setHint(QueryHints.HINT_CACHEABLE, true).getResultList();
		return resultList;
	}
}
