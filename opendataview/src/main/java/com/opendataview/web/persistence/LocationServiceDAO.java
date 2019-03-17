package com.opendataview.web.persistence;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.opendataview.web.model.LocationModel;

public interface LocationServiceDAO {

	List<LocationModel> readLocationModel() throws DataAccessException;

	void removeLocationModel(LocationModel locationModel);

	void updateLocationModel(LocationModel locationModel);

	void storeLocationModel(List<LocationModel> locationlist);

	List<LocationModel> searchLocationModel(String location_value) throws DataAccessException;

	void removeAllLocations(String username);

	void removeLocationByName(String csvName);

	List<LocationModel> showLocationByName(String csvName);

	boolean checkLocationExistanceByID(String queryidvalue);

	boolean checkLocationExistanceByOtherName(LocationModel locationModel);

	void updateQuery(List<String> listValues, boolean hasid);

	void executeQuery(String query);

	List<LocationModel> getLocationByID(String queryidvalue);

	List<LocationModel> searchLocationByFileName(String filename) throws DataAccessException;

}
