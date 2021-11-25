package com.klm.cases.df.auth;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONObject;



@Component
public class SimpleTravelApiAuth implements ApiAuth {

    private Logger logger = LoggerFactory.getLogger(SimpleTravelApiAuth.class);

    @Value("${simple-travel-api.auth.username}")
    private String username;

    @Value("${simple-travel-api.auth.password}")
    private String password;

    @Value("${simple-travel-api.url}")
    private String simpleTravelApiUrl;

    private volatile String token;
    private volatile long tokenExpiresAtMillis = Long.MIN_VALUE;

    @Override
    public String getToken() throws UnirestException {
        if (System.currentTimeMillis() > tokenExpiresAtMillis) {
            synchronized (this) {
                if (System.currentTimeMillis() > tokenExpiresAtMillis) {
                    obtainToken();
                }
            }
        }

        return token;
    }

    private void obtainToken() throws UnirestException {
    	JSONObject response = Unirest.post(simpleTravelApiUrl + "/oauth/token")
                .header("accept", "application/json")
                .header("content-type", "application/x-www-form-urlencoded")
                .basicAuth("travel-api-client", "psw")
                .queryString("grant_type", "client_credentials")
                .queryString("username", username)
                .queryString("password", password).asJson().getBody().getObject();

        token = response.get("access_token").toString();
        tokenExpiresAtMillis = System.currentTimeMillis() + response.getInt("expires_in") * 1000;

    	
    	
        logger.debug("Auth token received " + token);
    }
}
