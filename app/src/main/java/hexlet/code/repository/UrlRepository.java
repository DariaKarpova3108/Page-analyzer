package hexlet.code.repository;

import hexlet.code.model.Url;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UrlRepository extends BaseRepository {

    public static void save(Url url) throws SQLException {
        String sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, url.getName());
            pst.setTimestamp(2, url.getCreated_at());
            pst.executeUpdate();
            ResultSet generatedKeys = pst.getGeneratedKeys();
            if (generatedKeys.next()) {
                url.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("DB don't have return an id after saving entity");
            }
        }
    }

    public static Optional<Url> find(long id) throws SQLException {
        String sql = "SELECT * FROM urls WHERE id = ?";
        try (var conn = dataSource.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setLong(1, id);
            ResultSet resultSet = pst.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                Timestamp date = resultSet.getTimestamp("created_at");
                Url url = new Url(name, date);
                url.setId(id);
                return Optional.of(url);
            }
        }
        return Optional.empty();
    }

    public static Optional<Url> findByName(String urlName) throws SQLException {
        String sql = "SELECT * FROM urls WHERE name = ?";
        try (var conn = dataSource.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, urlName);
            ResultSet resultSet = pst.executeQuery();
            if (resultSet.next()) {
                long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                Timestamp date = resultSet.getTimestamp("created_at");
                Url url = new Url(name, date);
                url.setId(id);
                return Optional.of(url);
            }
        }
        return Optional.empty();
    }

    public static List<Url> getEntities() throws SQLException {
        String sql = "SELECT * FROM urls ORDER BY id";
        List<Url> urls = new ArrayList<>();
        try (var conn = dataSource.getConnection();
             Statement statement = conn.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                Timestamp created_at = resultSet.getTimestamp("created_at");
                Url url = new Url(name, created_at);
                url.setId(id);
                urls.add(url);
            }
        }
        return urls;
    }
}
