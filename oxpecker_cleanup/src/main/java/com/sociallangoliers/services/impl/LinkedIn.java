package com.sociallangoliers.services.impl;

import com.sociallangoliers.services.PruneSocial;
import org.scribe.model.Token;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("linkedin")
public class LinkedIn implements PruneSocial {
    @Override
    public boolean execute() {
        System.out.println("Under Development");
        return false;
    }

    @Override
    public Token getAccessToken() {
        return null;
    }
}
