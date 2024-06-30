package hexlet.code.repository;

import hexlet.code.model.Url;
import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UrlRepository extends BaseRepository {
    public static void saveToDataBase(Url url) throws SQLException {
        String sql = "INSERT (name, created_at) VALUES(?, ?)";
        try (var conn = dataSource.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, url.getName());
            pst.setTimestamp(2, url.getCreated_at());
            pst.executeUpdate();
            var generatedKeys = pst.getGeneratedKeys();
            if (generatedKeys.next()) {
                url.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }

    public static void save(URI uri) throws SQLException {
        try {
            URL url = uri.toURL();
            if (StringUtils.isNotBlank(url.getProtocol()) && StringUtils.isNotBlank(url.getHost())) {
                Timestamp currentDate = new Timestamp(System.currentTimeMillis());
                String adressCurrent = url.getProtocol() + "://" + url.getHost();
                if (url.getPort() != -1) {
                    adressCurrent = adressCurrent + ":" + url.getPort();
                }
                Url model = new Url(adressCurrent, currentDate);
                saveToDataBase(model);
            } else {
                System.out.println("Invalid URL: missing protocol or host");
            }
        } catch (MalformedURLException e) {
            System.out.println("URI to URL conversion error: " + e.getMessage());
        }
    }

    public static Optional<Url> find(Long id) throws SQLException {
        String sql = "SELECT * FROM urls WHERE id = ?";
        try (var conn = dataSource.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setLong(1, id);
            ResultSet resultSet = pst.executeQuery();
            while (resultSet.next()) {
                var name = resultSet.getString("name");
                var date = resultSet.getTimestamp("created_at");
                Url url = new Url(name, date);
                url.setId(id);
                return Optional.of(url);
            }
        }
        return Optional.empty();
    }

    public static List<Url> getURLs() throws SQLException {
        String sql = "SELECT * FROM urls";
        List<Url> urls = new ArrayList<>();
        try (var conn = dataSource.getConnection();
             Statement statement = conn.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                var id = resultSet.getLong("id");
                var name = resultSet.getString("name");
                var created_at = resultSet.getTimestamp("created_at");
                Url url = new Url(name, created_at);
                url.setId(id);
                urls.add(url);
            }
        }
        return urls;
    }
}
