package com.sociallangoliers.repository;

import com.sociallangoliers.db_common.models.config.tables.pojos.SocialApp;

import java.util.List;

public interface SocialAppRepository {

    List<SocialApp> getApplicationList();

    SocialApp getSocialAppByName(String name);

    SocialApp getSocialAppById(long id);
}
