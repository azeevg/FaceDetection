import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class FSUtil {
    private static FileFilter fileFilter = new FileFilter() {
        private final List<String> extensions = Arrays.asList(ImageIO.getReaderFormatNames());

        public boolean accept(File pathname) {
            String extension = FilenameUtils.getExtension(pathname.getName());

            if (!pathname.isFile() || !pathname.exists() || !extensions.contains(extension)) {
                return false;
            }

            return true;
        }
    };
    private final File output;
    private final List<Cascade> cascades;
    private final File directory;
    private PrintWriter printWriter;

    public FSUtil(@NotNull final String directory, @NotNull final String output, @NotNull final String cascade) throws IOException {
        this.directory = new File(directory);

        if (!this.directory.isDirectory()) {
            throw new IllegalArgumentException();
        }

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

    private void writeFeatureVector(@NotNull final BufferedImage image, @NotNull final ImageType imageType) {

        String featureVector = imageType.getNumber() + " ";
        featureVector += getFeatureVector(image);

        try {
            appendToOutputFile(featureVector);
        } catch (IOException e) {
            System.out.println("IOException in writeFeatureVector: " + e.getMessage());
        }
    }

    private String getFeatureVector(@NotNull final BufferedImage image) {
        final int scanningWindowSize = 100;

        IntegralImage integralImage = new IntegralImage(image);
        Cascade scaledCascade;
        String result = "";

        for (Cascade cascade : cascades) {
            scaledCascade = cascade.scale(scanningWindowSize);
            result += handleCascade(scaledCascade, integralImage);
        }

        return result;
    }

    private String handleCascade(@NotNull final Cascade scaledCascade, @NotNull final IntegralImage integralImage) {
        String result = "";
        int step = 5;

        int cascadeWidth = (int) scaledCascade.getWidth();
        int cascadeHeight = (int) scaledCascade.getHeight();

        Vector shift;
        for (int i = 0; i < (integralImage.getHeight() - cascadeHeight) / step; i++) {
            for (int j = 0; j < (integralImage.getWidth() - cascadeWidth) / step; j++) {
                shift = new Vector(j * step, i * step);
                result += integralImage.handleCascade(scaledCascade, shift) + " ";
            }
        }

        return result;
    }

    private void appendToOutputFile(@NotNull final String featureVector) throws IOException {
        printWriter.println(featureVector);
    }

    private void run(@NotNull final ImageType imageType) {
        Consumer<BufferedImage> consumer = img -> writeFeatureVector(img, imageType);

        try {
            printWriter = new PrintWriter(new BufferedWriter(new FileWriter(this.output, true)));
            forEachImage(consumer);
        } catch (IOException e) {
            System.out.println("IOException in method run: " + e.getMessage());
        } finally {
            printWriter.close();
        }
    }

    public static void main(String[] args) {
        String cascade = "./src/main/resources/cascade.json";

        String facesImagesDirectory = "./src/main/resources/photos/faces";
        String notFacesImagesDirectory = "./src/main/resources/photos/notFaces";

        //String facesPhotosDirectory = "C:\\Users\\GlAz\\Desktop\\faceDetection\\faces";
        //String notFacesImagesDirectory = "C:\\Users\\GlAz\\Desktop\\faceDetection\\notfaces";

        String output = "./src/main/testOutput.txt";

        FSUtil util;

        try {
            util = new FSUtil(facesImagesDirectory, output, cascade);
            util.run(ImageType.FACES);

            util = new FSUtil(notFacesImagesDirectory, output, cascade);
            util.run(ImageType.NOT_CLASSIFIED);

        } catch (IOException e) {
            System.out.println("IOException: cannot create file");
        }
    }
}

