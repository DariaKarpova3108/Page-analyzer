package hexlet.code.controllers;

import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import java.sql.SQLException;

import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlsController {
    /* - дописать здесь логику создания модели для передачи
         в шаблонизатор
         */
    public static void showUrl(Context ctx) throws SQLException {
        var urls = UrlRepository.getURLs();
        var page = new UrlsPage(urls);
        ctx.render("urls/showUrl.jte", model("page", page)); //написать шаблонизатор для урла и урлов и вставить сюда адреса
    }

    public static void showAllUrls(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("URL with id:" + id + " not found"));
        var page = new UrlPage(url);
        ctx.render("showAllUrls", model("page", page));
    }
}
