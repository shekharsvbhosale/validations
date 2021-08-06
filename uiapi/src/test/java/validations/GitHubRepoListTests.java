package validations;

import config.TestCore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.GitHubRepoList;
import utilities.ResponseParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class GitHubRepoListTests extends TestCore {
    private static final Logger LOGGER = LogManager.getLogger(GitHubRepoListTests.class);

    @Test(priority = 0)
    public void openInstance() throws IOException {
        GitHubRepoList gitHubRepoList = new GitHubRepoList(driver);
        String org = gitHubRepoList.getOrg();
        System.out.println("Selected org:: " + org);
        gitHubRepoList.openInstance();
        String title = gitHubRepoList.getTitleOfPage();
        Assert.assertTrue(title.contains(org.substring(1, 3)));
        LOGGER.info("Correct instance is opened: " + gitHubRepoList.getCurrentURL());
    }

    @Test(priority = 1)
    public void openRepositoriesTab() throws IOException {
        GitHubRepoList gitHubRepoList = new GitHubRepoList(driver);
        String org = gitHubRepoList.getOrg();
        try
        {
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
        Assert.assertEquals(gitHubRepoList.getCurrentURL(), expectedURL);

        LOGGER.info("Repositories tab is opened.");
    }

    @Test(priority = 2)
    public void compareRepositoryNamesWithAPIResponse() throws IOException {
        GitHubRepoList gitHubRepoList = new GitHubRepoList(driver);
        ResponseParser responseParser = new ResponseParser();
        String org = gitHubRepoList.getOrg();
        ArrayList<String> uilist = new ArrayList<>(gitHubRepoList.getRepositoryNameAndDescriptionsFromUI().keySet());
        Collections.sort(uilist);
        ArrayList<String> api = new ArrayList<>(responseParser.getRepositoryNameAndDescriptionFromAPI(org).keySet());
        Collections.sort(api);
        LOGGER.info("Validating UI and API response...");
        Assert.assertTrue(uilist.equals(api));
        LOGGER.info("Information (repository name and it's description) shown on UI matches, " +
                "which is retrieved from the API response.");
    }
}
