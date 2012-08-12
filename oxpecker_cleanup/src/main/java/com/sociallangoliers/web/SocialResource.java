package com.sociallangoliers.web;

import com.sociallangoliers.db_common.models.config.tables.pojos.SocialApp;
import com.sociallangoliers.repository.SocialAppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/social")
public class SocialResource {

    private final SocialAppRepository socialAppRepository;

    @Autowired
    public SocialResource(SocialAppRepository socialAppRepository) {
        this.socialAppRepository = socialAppRepository;
    }

    @RequestMapping(value = "/app/list", method = RequestMethod.GET)
    public List<SocialApp> getUsers() {
        return socialAppRepository.getApplicationList();

    }

}
