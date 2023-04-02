/*
 * twinder
 * CS6650 assignment API
 *
 * OpenAPI spec version: 1.2
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package io.swagger.client.api;

import io.swagger.client.model.Matches;
import io.swagger.client.model.ResponseMsg;
import org.junit.Test;
import org.junit.Ignore;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * API tests for MatchesApi
 */
@Ignore
public class MatchesApiTest {

    private final MatchesApi api = new MatchesApi();

    /**
     * 
     *
     * return a maximum of 100 matches for a user
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void matchesTest() throws Exception {
        String userID = null;
        Matches response = api.matches(userID);

        // TODO: test validations
    }
}
