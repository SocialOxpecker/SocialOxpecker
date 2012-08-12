package com.sociallangoliers.services.impl;

import com.sociallangoliers.services.PruneSocial;
import org.scribe.model.Token;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("foursquare")
public class FourSquare implements PruneSocial {
    @Override
    public boolean execute() {
        throw new RuntimeException("Not Yet Implemented");
    }

    @Override
    public Token getAccessToken() {
        throw new RuntimeException("Not Yet Implemented");
    }
}
