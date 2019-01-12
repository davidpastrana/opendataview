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

	  @PersistenceContext(unitName = "opendataview")
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
		  PropertiesModel clone = getEntityManager().createQuery("select p from properties p where p.username = 'admin'",
		            PropertiesModel.class).getResultList().get(0);//entityManager.find(PropertiesModel.class, (long) 1);
		  // There is no initial properties row that we can consider, we create a default one!!
		  if (clone == null) {
			  Query query = getEntityManager().createNativeQuery("INSERT INTO properties(archiveregex,cityregex,countrycode,csvfiles_dir,currencyregex,dateregex,documentregex,enriched_dir,executesqlqueries,fieldtypesdebugmode,geonames_dbdriver,geonames_dbpwd,geonames_dburl,geonames_dbusr,geonamesdebugmode,imageregex,latituderegex,latlngregex,longituderegex,missinggeoreference_dir,newformat_dir,nrowchecks,nutsregex,openinghoursregex,percentageregex,phoneregex,postcoderegex,processed_dir,pvalue_nrowchecks,removeexistingbdata,shaperegex,shapes_file,sqlinserts_file,st1city,st1postcode,st2city,st2postcode,st3city,testfile,testmode,tmp_dir,web_dbdriver,web_dbpwd,web_dburl,web_dbusr,yearregex,possiblenameregex,id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			  query.setParameter(1, ".*.(zip|7z|bzip(2)?|gzip|jar|t(ar|gz)|dmg)$");
			  query.setParameter(2, ".*[a-z]{3,30}.*");
			  query.setParameter(3, "AT");
			  query.setParameter(4, "/Users/david/Desktop/at_dump_v1/wwdagvat/");
			  query.setParameter(5, "^(\\\\\\\\d+|\\\\\\\\d+[.,'']\\\\\\\\d+)\\\\\\\\p{Sc}|\\\\\\\\p{Sc}(\\\\\\\\d+|\\\\\\\\d+[.,'']\\\\\\\\d+)$");
			  query.setParameter(6, "([0-9]{2})?[0-9]{2}( |-|\\\\\\\\/|.)[0-3]?[0-9]( |-|\\\\\\\\/|.)([0-9]{2})?[0-9]{2}");
			  query.setParameter(7, ".*.(doc(x|m)?|pp(t|s|tx)|o(dp|tp)|pub|pdf|csv|xls(x|m)?|r(tf|pt)|info|txt|tex|x(ml|html|ps)|rdf(a|s)?|owl)$,");
			  query.setParameter(8, "/Users/david/Desktop/at_dump_v1/wwdagvat/tmp/processed/enriched/");
			  query.setParameter(9, "true");
			  query.setParameter(10, "true");
			  query.setParameter(11, "org.postgresql.Driver");
			  query.setParameter(12, "postgres");
			  query.setParameter(13, "jdbc:postgresql://127.0.0.1:5432/geonames");
			  query.setParameter(14, "postgres");
			  query.setParameter(15, "false");
			  query.setParameter(16, ".*.(jpg|gif|png|bmp|ico)$");

			  query.setParameter(17, "^-?([1-8]?[1-9]|[1-9]0)\\\\\\\\.{1}\\\\\\\\d{4,9}$");
			  query.setParameter(18, "([+-]?\\\\\\\\d+\\\\\\\\.?\\\\\\\\d+)\\\\\\\\s*,\\\\\\\\s*([+-]?\\\\\\\\d+\\\\\\\\.?\\\\\\\\d+)");
			  query.setParameter(19, "^-?([1]?[1-7][1-9]|[1]?[1-8][0]|[1-9]?[0-9])\\\\\\\\.{1}\\\\\\\\d{4,9}$");
			  query.setParameter(20, "/Users/david/Desktop/at_dump_v1/wwdagvat/tmp/processed/discarded_files/");
			  




			  query.setParameter(21, "/Users/david/Desktop/at_dump_v1/wwdagvat/tmp/processed/new_format/");
			  query.setParameter(22, "20");
			  query.setParameter(23, "\\\\\\\\w{3,9}");
			  query.setParameter(24, "([a-z ]+ )?(mo(n(day)?)?|tu(e(s(day)?)?)?|we(d(nesday)?)?|th(u(r(s(day)\\\\u200C\\\\u200B?)?)?)?|fr(i(day)?)?\\\\u200C\\\\u200B|sa(t(urday)?)?|su(n\\\\u200C\\\\u200B(day)?)?)(-|:| ).*|([a-z ]+ )?(mo(n(tag)?)?|di(e(n(stag)?)?)?|mi(t(woch)?)?|do(n(er(s(tag)\\\\u200C\\\\u200B?)?)?)?|fr(i(tag)?)?\\\\u200C\\\\u200B|sa(m(stag)?)?|do(n(erstag)?)?)(-|:| ).*");
			  query.setParameter(25, "^(\\\\\\\\d+|\\\\\\\\d+[.,'']\\\\\\\\d+)%|%(\\\\\\\\d+|\\\\\\\\d+[.,'']\\\\\\\\d+)$");
			  query.setParameter(26, "^\\\\\\\\+?[0-9. ()-]{10,25}$");
			  

			  
			  query.setParameter(27, "^[0-9]{2}$|^[0-9]{4}$");
			  query.setParameter(28, "/Users/david/Desktop/at_dump_v1/wwdagvat/tmp/processed/");
			  query.setParameter(29, "0.3");
			  query.setParameter(30, "false");
			  query.setParameter(31, "point\\\\\\\\s*\\\\\\\\(([+-]?\\\\\\\\d+\\\\\\\\.?\\\\\\\\d+)\\\\\\\\s*,?\\\\\\\\s*([+-]?\\\\\\\\d+\\\\\\\\.?\\\\\\\\d+)\\\\\\\\)");
			  query.setParameter(32, "/NUTS_2013_SHP/data/NUTS_RG_01M_2013.shp");
			  query.setParameter(33, "/Users/david/Desktop/at_dump_v1/wwdagvat/tmp/processed/sql_inserts.sql");
			  query.setParameter(34, "select geonameid,name,latitude,longitude,population,elevation from geoname where asciiname = ? order by population desc");
			  query.setParameter(35, "select p.name,admin3name,code,p.latitude,p.longitude,g.population,g.elevation from postalcodes p inner join geoname g on p.admin3 = g.admin3 where code like ? order by code asc");

			  
			  query.setParameter(36, "select geonameid,name,latitude,longitude,population,elevation from geoname where asciiname like ? or asciiname like ? order by population desc");
			  query.setParameter(37, "select p.name,admin3name,code,p.latitude,p.longitude,g.population,g.elevation from postalcodes p inner join geoname g on p.admin3 = g.admin3 where code = ? order by code asc");
			  query.setParameter(38, "select geonameid,name,latitude,longitude,population,elevation from geoname where asciiname like ? or asciiname like ? order by population desc");

			  
			  
			  query.setParameter(39, "httpckan.data.ktn.gv.atstoragef20140630T133A123A02.832Zsteuergem12.csv");
			  query.setParameter(40, "true");
			  query.setParameter(41, "/Users/david/Desktop/at_dump_v1/wwdagvat/tmp/");
			  query.setParameter(42, "org.postgresql.Driver");
			  query.setParameter(43, "postgres");
			  query.setParameter(44, "jdbc:postgresql://127.0.0.1:5432/opendataview");
			  query.setParameter(45, "postgres");
			  query.setParameter(46, "^(?:18|20)\\\\\\\\d{2}$");

			  query.setParameter(47, ".*[0-9]+.*");
			  query.setParameter(48, 1);
			  query.executeUpdate();
			  clone = entityManager.find(PropertiesModel.class, (long) 1);
		  }
		  entityManager.detach(clone);
		  entityManager.flush();
		  clone.setId((long) 0);
		  clone.setUsername(user);
		  entityManager.merge(clone);
		  setEntityManager(entityManager);
	  }
}
