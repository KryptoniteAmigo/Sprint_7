package steps;

import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.praktikum.yandex.client.OrderClient;
import ru.praktikum.yandex.model.Order;

public class OrderSteps {
    private final OrderClient orderClient = new OrderClient();
    private final Gson gson = new Gson();

    @Step("Создать заказ")
    public Response createOrder(Order order) {
        return orderClient.createOrder(order);
    }

    @Step("Отменить заказ по track")
    public Response cancelOrder(int track) {
        return orderClient.cancelOrder(track);
    }

    @Step("Получить trackId заказа из ответа")
    public int getTrackId(Response createOrderResponse) {
        return createOrderResponse.jsonPath().getInt("track");
    }
}
