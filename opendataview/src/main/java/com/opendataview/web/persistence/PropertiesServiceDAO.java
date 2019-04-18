package com.opendataview.web.persistence;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.opendataview.web.model.PropertiesModel;

public interface PropertiesServiceDAO {

	List<PropertiesModel> readPropertiesModel(String user) throws DataAccessException;

	void updatePropertiesModel(PropertiesModel propModel);

	void createPropertiesModel(String user);
}
