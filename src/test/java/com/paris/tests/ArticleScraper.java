package com.paris.tests;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.time.Duration;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;
public class ArticleScraper {

    private WebDriver driver;

    public ArticleScraper(String browser, String os, String osVersion) throws Exception {
//        WebDriverManager.chromedriver().setup();
//        this.driver = new ChromeDriver();
    	String username = "prajaktadigraje_3Z1sg2";
        String accessKey = "prchXPx6qsU5cUg6qJQp";

        // Create browser capabilities
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("browserName", browser);

        // Add BrowserStack-specific options
        Map<String, Object> browserstackOptions = new HashMap<>();
        browserstackOptions.put("os", os);
        browserstackOptions.put("osVersion", osVersion);
        browserstackOptions.put("userName", username);
        browserstackOptions.put("accessKey", accessKey);
        browserstackOptions.put("sessionName", "ArticleScraper Test");

        // Attach bstack:options to capabilities
        caps.setCapability("bstack:options", browserstackOptions);

        URL browserStackUrl = new URL("https://hub-cloud.browserstack.com/wd/hub");
        this.driver = new RemoteWebDriver(browserStackUrl, caps);
    }

    public void openWebsite(String url) {
        driver.get(url);
        driver.manage().window().maximize();
    }

    public boolean isWebsiteInSpanish() {
        WebElement htmlElement = driver.findElement(By.tagName("html"));
        String langAttribute = htmlElement.getAttribute("lang");
        return "es-ES".equalsIgnoreCase(langAttribute);
    }

    public List<String> scrapeArticleHeaders() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//article[@class='c c-o c-d c--m-n   ']")));
        List<WebElement> articles = driver.findElements(By.xpath("//article[@class='c c-o c-d c--m-n   ']"));

        List<String> articleHeaders = new ArrayList<>();
        for (int i = 0; i < Math.min(articles.size(), 5); i++) {
            WebElement currentArticleHeading = articles.get(i).findElement(By.xpath("./header/h2/a"));
            articleHeaders.add(currentArticleHeading.getText());
        }
        return articleHeaders;
    }

    public String translateText(String text, String apiKey) throws Exception {
        String url = "https://google-translate113.p.rapidapi.com/api/v1/translator/html";

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("x-RapidAPI-Key", apiKey);
        connection.setRequestProperty("x-RapidAPI-Host", "google-translate113.p.rapidapi.com");
        connection.setDoOutput(true);

        JSONObject requestBody = new JSONObject();
        requestBody.put("from", "auto");
        requestBody.put("to", "en");
        requestBody.put("html", text);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
        StringBuilder response = new StringBuilder();
        String responseLine;
        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }

        JSONObject jsonResponse = new JSONObject(response.toString());
        return jsonResponse.getString("trans");
    }

    public void wordCount(List<String> articleHeaders) {
        StringBuilder combinedString = new StringBuilder();
        for (String str : articleHeaders) {
            combinedString.append(str).append(" ");
        }
        String[] words = combinedString.toString().toLowerCase().split("\\W+");
        Map<String, Integer> wordCountMap = new HashMap<>();
        for (String word : words) {
            wordCountMap.put(word, wordCountMap.getOrDefault(word, 0) + 1);
        }
        wordCountMap.forEach((word, count) -> {
           if (count > 2) {
                System.out.println(word + ": " + count);
            }
        });
    }

    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }
}
