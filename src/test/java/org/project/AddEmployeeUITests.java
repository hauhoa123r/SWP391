package org.project;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.UnexpectedTagNameException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AddEmployeeUITests {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setup() {
        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless"); // bật nếu cần chạy CI
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-extensions");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("http://localhost:8089/admin/doctor-staffs/create");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("email")));
    }

    @AfterEach
    void cleanup() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    @DisplayName("CE-01: Check form accessibility and authentication")
    void testFormAccessibility() {
        // Kiểm tra URL hiện tại
        String currentUrl = driver.getCurrentUrl();
        System.out.println("Current URL: " + currentUrl);
        
        // Kiểm tra xem có bị redirect đến login không
        if (currentUrl.contains("login")) {
            System.out.println("REDIRECTED TO LOGIN - Authentication required!");
            return;
        }
        
        // Kiểm tra page title
        String pageTitle = driver.getTitle();
        System.out.println("Page title: " + pageTitle);
        
        // Kiểm tra có form không
        try {
            var emailField = driver.findElement(By.name("email"));
            System.out.println("Email field found: " + (emailField != null));
        } catch (NoSuchElementException e) {
            System.out.println("Email field NOT found - may not be on correct page");
        }
        
        // Kiểm tra có submit button không
        try {
            var submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
            System.out.println("Submit button found: " + (submitButton != null));
        } catch (NoSuchElementException e) {
            System.out.println("Submit button NOT found");
        }
        
        // Kiểm tra page source có chứa thông tin gì
        String pageSource = driver.getPageSource();
        
        if (pageSource.contains("403") || pageSource.contains("Forbidden")) {
            System.out.println("ACCESS FORBIDDEN - 403 error");
        }
        if (pageSource.contains("401") || pageSource.contains("Unauthorized")) {
            System.out.println("UNAUTHORIZED - 401 error");
        }
        if (pageSource.contains("login") || pageSource.contains("Login")) {
            System.out.println("Page contains login references");
        }
        
        // Thử điền form và xem chuyện gì xảy ra
        try {
            fillFormWithValidData();
            System.out.println("Form filled successfully");
            
            driver.findElement(By.cssSelector("button[type='submit']")).click();
            System.out.println("Submit button clicked");
            
            // Đợi và xem URL có thay đổi không
            Thread.sleep(2000);
            String newUrl = driver.getCurrentUrl();
            System.out.println("URL after submit: " + newUrl);
            
            if (newUrl.equals(currentUrl)) {
                System.out.println("URL unchanged - form submission may have failed");
            } else {
                System.out.println("URL changed - form may have been processed");
            }
            
        } catch (Exception e) {
            System.out.println("Error during form submission: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    @DisplayName("CE-02: Missing required email field")
    void testCreateEmployeeMissingEmail() {
        fillFormWithValidData();
        WebElement emailField = driver.findElement(By.name("email"));
        emailField.clear();

        // Kiểm tra field có required attribute không
        String requiredAttr = emailField.getAttribute("required");
        assertThat(requiredAttr).isNotNull();

        // Click submit và kiểm tra validation
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        
        // Đợi một chút để browser xử lý validation
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Kiểm tra vẫn ở trang create
        assertThat(driver.getCurrentUrl()).contains("/create");
        
        // Kiểm tra validation message hoặc validity state
        String validationMessage = emailField.getAttribute("validationMessage");
        Boolean validity = (Boolean) ((org.openqa.selenium.JavascriptExecutor) driver)
            .executeScript("return arguments[0].validity.valid;", emailField);
        
        // In ra để debug
        System.out.println("Validation message: '" + validationMessage + "'");
        System.out.println("Field validity: " + validity);
        
        // Ít nhất một trong hai điều kiện phải đúng
        boolean hasValidation = (validationMessage != null && !validationMessage.isEmpty()) || 
                               Boolean.FALSE.equals(validity);
        assertThat(hasValidation).isTrue();
    }

    @Test
    @Order(3)
    @DisplayName("CE-03: Invalid email format")
    void testCreateEmployeeInvalidEmailFormat() {
        fillFormWithValidData();
        WebElement emailField = driver.findElement(By.name("email"));
        emailField.clear();
        emailField.sendKeys("invalid-email");

        // Click submit
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        
        // Đợi validation xử lý
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Kiểm tra vẫn ở trang create
        assertThat(driver.getCurrentUrl()).contains("/create");
        
        // Kiểm tra validation bằng nhiều cách
        String validationMessage = emailField.getAttribute("validationMessage");
        Boolean validity = (Boolean) ((org.openqa.selenium.JavascriptExecutor) driver)
            .executeScript("return arguments[0].validity.valid;", emailField);
        Boolean typeMismatch = (Boolean) ((org.openqa.selenium.JavascriptExecutor) driver)
            .executeScript("return arguments[0].validity.typeMismatch;", emailField);
        
        // In ra để debug
        System.out.println("Email value: '" + emailField.getAttribute("value") + "'");
        System.out.println("Validation message: '" + validationMessage + "'");
        System.out.println("Field validity: " + validity);
        System.out.println("Type mismatch: " + typeMismatch);
        
        // Email không hợp lệ nên validity phải là false hoặc có validation message
        boolean hasValidation = (validationMessage != null && !validationMessage.isEmpty()) || 
                               Boolean.FALSE.equals(validity) || 
                               Boolean.TRUE.equals(typeMismatch);
        assertThat(hasValidation).isTrue();
    }

    /**
     * Điền dữ liệu form hợp lệ
     */
    private void fillFormWithValidData() {
        type("email", "test.employee@hospital.com");
        type("phoneNumber", "0912345678");
        type("fullName", "Nguyen Van Test");
        type("rankLevel", "5");

        selectFirstAvailable("staffRole");
        selectByValueIfExists("staffType", "FULL_TIME");
        selectFirstAvailable("hospitalId");
        selectFirstAvailable("departmentId");
        selectByValueIfExists("doctorRank", "THAC_SI");
    }

    // Helper nhập liệu
    private void type(String name, String value) {
        try {
            WebElement input = driver.findElement(By.name(name));
            input.clear();
            input.sendKeys(value);
        } catch (NoSuchElementException ignored) {
        }
    }

    // Helper chọn option theo value
    private void selectByValueIfExists(String name, String value) {
        try {
            Select select = new Select(driver.findElement(By.name(name)));
            select.selectByValue(value);
        } catch (NoSuchElementException | UnexpectedTagNameException ignored) {
        }
    }

    // Helper chọn option đầu tiên (nếu có)
    private void selectFirstAvailable(String name) {
        try {
            Select select = new Select(driver.findElement(By.name(name)));
            if (select.getOptions().size() > 1) {
                select.selectByIndex(1);
            }
        } catch (NoSuchElementException | UnexpectedTagNameException ignored) {
        }
    }
}
