package ru.praktikum.yandex.client;

import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import ru.praktikum.yandex.model.Order;
import ru.praktikum.yandex.model.OrdersFilter;

import static io.restassured.RestAssured.given;

public class OrderClient {
    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";
    private static final String ORDER_PATH = "/api/v1/orders";
    private static final String CANCEL_PATH = "/api/v1/orders/cancel";
    private final Gson gson = new Gson();

    public OrderClient() {
        RestAssured.baseURI = BASE_URL;
    }

    public Response createOrder(Order order) {
        String json = gson.toJson(order);
        return given()
                .header("Content-type", "application/json")
                .body(json)
                .post(ORDER_PATH);
    }

    public Response getOrdersList() {
        return given()
                .header("Content-type", "application/json")
                .when()
                .get(ORDER_PATH);
    }

    public Response getOrdersList(OrdersFilter filter) {
        RequestSpecification spec = given()
                .header("Content-type", "application/json");

        if (filter.getCourierId() != null) {
            spec.queryParam("courierId", filter.getCourierId());
        }
        if (filter.getNearestStation() != null) {
            String nearestStationsJson = gson.toJson(filter.getNearestStation());
            spec.queryParam("nearestStation", nearestStationsJson);
        }
        if (filter.getLimit() != null) {
            spec.queryParam("limit", filter.getLimit());
        }
        if (filter.getPage() != null) {
            spec.queryParam("page", filter.getPage());
        }
        return spec.when().get(ORDER_PATH);
        }

    public Response cancelOrder(int track) {
        return given()
                .header("Content-type", "application/json")
                .queryParam("track", track)
                .when()
                .put(CANCEL_PATH);
    }
    }


