package hexlet.code.repository;

import hexlet.code.model.Checks;
import hexlet.code.model.Url;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.sql.Timestamp;

public class CheckRepository extends BaseRepository {

    //дописать сюда методы для добавления в таблицу
    public static Checks saveCheckedUrl(Url urlModel) throws IOException {
        Document document = Jsoup.connect(urlModel.getName()).get();
        Elements titleElement = document.select("head > title");
        Elements h1Element = document.select("h1");
        Elements descriptionMeta = document.select("meta[name=description]");

        URI uri = URI.create(urlModel.getName());
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        int statusCode = conn.getResponseCode();
        conn.disconnect();
        Timestamp date = new Timestamp(System.currentTimeMillis());

        String title = titleElement != null ? titleElement.text() : "";
        String h1 = h1Element != null ? h1Element.text() : "";
        String description = descriptionMeta != null ? descriptionMeta.text() : "";
        Checks checks = new Checks(statusCode, title, h1, description, date);
        return checks;
    }
}
