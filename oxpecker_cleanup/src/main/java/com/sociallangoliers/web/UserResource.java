package com.sociallangoliers.web;

import com.sociallangoliers.db_common.models.config.tables.pojos.User;
import com.sociallangoliers.repository.UserRepository;
import com.sociallangoliers.services.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@Api("User")
public class UserResource {

    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public UserResource(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @RequestMapping(value = "/config/list", method = RequestMethod.GET)
    public List<com.sociallangoliers.models.User> getUsersConfig() {
        return
            userService.getUserListing();

    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<User> getUsers() {
        return
            userRepository.getUserList();

    }
}
