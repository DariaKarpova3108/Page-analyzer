package hexlet.code.controllers;

import hexlet.code.dto.MainPage;
import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.CheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlsController {
    public static void create(Context ctx) throws SQLException {
        var inputUrl = ctx.formParam("url");
        URL url = null;
        try {
            assert inputUrl != null;
            if (!inputUrl.isEmpty()) {
                URI uri = URI.create(inputUrl);
                url = uri.toURL();
            }
        } catch (MalformedURLException e) {
            ctx.sessionAttribute("flash-type", "Некорректный URL");
            ctx.sessionAttribute("flash", "wrong");
            var page = new MainPage();
            page.setFlash(ctx.consumeSessionAttribute("flash"));
            page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
            ctx.render("index.jte", model("page", page));
            return;
        }

        assert url != null;
        String urlString = url.getProtocol() + "://" + url.getAuthority();
        Timestamp currentDate = new Timestamp(System.currentTimeMillis());
        Url urlModel = new Url(urlString, currentDate);

        if (UrlRepository.findByName(urlString).isEmpty()) {
            UrlRepository.save(urlModel);
            ctx.sessionAttribute("flash", "success");
            ctx.sessionAttribute("flash-type", "Страница успешно добавлена");
            ctx.redirect(NamedRoutes.listUrlsPath());
        } else if (UrlRepository.findByName(urlString).isPresent()) {
            ctx.sessionAttribute("flash", "unchanged");
            ctx.sessionAttribute("flash-type", "Страница уже существует");
            ctx.redirect(NamedRoutes.listUrlsPath());
        }
    }

    public static void showListUrls(Context ctx) throws SQLException {
        List<Url> urlsList = UrlRepository.getEntities();
        var checks = CheckRepository.getListLastCheck();
        var page = new UrlsPage(urlsList, checks);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("urls/showListUrls.jte", model("page", page));
    }

    // ПРОВЕРИТЬ КОД МЕТОДОВ НАПИСАННЫХ НИЖЕ
    public static void showUrl(Context ctx) throws SQLException {
        long id = ctx.pathParamAsClass("id", Long.class).get();
        Url url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("URL with id:" + id + " not found"));
        UrlPage page = new UrlPage(url, null);
        ctx.render("urls/showUrl.jte", model("page", page));
    }

    public static void saveCheckUrl(Context ctx) throws SQLException, IOException {
        long id = ctx.pathParamAsClass("id", Long.class).get();
        Url url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("URL with id:" + id + " not found"));
        UrlCheck checks = CheckRepository.parsingURL(url.getName());
        checks.setUrlId(url.getId());
        CheckRepository.saveCheckedUrl(checks);
        ctx.redirect(NamedRoutes.urlCheckPath(id));
    }

    //добавить вывод флеш-сообщений
    public static void checkUrl(Context ctx) throws SQLException, IOException {
        long id = ctx.pathParamAsClass("id", Long.class).get();
        Url url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("URL with id:" + id + " not found"));
        var checksList = CheckRepository.getListCheck(id);
        var page = new UrlPage(url, checksList);
        ctx.render("urls/showUrl.jte", model("page", page));
    }

    /*
    * НЕВЕРНАЯ ЛОГИАКА ПРОВЕРОК, Т К АЙДИ СЧИТАЕТ ПООЧЕРЕДНО У ВСЕХ САЙТОВ А НЕ У ОДНОГО
    * */
}


