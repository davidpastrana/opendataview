package com.opendataview.web.persistence;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opendataview.web.model.PropertiesModel;

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

	@Override
	@Transactional
	public List<PropertiesModel> readPropertiesModel(String user) throws DataAccessException {

		Query query = getEntityManager().createQuery("select a from properties a where a.username='" + user + "'",
				PropertiesModel.class);
		List<PropertiesModel> resultList = query.getResultList();
		log.info("PROPIEDADES..." + resultList.size());
		return resultList;
	}

	@Override
	@Transactional
	public void updatePropertiesModel(PropertiesModel propModel) {

		// Exception to only allow the admin user to make changes!
		if (propModel.getWeb_dbdriver() == null) {

			Iterator itr = getEntityManager()
					.createQuery(
							"select web_dbdriver,web_dburl,web_dbusr,web_dbpwd from properties where username='admin'")
					.getResultList().iterator();
			while (itr.hasNext()) {
				Object[] tuple = (Object[]) itr.next();
				propModel.setWeb_dbdriver((String) tuple[0]);
				propModel.setWeb_dburl((String) tuple[1]);
				propModel.setWeb_dbusr((String) tuple[2]);
				propModel.setWeb_dbpwd((String) tuple[3]);

			}
		}

		log.info("the country code is : " + propModel.getCountrycode());
		PropertiesModel item = entityManager.merge(propModel);
		entityManager.merge(item);
	}

	@Override
	@Transactional
	public void createPropertiesModel(String user) {

		PropertiesModel clone = null;
		// if we already have properties from the admin
		if (getEntityManager()
				.createQuery("select p from properties p where p.username = 'admin'", PropertiesModel.class)
				.getResultList().size() > 0) {
			clone = getEntityManager()
					.createQuery("select p from properties p where p.username = 'admin'", PropertiesModel.class)
					.getResultList().get(0);// entityManager.find(PropertiesModel.class, (long) 1);
		}
		// if it is the first start without DB information
		if (clone == null) {

			// select * from properties where username = 'admin'

			// 38 field in properties table
			Query query = getEntityManager().createNativeQuery(
					"INSERT INTO properties(id,mapsAPI,executeSQLqueries,autodetectSchema,fieldtypesdebugmode,active_dictionary,dictionary_matches,nrowchecks,pvalue_nrowchecks,imageRegex, phoneRegex,cityRegex,archiveRegex,documentRegex,openinghoursRegex,dateRegex,yearRegex,currencyRegex,percentageRegex,postcodeRegex, nutsRegex,shapeRegex,latitudeRegex,longitudeRegex,latlngRegex,possiblenameRegex,countrycode,geonames_dbdriver,geonames_dburl,geonames_dbusr, geonames_dbpwd,web_dbdriver,web_dburl,web_dbusr,web_dbpwd,st1postcode,st1city,geonamesdebugmode,descriptionRegex,iconmarker) VALUES (?,?,?,?,?,?,?,?,?,?, ?,?,?,?,?,?,?,?,?,?, ?,?,?,?,?,?,?,?,?,?, ?,?,?,?,?,?,?,?,?,?)");

			query.setParameter(1, 1);
			query.setParameter(2, "AIzaSyDadUi1huN5fz0Z15duVzgiS1mjXsE89oE");
			query.setParameter(3, "false");
			query.setParameter(4, "true");
			query.setParameter(5, "true");
			query.setParameter(6, "true");
			query.setParameter(7,
					"%title%, %name%, nom%, artbez, parkanlage, bezeichnung, station : titles;\n"
							+ "%description%, %comment%, bemerkung: description;\n"
							+ "%latitud%, %coord%x%, lat, breitegrad : latitudes;\n"
							+ "%longitud%, %coord%y%, lng, long, laengengrad : longitudes;\n"
							+ "%geom%, shape : shape;\n" + "#coord% : latlong;\n" + "%publisher% : data_publishers;\n"
							+ "%mail, correo : emails;\n" + "web%, %url%, link, %ftp%, %homepage% : urls;\n"
							+ "phone, tel%, tf: phones;\n" + "city, place, %address%, adresse : cities;\n"
							+ "zipcode, postal%, cp : postal_codes;\n" + "%hours, %open%, %working% : working_hours;\n"
							+ "%date% : dates;\n" + "%image%, %img%, %gif% : images;");
			query.setParameter(8, "10");
			query.setParameter(9, "0.5");
			query.setParameter(10, ".*.(jpg|gif|png|bmp|ico)$");
			query.setParameter(11, "^\\+?[0-9. ()-]{10,25}$");
			query.setParameter(12, ".*[a-z]{3,30}.*");
			query.setParameter(13, ".*.(zip|7z|bzip(2)?|gzip|jar|t(ar|gz)|dmg)$");
			query.setParameter(14,
					".*.(doc(x|m)?|pp(t|s|tx)|o(dp|tp)|pub|pdf|csv|xls(x|m)?|r(tf|pt)|info|txt|tex|x(ml|html|ps)|rdf(a|s)?|owl)$,");
			query.setParameter(15,
					"([a-z ]+ )?(mo(n(day)?)?|tu(e(s(day)?)?)?|we(d(nesday)?)?|th(u(r(s(day)\\\\u200C\\\\u200B?)?)?)?|fr(i(day)?)?\\\\u200C\\\\u200B|sa(t(urday)?)?|su(n\\\\u200C\\\\u200B(day)?)?)(-|:| ).*|([a-z ]+ )?(mo(n(tag)?)?|di(e(n(stag)?)?)?|mi(t(woch)?)?|do(n(er(s(tag)\\\\u200C\\\\u200B?)?)?)?|fr(i(tag)?)?\\\\u200C\\\\u200B|sa(m(stag)?)?|do(n(erstag)?)?)(-|:| ).*");
			query.setParameter(16, "([0-9]{2})?[0-9]{2}( |-|\\/|.)[0-3]?[0-9]( |-|\\/|.)([0-9]{2})?[0-9]{2}");
			query.setParameter(17, "^(?:18|20)\\d{2}$");
			query.setParameter(18, "\\w{3,9}$");
			query.setParameter(19, "^(\\d+|\\d+[.,'']\\d+)%|%(\\d+|\\d+[.,'']\\d+)$");
			query.setParameter(20, "\\w{3,9}");
			query.setParameter(21, "\\w{3,9}");
			query.setParameter(22, "point\\s*\\(([+-]?\\d+\\.?\\d+)\\s*,?\\s*([+-]?\\d+\\.?\\d+)\\)");
			query.setParameter(23, "^-?([1-8]?[1-9]|[1-9]0)\\.{1}\\d{4,9}$");
			query.setParameter(24, "^-?([1]?[1-7][1-9]|[1]?[1-8][0]|[1-9]?[0-9])\\.{1}\\d{4,9}$");
			query.setParameter(25, "([+-]?\\d+\\.?\\d+)\\s*,\\s*([+-]?\\d+\\.?\\d+)");
			query.setParameter(26, ".*[0-9]+.*");
			query.setParameter(27, "AT");
			query.setParameter(28, "org.postgresql.Driver");
			query.setParameter(29, "jdbc:postgresql://127.0.0.1:5432/geonames");
			query.setParameter(30, "postgres");
			query.setParameter(31, "postgres");
			query.setParameter(32, "org.postgresql.Driver");
			query.setParameter(33, "jdbc:postgresql://127.0.0.1:5432/opendataview");
			query.setParameter(34, "postgres");
			query.setParameter(35, "postgres");
			query.setParameter(36,
					"select p.name,admin3name,code,p.latitude,p.longitude,g.population,g.elevation from postalcodes p inner join geoname g on p.admin3 = g.admin3 where code like ? order by code asc");
			query.setParameter(37,
					"select geonameid,name,latitude,longitude,population,elevation from geoname where asciiname = ? order by population desc");
			query.setParameter(38, "true");
			query.setParameter(39, "[\\s\\S]{100,}");
			query.setParameter(40, "#F44336+10+0.8");

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
