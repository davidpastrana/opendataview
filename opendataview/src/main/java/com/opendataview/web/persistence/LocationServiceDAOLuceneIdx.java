package com.opendataview.web.persistence;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.opendataview.web.model.LocationModel;

public interface LocationServiceDAOLuceneIdx {
	void initialize();

	List<LocationModel> readLocationModel() throws DataAccessException;
}
