package com.sociallangoliers.services;

import org.scribe.model.Token;

public interface PruneSocial {

    boolean execute();
    Token getAccessToken();
}
