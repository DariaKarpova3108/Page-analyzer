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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;

public class CheckRepository extends BaseRepository {

    //дописать сюда методы для добавления в таблицу
    public static void saveCheckedUrl(Url urlModel) throws SQLException, IOException {
        String sql = "INSERT INTO checks (status, title, h1, description, created_at) VALUES (?, ?, ?, ?, ?)";
        Checks urlCheck = parsingURL(urlModel);
        try (var conn = dataSource.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, urlCheck.getStatus());
            pst.setString(2, urlCheck.getTitle());
            pst.setString(3, urlCheck.getH1());
            pst.setString(4, urlCheck.getDescription());
            pst.setTimestamp(5, urlCheck.getCreated_at());
            pst.executeUpdate();
            var generatedKeys = pst.getGeneratedKeys();
            if (generatedKeys.next()) {
                urlCheck.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }

    public static Optional<Checks> getLastCheck(long id) throws SQLException, IOException {
        String sql = "SELECT * FROM checks WHERE id = ? " +
                "ORDER BY created_at DESC LIMIT 1";
        try (var conn = dataSource.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setLong(1, id);
            ResultSet resultSet = pst.executeQuery();
            if (resultSet.next()) {
                long urlId = resultSet.getLong("url_id");
                var status = resultSet.getInt("status");
                var title = resultSet.getString("title");
                var h1 = resultSet.getString("h1");
                var description = resultSet.getString("description");
                var createdAt = resultSet.getTimestamp("created_at");
                var urlCheck = new Checks(status, title, h1, description, createdAt);
                urlCheck.setUrl_id(urlId);
                return Optional.of(urlCheck);
            }
        }
        return Optional.empty();
    }

    public static Checks parsingURL(Url urlModel) throws IOException {
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
