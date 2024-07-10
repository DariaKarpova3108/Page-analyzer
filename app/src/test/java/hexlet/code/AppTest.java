package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {
    private static Javalin app;

    @BeforeEach
    public final void setUp() throws SQLException, IOException {
        app = App.getApp();
    }

    @Test
    public void testMainPage() {
        JavalinTest.test(app, ((server, client) -> {
            Response response = client.get(NamedRoutes.rootPath());
            assertThat(response.code()).isEqualTo(200);
        }));
    }

    @Test
    public void testCreateUrl() {
        JavalinTest.test(app, ((server, client) -> {
            var requestBody = "url=https://ru.hexlet.io";
            var response = client.post(NamedRoutes.listUrlsPath(), requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://ru.hexlet.io");
        }));
    }

    @Test
    public void testShowListUrl() {
        JavalinTest.test(app, ((server, client) -> {
            var response = client.get(NamedRoutes.listUrlsPath());
            assertThat(response.code()).isEqualTo(200);
        }));
    }

    @Test
    public void testShowUrl() throws SQLException {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        var url = new Url("https://hexlet.io", date);
        UrlRepository.save(url);
        JavalinTest.test(app, ((server, client) -> {
            var response = client.get(NamedRoutes.urlPath(url.getId()));
            assertThat(response.code()).isEqualTo(200);
        }));
    }

    @Test
    public void testShowUrlNotFound() {
        JavalinTest.test(app, ((server, client) -> {
            var response = client.get(NamedRoutes.urlPath("9999"));
            assertThat(response.code()).isEqualTo(404);
        }));
    }

//дописать тесты потом сюда дальше, после корректировки 7 и 8 шага


}
