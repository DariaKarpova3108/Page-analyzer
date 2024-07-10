package hexlet.code.repository;

import hexlet.code.model.UrlCheck;
import hexlet.code.model.Url;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class CheckRepository extends BaseRepository {

    //добавить метод показать проверки
    public static void saveCheckedUrl(UrlCheck urlCheck) throws SQLException, IOException {
        String sql = "INSERT INTO url_checks (status_code, title, h1, description, url_id, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (var conn = dataSource.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, urlCheck.getStatusCode());
            pst.setString(2, urlCheck.getTitle());
            pst.setString(3, urlCheck.getH1());
            pst.setString(4, urlCheck.getDescription());
            pst.setLong(5, urlCheck.getUrlId());
            pst.setTimestamp(6, urlCheck.getCreatedAt());
            pst.executeUpdate();
            ResultSet generatedKeys = pst.getGeneratedKeys();
            if (generatedKeys.next()) {
                urlCheck.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }

    public static Optional<UrlCheck> getLastCheck(long id) throws SQLException {
        String sql = "SELECT * FROM url_checks WHERE id = ? " +
                "ORDER BY created_at DESC LIMIT 1";
        try (var conn = dataSource.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setLong(1, id);
            ResultSet resultSet = pst.executeQuery();
            if (resultSet.next()) {
                long urlId = resultSet.getLong("url_id");
                var status = resultSet.getInt("status_code");
                var title = resultSet.getString("title");
                var h1 = resultSet.getString("h1");
                var description = resultSet.getString("description");
                var createdAt = resultSet.getTimestamp("created_at");
                var urlCheck = new UrlCheck(status, title, h1, description, createdAt);
                urlCheck.setUrlId(urlId);
                return Optional.of(urlCheck);
            }
        }
        return Optional.empty();
    }

    public static Map<Url, UrlCheck> getListLastCheck() throws SQLException {
        String sql = "SELECT * FROM url_checks " +
                "INNER JOIN urls ON url_checks.url_id = urls.id" +
                " ORDER BY url_checks.created_at DESC LIMIT 1";
        Map<Url, UrlCheck> listChecks = new LinkedHashMap<>();
        try (var conn = dataSource.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            ResultSet resultSet = pst.executeQuery();
            while (resultSet.next()) {
                var id = resultSet.getLong("urls.id");
                var name = resultSet.getString("urls.name");
                var date = resultSet.getTimestamp("urls.created_at");
                var urlModel = new Url(name, date);
                urlModel.setId(id);

                var idCheck = resultSet.getLong("url_checks.id");
                var urlId = resultSet.getLong("url_checks.url_id");
                var status = resultSet.getInt("url_checks.status_code");
                var title = resultSet.getString("url_checks.title");
                var h1 = resultSet.getString("url_checks.h1");
                var description = resultSet.getString("url_checks.description");
                var createdAt = resultSet.getTimestamp("url_checks.created_at");
                var urlCheck = new UrlCheck(status, title, h1, description, createdAt);
                urlCheck.setUrlId(urlId);
                urlCheck.setId(idCheck);
                listChecks.put(urlModel, urlCheck);
                return listChecks;
            }
        }
        return listChecks;
    }

    public static UrlCheck parsingURL(String urlModel) throws IOException {
        Document document = Jsoup.connect(urlModel).get();
        Elements titleElement = document.select("head > title");
        Elements h1Element = document.select("h1");
        Elements descriptionMeta = document.select("meta[name=description]");

        URI uri = URI.create(urlModel);
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        int statusCode = conn.getResponseCode();
        conn.disconnect();
        Timestamp date = new Timestamp(System.currentTimeMillis());

        String title = titleElement.text();
        String h1 = h1Element.text();
        String description = descriptionMeta.attr("content");
        return new UrlCheck(statusCode, title, h1, description, date);
    }
}
