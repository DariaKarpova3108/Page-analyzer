package hexlet.code;

import io.javalin.Javalin;

public class App {
// hostname  dpg-cpt9e4qju9rs73ant040-a
// port      5432
// database  page_analyzer_db_oxdu
// username  page_analyzer_db_oxdu_user
// password  4wyudCOvKC1lp37X9F676oSjYKzAZjid
// postgresql://page_analyzer_db_oxdu_user:4wyudCOvKC1lp37X9F676oSjYKzAZjid@dpg-cpt9e4qju9rs73ant040-a/page_analyzer_db_oxdu
// postgresql://page_analyzer_db_oxdu_user:4wyudCOvKC1lp37X9F676oSjYKzAZjid@dpg-cpt9e4qju9rs73ant040-a.oregon-postgres.render.com/page_analyzer_db_oxdu


    private static final int DEFAULT_PORT = 8080;
    private static final int PORT = 5432;

    public static Javalin getApp() {
        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
        });

        app.get("/", ctx -> ctx.result("Hello World"));

        return app;
    }

    public static void main(String[] args) {
        var app = getApp();
        app.start(PORT);
    }
}
