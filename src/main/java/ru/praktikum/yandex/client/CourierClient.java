package ru.praktikum.yandex.client;

import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import ru.praktikum.yandex.model.Courier;
import ru.praktikum.yandex.model.CourierCredentials;

import static io.restassured.RestAssured.given;

public class CourierClient {
    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";
    private static final String COURIER_PATH = "/api/v1/courier";
    private static final String LOGIN_PATH = "/api/v1/courier/login";

    private final Gson gson = new Gson();

    public CourierClient() {
        RestAssured.baseURI = BASE_URL;
    }

    public Response createCourier(Courier courier) {
        String json = gson.toJson(courier);
        return given()
                .header("Content-type", "application/json")
                .body(json)
                .post(COURIER_PATH);
    }

    public Response loginCourier(CourierCredentials courierCredentials) {
        String json = gson.toJson(courierCredentials);
        return given()
                .header("Content-type", "application/json")
                .body(json)
                .post(LOGIN_PATH);
    }

    public void deleteCourier(int courierId) {
        given()
                .header("Content-type", "application/json")
                .delete(COURIER_PATH + "/" + courierId);
    }
}
