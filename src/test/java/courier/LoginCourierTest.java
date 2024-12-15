package courier;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.praktikum.yandex.model.Courier;
import ru.praktikum.yandex.model.CourierCreateResponse;
import ru.praktikum.yandex.model.CourierCredentials;
import steps.CourierSteps;

public class LoginCourierTest {

    private CourierSteps courierSteps;
    private CourierCredentials courierCredentials;
    private Courier courier;
    private Integer courierId;

    @Before
    public void setUp() {
        courierSteps = new CourierSteps();
        courier = new Courier("amigo123", "12345", "John");
        courierCredentials = new CourierCredentials("amigo123", "12345");
        courierId = null;

        Response response = courierSteps.createCourier(courier);
        Assert.assertEquals("Статус код должен быть 201",201, response.getStatusCode());

        CourierCreateResponse courierCreateResponse = new CourierCreateResponse();
        Assert.assertTrue("Поле ok должно быть со значением true", courierCreateResponse.getOk());

        courierId = courierSteps.getCourierId(courierCredentials);
        Assert.assertNotNull("У курьера отсутствует ID", courierId);
    }

    @Test
    @DisplayName("Курьер может авторизоваться при правильных логине и пароле")
    public void testCourierCanLogin() {
        CourierCredentials credentials = new CourierCredentials(courier.getLogin(), courier.getPassword());
        Response loginResponse = courierSteps.loginCourier(credentials);
        Assert.assertEquals(200, loginResponse.statusCode());
        int id = loginResponse.jsonPath().getInt("id");
        Assert.assertTrue("ID должен быть больше 0", id > 0);
    }

    @Test
    @DisplayName("Нельзя авторизоваться без логина")
    public void testLoginWithoutLogin() {
        CourierCredentials credentials = new CourierCredentials(null, courier.getPassword());
        Response loginResponse = courierSteps.loginCourier(credentials);
        Assert.assertEquals(400, loginResponse.statusCode());
        String message = loginResponse.jsonPath().getString("message");
        Assert.assertEquals("Недостаточно данных для входа", message);
    }

    @Test
    @DisplayName("Нельзя авторизоваться без пароля")
    public void testLoginWithoutPassword() {
        CourierCredentials credentials = new CourierCredentials(courier.getLogin(), null);
        Response loginResponse = courierSteps.loginCourier(credentials);
        Assert.assertEquals(400, loginResponse.statusCode());
        String message = loginResponse.jsonPath().getString("message");
        Assert.assertEquals("Недостаточно данных для входа", message);
    }

    @Test
    @DisplayName("Ошибка при авторизации с неверным паролем")
    public void testLoginWithWrongPassword() {
        CourierCredentials credentials = new CourierCredentials(courier.getLogin(), "wrongPass");
        Response loginResponse = courierSteps.loginCourier(credentials);
        Assert.assertEquals(404, loginResponse.statusCode());
        String message = loginResponse.jsonPath().getString("message");
        Assert.assertNotNull("Сообщение об ошибке должно присутствовать", message);
    }

    @Test
    @DisplayName("Ошибка при авторизации с неверным логином")
    public void testLoginWithWrongLogin() {
        CourierCredentials credentials = new CourierCredentials("nonExistentLogin", courier.getPassword());
        Response loginResponse = courierSteps.loginCourier(credentials);
        Assert.assertEquals(404, loginResponse.statusCode());
        String message = loginResponse.jsonPath().getString("message");
        Assert.assertNotNull("Сообщение об ошибке должно быть", message);
    }

    @Test
    @DisplayName("Ошибка при авторизации несуществующего курьера")
    public void testLoginNonExistentCourier() {
        CourierCredentials credentials = new CourierCredentials("someRandomLogin", "someRandomPass");
        Response loginResponse = courierSteps.loginCourier(credentials);
        Assert.assertEquals(404, loginResponse.statusCode());
        String message = loginResponse.jsonPath().getString("message");
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            courierSteps.deleteCourier(courierId);
        }
    }
}
