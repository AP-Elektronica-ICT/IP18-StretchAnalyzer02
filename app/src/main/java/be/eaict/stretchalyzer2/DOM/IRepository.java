package be.eaict.stretchalyzer2.DOM;

import java.util.List;

/**
 * Created by Kevin-Laptop on 1/03/2018.
 */

public interface IRepository {

    //region GETTERS
    List<User> getUsers();

    List<User> getUsers(List<Integer> idList);

    //endregion

    //region SETTERS

    void createUser(User user);
    void updateUser(User user);
    void createOrUpdateUser(User user);

    //endregion
}
