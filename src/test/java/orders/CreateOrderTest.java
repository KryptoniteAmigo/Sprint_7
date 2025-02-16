package orders;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.praktikum.yandex.model.Order;
import steps.OrderSteps;

import java.util.Arrays;
import java.util.List;

@RunWith(Parameterized.class)
public class CreateOrderTest {
    private final OrderSteps orderSteps = new OrderSteps();
    private int track;

    @Parameterized.Parameter
    public List<String> color;

    @Parameterized.Parameters(name = "Цвет: {0}")
    public static Object[][] getColorData() {
        return new Object[][] {
                { Arrays.asList() },
                { Arrays.asList("BLACK") },
                { Arrays.asList("GREY") },
                { Arrays.asList("BLACK", "GREY") }
        };
    }

    @Test
    @DisplayName("Создание заказа с разными вариантами цвета")
    public void testCreateOrderWithVariousColors() {
        Order order = new Order(
                "Naruto",
                "Uchiha",
                "Konoha, 142 apt.",
                4,
                "+7 800 355 35 35",
                5,
                "2020-06-06",
                "Saske, come back to Konoha",
                color
        );

        Response response = orderSteps.createOrder(order);

        Assert.assertEquals("Статус код должен быть 201", 201, response.statusCode());

        track = response.jsonPath().getInt("track");
        Assert.assertTrue("track должен быть числом больше 0", track > 0);
    }

    @After
    public void tearDown() {
        if (track > 0) {
            Response cancelResponse = orderSteps.cancelOrder(track);
            cancelResponse.then().statusCode(200);

            boolean isOk = cancelResponse.jsonPath().getBoolean("ok");
            Assert.assertTrue("Не удалось отменить заказ", isOk);
        }
    }
}
