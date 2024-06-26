package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.javalin.Javalin;

import java.io.IOException;
import java.sql.SQLException;

public class App {
// hostname  dpg-cpt9e4qju9rs73ant040-a
// port      5432
// database  page_analyzer_db_oxdu
// username  page_analyzer_db_oxdu_user
// password  4wyudCOvKC1lp37X9F676oSjYKzAZjid
// postgresql://page_analyzer_db_oxdu_user:4wyudCOvKC1lp37X9F676oSjYKzAZjid@dpg-cpt9e4qju9rs73ant040-a/page_analyzer_db_oxdu
// postgresql://page_analyzer_db_oxdu_user:4wyudCOvKC1lp37X9F676oSjYKzAZjid@dpg-cpt9e4qju9rs73ant040-a.oregon-postgres.render.com/page_analyzer_db_oxdu

    //остался вопрос по поводу создания БД в PostgreSQL как локальную?

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

    public static Javalin getApp() throws SQLException, IOException {
        String url = getUrl();
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);

        var dataSource = new HikariDataSource(hikariConfig);
        BaseRepository.dataSource = dataSource;


        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
        });

        app.get("/", ctx -> ctx.result("Hello World"));

        return app;
    }

    public static void main(String[] args) throws SQLException, IOException {
        var app = getApp();
        app.start(getPort());
    }
}
