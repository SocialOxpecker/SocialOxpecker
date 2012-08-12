package com.sociallangoliers.support;

public enum TwitterActions {
    Tweets(1),
    Retweets(2),
    Favorites(3),
    DirectMessages(4),
    Lists(5),
    Friendships(6);
    int id;

    TwitterActions(int value) {
        id = value;

    }

    public int getId() {
        return id;
    }
}
