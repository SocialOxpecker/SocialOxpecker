package com.sociallangoliers.services.impl;

import com.sociallangoliers.config.Configuration;
import com.sociallangoliers.services.PruneSocial;
import org.scribe.model.Token;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 1/15/13
 * Time: 8:40 PM
 */
@Service
@Qualifier("reddit")
public class Reddit implements PruneSocial {

    public static void main(String[] args) {
        Configuration config = Configuration.getInstance();

    }

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
