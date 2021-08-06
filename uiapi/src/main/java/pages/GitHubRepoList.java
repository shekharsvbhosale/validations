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

public class GitHubRepoList extends PageCore {
    private static final Logger LOGGER = LogManager.getLogger(GitHubRepoList.class);

    public GitHubRepoList(WebDriver driver) {
        super(driver);
    }

    public String getInstanceURL() {
        String instance, proj, url = null;
        try {
            instance = getPropertyValue("auturl");
            proj = getPropertyValue("org");
            if (!instance.matches(".*[/]$"))
                instance = instance + "/";
            if (instance.isEmpty() || proj.isEmpty()) {
                LOGGER.warn("No instance is provided. Terminating execution.");
                System.exit(-1);
            }
            url = instance + proj;
        } catch (InvalidPathException | IOException e) {
            e.printStackTrace();
        }
        return url;
    }

    public void openInstance() {
        LOGGER.info(getInstanceURL());
        driver.get(getInstanceURL());
    }

    public WebElement displayRepositoryTab() {
        return fluentWaitWithCustomTimeout("repositoriesTab", 3);
    }

    public String displayOrgName() {
        return getTextFromCurrentElement("orgName", 2);
    }

    public void openRepositoryTab() {
        displayRepositoryTab().click();
        fluentWaitWithCustomTimeout("searchRepoField", 4);
    }

    public boolean displaySearchField() {
        return checkIfElementIsPresent("searchRepoField");
    }

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
            if (repositoriesWithDesc.size() == repoDesc.size())
                repositoryDetails_UI.put(formatRepoNamesRemoveArchivedString(repositoriesWithDesc,i)
                        , formatRepoNamesRemoveArchivedString(repoDesc,i));
        }
        for (WebElement element : repositoriesWithoutDesc) {
            repositoryDetails_UI.putIfAbsent(element.getText(), null);
        }
        return repositoryDetails_UI;
    }

    private String formatRepoNamesRemoveArchivedString(List<WebElement> list,int index)
    {
        return list.get(index).getText().replaceAll(" Archived", "");
    }
}
