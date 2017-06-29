package com.spatialdatasearch.at.persistence;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.spatialdatasearch.at.model.RouteModel;

public interface RouteServiceDAO {

  void storeRouteModel(List<RouteModel> list);

  List<RouteModel> readRouteModelName(String nameRouteValue) throws DataAccessException;

  void removeRoute();

  List<RouteModel> readRouteModel() throws DataAccessException;

}
