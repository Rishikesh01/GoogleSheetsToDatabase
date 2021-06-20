package util;

import com.beust.jcommander.Parameter;
import domain.ApplicationProperties;
import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Setter
@Getter
public class YamlReadingUtil {
    @Parameter(
            names = {"--application-config", "-C"},
            description = "location of other application.yaml you want to load"
    )
    private String fileLocation;

    public ApplicationProperties read() {
        Yaml yaml = new Yaml(new Constructor(ApplicationProperties.class));
        InputStream inputStream;
        if (fileLocation != null) {
            try {
                inputStream = new FileInputStream(fileLocation);
                return yaml.load(inputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        inputStream = YamlReadingUtil.class.getClassLoader()
                .getResourceAsStream("application.yml");
        return yaml.load(inputStream);
    }
}
