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

public class CreateCourierTest {

    private CourierSteps courierSteps;
    private CourierCredentials courierCredentials;
    private Courier courier;
    private Integer courierId;

    @Before
    public void setUp() {
        courierSteps = new CourierSteps();
        String randomLogin = "amigo_" + System.currentTimeMillis();
        courier = new Courier(randomLogin, "12345", "John");
        courierCredentials = new CourierCredentials(randomLogin, "12345");
        courierId = null;
    }

    @Test
    @DisplayName("Создание курьера")
    public void testCreateCourierSuccessfully() {
        Response response = courierSteps.createCourier(courier);
        Assert.assertEquals("Статус код должен быть 201",201, response.getStatusCode());

        CourierCreateResponse createResponse = response.as(CourierCreateResponse.class);
        Assert.assertTrue("Поле ok должно быть со значением true", createResponse.getOk());

        courierId = courierSteps.getCourierId(courierCredentials);
        Assert.assertNotNull("У курьера отсутствует ID", courierId);
    }

    @Test
    @DisplayName("Два одинаковых курьера")
    public void testCannotCreateIdenticalCouriers() {
        Response firstResponse = courierSteps.createCourier(courier);
        Assert.assertEquals("Статус код должен быть 201",201, firstResponse.getStatusCode());

        CourierCreateResponse createResponse = firstResponse.as(CourierCreateResponse.class);
        Assert.assertTrue("Поле ok должно быть со значением true", createResponse.getOk());

        courierId = courierSteps.getCourierId(courierCredentials);
        Assert.assertNotNull("У курьера отсутствует ID", courierId);

        Response secondResponse = courierSteps.createCourier(courier);
        Assert.assertEquals("При дубликате курьера должен быть статус код 409", 409, secondResponse.getStatusCode());
        String message = secondResponse.jsonPath().getString("message");
        String expected = "Этот логин уже используется. Попробуйте другой.";
        Assert.assertEquals("Уведомление о повторном использовании логина с ошибкой или отсутствует", expected, message);
    }

    @Test
    @DisplayName("Создание курьера без пароля")
    public void testCreateCourierWithoutPassword() {
        courier = new Courier("amigo123", null, "John");
        Response response = courierSteps.createCourier(courier);
        Assert.assertEquals("Статус код должен быть 400",400, response.getStatusCode());

        String message = response.jsonPath().getString("message");
        String expected = "Недостаточно данных для создания учетной записи";
        Assert.assertEquals("Уведомление о неуспешном создании учетной записи с ошибкой или отсутствует", expected, message);
    }

    @Test
    @DisplayName("Создание курьера без логина")
    public void testCreateCourierWithoutLogin() {
        courier = new Courier(null, "12345", "John");
        Response response = courierSteps.createCourier(courier);
        Assert.assertEquals("Статус код должен быть 400",400, response.getStatusCode());

        String message = response.jsonPath().getString("message");
        String expected = "Недостаточно данных для создания учетной записи";
        Assert.assertEquals("Уведомление о неуспешном создании учетной записи с ошибкой или отсутствует", expected, message);
    }

    @Test
    @DisplayName("Создание курьера с уже существующим логином")
    public void testCreateCourierWithExistingLogin() {
        String randomLogin = "amigo_" + System.currentTimeMillis();
        courier = new Courier(randomLogin, "12345", "John");

        Response firstResponse = courierSteps.createCourier(courier);
        Assert.assertEquals("Статус код должен быть 201",201, firstResponse.getStatusCode());
        CourierCreateResponse createResponse = firstResponse.as(CourierCreateResponse.class);
        Assert.assertTrue("Поле ok должно быть со значением true", createResponse.getOk());

        courier = new Courier(randomLogin, "54321", "Nick");
        Response secondResponse = courierSteps.createCourier(courier);
        Assert.assertEquals("При дубликате курьера должен быть статус код 409", 409, secondResponse.getStatusCode());

        String message = secondResponse.jsonPath().getString("message");
        String expected = "Этот логин уже используется. Попробуйте другой.";
        Assert.assertEquals("Уведомление о повторном использовании логина с ошибкой или отсутствует", expected, message);
    }

    @Test
    @DisplayName("Запрос возвращает правильный код ответа при успешном создании (201)")
    public void testCreateCourierReturnsCorrectStatusCode() {
        Response response = courierSteps.createCourier(courier);
        Assert.assertEquals("Статус код должен быть 201",201, response.getStatusCode());
    }

    @After
    public void tearDown() {
        if(courierId != null) {
            courierSteps.deleteCourier(courierId);
        }
    }
}
