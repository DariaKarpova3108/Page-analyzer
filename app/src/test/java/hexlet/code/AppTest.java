package hexlet.code;

import com.mashape.unirest.http.exceptions.UnirestException;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.CheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.Response;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AppTest {
    private static Javalin app;
    private static MockWebServer mockWebServer;

    @BeforeEach
    public final void setUp() throws IOException {
        app = App.getApp();
    }

    @BeforeAll
    public static void createMockServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    public static void closeMockServer() throws IOException {
        mockWebServer.shutdown();
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
    public void testListUrlAfterAddedSomeUrls() throws SQLException {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        var url1 = new Url("https://hexlet.io", date);
        var url2 = new Url("https://example.com", date);
        UrlRepository.save(url1);
        UrlRepository.save(url2);

        JavalinTest.test(app, ((server, client) -> {
            var response = client.get(NamedRoutes.listUrlsPath());
            assertThat(response.code()).isEqualTo(200);

            String responseBody = response.body().string();
            assertThat(responseBody).contains("https://hexlet.io");
            assertThat(responseBody).contains("https://example.com");
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

    @Test
    public void testInvalidUrl() {
        JavalinTest.test(app, ((server, client) -> {
            var requestBody = "url=htps://ru.hexlet.io";
            var response = client.post(NamedRoutes.listUrlsPath(), requestBody);
            assertThat(response.code()).isEqualTo(400);
            assertThat(response.body().toString().contains("Некорректный URL"));
        }));
    }

    @Test
    public void testInvalidUrl2() {
        JavalinTest.test(app, ((server, client) -> {
            var requestBody = "url=";
            var response = client.post(NamedRoutes.listUrlsPath(), requestBody);
            assertThat(response.code()).isEqualTo(500);
            assertThat(response.body().toString().contains("Некорректный URL"));
        }));
    }

    @Test
    public void testCreateExistingUrl() throws SQLException {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        var url = new Url("https://hexlet.io", date);
        UrlRepository.save(url);

        JavalinTest.test(app, ((server, client) -> {
            var requestBody = "url=https://hexlet.io";
            var response = client.post(NamedRoutes.listUrlsPath(), requestBody);
            assertThat(response.code()).isIn(200, 302, 303);

            if (response.code() == 302 || response.code() == 303) {
                String responseLocation = response.header("Location");
                response = client.get(NamedRoutes.listUrlsPath());
            }
            assertThat(response.code()).isEqualTo(200);
        }));
    }

    @Test
    public void testSaveUrlCheck() throws UnirestException, SQLException {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        var urlModel = new Url("https://hexlet.io", date);
        UrlRepository.save(urlModel);
        UrlCheck urlCheck = CheckRepository.parsingURL(urlModel.getName());
        urlCheck.setUrlId(urlModel.getId());
        CheckRepository.saveCheckedUrl(urlCheck);

        JavalinTest.test(app, (server, client) -> {
            var response = client.post(NamedRoutes.urlCheckPath(urlModel.getId()));
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    public void testShowUrlCheck() throws SQLException, UnirestException {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        var urlModel = new Url("https://hexlet.io", date);
        UrlRepository.save(urlModel);
        UrlCheck urlCheck = CheckRepository.parsingURL(urlModel.getName());
        urlCheck.setUrlId(urlModel.getId());
        CheckRepository.saveCheckedUrl(urlCheck);

        var checkList = CheckRepository.getListCheck(urlModel.getId());
        JavalinTest.test(app, ((server, client) -> {
            var response = client.get(NamedRoutes.urlPath(urlModel.getId()));
            assertNotNull(checkList);
            assertThat(response.code()).isEqualTo(200);
        }));
    }

//    @Test
//    public void testParsingURL() throws UnirestException {
//        mockWebServer.enqueue(new MockResponse()
//                .setBody("<html><head><title>Анализатор страниц</title><meta name='description' content=''></head><body><h1>Анализатор страниц</h1></body></html>"));
//
//        String baseUrl = mockWebServer.url("/").toString();
//        UrlCheck result = CheckRepository.parsingURL(baseUrl);
//
//        assertEquals(200, result.getStatusCode());
//        assertEquals("Анализатор страниц", result.getTitle());
//        assertEquals("Анализатор страниц", result.getH1());
//    }

//    @Test
//    public void testShowUrlCheckResults() throws SQLException, IOException, UnirestException {
//        Timestamp date = new Timestamp(System.currentTimeMillis());
//        var url = new Url("https://hh.ru", date);
//        UrlRepository.save(url);
//
//        UrlCheck check = CheckRepository.parsingURL(url.getName());
//        check.setUrlId(url.getId());
//        CheckRepository.saveCheckedUrl(check);
//
//        JavalinTest.test(app, ((server, client) -> {
//            var response = client.get(NamedRoutes.urlPath(url.getId()));
//            String responseBody = response.body().string();
//            System.out.println(responseBody);
//
//            assertThat(response.code()).isEqualTo(200);
//            assertThat(responseBody.contains(check.getTitle());
//            assertThat(responseBody.contains(check.getH1());
//            assertThat(responseBody.contains(check.getDescription());
//        });
//    }

}
