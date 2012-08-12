package com.sociallangoliers.repository.impl;

import com.sociallangoliers.db_common.models.config.tables.pojos.User;
import com.sociallangoliers.repository.UserRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static com.sociallangoliers.db_common.models.config.Config.CONFIG;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final DSLContext dslContext;

    @Autowired
    public UserRepositoryImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public List<User> getUserList() {
        return
        dslContext.selectFrom(CONFIG.USER)
            .fetchInto(User.class);
    }
}
