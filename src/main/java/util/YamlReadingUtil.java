package util;

import com.beust.jcommander.Parameter;
import domain.ApplicationProperties;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

public class YamlReadingUtil {
    @Parameter(
            names = {"--application-config", "-C"},
            description = "location of other application.yaml you want to load",
            arity = 1
    )
    private String fileLocation;

    public ApplicationProperties read() {
        Yaml yaml = new Yaml(new Constructor(ApplicationProperties.class));
        InputStream inputStream;
        if (fileLocation == null) {
            inputStream = this.getClass()
                    .getClassLoader()
                    .getResourceAsStream("application.yml");
            return yaml.load(inputStream);
        }
        inputStream = this.getClass().getClassLoader().getResourceAsStream(fileLocation);
        return yaml.load(inputStream);
    }
}
