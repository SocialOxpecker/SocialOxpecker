package com.sociallangoliers.repository.impl;

import com.sociallangoliers.db_common.models.config.tables.pojos.SocialApp;
import com.sociallangoliers.repository.SocialAppRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sociallangoliers.db_common.models.config.Config.CONFIG;

@Repository
public class SocialAppRepositoryImpl implements SocialAppRepository {

    private final DSLContext dslContext;

    @Autowired
    public SocialAppRepositoryImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }


    /**
     * @see SocialAppRepository#getApplicationList()
     */
    @Override
    public List<SocialApp> getApplicationList() {
        return
        dslContext.selectFrom(CONFIG.SOCIAL_APP)
            .fetchInto(SocialApp.class);

    }

    @Override
    public SocialApp getSocialAppByName(String name) {
        return
            dslContext.selectFrom(CONFIG.SOCIAL_APP)
                .where(CONFIG.SOCIAL_APP.SOCIAL_APP_TYPE.eq(name))
                .fetchOneInto(SocialApp.class);
    }

    public SocialApp getSocialAppById(long id) {
        return
            dslContext.selectFrom(CONFIG.SOCIAL_APP)
                .where(CONFIG.SOCIAL_APP.APP_ID.eq(id))
                .fetchOneInto(SocialApp.class);
    }



}
