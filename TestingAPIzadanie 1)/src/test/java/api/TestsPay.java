package api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TestsPay {

    private final static String BASE_URL = "https://crm.ru/api/v1";

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = BASE_URL;
    }

    // Вспомогательный метод для создания тела запроса
    private Map<String, Object> createRequestBody(double amount) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", amount);
        return requestBody;
    }

    //POST - тест на пополнение баланса менеджером
    @Test
    public void payForSubscriberByManagerTest() {
        String validMsisdn = "79241263770";
        double paymentAmount = 120.0;

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer valid_manager_token")
                .pathParam("msisdn", validMsisdn)
                .body(createRequestBody(paymentAmount))
                .when()
                .post("/pay/{msisdn}")
                .then()
                .statusCode(200)
                .body("data.msisdn", equalTo(validMsisdn))
                .body("data.balance", is(notNullValue()))
                .body("message", containsString("Баланс успешно пополнен менеджером"));
    }

    //POST - тест на пополнение баланса абонентом
    @Test
    public void payForSubscriberBySelfTest() {
        String subscriberMsisdn = "79241263770";
        double paymentAmount = 120.0;

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer valid_subscriber_token")
                .pathParam("msisdn", subscriberMsisdn)
                .body(createRequestBody(paymentAmount))
                .when()
                .post("/pay/{msisdn}")
                .then()
                .statusCode(200)
                .body("data.msisdn", equalTo(subscriberMsisdn));
    }

    //POST - тест на пополнение баланса менеджером на отрицательную сумму платежа
    @Test
    public void payWithNegativeSumForSubscriberByManagerTest() {
        String validMsisdn = "79241263770";
        double negativeAmount = -10.0;

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer valid_manager_token")
                .pathParam("msisdn", validMsisdn)
                .body(createRequestBody(negativeAmount))
                .when()
                .post("/pay/{msisdn}")
                .then()
                .statusCode(400)
                .body("errorCode", equalTo(400))
                .body("message", containsString("Сумма должна быть положительной > 0"));
    }

    //POST - тест на пополнение баланса менеджером на 0
    @Test
    public void payWithNullForSubscriberByManagerTest() {
        String validMsisdn = "79241263770";
        double zeroAmount = 0.0;

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer valid_manager_token")
                .pathParam("msisdn", validMsisdn)
                .body(createRequestBody(zeroAmount))
                .when()
                .post("/pay/{msisdn}")
                .then()
                .statusCode(400)
                .body("errorCode", equalTo(400))
                .body("message", containsString("Сумма должна быть положительной и больше 0"));
    }

    @Test
    public void payForNoSubscriberByManagerTest() {
        String nonExistentMsisdn = "70000000000";
        double paymentAmount = 100.0;

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer valid_manager_token")
                .pathParam("msisdn", nonExistentMsisdn)
                .body(createRequestBody(paymentAmount))
                .when()
                .post("/pay/{msisdn}")
                .then()
                .statusCode(404)
                .body("errorCode", equalTo(404))
                .body("message", equalTo("Абонент не найден в системе"));
    }

}
