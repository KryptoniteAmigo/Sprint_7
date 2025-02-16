package orders;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;
import ru.praktikum.yandex.client.OrderClient;
import ru.praktikum.yandex.model.OrdersFilter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class GetOrdersListTest {
    private final OrderClient orderClient = new OrderClient();

    @Test
    @DisplayName("Получение списка заказов без параметров")
    public void testGetOrdersListWithoutParams() {
        Response response = orderClient.getOrdersList();
        assertEquals("Статус код должен быть 200", 200, response.getStatusCode());
        int ordersCount = response.jsonPath().getList("orders").size();
        assertTrue("Список заказов должен быть не пустым", ordersCount > 0);
    }

    @Test
    @DisplayName("Получение списка заказов с фильтром по станции метро и лимитом")
    public void testGetOrdersListWithNearestStation() {
        OrdersFilter filter = new OrdersFilter();
        filter.setNearestStation(Arrays.asList("1", "2"));
        filter.setLimit(5);

        Response response = orderClient.getOrdersList(filter);
        assertEquals("Статус код должен быть 200", 200, response.getStatusCode());

        List<Map<String, Object>> orders = response.jsonPath().getList("orders");

        assertNotNull("Поле 'orders' отсутствует в ответе", orders);
        assertTrue("Количество заказов должно быть не более " + filter.getLimit(),
                orders.size() <= filter.getLimit());
    }

    @Test
    @DisplayName("Получение списка заказов для несуществующего курьера (courierId)")
    public void testGetOrdersListWithNonExistentCourier() {
        OrdersFilter filter = new OrdersFilter(999999, null, null, null);

        Response response = orderClient.getOrdersList(filter);
        assertEquals("Статус код должен быть 404", 404, response.getStatusCode());

        String message = response.jsonPath().getString("message");
        Assert.assertEquals("Курьер с идентификатором 999999 не найден", message);
    }
}
