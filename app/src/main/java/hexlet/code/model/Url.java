package hexlet.code.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class Url {
    private long id;
    private String name;
    private Timestamp created_at;

    public Url(String name, Timestamp created_at) {
        this.name = name;
        this.created_at = created_at;
    }
}
