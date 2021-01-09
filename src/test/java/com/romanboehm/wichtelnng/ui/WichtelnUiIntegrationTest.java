package com.romanboehm.wichtelnng.ui;

import org.apache.commons.lang3.SystemUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Testcontainers
@ExtendWith(ScreenshotOnFailureExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WichtelnUiIntegrationTest {

    private final static String HOST_IP_ADDRESS = SystemUtils.IS_OS_LINUX ? "172.17.0.1" : "host.docker.internal";
    private final static String ADD_PARTICIPANT_BUTTON_ID = "add-participant-button";
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
    private final static String LOCALDATE_ERROR_ID = LOCALDATE_ID + "-error";
    private final static String LOCALTIME_ERROR_ID = LOCALTIME_ID + "-error";
    private final static String PLACE_ID = "place";
    private final static String PLACE_ERROR_ID = PLACE_ID + "-error";
    private final static String HOST_NAME_ID = "host-name";
    private final static String HOST_NAME_ERROR_ID = HOST_NAME_ID + "-error";
    private final static String HOST_EMAIL_ID = "host-email";
    private final static String HOST_EMAIL_ERROR_ID = HOST_EMAIL_ID + "-error";
    private final static String PREVIEW_BUTTON_ID = "preview-button";
    private final static String RESET_BUTTON_ID = "reset-button";
    private final static String PARTICIPANTS_TABLE_ID = "participants-table";

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
    public void shouldDisplayEventCreationForm() {
        WebElement eventCreationForm = supply(FORM_ID);
        Assertions.assertThat(eventCreationForm).isNotNull();
        WebElement title = supply(TITLE_ID);
        Assertions.assertThat(title).isNotNull();
        WebElement description = supply(DESCRIPTION_ID);
        Assertions.assertThat(description).isNotNull();
        WebElement monetaryAmountNumber = supply(MONETARYAMOUNT_NUMBER_ID);
        Assertions.assertThat(monetaryAmountNumber).isNotNull();
        WebElement monetaryAmountCurrency = supply(MONETARYAMOUNT_CURRENCY_ID);
        Assertions.assertThat(monetaryAmountCurrency).isNotNull();
        WebElement localDateTime = supply(LOCALDATE_ID);
        Assertions.assertThat(localDateTime).isNotNull();
        WebElement place = supply(PLACE_ID);
        Assertions.assertThat(place).isNotNull();
        WebElement hostName = supply(HOST_NAME_ID);
        Assertions.assertThat(hostName).isNotNull();
        WebElement hostEmail = supply(HOST_EMAIL_ID);
        Assertions.assertThat(hostEmail).isNotNull();
    }

    @Test
    public void shouldDisplaySubmitAndResetButtons() {
        WebElement submitButton = supply(PREVIEW_BUTTON_ID);
        Assertions.assertThat(submitButton.getText()).isEqualTo("Preview...");
        WebElement resetButton = supply(RESET_BUTTON_ID);
        Assertions.assertThat(resetButton.getText()).isEqualTo("Reset");
    }

    @Test
    public void shouldAddAndRemoveParticipants() {
        WebElement participantsTable = supply(PARTICIPANTS_TABLE_ID);
        Assertions.assertThat(participantsTable.findElements(By.cssSelector("tbody tr"))).hasSize(3);

        // Remove buttons should be hidden initially since we do _not_ have more than three participants
        Assertions.assertThat(supply("remove-participants0-button").isDisplayed()).isFalse();
        Assertions.assertThat(supply("remove-participants1-button").isDisplayed()).isFalse();
        Assertions.assertThat(supply("remove-participants2-button").isDisplayed()).isFalse();

        Assertions.assertThat(supply(ADD_PARTICIPANT_BUTTON_ID)).isNotNull();
        supply(ADD_PARTICIPANT_BUTTON_ID).click();
        supply(ADD_PARTICIPANT_BUTTON_ID).click();

        fillRow(0, "Angus Young", "angusyoung@acdc.net");
        fillRow(1, "Malcolm Young", "malcolmyoung@acdc.net");
        fillRow(2, "Phil Rudd", "philrudd@acdc.net");
        fillRow(3, "Bon Scott", "bonscott@acdc.net");
        fillRow(4, "Cliff Williams", "cliffwilliams@acdc.net");

        participantsTable = supply(PARTICIPANTS_TABLE_ID);
        Assertions.assertThat(participantsTable.findElements(By.cssSelector("tbody tr"))).hasSize(5);
        Assertions.assertThat(tableData()).containsExactly(
                List.of("Angus Young", "angusyoung@acdc.net"),
                List.of("Malcolm Young", "malcolmyoung@acdc.net"),
                List.of("Phil Rudd", "philrudd@acdc.net"),
                List.of("Bon Scott", "bonscott@acdc.net"),
                List.of("Cliff Williams", "cliffwilliams@acdc.net")
        );

        // Remove buttons should now be displayed since we have more than three participants
        Assertions.assertThat(supply("remove-participants0-button").isDisplayed()).isTrue();
        Assertions.assertThat(supply("remove-participants1-button").isDisplayed()).isTrue();
        Assertions.assertThat(supply("remove-participants2-button").isDisplayed()).isTrue();

        // Button id suffix (i.e. index) get recalculated after every removal, so in order to remove participants with
        // actual indices 1 and 2, we need to click removeParticipantButton1 twice
        supply("remove-participants1-button").click();
        supply("remove-participants1-button").click();

        participantsTable = supply(PARTICIPANTS_TABLE_ID);
        Assertions.assertThat(participantsTable.findElements(By.cssSelector("tbody tr"))).hasSize(3);
        Assertions.assertThat(tableData()).containsExactly(
                List.of("Angus Young", "angusyoung@acdc.net"),
                List.of("Bon Scott", "bonscott@acdc.net"),
                List.of("Cliff Williams", "cliffwilliams@acdc.net")
        );

        // Remove buttons should be hidden again
        Assertions.assertThat(supply("remove-participants0-button").isDisplayed()).isFalse();
        Assertions.assertThat(supply("remove-participants1-button").isDisplayed()).isFalse();
        Assertions.assertThat(supply("remove-participants2-button").isDisplayed()).isFalse();
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
        WebElement monetaryAmountCurrency = supply(MONETARYAMOUNT_CURRENCY_ID);
        monetaryAmountCurrency.sendKeys("XXXX"); // not a valid currency
        WebElement localDate = supply(LOCALDATE_ID);
        localDate.sendKeys(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        WebElement localTime = supply(LOCALTIME_ID);
        localTime.sendKeys(LocalTime.now().minus(1, ChronoUnit.HOURS) // before present
                .format(DateTimeFormatter.ofPattern("HH:mm"))
        );
        WebElement place = supply(PLACE_ID);
        place.sendKeys("Sydney".repeat(20)); // too long
        WebElement hostName = supply(HOST_NAME_ID);
        hostName.sendKeys("George Young".repeat(20)); // too long
        WebElement hostEmail = supply(HOST_EMAIL_ID);
        hostEmail.sendKeys("georgeyoungacdc.net"); // no '@'

        fillRow(0, "Angus Young".repeat(20), "angusyoung@acdc.net"); // too long
        fillRow(1, "Malcolm Young", "malcolmyoung@acdc.net");
        fillRow(2, "Phil Rudd".repeat(20), "philrudd@acdc.net"); // too long

        WebElement previewButton = supply(PREVIEW_BUTTON_ID);
        previewButton.click();

        WebElement titleError = supply(TITLE_ERROR_ID);
        Assertions.assertThat(titleError.isDisplayed()).isTrue();
        WebElement descriptionError = supply(DESCRIPTION_ERROR_ID);
        Assertions.assertThat(descriptionError.isDisplayed()).isTrue();
        WebElement monetaryAmountNumberError = supply(MONETARYAMOUNT_NUMBER_ERROR_ID);
        Assertions.assertThat(monetaryAmountNumberError.isDisplayed()).isTrue();
        WebElement localDateError = supply(LOCALDATE_ERROR_ID);
        Assertions.assertThat(localDateError.isDisplayed()).isTrue();
        WebElement localTimeError = supply(LOCALTIME_ERROR_ID);
        Assertions.assertThat(localTimeError.isDisplayed()).isTrue();
        WebElement placeError = supply(PLACE_ERROR_ID);
        Assertions.assertThat(placeError.isDisplayed()).isTrue();
        WebElement hostNameError = supply(HOST_NAME_ERROR_ID);
        Assertions.assertThat(hostNameError.isDisplayed()).isTrue();
        WebElement hostEmailError = supply(HOST_EMAIL_ERROR_ID);
        Assertions.assertThat(hostEmailError.isDisplayed()).isTrue();

        WebElement angusFirstNameError = supply("participants0-name-error");
        Assertions.assertThat(angusFirstNameError.isDisplayed()).isTrue();
        WebElement malcolmLastNameError = supply("participants2-name-error");
        Assertions.assertThat(malcolmLastNameError.isDisplayed()).isTrue();
    }

    private void fillRow(int rowIndex, String name, String email) {
        WebElement participantsTable = supply(PARTICIPANTS_TABLE_ID);
        WebElement row = participantsTable.findElements(By.cssSelector("tbody tr")).get(rowIndex);
        List<WebElement> inputFields = row.findElements(By.cssSelector("input"));
        WebElement firstNameInput = inputFields.get(0);
        firstNameInput.sendKeys(name);
        WebElement emailInput = inputFields.get(1);
        emailInput.sendKeys(email);
    }

    private List<List<String>> tableData() {
        WebElement participantsTable = supply(PARTICIPANTS_TABLE_ID);
        List<List<String>> rows = new ArrayList<>();
        participantsTable.findElements(By.cssSelector("tbody tr")).forEach(row -> {
            List<WebElement> inputFields = row.findElements(By.cssSelector("input"));
            rows.add(
                    inputFields.stream()
                            .map(webElement -> webElement.getAttribute("value"))
                            .filter(value -> !(value.equalsIgnoreCase("x")))
                            .collect(Collectors.toList())
            );
        });
        return rows;

    }
}