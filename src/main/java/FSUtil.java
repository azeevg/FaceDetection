import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.io.File;

public class FSUtil {
    final File directory;
    final String output;

    public FSUtil(@NotNull String directory, @NotNull String output) {
        this.directory = new File(directory);
        this.output = output;
    }

    public static boolean isImage(@NotNull File file) {
        String[] strings = file.getName().split("[.]");
        List<String> parsedName = Arrays.asList(strings);
        String ext = parsedName.get(parsedName.size() - 1);
        if (Arrays.asList(ImageIO.getReaderFormatNames()).contains("ext"))
            return true;

        return false;
    }

    public void forEachImage(@NotNull Consumer<BufferedImage> consumer) throws IOException {
        for (File f : directory.listFiles()) {
            if (isImage(f)) {
                BufferedImage bufferedImage = ImageIO.read(f);
                consumer.accept(bufferedImage);
            }
        }
    }

    private static void writeFeatureVector(BufferedImage image) {
        /*
        *
        * Here will be a method creates text-file with feature vector
        *
        * */
    }

    public static void main(String[] args) {
        Consumer<BufferedImage> bufferedImageConsumer = img -> writeFeatureVector(img);
    }
}
