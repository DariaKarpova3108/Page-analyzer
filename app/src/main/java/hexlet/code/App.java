package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.controllers.RootController;
import hexlet.code.controllers.UrlsController;
import hexlet.code.repository.BaseRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

@Slf4j
public class App {

    public static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.parseInt(port);
    }

    public static String getUrl() {
        String url = System.getenv().getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1");
        return url;
    }

    //объект TemplateEngine, который используется для рендеринга HTML-шаблонов.
    //ClassLoader используется для загрузки классов и ресурсов.
    // ResourceCodeResolver отвечает за нахождение и загрузку шаблонов из указанного местоположения
    //TemplateEngine, который будет использовать codeResolver для нахождения шаблонов и генерировать HTML-контент
    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
        return templateEngine;
    }

    public static Javalin getApp() throws IOException {
        String url = getUrl();
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);

        var dataSource = new HikariDataSource(hikariConfig);
        BaseRepository.dataSource = dataSource;

        var sql = readResources("schema.sql");
        log.info(sql);

        try (Connection conn = dataSource.getConnection();
             Statement statement = conn.createStatement()) {
            if (conn != null) {
                statement.execute(sql);
                System.out.println("Connected to the database!");
            } else {
                System.out.println("Failed to make connection!");
            }
        } catch (SQLException e) {
            e.getMessage();
        }

        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
        });

        app.before(ctx -> {
            ctx.contentType("text/html; charset=utf-8");
        });
        app.get(NamedRoutes.rootPath(), RootController::index);
        app.post(NamedRoutes.listUrlsPath(), UrlsController::create);
        app.get(NamedRoutes.listUrlsPath(), UrlsController::showListUrls);
        app.get(NamedRoutes.urlPath("{id}"), UrlsController::showUrl);
        app.post(NamedRoutes.urlCheckPath("{id}"), UrlsController::saveCheckUrl);
        app.get(NamedRoutes.urlCheckPath("{id}"), UrlsController::checkUrl);
        return app;
    }

    private static String readResources(String fileName) throws IOException {
        var inputStream = App.class.getClassLoader().getResourceAsStream(fileName);
        assert inputStream != null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    public static void main(String[] args) throws SQLException, IOException {
        var app = getApp();
        app.start(getPort());
    }
}
