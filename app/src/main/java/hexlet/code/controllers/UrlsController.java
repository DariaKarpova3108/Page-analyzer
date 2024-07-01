package hexlet.code.controllers;

import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Checks;
import hexlet.code.model.Url;
import hexlet.code.repository.CheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlsController { //добавить вывод ФЛЕШ сообщений в зависимости от результата проверки
    public static void create(Context ctx) throws SQLException {
        try {
            var uri = ctx.formParamAsClass("url", URI.class).get();
            UrlRepository.save(uri);
            var page = UrlRepository.getURLs();
            ctx.render("urls/showAllUrls", model("page", page)).status(200);
        } catch (SQLException e) {
            ctx.result(e.getMessage()).status(400);
        }
    }

    public static void showAllUrls(Context ctx) throws SQLException {
        try {
            List<Url> urls = UrlRepository.getURLs();
            if (!urls.isEmpty()) {
                UrlsPage page = new UrlsPage(urls, null);
                ctx.render("urls/showAllUrls", model("page", page)).status(200);
            } else {
                ctx.result("Not Found").status(404);
            }
        } catch (SQLException e) {
            ctx.result(e.getMessage()).status(500);
        }
    }

    public static void showUrl(Context ctx) throws SQLException {
        try {
            long id = ctx.pathParamAsClass("id", Long.class).get();
            Url url = UrlRepository.find(id)
                    .orElseThrow(() -> new NotFoundResponse("URL with id:" + id + " not found"));
            UrlPage page = new UrlPage(url, null);
            ctx.render("urls/showUrl.jte", model("page", page)).status(200);
        } catch (NotFoundResponse e) {
            ctx.result(e.getMessage()).status(404);
        } catch (SQLException e) {
            ctx.result(e.getMessage()).status(500);
        }
    }

    public static void showAllUrlsWithChecks(Context ctx) throws SQLException {
        try {
            List<Url> urls = UrlRepository.getURLs();
            Map<Url, Checks> checks = new LinkedHashMap<>();
            for (var url : urls) {
                Checks check = CheckRepository.checkUrl(url);
                checks.put(url, check);
            }
            if (!urls.isEmpty()) {
                UrlsPage page = new UrlsPage(urls, checks);
                ctx.render("urls/showAllUrls", model("page", page)).status(200);
            } else {
                ctx.result("Not Found").status(404);
            }
        } catch (SQLException e) {
            ctx.result(e.getMessage()).status(500);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void checkUrl(Context ctx) throws SQLException, IOException {
        try {
            long id = ctx.pathParamAsClass("id", Long.class).get();
            Url url = UrlRepository.find(id)
                    .orElseThrow(() -> new NotFoundResponse("URL not found"));
            Checks check = CheckRepository.checkUrl(url);
            var page = new UrlPage(url, check);
            ctx.render("urls/showUrl.jte", model("page", page)).status(200);
        } catch (NotFoundResponse e) {
            ctx.result(e.getMessage()).status(404);
        }

    }
}
