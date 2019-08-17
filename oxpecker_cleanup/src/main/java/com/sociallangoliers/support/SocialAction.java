package com.sociallangoliers.support;

public enum SocialAction {
    posts(1),
    reposts(2),
    favorites(3),
    directmessages(4),
    lists(5),
    friendships(6);
    int id;

    SocialAction(int value) {
        id = value;

    }

    public int getId() {
        return id;
    }
}
