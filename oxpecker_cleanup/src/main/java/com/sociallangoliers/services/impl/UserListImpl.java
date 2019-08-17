package com.sociallangoliers.services.impl;

import com.google.common.collect.Lists;
import com.sociallangoliers.db_common.models.config.tables.pojos.UserSocial;
import com.sociallangoliers.db_common.models.config.tables.records.UserRecord;
import com.sociallangoliers.models.User;
import com.sociallangoliers.services.UserService;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sociallangoliers.db_common.models.config.Config.CONFIG;

@Service
public class UserListImpl implements UserService {
    private final DSLContext dslContext;

    @Autowired
    public UserListImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public List<User> getUserListing() {
        List<User> list = dslContext.selectFrom(CONFIG.USER).fetchInto(User.class);
        hydrateUser(list);
        return list;

    }

    private void hydrateUser(List<User> list) {
        Map<Long, User> userIds = list.stream().collect(Collectors.toMap(User::getUserId, t -> t));
        dslContext.select(CONFIG.USER_SOCIAL.fields())
            .from(CONFIG.USER_SOCIAL)
            .where(CONFIG.USER_SOCIAL.USER_ID.in(userIds.keySet()))
            .fetchInto(UserSocial.class)
            .forEach(t ->
                {
                    final User u = userIds.get(t.getUserId());
                    if (u != null) {
                        u.getEnabledSocialApps().add(t);
                    }
                }
            );

    }


    @Override
    public User createUser(User user) {
        UserRecord userRecord = dslContext.newRecord(CONFIG.USER);
        userRecord.from(user);
        userRecord.store();
        userRecord.refresh();
        return getUserById(userRecord.getUserId(), false);
    }

    @Override
    public User getUserById(long id, boolean hydrated) {
        User user = dslContext.selectFrom(CONFIG.USER)
            .where(CONFIG.USER.USER_ID.eq(id))
            .fetchOneInto(User.class);

        if (hydrated) {
            hydrateUser(Lists.newArrayList(user));
        }
        return user;
    }

    @Override
    public void deleteUserById(long id) {
        dslContext.delete(CONFIG.USER)
            .where(CONFIG.USER.USER_ID.eq(id))
            .execute();

    }
}
