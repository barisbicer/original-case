package com.klm.cases.df.auth;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
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
    	
    	
    	
         HttpResponse<JsonNode> response = Unirest.post(simpleTravelApiUrl + "/oauth/token")
                .header("accept", "application/json")
                .header("content-type", "application/x-www-form-urlencoded")
                .queryString("grant_type", "client_credentials")
                .queryString("username", username)
                .queryString("password", password).asJson();
     
    	
    	
    	System.out.print("response:"+response);
    
    	
       
       tokenExpiresAtMillis = System.currentTimeMillis() + response.getStatus() * 1000;

        
      
    	
    	
        logger.debug("Auth token received " + token);
    }
}
