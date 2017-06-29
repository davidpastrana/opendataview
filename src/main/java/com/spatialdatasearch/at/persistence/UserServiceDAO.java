package com.spatialdatasearch.at.persistence;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.spatialdatasearch.at.model.UserModel;

public interface UserServiceDAO {

  void registerUserModel(UserModel user);

  List<UserModel> readUserModel(String username, String password) throws DataAccessException;

}
