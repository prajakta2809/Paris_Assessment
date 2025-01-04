package com.paris.tests;

import com.paris.tests.ArticleScraper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.Parameters;

public class ArticleTest {

    private ArticleScraper scraper;

    @Parameters({"browser", "os", "osVersion"})
    @BeforeClass
    public void setUp(String browser, String os, String osVersion)throws Exception {
        scraper = new ArticleScraper(browser, os, osVersion);
    }

    @Test
    public void testArticleScrapingAndTranslation() {
        try {
            scraper.openWebsite("https://elpais.com/");

            // Verify website language
            boolean isSpanish = scraper.isWebsiteInSpanish();
            System.out.println("Is website in Spanish? " + isSpanish);

            // Scrape article headers
            List<String> articleHeaders = scraper.scrapeArticleHeaders();
            System.out.println("Scraped article headers: " + articleHeaders);

            // Translate headers
            List<String> articleHeaders_translated = new ArrayList<>();

            String apiKey = "b86c7d6e1amsh7ba8a3b28ec07f4p16b9d0jsn9b50d7cf7ad8";
            for (String header : articleHeaders) {
                String translatedHeader = scraper.translateText(header, apiKey);
                System.out.println("Translated Header: " + translatedHeader);
                articleHeaders_translated.add(translatedHeader);
            }

            // Word count
            scraper.wordCount(articleHeaders_translated);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public void tearDown() {
        scraper.closeBrowser();
    }
}
