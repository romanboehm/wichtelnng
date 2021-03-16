package com.romanboehm.wichtelnng.ui;

import com.romanboehm.wichtelnng.CustomSpringBootTest;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.boot.web.server.LocalServerPort;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Testcontainers
@ExtendWith(ScreenshotOnFailureExtension.class)
@CustomSpringBootTest(webEnvironment = RANDOM_PORT)
public class WichtelnUiIntegrationTest {

    private final static String HOST_IP_ADDRESS = SystemUtils.IS_OS_LINUX ? "172.17.0.1" : "host.docker.internal";
    private final static String FORM_ID = "event-creation-form";
    private final static String TITLE_ID = "title";
    private final static String TITLE_ERROR_ID = TITLE_ID + "-error";
    private final static String DESCRIPTION_ID = "description";
    private final static String DESCRIPTION_ERROR_ID = DESCRIPTION_ID + "-error";
    private final static String MONETARYAMOUNT_NUMBER_ID = "monetary-amount-number";
    private final static String MONETARYAMOUNT_NUMBER_ERROR_ID = MONETARYAMOUNT_NUMBER_ID + "-error";
    private final static String MONETARYAMOUNT_CURRENCY_ID = "monetary-amount-currency";
    private final static String LOCALDATE_ID = "local-date";
    private final static String LOCALTIME_ID = "local-time";
    private final static String TIMEZONE_ID = "timezone";
    private final static String LOCALDATE_ERROR_ID = LOCALDATE_ID + "-error";
    private final static String LOCALTIME_ERROR_ID = LOCALTIME_ID + "-error";
    private final static String HOST_NAME_ID = "host-name";
    private final static String HOST_NAME_ERROR_ID = HOST_NAME_ID + "-error";
    private final static String HOST_EMAIL_ID = "host-email";
    private final static String HOST_EMAIL_ERROR_ID = HOST_EMAIL_ID + "-error";
    private final static String SUBMIT_BUTTON_ID = "submit-button";

    private RemoteWebDriver webDriver;

    @Container
    private BrowserWebDriverContainer<?> container = new BrowserWebDriverContainer<>()
            .withSharedMemorySize(536_870_912L) // 512 MiB
            // This is a workaround to make the container start under WSL 2,
            // Cf. https://github.com/testcontainers/testcontainers-java/issues/2552
            .withCapabilities(new FirefoxOptions());


    @BeforeEach
    public void establishWebDriver(@LocalServerPort int port) {
        webDriver = container.getWebDriver();
        webDriver.get("http://" + HOST_IP_ADDRESS + ":" + port + "/wichteln"); // Port is dynamic.
    }

    private WebElement supply(String idString) {
        return webDriver.findElement(By.id(idString));
    }

    @Test
    public void shouldDisplayCreateEventForm() {
        WebElement eventCreationForm = supply(FORM_ID);
        assertThat(eventCreationForm).isNotNull();
        WebElement title = supply(TITLE_ID);
        assertThat(title).isNotNull();
        WebElement description = supply(DESCRIPTION_ID);
        assertThat(description).isNotNull();
        WebElement monetaryAmountNumber = supply(MONETARYAMOUNT_NUMBER_ID);
        assertThat(monetaryAmountNumber).isNotNull();
        WebElement monetaryAmountCurrency = supply(MONETARYAMOUNT_CURRENCY_ID);
        assertThat(monetaryAmountCurrency).isNotNull();
        WebElement localDate = supply(LOCALDATE_ID);
        assertThat(localDate).isNotNull();
        WebElement localTime = supply(LOCALTIME_ID);
        assertThat(localTime).isNotNull();
        WebElement timezone = supply(TIMEZONE_ID);
        assertThat(timezone).isNotNull();
        WebElement hostName = supply(HOST_NAME_ID);
        assertThat(hostName).isNotNull();
        WebElement hostEmail = supply(HOST_EMAIL_ID);
        assertThat(hostEmail).isNotNull();
    }

    @Test
    public void shouldDisplaySubmitButton() {
        WebElement submitButton = supply(SUBMIT_BUTTON_ID);
        assertThat(submitButton.getText()).isEqualTo("Submit");
    }

    @Test
    public void shouldValidateFormInput() {
        // Fill with invalid data
        WebElement title = supply(TITLE_ID);
        title.sendKeys("AC/DC Secret Santa".repeat(20)); // too long
        WebElement description = supply(DESCRIPTION_ID); // too long
        description.sendKeys("There's gonna be some santa'ing".repeat(100));
        WebElement monetaryAmountNumber = supply(MONETARYAMOUNT_NUMBER_ID);
        monetaryAmountNumber.sendKeys("-1"); // negative
        WebElement localDate = supply(LOCALDATE_ID);
        localDate.sendKeys(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        WebElement localTime = supply(LOCALTIME_ID);
        localTime.sendKeys(LocalTime.now().minus(1, ChronoUnit.HOURS) // before present
                .format(DateTimeFormatter.ofPattern("HH:mm"))
        );
        WebElement hostName = supply(HOST_NAME_ID);
        hostName.sendKeys("George Young".repeat(20)); // too long
        WebElement hostEmail = supply(HOST_EMAIL_ID);
        hostEmail.sendKeys("georgeyoungacdc.net"); // no '@'

        WebElement saveButton = supply(SUBMIT_BUTTON_ID);
        saveButton.click();

        WebElement titleError = supply(TITLE_ERROR_ID);
        assertThat(titleError.isDisplayed()).isTrue();
        WebElement descriptionError = supply(DESCRIPTION_ERROR_ID);
        assertThat(descriptionError.isDisplayed()).isTrue();
        WebElement monetaryAmountNumberError = supply(MONETARYAMOUNT_NUMBER_ERROR_ID);
        assertThat(monetaryAmountNumberError.isDisplayed()).isTrue();
        WebElement localDateError = supply(LOCALDATE_ERROR_ID);
        assertThat(localDateError.isDisplayed()).isTrue();
        WebElement localTimeError = supply(LOCALTIME_ERROR_ID);
        assertThat(localTimeError.isDisplayed()).isTrue();
        WebElement hostNameError = supply(HOST_NAME_ERROR_ID);
        assertThat(hostNameError.isDisplayed()).isTrue();
        WebElement hostEmailError = supply(HOST_EMAIL_ERROR_ID);
        assertThat(hostEmailError.isDisplayed()).isTrue();
    }
}