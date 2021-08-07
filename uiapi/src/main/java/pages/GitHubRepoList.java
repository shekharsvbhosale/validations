package pages;

import core.PageCore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.LinkedHashMap;
import java.util.List;

/*This class is implementation of all operations which are going to be executed to achieve the project goal.
* which is validating UI and API data.*/
public class GitHubRepoList extends PageCore {
    private static final Logger LOGGER = LogManager.getLogger(GitHubRepoList.class);

    public GitHubRepoList(WebDriver driver) {
        super(driver);
    }

    /*Instance URL is provided as property file, since it could change for on-premise platform, but endpoint will
    remain same.
    If instance is not provided, program execution can't proceed and so terminate execution.*/
    public String getInstanceURL() {
        String instance, org, url = null;
        try {
            instance = getPropertyValue("auturl");
            org = getOrg();
            if (!instance.matches(".*[/]$"))
                instance = instance + "/";
            if (instance.isEmpty() || org.isEmpty()) {
                LOGGER.warn("No instance is provided. Terminating execution.");
                System.exit(-1);
            }
            url = instance + org;
        } catch (InvalidPathException | IOException e) {
            e.printStackTrace();
        }
        return url;
    }

    public void openInstance() {
        LOGGER.info(getInstanceURL());
        driver.get(getInstanceURL());
    }

    public WebElement displayRepositoryTabForOrg() {
        return fluentWaitWithCustomTimeout("repositoriesTabForOrg", 6);
    }

    public boolean openRepoTab() {
        displayRepositoryTabForOrg().click();
        fluentWaitWithCustomTimeout("searchRepoField", 6);
        return false;
    }

    /*Method to check if no repository message is displayed when there are no repositories present.*/
    public boolean verifyIfNoRepositoriesPresent() {
        return driver.findElement(locatorParser.getElementLocator("noReposPresent")).isDisplayed();
    }

    /*This method is implemented to retrieve repository details like name and description which are shown on UI
    * Names and descriptions are stored in different datasets
    * Data for repositories with no description is stored in different dataset to ease further validation operations.
    * Repository names with string 'Archived ' is formatted before storing in dataset.
    * Repository descriptions which are null are formatted to store 'null_string' to ease further validation ops.*/

    public LinkedHashMap<String, String> getRepositoryNameAndDescriptionsFromUI() {
        LinkedHashMap<String, String> repositoryDetails_UI = new LinkedHashMap<>();
        List<WebElement> originalListNames, repoDesc, repositoriesWithoutDesc, repositoriesWithDesc;
        originalListNames = driver.findElements(locatorParser
                .getElementLocator("repositoryNameInRTab"));
        repoDesc = driver.findElements(locatorParser
                .getElementLocator("repositoryDescription"));
        repositoriesWithDesc = driver.findElements(locatorParser.getElementLocator("reposWithDesc"));
        originalListNames.removeAll(repositoriesWithDesc);
        repositoriesWithoutDesc = originalListNames;
        for (int i = 0; i < repositoriesWithDesc.size(); i++) {
            if (repositoriesWithDesc.size() == repoDesc.size()) {
                repositoryDetails_UI.put(formatRepoNamesRemoveArchivedString(repositoriesWithDesc, i),
                        formatRepoNamesRemoveArchivedString(repoDesc, i));
            }
        }
        /*If description is null, we are replacing null with 'null_string' so that we can sort the dataset
        * properly to validate against API response*/
        for (WebElement element : repositoriesWithoutDesc) {
            repositoryDetails_UI.putIfAbsent(element.getText(), "null");
            LOGGER.info("No description is shown on UI for the repository: "+element.getText());
        }
        LOGGER.info("Repository Details from UI: "+repositoryDetails_UI);
        return repositoryDetails_UI;
    }

    private String formatRepoNamesRemoveArchivedString(List<WebElement> list, int index) {
        return list.get(index).getText().replaceAll(" Archived", "");
    }

    /*This method is used mainly to get user input for GitHub organization.
    * If user do not provide input, default value will be picked up from configuration and will be returned
    * for further execution.*/
    public String getOrg() throws IOException {
        String org = System.getProperty("org");
        if (org == null || org.isEmpty())
            org = getPropertyValue("org");
        return org;
    }
}
