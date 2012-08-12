package com.sociallangoliers.models;

import com.google.common.collect.Lists;
import com.sociallangoliers.db_common.models.config.tables.interfaces.IUser;
import com.sociallangoliers.db_common.models.config.tables.pojos.UserSocial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class User implements IUser {
    private Long userId;
    private String name;
    private String userName;
    private String password;
    private Long createdById;
    private LocalDateTime creationDate;
    private Long modifiedById;
    private LocalDateTime modificationDate;
    private LocalDateTime passwordExpirationDate;
    private Boolean active;
    private Boolean ssoEnabled;
    private OffsetDateTime lastLoginDatetime;
    List<UserSocial> enabledSocialApps = Lists.newArrayList();

    public User(IUser value) {
        from(value);
    }

    @Override
    public void from(IUser from) {
        setUserId(from.getUserId());
        setName(from.getName());
        setUserName(from.getUserName());
        setPassword(from.getPassword());
        setCreatedById(from.getCreatedById());
        setCreationDate(from.getCreationDate());
        setModifiedById(from.getModifiedById());
        setModificationDate(from.getModificationDate());
        setPasswordExpirationDate(from.getPasswordExpirationDate());
        setActive(from.getActive());
        setSsoEnabled(from.getSsoEnabled());
        setLastLoginDatetime(from.getLastLoginDatetime());
    }

    @Override
    public <E extends IUser> E into(E into) {
        into.from(this);
        return into;
    }
}
