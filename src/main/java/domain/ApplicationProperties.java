package domain;

import lombok.Data;

@Data
public class ApplicationProperties {
    private String connectionURL;
    private String username;
    private String password;
}
