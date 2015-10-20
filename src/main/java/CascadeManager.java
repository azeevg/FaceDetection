import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CascadeManager {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<Cascade> readCascade(@NotNull final String source) throws IOException {
        List<Cascade> cascades = mapper.readValue(new File(source), new TypeReference<List<Cascade>>() {
        });
        return cascades;
    }

    public static void writeCascade(@NotNull final String dest, @NotNull final List<Cascade> cascade) throws IOException {
        mapper.writeValue(new File(dest), cascade);
    }
}