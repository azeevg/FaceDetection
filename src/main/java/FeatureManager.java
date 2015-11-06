import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FeatureManager {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<Feature> readCascade(@NotNull final String source) throws IOException {
        List<Feature> features = mapper.readValue(new File(source), new TypeReference<List<Feature>>() {
        });
        return features;
    }

    public static void writeCascade(@NotNull final String dest, @NotNull final List<Feature> feature) throws IOException {
        mapper.writeValue(new File(dest), feature);
    }
}