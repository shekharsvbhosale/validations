package utilities;

import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static io.restassured.RestAssured.given;

/*This class is API utility which deals mainly with parsing of API response.
* RestAssured built-in features are used to extract the values for received response and stored in dataset.
* Dataset might contain null values which are later replaced by null_string to ease further validation operations
* */

public class ResponseParser {
    private static final Logger LOGGER = LogManager.getLogger(ResponseParser.class);
    String endpoint;

    public LinkedHashMap<String, String> getRepositoryNameAndDescriptionFromAPI(String org){
        org = org.toLowerCase();
        endpoint = "https://api.github.com/orgs/" + org +
                "/repos?type=all&sort=pushed&page=1&direction=full_name&order=asc";
        Response response = given().
                when().get(endpoint).
                then().extract().response();

        LinkedHashMap<String, String> repositoryDetails = new LinkedHashMap<>();
        List<Object> jsonResponse = response.jsonPath().getList("$");
        for (int i = 0; i < jsonResponse.size(); i++) {
            String nm = response.jsonPath().getString("full_name[" + i + "]")
                    .replaceAll(org + "/", "");
            String des = response.jsonPath().getString("description[" + i + "]");
            repositoryDetails.put(nm, des);
        }
        /*If description is null, we are replacing null with 'null_string' so that we can sort the dataset
         * properly to validate against UI response*/
        for (Map.Entry<String, String> repoSet :
                repositoryDetails.entrySet()) {
            if(repoSet.getValue()==null) {
                repositoryDetails.put(repoSet.getKey(), "null");
                LOGGER.info("Description is null/empty from retrieved API response for the repository: "
                        + repoSet.getKey());
            }
        }
        LOGGER.info("Repository Details from API: "+repositoryDetails);
        return repositoryDetails;
    }
}