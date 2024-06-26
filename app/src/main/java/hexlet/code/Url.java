package hexlet.code;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@ToString

public class Url {
    private long id;
    private String name;
    private Timestamp created_at;

}
