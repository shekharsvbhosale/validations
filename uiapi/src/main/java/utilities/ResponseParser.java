package utilities;

import io.restassured.response.Response;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static io.restassured.RestAssured.given;

public class ResponseParser {
    String endpoint;

    public HashMap getRepositoryNameAndDescriptionFromAPI(String org) {
        org = org.toLowerCase();
        endpoint = "https://api.github.com/orgs/" + org +
                "/repos?type=all&sort=pushed&page=1&direction=full_name&order=asc";
        Response response = given().
                when().get(endpoint).
                then().extract().response();

        HashMap<String, String> repositoryDetails = new LinkedHashMap<>();
        List<String> jsonResponse = response.jsonPath().getList("$");
        for (int i = 0; i < jsonResponse.size(); i++) {
            String nm = response.jsonPath().getString("full_name[" + i + "]")
                    .replaceAll(org + "/", "");
            String des = response.jsonPath().getString("description[" + i + "]");
            repositoryDetails.put(nm, des);
        }
        return repositoryDetails;
    }
}