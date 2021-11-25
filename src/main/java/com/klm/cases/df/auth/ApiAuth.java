package com.klm.cases.df.auth;

import kong.unirest.UnirestException;

public interface ApiAuth {
    String getToken() throws UnirestException;
}
