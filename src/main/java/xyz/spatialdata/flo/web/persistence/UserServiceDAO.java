package xyz.spatialdata.flo.web.persistence;

import java.util.List;

import org.springframework.dao.DataAccessException;

import xyz.spatialdata.flo.web.model.UserModel;

public interface UserServiceDAO {

  void registerUserModel(UserModel user);

  List<UserModel> readUserModel(String username, String password) throws DataAccessException;

}
