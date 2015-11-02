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
    private File directory;
    private final File output;
    private final List<Cascade> cascades;

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

    public void setDirectory(@NotNull final String directory) throws IllegalArgumentException {
        this.directory = new File(directory);

        if (!this.directory.isDirectory()) {
            throw new IllegalArgumentException();
        }
    }

    public FSUtil(@NotNull final String directory, @NotNull final String output, @NotNull final String cascade) throws IOException {
        setDirectory(directory);

        this.output = new File(output);

        if (!this.output.exists()) {
            this.output.createNewFile();
        }

        cascades = CascadeManager.readCascade(cascade);
    }

    public void forEachImage(@NotNull final Consumer<BufferedImage> consumer) throws IOException {
        for (final File f : directory.listFiles(fileFilter)) {
            BufferedImage bufferedImage = ImageIO.read(f);
            consumer.accept(bufferedImage);
        }
    }

    private void writeFeatureVector(@NotNull final BufferedImage image, final boolean isFace) {

        String featureVector;
        IntegralImage integralImage = new IntegralImage(image);

        if (isFace) {
            featureVector = "1 ";
        } else {
            featureVector = "0 ";
        }

        for (Cascade cascade : cascades) {
            featureVector += handleCascade(integralImage);
            appendToOutputFile(featureVector);
        }


    }

    private void appendToOutputFile(String featureVector) {
    }

    private String handleCascade(IntegralImage integralImage) {
    }

    public static void main(String[] args) {
        FSUtil util;
        String cascade = "./src/main/resources/cascade.json";
        String facesPhotosDirectory = "./src/main/resources/photos/faces";
        String notFacesPhotosDirectory = "./src/main/resources/photos/notFaces";
        String output = "./src/main/output.txt";
        boolean isFace = true;

        // Non-static method cannot be referenced from a static context
        Consumer<BufferedImage> faceImageConsumer = img -> writeFeatureVector(img, isFace);
        Consumer<BufferedImage> notFaceImageConsumer = img -> writeFeatureVector(img, !isFace);

        try {
            util = new FSUtil(facesPhotosDirectory, output, cascade);
            util.forEachImage(faceImageConsumer);

            util.setDirectory(notFacesPhotosDirectory);
            util.forEachImage(notFaceImageConsumer);

        } catch (IOException e) {
            System.out.println("IOException: cannot create file");
        }





    }
}
