package com.opendataview.web.persistence;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.opendataview.web.model.UserModel;

public interface UserServiceDAO {

  void registerUserModel(UserModel user);

  List<UserModel> readUserModel(String username, String password) throws DataAccessException;

UserModel getUser(String username, String password) throws DataAccessException;

Boolean isUsernameRegistered(String username) throws DataAccessException;
Boolean isEmailRegistered(String email) throws DataAccessException;

}
