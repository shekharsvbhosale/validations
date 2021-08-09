package validations;

import config.TestCore;
import io.qameta.allure.*;
import listeners.TestReportListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.GitHubRepoList;
import utilities.ResponseParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*Core Test class which will be used to achieve project goal.
* This class contains test methods to validate possible test cases.
* Each method executed with its own object properties, mostly.*/
@Listeners(TestReportListener.class)
@Epic("GitHub Repositories Validation")
@Feature("Repository Tab List")
public class GitHubRepoListTests extends TestCore {
    private static final Logger LOGGER = LogManager.getLogger(GitHubRepoListTests.class);

    /*Validation for opening GitHub page for provided organization.
    * Only title validation is added. We can add multiple validations here.*/
    @Issues({@Issue("openInstance")})
    @Story("Open GitHub organization instance")
    @Test(description = "Validate if correct instance is opened.")
    @Description("Test Description: Correct instance based on provided input must be opened.")
    @Severity(SeverityLevel.CRITICAL)
    @Step("1. Go to provided GitHub instance.")
    public void openInstance() throws IOException {
        GitHubRepoList gitHubRepoList = new GitHubRepoList(driver);
        String org = gitHubRepoList.getOrg();
        gitHubRepoList.openInstance();
        String title = gitHubRepoList.getTitleOfPage();

        Assert.assertTrue(title.contains(org.substring(1, 3)));
        boolean result = title.contains(org.substring(1, 3));
        if(result)
            LOGGER.info("Correct instance is opened: " + gitHubRepoList.getCurrentURL());
        else
            LOGGER.error("Invalid instance.");
        Assert.assertTrue(result);
    }

    /*Method to open repository tab for provided organization
    * Currently assertion implemented for domain URL only. Multiple assertions can be added here.
    * Domain URL changes based on the organization type i.e. 'personal' and 'organization' values.*/
    @Issues({@Issue("openRepositoriesTab")})
    @Story("Open Repository Tab")
    @Test(description = "Validate if user is able to open repositories tab.")
    @Description("Test Description: Validate if user is able to open repositories tab.")
    @Severity(SeverityLevel.MINOR)
    @Step("1. Go to organization page."+"\r\n"+"2. Go to Repositories tab.")
    public void openRepositoriesTab() throws IOException {
        GitHubRepoList gitHubRepoList = new GitHubRepoList(driver);
        String org = gitHubRepoList.getOrg();
        try
        {
            /*Check if repositories are present in the given account. Simple validation
            * based on message shown on UI is added.*/
            Assert.assertTrue(gitHubRepoList.verifyIfNoRepositoriesPresent());
            try
            {
                gitHubRepoList.openRepoTab();
            }catch (NoSuchElementException e)
            {
                LOGGER.info("Opening repository tab...");
                gitHubRepoList.displayRepositoryTabForOrg().click();
            }
        }catch (NoSuchElementException e)
        {
            LOGGER.warn(org+" has one or more repositories.");
        }
        String expectedURL;
        if(!gitHubRepoList.openRepoTab())
            expectedURL = GitHubRepoList.getPropertyValue("auturl") + "/orgs/"
                    + org + "/repositories";
        else
            expectedURL = GitHubRepoList.getPropertyValue("auturl") + "/"+ org + "?tab=repositories";
        boolean result = gitHubRepoList.getCurrentURL().equals(expectedURL);
        if(result)
            LOGGER.info("Repositories tab is opened.");
        else
            LOGGER.error("Repositories tab is not opened.");
        Assert.assertTrue(result);
    }

    /*Method to validate Repository name shown on UI and received from API response.
    * First read the dataset for UI and then for API. Compare both.
    * Convert dataset to Collection, here ArrayList and sort it.
    * If comparison equals to true, then we can conclude UI and API data is same, else mark test as failure.
    */

    @Issues({@Issue("validateRepositoryNameWithAPIResponse")})
    @Story("Validate repository names.")
    @Test(description = "Validate repository names",
            dependsOnMethods = {"openInstance","openRepositoriesTab"})
    @Description("Test Description: Validate repository name shown on UI w.r.t. that " +
            "retrieved from API response.")
    @Severity(SeverityLevel.CRITICAL)
    @Step("1. Go to organization page."+"\r\n"+"2. Go to Repositories tab."+"\r\n"+"3. Extract and store " +
            "repository name shown on UI."+"\r\n"+"4. Extract and store name received in API response."+"\r\n"
            +"5. Compare and assert name values.")
    public void validateRepositoryNameWithAPIResponse() throws IOException {
        GitHubRepoList gitHubRepoList = new GitHubRepoList(driver);
        ResponseParser responseParser = new ResponseParser();
        String org = gitHubRepoList.getOrg();
        gitHubRepoList.openRepoTab();
        LOGGER.info("Validating repository name...");
        List<String> uikey,apikey,uivalue,apivalue;
        uikey = new ArrayList<>(gitHubRepoList.getRepositoryNameAndDescriptionsFromUI().keySet());
        Collections.sort(uikey);
        apikey = new ArrayList<>(responseParser.getRepositoryNameAndDescriptionFromAPI(org).keySet());
        Collections.sort(apikey);
        boolean result = apikey.equals(uikey);
        if(result)
            LOGGER.info("Repository name shown on UI matches with that retrieved from the API response.");
        else
            LOGGER.error("Repository name shown on UI doesn't match with that retrieved from the API response.");
        Assert.assertTrue(result);
    }

    /*Method to validate Repository description shown on UI and received from API response.
     * First read the dataset for UI and then for API. Compare both.
     * Convert dataset to Collection, here ArrayList and sort it.
     * If comparison equals to true, then we can conclude UI and API data is same, else mark test as failure.
     */
    @Issues({@Issue("validateRepositoryDescriptionWithAPIResponse")})
    @Story("Validate repository description.")
    @Test(description = "Validate repository description",
            dependsOnMethods = {"openInstance","openRepositoriesTab"})
    @Description("Test Description: Validate repository description shown on UI w.r.t. that " +
            "retrieved from API response.")
    @Severity(SeverityLevel.CRITICAL)
    @Step("1. Go to organization page."+"\r\n"+"2. Go to Repositories tab."+"\r\n"+"3. Extract and store " +
            "repository description shown on UI."+"\r\n"+"4. Extract and store description received in API response."+"\r\n"
            +"5. Compare and assert description values.")
    public void validateRepositoryDescriptionWithAPIResponse() throws IOException {
        GitHubRepoList gitHubRepoList = new GitHubRepoList(driver);
        ResponseParser responseParser = new ResponseParser();
        String org = gitHubRepoList.getOrg();
        gitHubRepoList.openRepoTab();
        LOGGER.info("Validating repository description...");
        List<String> uivalue,apivalue;
        uivalue =  new ArrayList<>(gitHubRepoList.getRepositoryNameAndDescriptionsFromUI().values());
        Collections.sort(uivalue);
        apivalue = new ArrayList<>(responseParser.getRepositoryNameAndDescriptionFromAPI(org).values());
        Collections.sort(apivalue);
        boolean result = apivalue.equals(uivalue);
        if(result)
            LOGGER.info("Repository description shown on UI matches with that retrieved from the API response.");
        else
            LOGGER.error("Repository description shown on UI doesn't match with that retrieved from the API response.");
        Assert.assertTrue(result);
    }
}
