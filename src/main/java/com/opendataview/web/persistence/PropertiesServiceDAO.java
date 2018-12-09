package com.opendataview.web.persistence;

import java.util.List;

import org.apache.wicket.protocol.http.WebSession;
import org.springframework.dao.DataAccessException;

import com.opendataview.web.model.LocationModel;
import com.opendataview.web.model.PropertiesModel;

public interface PropertiesServiceDAO {

	List<PropertiesModel> readPropertiesModel(WebSession session) throws DataAccessException;


	void updatePropertiesModel(PropertiesModel propModel);
	void createPropertiesModel(String user);
}
