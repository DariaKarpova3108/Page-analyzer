package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.repository.BaseRepository;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;

import java.io.IOException;
import java.sql.SQLException;

public class App {

    public static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.valueOf(port);
    }

    public static String getUrl() {
        String url = System.getenv("JDBC_DATABASE_URL");
        if (url == null || url.isEmpty()) {
            url = "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1";
        }
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

    public static Javalin getApp() throws SQLException, IOException {
        String url = getUrl();
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);

        var dataSource = new HikariDataSource(hikariConfig);
        BaseRepository.dataSource = dataSource;


        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
        });

        app.get("/", ctx -> ctx.render("mainPage.jte"));

        return app;
    }

    public static void main(String[] args) throws SQLException, IOException {
        var app = getApp();
        app.start(getPort());
        //app.start(7070);
    }
}
