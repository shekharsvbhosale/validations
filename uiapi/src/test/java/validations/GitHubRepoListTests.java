package validations;

import config.TestCore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
        String org = GitHubRepoList.getPropertyValue("org");
        //System.out.println("Selected org:: "+org);
        gitHubRepoList.openInstance();
        String title = gitHubRepoList.getTitleOfPage();
        Assert.assertTrue(title.contains(org.substring(1, 3)));
        LOGGER.info("Correct instance is opened: " + gitHubRepoList.getCurrentURL());
        Assert.assertTrue(gitHubRepoList.displayOrgName().contains(org.substring(1, 3)));
        LOGGER.info("Correct organization name is displayed.");
    }

    @Test(priority = 1, dependsOnMethods = {"checkIfOrgHasNoRepositories"})
    public void openRepositoriesTab() throws IOException {
        GitHubRepoList gitHubRepoList = new GitHubRepoList(driver);
        LOGGER.info("Opening repository tab...");
        if (flagValue) {
            gitHubRepoList.openRepositoryTab();
            String expectedURL = GitHubRepoList.getPropertyValue("auturl") + "/orgs/"
                    + GitHubRepoList.getPropertyValue("org") + "/repositories";
            Assert.assertEquals(gitHubRepoList.getCurrentURL(), expectedURL);
            LOGGER.info("Correct repository list page displayed.");
        } else
            LOGGER.info("This organization doesn't have any public repositories yet.");
    }

    @Test(priority = 2)
    public void checkIfOrgHasNoRepositories() throws IOException {
        GitHubRepoList gitHubRepoList = new GitHubRepoList(driver);
        if (gitHubRepoList.displaySearchField()) {
            flagValue = true;
            LOGGER.info("Provided organization has one or more than one repositories.");
        } else
            LOGGER.info("This organization has no repositories.");
        Assert.assertEquals(gitHubRepoList.getCurrentURL(), gitHubRepoList.getInstanceURL());
    }

    @Test(priority = 3)
    public void compareRepositoryNamesWithAPIResponse() throws IOException {
        GitHubRepoList gitHubRepoList = new GitHubRepoList(driver);
        ResponseParser responseParser = new ResponseParser();
        String org = GitHubRepoList.getPropertyValue("org");
        ArrayList<String> uilist = new ArrayList<>(gitHubRepoList.getRepositoryNameAndDescriptionsFromUI().keySet());
        Collections.sort(uilist);
        ArrayList<String> api = new ArrayList<>(responseParser.getRepositoryNameAndDescriptionFromAPI(org).keySet());
        Collections.sort(api);
        Assert.assertTrue(uilist.equals(api));
        LOGGER.info("Information (repository name and it's description) shown on UI matches, " +
                "which is retrieved from the API response.");
    }
}
