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
        courierId = null;
    }

    @Test
    @DisplayName("Курьер может авторизоваться при правильных логине и пароле")
    public void testCourierCanLogin() {
        String randomLogin = "amigo_" + System.currentTimeMillis();
        courier = new Courier(randomLogin, "12345", "John");
        courierCredentials = new CourierCredentials(randomLogin, "12345");
        Response response = courierSteps.createCourier(courier);
        Assert.assertEquals("Статус код должен быть 201",201, response.getStatusCode());
        CourierCreateResponse createResponse = response.as(CourierCreateResponse.class);
        Assert.assertTrue("Поле ok должно быть со значением true", createResponse.getOk());
        courierId = courierSteps.getCourierId(courierCredentials);
        Assert.assertNotNull("У курьера отсутствует ID", courierId);

        CourierCredentials credentials = new CourierCredentials(courier.getLogin(), courier.getPassword());
        Response loginResponse = courierSteps.loginCourier(credentials);
        Assert.assertEquals(200, loginResponse.statusCode());
        int id = loginResponse.jsonPath().getInt("id");
        Assert.assertTrue("ID должен быть больше 0", id > 0);
    }

    @Test
    @DisplayName("Нельзя авторизоваться без логина")
    public void testLoginWithoutLogin() {
        String randomLogin = "amigo_" + System.currentTimeMillis();
        courier = new Courier(randomLogin, "12345", "John");
        CourierCredentials credentials = new CourierCredentials(null, courier.getPassword());
        Response loginResponse = courierSteps.loginCourier(credentials);

        Assert.assertEquals(400, loginResponse.statusCode());
        String message = loginResponse.jsonPath().getString("message");
        Assert.assertEquals("Недостаточно данных для входа", message);
    }

    @Test
    @DisplayName("Нельзя авторизоваться без пароля")
    public void testLoginWithoutPassword() {
        String randomLogin = "amigo_" + System.currentTimeMillis();
        courier = new Courier(randomLogin, "12345", "John");
        CourierCredentials credentials = new CourierCredentials(courier.getLogin(), "");
        Response loginResponse = courierSteps.loginCourier(credentials);

        Assert.assertEquals(400, loginResponse.statusCode());
        String message = loginResponse.jsonPath().getString("message");
        Assert.assertEquals("Недостаточно данных для входа", message);
    }

    @Test
    @DisplayName("Ошибка при авторизации с неверным паролем")
    public void testLoginWithWrongPassword() {
        String randomLogin = "amigo_" + System.currentTimeMillis();
        courier = new Courier(randomLogin, "12345", "John");
        CourierCredentials credentials = new CourierCredentials(courier.getLogin(), "wrongPass");
        Response loginResponse = courierSteps.loginCourier(credentials);

        Assert.assertEquals(404, loginResponse.statusCode());
        String message = loginResponse.jsonPath().getString("message");
        Assert.assertNotNull("Сообщение об ошибке должно присутствовать", message);
    }

    @Test
    @DisplayName("Ошибка при авторизации с неверным логином")
    public void testLoginWithWrongLogin() {
        String randomLogin = "amigo_" + System.currentTimeMillis();
        courier = new Courier(randomLogin, "12345", "John");
        CourierCredentials credentials = new CourierCredentials("nonExistentLogin", courier.getPassword());
        Response loginResponse = courierSteps.loginCourier(credentials);

        Assert.assertEquals(404, loginResponse.statusCode());
        String message = loginResponse.jsonPath().getString("message");
        Assert.assertNotNull("Должно быть сообщение об ошибке", message);
    }

    @Test
    @DisplayName("Ошибка при авторизации несуществующего курьера")
    public void testLoginNonExistentCourier() {
        CourierCredentials credentials = new CourierCredentials("someRandomLogin", "someRandomPass");
        Response loginResponse = courierSteps.loginCourier(credentials);

        Assert.assertEquals(404, loginResponse.statusCode());
        String message = loginResponse.jsonPath().getString("message");
        String expected = "Учетная запись не найдена";
        Assert.assertEquals("Уведомление об отсутствии учетной записи с ошибкой либо отсутствует", expected, message);
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            courierSteps.deleteCourier(courierId);
        }
    }
}
