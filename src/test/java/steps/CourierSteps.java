package steps;

import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.praktikum.yandex.client.CourierClient;
import ru.praktikum.yandex.model.Courier;
import ru.praktikum.yandex.model.CourierCredentials;
import ru.praktikum.yandex.model.CourierLoginResponse;

public class CourierSteps {
    private final CourierClient courierClient = new CourierClient();
    Gson gson = new Gson();

    @Step("Создать курьера")
    public Response createCourier(Courier courier) {
        return courierClient.createCourier(courier);
    }

    @Step("Авторизовать курьера")
    public Response loginCourier(CourierCredentials courierCredentials) {
        return courierClient.loginCourier(courierCredentials);
    }

    @Step("Получить ID курьера")
    public Integer getCourierId(CourierCredentials courierCredentials) {
        Response response = loginCourier(courierCredentials);
        if (response.statusCode() == 200) {
            CourierLoginResponse loginResponse = gson.fromJson(response.asString(), CourierLoginResponse.class);
            return loginResponse.getId();
        } else {
            return null;
        }
    }

    @Step("Удалить курьера")
    public Response deleteCourier(int courierId) {
        return courierClient.deleteCourier(courierId);
    }
}
