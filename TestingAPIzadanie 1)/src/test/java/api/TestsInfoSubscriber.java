package api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class TestsInfoSubscriber {
    private final static String BASE_URL = "https://crm.ru/api/v1";

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = BASE_URL;
    }

    //GET - тест на успешное нахождении информации об абоненте
    @Test
    public void getInfoAboutSubscriberTest() {
        String validMsisdn = "79241263770";

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer valid_manager_token")
                .pathParam("msisdn", validMsisdn)
                .when()
                .get("/infoSubscriber/{msisdn}")
                .then()
                .statusCode(200)
                .body("data.msisdn", equalTo(validMsisdn))
                .body("data.balance", is(notNullValue()))
                .body("data.tariffId", is(notNullValue()))
                .body("data.tariffName", is(notNullValue()))
                .body("message", equalTo("Данные об абоненте успешно получены"));
    }

    //GET - тест на нахождение информации об абоненте с инвалидными данными
    @Test
    public void getInfoAboutSubscriberWithInvalidFormat() {
        String invalidMsisdn = "12345"; // Невалидный формат номера

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer valid_manager_token")
                .pathParam("msisdn", invalidMsisdn)
                .when()
                .get("/infoSubscriber/{msisdn}")
                .then()
                .statusCode(400)
                .body("errorCode", equalTo(400))
                .body("message", containsString("Некорректный формат данных"));
    }

    //GET - тест на нахождение информации об абоненте без прав менеджера
    @Test
    public void getInfoAboutSubscriberByUnauthorized() {
        String validMsisdn = "79123456789";

        // Тест без токена авторизации
        given()
                .contentType(ContentType.JSON)
                .pathParam("msisdn", validMsisdn)
                .when()
                .get("/infoSubscriber/{msisdn}")
                .then()
                .statusCode(403)
                .body("errorCode", equalTo(403))
                .body("message", equalTo("Нет прав для просмотра информации об абоненте"));
    }

    //GET - тест на нахождение информации об абоненте с некорректным номером
    @Test
    public void getInfoAboutSubscriberWithNotNumber() {
        String nonExistentMsisdn = "70000000000";

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer valid_manager_token")
                .pathParam("msisdn", nonExistentMsisdn)
                .when()
                .get("/infoSubscriber/{msisdn}")
                .then()
                .statusCode(404)
                .body("errorCode", equalTo(404))
                .body("message", equalTo("Абонент с таким номером не найден не найден"));
    }

}