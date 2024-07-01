package hexlet.code.dto;

import hexlet.code.model.Checks;
import hexlet.code.model.Url;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UrlPage extends BasePage {
    private Url url;
    private Checks checks;
}
