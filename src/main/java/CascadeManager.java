import com.sun.istack.internal.NotNull;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class CascadeManager {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<Cascade> readCascade(@NotNull final String source) throws IOException {
        List<Cascade> cascades = mapper.readValue(new File(source), new TypeReference<List<Cascade>>() {});
        return cascades;
    }

    public static void writeCascade(@NotNull final String dest, @NotNull final List<Cascade> cascade) {
        try {
            mapper.writeValue(new File(dest), cascade);
        } catch (IOException e) {
            System.out.println("IOException: " + e);;
        }

    }
}