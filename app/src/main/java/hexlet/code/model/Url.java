package hexlet.code.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.net.URL;
import java.sql.Timestamp;

@Getter
@Setter
@ToString

public class Url {
    private long id;
    private String name;
    private Timestamp created_at;

    public Url(String name, Timestamp created_at) {
        this.name = name;
        this.created_at = created_at;
    }
}
