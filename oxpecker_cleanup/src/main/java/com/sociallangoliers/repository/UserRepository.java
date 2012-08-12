package com.sociallangoliers.repository;

import com.sociallangoliers.db_common.models.config.tables.pojos.User;

import java.util.List;

public interface UserRepository {


    List<User> getUserList();
}
