package com.serenitydojo.playwright;

import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.junit.Options;
import com.microsoft.playwright.junit.OptionsFactory;
import com.microsoft.playwright.junit.UsePlaywright;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static java.util.ArrayList.*;
class GlassLewisDemo {
    private final Page page;

    GlassLewisDemo(Page page) {
        this.page = page;
    }

    void OpenPage() {
        this.page.navigate("https://viewpoint.glasslewis.com/WD/?siteId=DemoClient");
    }

    void TriggerOutOfFocus() {
        this.page.locator("[id='filter-country'] .fieldset-county .checkbox label").first().click();
    }

    void FilterGridByCountry(String country) {
        // Filter grid by country = Belgium
        this.page.locator("[id='filter-country'] input[data-bind*='visible:displaySearchBox']").fill(country);
        TriggerOutOfFocus();
        TriggerOutOfFocus();
        this.page.locator("[id='filter-country'] #btn-update").click();
    }

    void SearchAndGoToCompanyDetails(String companyName, String country) {
        page.locator("[id=header-search] input[class='k-input']").fill(companyName);
        page.getByText(companyName + " - " + country).click();
    }

}

@UsePlaywright(ASimplePlaywrightTest.MyOptions.class)
public class ASimplePlaywrightTest {

    public static class MyOptions implements OptionsFactory{

        // Set to run headless browser.
        @Override
        public Options getOptions() {
            return new Options()
                    .setHeadless(false)
                    .setLaunchOptions(
                            new BrowserType.LaunchOptions()
                                  .setArgs(Arrays.asList("--no-sandbox",
                                                             "--disable-gpu",
                                                             "--disable-extesions"
                                  ))
                    );
        }
    }

    ///  Sample code for technical interview.
    @Test
    void filterGridByCountry(Page page) throws InterruptedException {
        // Go to site
        // page.navigate("https://viewpoint.glasslewis.com/WD/?siteId=DemoClient");
        GlassLewisDemo glassLewisDemoPage = new GlassLewisDemo(page);
        glassLewisDemoPage.OpenPage();

        // Filter grid by country = Belgium
        //  page.locator("[id='filter-country'] input[data-bind*='visible:displaySearchBox']").fill("Belgium");
        //  page.locator("[id='filter-country'] .fieldset-county .checkbox label").first().click();
        //  page.locator("[id='filter-country'] .fieldset-county .checkbox label").first().click();
        //  page.locator("[id='filter-country'] #btn-update").click();
        glassLewisDemoPage.FilterGridByCountry("Belgium");


        // Get all rows to see if there are matching results
        int matchingSearchResults = page.locator("[id='grid'] [class*='content'] tbody tr").count();
        Assertions.assertTrue(matchingSearchResults > 0 );

        // Get the data in the last column and see if all of them are from Belgium
        // page.locator("[id='grid'] [class*='content'] tbody tr").first().click();
        glassLewisDemoPage.TriggerOutOfFocus();
        List<String> countryResults = page.locator("[id='grid'] [class*='content'] tbody tr td:nth-child(5)").allTextContents();
        for (String country : countryResults) {
            Assertions.assertTrue(country.contains("Belgium"));
        }
    }

    @Test
    void searchByCompany(Page page){
        // Go to site
        // page.navigate("https://viewpoint.glasslewis.com/WD/?siteId=DemoClient");
        GlassLewisDemo glassLewisDemoPage = new GlassLewisDemo(page);
        glassLewisDemoPage.OpenPage();

        // Fill in the search box with "Activision Blizzard Inc"
        // page.locator("[id=header-search] input[class='k-input']").fill("Activision Blizzard Inc");
        // page.getByText("Activision Blizzard Inc - United States").click();
        glassLewisDemoPage.SearchAndGoToCompanyDetails("Activision Blizzard Inc", "United States");

        // Assert that the page is in the correct page by its Header text
        PlaywrightAssertions.assertThat(page.locator("[id='detail-issuer-name']")).containsText("Activision Blizzard Inc");

        // Assert that the grid is displayed
        PlaywrightAssertions.assertThat(page.locator("#detail-meetings-table-container"));
    }
}
