package api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TestsAddSubscriber {
    private final static String BASE_URL = "https://crm.ru/api/v1";

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = BASE_URL;
    }

    //POST - тест на успешное добавление абонента
    @Test
    public void addNewSubscriberTest() {
        SubscriberData newSubscriber = new SubscriberData(
                "79241263770",
                100.0,
                12
        );

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer valid_manager_token")
                .body(newSubscriber)
                .when()
                .post("/save")
                .then()
                .statusCode(200)
                .body("data.msisdn", equalTo(newSubscriber.getMsisdn()))
                .body("data.balance", equalTo(newSubscriber.getBalance().floatValue()))
                .body("data.tariffId", equalTo(newSubscriber.getTariffId()))
                .body("message", containsString("Абонент успешно добавлен"));
    }

    //POST - тест на добавление абонента с некорректным номером
    @Test
    public void addSubscriberWithInvalidDataTest() {
        SubscriberData invalidSubscriber = new SubscriberData(
                "792412623",
                100.0,
                12
        );

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer valid_manager_token")
                .body(invalidSubscriber)
                .when()
                .post("/save")
                .then()
                .statusCode(400)
                .body("errorCode", equalTo(400))
                .body("message", containsString("Некорректный формат данных"));
    }

    //POST - тест на добавление абонента без прав
    @Test
    public void addSubscriberUnauthorizedTest() {
        SubscriberData validSubscriber = new SubscriberData(
                "79241263770",
                100.0,
                12
        );

        given()
                .contentType(ContentType.JSON)
                .body(validSubscriber)
                .auth().none()
                .when()
                .post("/save")
                .then()
                .statusCode(403)
                .body("errorCode", equalTo(403))
                .body("message", containsString("Нет прав на добавление абонента"));
    }

    //POST - тест на добавление абонента с несуществующим тарифом
    @Test
    public void addSubscriberWithInvalidTariffTest() {
        SubscriberData subscriberWithInvalidTariff = new SubscriberData(
                "79241263770",
                67.0,
                1
        );

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer valid_manager_token")
                .body(subscriberWithInvalidTariff)
                .when()
                .post("/save")
                .then()
                .statusCode(404)
                .body("errorCode", equalTo(404))
                .body("message", containsString("Данный тариф не существует"));
    }

    //POST - тест на добавление существующего пользователя
    @Test
    public void addExistingSubscriberTest() {
        SubscriberData existingSubscriber = new SubscriberData(
                "79241263770",
                180.0,
                11
        );

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer valid_manager_token")
                .body(existingSubscriber)
                .when()
                .post("/save")
                .then()
                .statusCode(409)
                .body("errorCode", equalTo(409))
                .body("message", containsString("Данный абонент уже зарегистрирован в системе"));
    }
}