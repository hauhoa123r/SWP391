//package org.project.e2e;
//
//import io.github.bonigarcia.wdm.WebDriverManager;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.CsvFileSource;
//import org.openqa.selenium.*;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.Select;
//import org.openqa.selenium.support.ui.WebDriverWait;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.server.LocalServerPort;
//
//import java.time.Duration;
//import java.util.concurrent.TimeUnit;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//class DoctorStaffE2EParamTest {
//    private static final long PAUSE_BETWEEN_CASES_MS = 4000; // adjust for slower run
//
//    @LocalServerPort
//    private int port;
//
//    private WebDriver driver;
//    private WebDriverWait wait;
//
//    private String baseUrl() {
//        return "http://localhost:" + port;
//    }
//
//    @BeforeEach
//    void setUpBrowser() {
//        WebDriverManager.chromedriver().setup();
//        ChromeOptions opts = new ChromeOptions();
//        // opts.addArguments("--headless=new", "--window-size=1920,1080");
//        driver = new ChromeDriver(opts);
//        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
//        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//    }
//
//    @AfterEach
//    void tearDown() {
//        if (driver != null) {
//            driver.quit();
//        }
//    }
//
//    private void openCreateForm() {
//        driver.get(baseUrl() + "/admin/doctor-staffs/create");
//        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("email")));
//    }
//
//    /**
//     * CSV format: description,email,phone,fullName,staffType,rankLevel,expectValid
//     */
//    @ParameterizedTest(name = "TC{index}: {0}")
//    @CsvFileSource(resources = "/testdata/doctor_staff_cases.csv", numLinesToSkip = 1)
//    @DisplayName("DoctorStaff create – data-driven 20 test cases")
//    void createStaff_cases(String description,
//                           String email,
//                           String phone,
//                           String fullName,
//                           String staffType,
//                           String rankLevel,
//                           String expectValidRaw) {
//
//        System.out.printf("[TEST] %s | expect: %s\n", description, expectValidRaw);
//        openCreateForm();
//
//        // Replace dynamic email placeholder
//        if (email != null && email.contains("__RANDOM__")) {
//            email = email.replace("__RANDOM__", String.valueOf(System.currentTimeMillis()));
//        }
//
//        // Fill in form fields if present
//        if (email != null && !email.isBlank())
//            driver.findElement(By.name("email")).sendKeys(email);
//
//        if (phone != null && !phone.isBlank())
//            driver.findElement(By.name("phoneNumber")).sendKeys(phone);
//
//        if (fullName != null && !fullName.isBlank())
//            driver.findElement(By.name("fullName")).sendKeys(fullName);
//
//        new Select(driver.findElement(By.name("staffRole"))).selectByValue("DOCTOR");
//
//        if (staffType != null && !staffType.isBlank()) {
//            new Select(driver.findElement(By.name("staffType"))).selectByValue(staffType);
//        }
//
//        if (rankLevel != null && !rankLevel.isBlank()) {
//            driver.findElement(By.name("rankLevel")).sendKeys(rankLevel);
//        }
//
//        // Chọn bệnh viện (giả định có sẵn)
//        new Select(driver.findElement(By.name("hospitalId"))).selectByIndex(1);
//
//        // Gửi form
//        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(
//                By.xpath("//button[@type='submit' and contains(normalize-space(.), 'Thêm mới')]")));
//        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", button);
//        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
//
//        String expectValid = expectValidRaw == null ? "" : expectValidRaw.trim().toLowerCase();
//
//        if ("valid".equals(expectValid)) {
//            wait.until(ExpectedConditions.urlMatches(".*/admin/doctor-staffs/detail/\\d+$"));
//            Assertions.assertTrue(driver.getCurrentUrl().contains("/admin/doctor-staffs/detail/"),
//                    "[✔] Redirected to detail page as expected: " + description);
//        } else {
//            wait.until(ExpectedConditions.urlContains("/admin/doctor-staffs/create"));
//            Assertions.assertTrue(driver.getCurrentUrl().contains("/admin/doctor-staffs/create"),
//                    "[✘] Stayed on create page as expected (invalid input): " + description);
//        }
//        // chờ 1.5s để dễ quan sát log/ UI nếu đang mở trình duyệt visible
//        try { Thread.sleep(PAUSE_BETWEEN_CASES_MS); } catch (InterruptedException ignored) {}
//    }
//}
