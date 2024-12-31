package com.paris.tests;
import java.net.HttpURLConnection;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import org.json.JSONObject;
import java.util.ArrayList;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import io.github.bonigarcia.wdm.WebDriverManager;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
public class Article {
	public static void wordCount( List<String> article_header_list) {
		StringBuilder combinedString = new StringBuilder();
        for (String str : article_header_list) {
            combinedString.append(str).append(" ");
        }
        String[] words = combinedString.toString().toLowerCase().split("\\W+");
        HashMap<String, Integer> wordCountMap = new HashMap<>();
        for (String word : words) {
            wordCountMap.put(word, wordCountMap.getOrDefault(word, 0) + 1);
        }
        for (Map.Entry<String, Integer> entry : wordCountMap.entrySet()) {
        	if(entry.getValue()>2) {
                System.out.println(entry.getKey() + ": " + entry.getValue());

        	}
        }
		
	}
	 public static String translateText(String text, String apiKey) throws Exception {
	        // URL for Rapid Translate API
	        String url = "https://google-translate113.p.rapidapi.com/api/v1/translator/html";

	        // Set up the connection
	        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
	        connection.setRequestMethod("POST");
	        connection.setRequestProperty("Content-Type", "application/json");
	        connection.setRequestProperty("x-RapidAPI-Key", apiKey);
	        connection.setRequestProperty("x-RapidAPI-Host", "google-translate113.p.rapidapi.com");
	        connection.setDoOutput(true);

	        // JSON body for the translation request
	        JSONObject requestBody = new JSONObject();
	        requestBody.put("from", "auto"); // Automatically detect source language
	        requestBody.put("to", "en"); // Target language (English)
	        requestBody.put("html", text); // Text or HTML content to translate

	        // Send request
	        try (OutputStream os = connection.getOutputStream()) {
	            byte[] input = requestBody.toString().getBytes("utf-8");
	            os.write(input, 0, input.length);
	        }

	        // Read the response
	        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
	        StringBuilder response = new StringBuilder();
	        String responseLine;
	        while ((responseLine = br.readLine()) != null) {
	            response.append(responseLine.trim());
	        }

	        // Parse the response and return the translated text
	        JSONObject jsonResponse = new JSONObject(response.toString());

	        return jsonResponse.getString("trans");
	    }
	public static void main(String[] args) {
		System.out.println("Execution started...");
        // Set up ChromeDriver path
        //System.setProperty("webdriver.chrome.driver", "C:/Users/digra/eclipse-workspace/paris/chromedriver");
        WebDriverManager.chromedriver().setup();

        // Initialize WebDriver
        WebDriver driver = new ChromeDriver();
       try {
        // Open elpais website
        driver.get("https://elpais.com/");
        
        // Maximize the browser window
        driver.manage().window().maximize();
        
        
        //1.Ensure that the website's text is displayed in Spanish.
        WebElement htmlElement = driver.findElement(By.tagName("html"));
        String langAttribute = htmlElement.getAttribute("lang");
        // Check if the lang attribute is set to Spanish
        if ("es-ES".equalsIgnoreCase(langAttribute)) {
            System.out.println("The website's language is Spanish.");
        } else {
            System.out.println("The website's language is NOT Spanish. Detected: " + langAttribute);
        }
        
        
        //2.Scrape Articles from the Opinion Section:
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//div[@class='b-t_w b_sb']/div[@class='b-t_d _g _g-xs b_cnb-lg b_row-2 b-t_d-6']/article")));
        String whole_div_of_articles="//div[@class='b-t_w b_sb']/div[@class='b-t_d _g _g-xs b_cnb-lg b_row-2 b-t_d-6']/article";
        List<WebElement>articles = driver.findElements(By.xpath(whole_div_of_articles));
        System.out.println("Articles Heading: "+articles.size());
        int i=0;
        List<String>article_header_list=new ArrayList<String>();
        List<String>article_header_list_translated=new ArrayList<String>();

        for (WebElement link : articles) {
        	if(i<5) {
        		//String article_header_xpath=whole_div_of_articles+"/header/h2/a";
            	System.out.println(i+1+" "+"Article Heading: ");
            	WebElement current_article_heading= link.findElement(By.xpath("./header/h2/a"));
                System.out.println(current_article_heading.getText());
                article_header_list.add(current_article_heading.getText());
                //String article_contents_xpath=whole_div_of_articles+"/p";
            	WebElement current_article_contents= link.findElement(By.xpath("./p"));
                System.out.println(current_article_contents.getText());
               
        	}
        	i++;

        }
        
        
        //3.Translate Article Headers:
        String apiKey = "b86c7d6e1amsh7ba8a3b28ec07f4p16b9d0jsn9b50d7cf7ad8";

        for (String title : article_header_list) {
            String translatedTitle = translateText(title, apiKey);
            System.out.println("Original Title: " + title);
            System.out.println("Translated Title: " + translatedTitle);
            article_header_list_translated.add(translatedTitle);
            System.out.println("-----");
        }
        
        
        //4.WordCount
        wordCount(article_header_list_translated);
        
        
       }catch (Exception e) {
           e.printStackTrace();
       } finally {
           // Close the browser
           driver.quit();
       }
       
    }
}
