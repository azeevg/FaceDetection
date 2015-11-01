import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.io.File;

public class FSUtil {
    final File directory;
    final File output;

    private static FileFilter fileFilter = new FileFilter() {
        private final List<String> extensions = Arrays.asList(ImageIO.getReaderFormatNames());

        public boolean accept(File pathname) {
            String extension = FilenameUtils.getExtension(pathname.getName());

            if (!pathname.isFile() || pathname.exists() || !extensions.contains(extension)) {
                return false;
            }

            return true;
        }
    };

    public FSUtil(@NotNull String directory, @NotNull String output) throws IOException {
        this.directory = new File(directory);

        if (!this.directory.isDirectory()) {
            throw new IllegalArgumentException();
        }

        this.output = new File(output);

        if (!this.output.exists()) {
            this.output.createNewFile();
        }
    }

    public void forEachImage(@NotNull Consumer<BufferedImage> consumer) throws IOException {
        for (final File f : directory.listFiles(fileFilter)) {
            BufferedImage bufferedImage = ImageIO.read(f);
            consumer.accept(bufferedImage);
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
        FSUtil util;

        try {
            util = new FSUtil("./src/main/resources/photos", "./src/main/output.txt");
        } catch (IOException e) {
            System.out.println("IOException: cannot create file");
        }



    }
}
