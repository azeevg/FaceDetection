package violajones;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FeatureManager {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static List<Feature> readCascade(@NotNull final InputStream in) throws IOException {
        return MAPPER.readValue(in, new TypeReference<List<Feature>>() {
        });
    }

    public static void writeCascade(@NotNull final String dest, @NotNull final List<Feature> feature) throws IOException {
        MAPPER.writeValue(new File(dest), feature);
    }
}