package com.sociallangoliers.web;

import com.sociallangoliers.db_common.models.config.tables.pojos.SocialApp;
import com.sociallangoliers.db_common.models.config.tables.pojos.UserSocial;
import com.sociallangoliers.models.User;
import com.sociallangoliers.repository.SocialAppRepository;
import com.sociallangoliers.services.SocialAdapter;
import com.sociallangoliers.services.UserService;
import com.sociallangoliers.support.SocialCode;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import scala.Tuple2;

import java.util.List;

@RestController
@RequestMapping("/users")
@Api("User")
public class UserResource {


    private final SocialAppRepository socialAppRepository;
    private final UserService userService;
    private final SocialAdapter adapter;

    @Autowired
    public UserResource(SocialAppRepository socialAppRepository, UserService userService, SocialAdapter adapter) {
        this.socialAppRepository = socialAppRepository;
        this.userService = userService;
        this.adapter = adapter;
    }


    @RequestMapping(value = "/", method = RequestMethod.POST)
    public User createUser(User user) {
        return userService.createUser(user);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public User getUserById(@PathVariable("id") long id) {
        return userService.getUserById(id, false);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String deleteUserById(@PathVariable("id") long id) {
        userService.deleteUserById(id);
        return String.format("Woot, user: %s deleted", id);
    }


    @RequestMapping(value = "/users/{social}/authenticate/{userId}", method = RequestMethod.GET)
    String authenticateUser(@PathVariable("social") final String socialAppName, @PathVariable("userId") final long userId) {
        User user = userService.getUserById(userId, true);
        SocialApp socialAppByName = socialAppRepository.getSocialAppByName(socialAppName);
        UserSocial dummy = user.getEnabledSocialApps().iterator().next();
        SocialCode socialCode = SocialCode.valueOf(socialAppByName.getSocialAppType().toLowerCase());
        String url =  adapter.validate(socialCode, socialAppByName.getClientId(), socialAppByName.getSecret(),
            dummy.getAccessToken(), dummy.getSecretToken());
        return String.format("%s:%s:%s", user.getName(), socialAppByName.getSocialAppType(), url);

    }

    @RequestMapping(value = "/users/{social}/validate/{userId}", method = RequestMethod.GET)
    String validateUser(@PathVariable("social") final String socialAppName, @PathVariable("userId") final long userId,
                        @RequestParam(value = "rows") String tokenValidation) {
        User user = userService.getUserById(userId, true);
        SocialApp socialAppByName = socialAppRepository.getSocialAppByName(socialAppName);
        return String.format("%s:%s", user.getName(), socialAppByName.getSocialAppType());

    }


    @RequestMapping(value = "/config/list", method = RequestMethod.GET)
    public List<com.sociallangoliers.models.User> getUsersConfig() {
        return
            userService.getUserListing();

    }
}
