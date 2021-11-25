package com.klm.cases.df.restklm;

import com.klm.cases.df.auth.ApiAuth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONObject;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;


import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class SimpleTravelApiController {

    private ApiAuth apiAuth;

    @Value("${simple-travel-api.url}")
    private String simpleTravelApiUrl;

    @Autowired
    public SimpleTravelApiController(ApiAuth apiAuth) {
        this.apiAuth = apiAuth;
    }

    @SuppressWarnings("rawtypes")
	private HttpRequest unirestGetWithToken(String url) throws UnirestException {
        return Unirest.get(url).header("Authorization", "Bearer " + apiAuth.getToken());
    }


    @RequestMapping(method = GET, value = "/airports", produces = "application/json; charset=UTF-8")
    public Callable<ResponseEntity<Object>> query(@RequestParam Map<String, Object> params) throws UnirestException {
        return () -> {
            
			HttpRequest getRequest = unirestGetWithToken(simpleTravelApiUrl + "/airports").queryString(params);
            return new ResponseEntity<>(getRequest.asString().getBody(), HttpStatus.OK);
        };
    }

    @SuppressWarnings("unchecked")
	@RequestMapping(method = GET, value = "/airports/{key}", produces = "application/json; charset=UTF-8")
    public Callable<ResponseEntity<Object>> show(@RequestParam Map<String, Object> params,
                                                 @PathVariable("key") String key) throws UnirestException {
        return () -> {
            @SuppressWarnings("rawtypes")
			HttpRequest getRequest = unirestGetWithToken(simpleTravelApiUrl + "/airports/{key}").routeParam("key", key).queryString(params);
            return new ResponseEntity<>(getRequest.asString().getBody(), HttpStatus.OK);
        };
    }

    @SuppressWarnings("unchecked")
	@RequestMapping(method = GET, value = "/fares/{origin}/{destination}", produces = "application/json; charset=UTF-8")
    public Callable<ResponseEntity<Object>> calculateFare(@RequestParam Map<String, Object> params,
                                                          @PathVariable("origin") String origin,
                                                          @PathVariable("destination") String destination) {
        return () -> {
            @SuppressWarnings("rawtypes")
			HttpRequest faresRequest = unirestGetWithToken(simpleTravelApiUrl + "/fares/{origin}/{destination}")
                    .routeParam("origin", origin)
                    .routeParam("destination", destination)
                    .queryString(params);
            CompletableFuture<HttpResponse<JsonNode>> faresResponseFuture = faresRequest.asJsonAsync();

            @SuppressWarnings("rawtypes")
			HttpRequest originRequest = unirestGetWithToken(simpleTravelApiUrl + "/airports/{key}")
                    .routeParam("key", origin).queryString(params);
            CompletableFuture<HttpResponse<JsonNode>> originResponseFuture = originRequest.asJsonAsync();


            @SuppressWarnings("rawtypes")
			HttpRequest destinationRequest = unirestGetWithToken(simpleTravelApiUrl + "/airports/{key}")
                    .routeParam("key", destination)
                    .queryString(params);
            CompletableFuture<HttpResponse<JsonNode>> destinationResponseFuture = destinationRequest.asJsonAsync();

            HttpResponse<JsonNode> faresResponse = faresResponseFuture.get();
            HttpResponse<JsonNode> originResponse = originResponseFuture.get();
            HttpResponse<JsonNode> destinationResponse = destinationResponseFuture.get();

            JSONObject result = faresResponse.getBody().getObject();
            result.put("origin", originResponse.getBody().getObject());
            result.put("destination", destinationResponse.getBody().getObject());

            return new ResponseEntity<>(result.toString(), HttpStatus.OK);
        };
    }

}
