package com.sociallangoliers.services.impl;

import com.sociallangoliers.db_common.models.config.tables.pojos.UserSocial;
import com.sociallangoliers.models.User;
import com.sociallangoliers.repository.UserRepository;
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
    private final UserRepository userRepository;

    @Autowired
    public UserListImpl(DSLContext dslContext, UserRepository userRepository) {
        this.dslContext = dslContext;
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getUserListing() {
        List<User> list = userRepository.getUserList()
            .stream()
            .map(User::new)
            .collect(Collectors.toList());

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


        return list;

    }
}
