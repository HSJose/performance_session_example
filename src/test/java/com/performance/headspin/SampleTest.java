package com.performance.headspin;

import java.io.IOException;
import java.net.URL;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;



public class SampleTest
{   
    // global driver variables
    private static AppiumDriver<MobileElement> driver = null;
    public static WebDriverWait wait;
    
    // API Tokens and necessary URLs
    private static final String apiToken = System.getenv("API_KEY");
    public static final String webDriverUrl = "https://us-mv.headspin.io:3029/v0/"+apiToken+"/wd/hub";
    public static final String API_URL = "https://"+apiToken+"@api-dev.headspin.io";
    public static final String session_endpoint = "/v0/sessions";
    public static final String add_label = "/label/add";
    public static final String session_analysis_status = "/analysis/status/";

    // Session Information
    public static String session_id;
    public static long session_start_time;
    public static long test_start;
    public static long test_end;

    // Desired capabilities
    public static final String deviceName = "SM-F707U1";
    public static final String udid = "R3CR10QZA1W";
    public static final String automationName = "UiAutomator2";
    public static final String platformName = "Android";
    public static final String browserName = "Chrome";
    
    @BeforeMethod
    public void setup() throws IOException, InterruptedException
    {
        System.out.println(apiToken);
        DesiredCapabilities caps = new DesiredCapabilities();

        caps.setCapability("deviceName", deviceName);
        caps.setCapability("udid", udid);
        caps.setCapability("automationName", automationName);
        caps.setCapability("platformName", platformName);
        caps.setCapability("browserName", browserName);
        caps.setCapability("headspin:capture", true);
        caps.setCapability("headspin:testName", "Example Using Session API");
        
        URL ServiceUrl = new URL(webDriverUrl);

        System.out.println("Starting Driver");
        driver = new AndroidDriver<MobileElement>(ServiceUrl, caps);
        System.out.println("Driver started.");

        session_start_time = Instant.now().getEpochSecond();
        wait = new WebDriverWait(driver, 10);
        String temp = SendGETRequest(API_URL+session_endpoint+"?num_sessions=1").body();

        ObjectMapper mapper = new ObjectMapper();
        Sessions sesh = mapper.readValue(temp, Sessions.class);

        session_id = sesh.getSessions().get(0).getSession_id();
        System.out.println("Session Id: "+session_id);
    }

    @Test
    public void Test() throws InterruptedException, IOException
    {   
        /*
        Use the dynamic loaing example page to run the session analysis
        */

        // Locator Strings
        String StartButtonLocator = "//button[text()=\"Start\"]";
        String HelloWorldLocator = "//div[@id=\"finish\"]";

        // Test Steps
        driver.get("https://the-internet.herokuapp.com/dynamic_loading/2");

        WebElement startButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(StartButtonLocator)));
        System.out.println("Page Loading");
        test_start = Instant.now().getEpochSecond();
        startButton.click();
        
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(HelloWorldLocator)));
        test_end = Instant.now().getEpochSecond();
        System.out.println("Page Loaded");

        HashMap<String, String> test_result = new HashMap<String, String>();
        test_result.put("session_id", session_id);
        test_result.put("status", "passed");
        System.out.println(SendPOSTRequest("https://api-dev.headspin.io/v0/perftests/upload", test_result).body());
    }

    @AfterMethod
    public void teardown() throws IOException, InterruptedException
    {
        // Label Creation
        HashMap<String, String> label_data_to_send = new HashMap<String, String>();
        label_data_to_send.put("name", "Loading Animation Example");
        label_data_to_send.put("label_type", "page-load-request");
        label_data_to_send.put("ts_start", String.valueOf(test_start));
        label_data_to_send.put("ts_end", String.valueOf(test_end));

        System.out.println(SendPOSTRequest(API_URL+session_endpoint+"/"+session_id+add_label, label_data_to_send).body());

        // Test Clean Up
        if(driver != null)
        {
            System.out.println("Quitting the driver");
            driver.quit();
        }

        System.out.println(SendGETRequest(API_URL+session_endpoint+session_analysis_status+session_id).body());
    }

    // Serialization helper method
    public String DataBuilder(HashMap<String, String> data) throws JsonProcessingException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        String object = objectMapper.writeValueAsString(data);
        return object;
    }

    // Helper method for sending POST request
    public HttpResponse<String> SendPOSTRequest(String API_URI, HashMap<String, String> data) throws IOException, InterruptedException
    {   
        String data_object = DataBuilder(data);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(API_URI))
            .POST(HttpRequest.BodyPublishers.ofString(data_object))
            .header("Authorization", "Bearer "+apiToken)
            .header("Accept", "application/json")
            .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("URL: "+response.uri());
        System.out.println("Response Code: "+response.statusCode());
        return response;
    }

    // Helper method for sending GET request
    public HttpResponse<String> SendGETRequest(String API_URI) throws IOException, InterruptedException
    {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(API_URI))
            .header("Authorization", "Bearer "+apiToken)
            .header("Accept", "application/json")
            .GET()
            .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("URL: "+response.uri());
        System.out.println("Response Code: "+response.statusCode());
        return response;
    }
}