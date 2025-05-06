package api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

public class TestsChangeTariffForSubscriber {
    private final static String BASE_URL = "https://crm.ru/api/v1";

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = BASE_URL;
    }

    //создание  тела запроса
    private Map<String, Object> createRequestBody(int tariffId) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("tariffId", tariffId);
        return requestBody;
    }

    //POST - тест на изменения тарифа менеджером абоненту
    @Test
    public void changeTariffForSubscriberByManagerTest() {
        String validMsisdn = "79241263770";
        int newTariffId = 11;

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer valid_manager_token")
                .pathParam("msisdn", validMsisdn)
                .body(createRequestBody(newTariffId))
                .when()
                .patch("/changeTariff/{msisdn}")
                .then()
                .statusCode(200)
                .body("data.tariffId", equalTo(newTariffId))
                .body("data.msisdn", equalTo(validMsisdn))
                .body("message", equalTo("Тариф успешно изменен"));
    }

    //POST - тест на изменения тарифа абоненту менеджером при невалидном номере
    @Test
    public void changeTariffForSubscriberByManagerWithInvalidNumberTest() {
        String invalidMsisdn = "777";
        int newTariffId = 12;

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer valid_manager_token")
                .pathParam("msisdn", invalidMsisdn)
                .body(createRequestBody(newTariffId))
                .when()
                .patch("/changeTariff/{msisdn}")
                .then()
                .statusCode(400)
                .body("errorCode", equalTo(400))
                .body("message", containsString("Некорректный формат данных"));
    }

    //POST - тест на изменения тарифа абонентом
    @Test
    public void changeTariffForSubscriberBySelfTest() {
        String validMsisdn = "79241263770";
        int newTariffId = 11;

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer valid_user_token")
                .pathParam("msisdn", validMsisdn)
                .body(createRequestBody(newTariffId))
                .when()
                .patch("/changeTariff/{msisdn}")
                .then()
                .statusCode(403)
                .body("errorCode", equalTo(403))
                .body("message", equalTo("Абоент не может менять тариф, нет прав"));
    }

    //POST - тест на изменения тарифа несуществующему абоненту менеджером
    @Test
    public void changeTariffForNotSubscriberByManagerTest() {
        String nonExistentMsisdn = "77762230331";
        int newTariffId = 11;

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer valid_manager_token")
                .pathParam("msisdn", nonExistentMsisdn)
                .body(createRequestBody(newTariffId))
                .when()
                .patch("/changeTariff/{msisdn}")
                .then()
                .statusCode(404)
                .body("errorCode", equalTo(404))
                .body("message", equalTo("Абонент не найден"));
    }

    //POST - тест на изменения тарифа несуществующим абоненту менеджером
    @Test
    public void changeTariffOnInvalidForSubscriberByManagerTest() {
        String validMsisdn = "79241263770";
        int nonExistentTariffId = 1;

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer valid_manager_token")
                .pathParam("msisdn", validMsisdn)
                .body(createRequestBody(nonExistentTariffId))
                .when()
                .patch("/changeTariff/{msisdn}")
                .then()
                .statusCode(404)
                .body("errorCode", equalTo(404))
                .body("message", equalTo("Тариф не найден"));
    }
}