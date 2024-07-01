package hexlet.code.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@ToString

public class Checks {
    private long id;
    private long url_id;
    private int status;
    private String title;
    private String h1;
    private String description;
    private Timestamp created_at;

    public Checks(int status, String title, String h1, String description, Timestamp created_at) {
        this.status = status;
        this.title = title;
        this.h1 = h1;
        this.description = description;
        this.created_at = created_at;
    }
}
