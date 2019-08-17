package com.sociallangoliers.services;

import com.sociallangoliers.models.User;

import java.util.List;

public interface UserService {

    List<User> getUserListing();

    User createUser(User user);

    User getUserById(long id, boolean hydrated);

    void deleteUserById(long id);
}
