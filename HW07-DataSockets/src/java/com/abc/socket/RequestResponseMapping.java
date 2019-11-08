package com.abc.socket;

import java.util.*;

public class RequestResponseMapping {
    private static RequestResponseMapping singleton;

    private final Map<String, String> map;
    private final String disconnectRequest;
    private final String disconnectResponse;
    private final String[] requestList;

    private RequestResponseMapping() {
        map = new LinkedHashMap<>(); // keeps keys in order of entry
        disconnectRequest = "GOODBYE";
        disconnectResponse = "BYE-BYE";

        map.put("Hello", "Hello!");
        map.put("How are you?", "Great!");
        map.put("Do you like snow?", "Yes!");
        map.put("Java is fun", "A lot of fun!");
        map.put("How many days?", "Ten days");
        map.put("How many weeks?", "Three weeks");
        map.put("What is the password?", "Open sesame");

        requestList = map.keySet().toArray(new String[0]);
    }

    public static synchronized RequestResponseMapping getInstance() {
        if ( singleton == null ) {
            singleton = new RequestResponseMapping();
        }

        return singleton;
    }

    /**
     * All possible requests&mdash;except the "disconnect request".
     */
    public String[] getRequestList() {
        // clone to be sure that no one alters what is in each slot
        return requestList.clone();
    }

    /**
     * Returns a randomly selected request.
     * Never returns the "disconnect request".
     */
    public String getRandomRequest() {
        int idx = (int) (Math.random() * requestList.length);
        return requestList[idx];
    }

    /**
     * Returns the appropriate response to the specified request, if found.
     * Returns <tt>null</tt> if the request is not found or if the request
     * is the "disconnect request".
     */
    public String getResponse(String request) {
        return map.get(request);
    }

    public String getDisconnectRequest() {
        return disconnectRequest;
    }


    public String getDisconnectResponse() {
        return disconnectResponse;
    }

    public boolean isDisconnectRequest(String request) {
        return disconnectRequest.equals(request);
    }
}
