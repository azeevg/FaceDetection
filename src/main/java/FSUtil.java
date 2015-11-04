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
    private File directory;
    private PrintWriter printWriter;

    public FSUtil(@NotNull final String directory, @NotNull final String output, @NotNull final String cascade) throws IOException {
        setDirectory(directory);

        this.output = new File(output);

        if (!this.output.exists()) {
            this.output.createNewFile();
        }

        cascades = CascadeManager.readCascade(cascade);
    }


    public void setDirectory(@NotNull final String directory) throws IllegalArgumentException {
        this.directory = new File(directory);

        if (!this.directory.isDirectory()) {
            throw new IllegalArgumentException();
        }
    }

    public void forEachImage(@NotNull final Consumer<BufferedImage> consumer) throws IOException {
        for (final File f : directory.listFiles(fileFilter)) {
            BufferedImage bufferedImage = ImageIO.read(f);
            consumer.accept(bufferedImage);
        }
    }

    private void writeFeatureVector(@NotNull final BufferedImage image, final boolean isFace) {

        String featureVector;

        if (isFace) {
            featureVector = "1 ";
        } else {
            featureVector = "0 ";
        }

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
//        System.out.println(featureVector);
    }

    // temporary solution
    private void run(@NotNull final String notFacesPhotosDirectory) {
        boolean isFace = true;

        Consumer<BufferedImage> faceImageConsumer = img -> writeFeatureVector(img, isFace);
        Consumer<BufferedImage> notFaceImageConsumer = img -> writeFeatureVector(img, !isFace);

        try {
            printWriter = new PrintWriter(new BufferedWriter(new FileWriter(this.output, true)));
            forEachImage(faceImageConsumer);
            setDirectory(notFacesPhotosDirectory);
            forEachImage(notFaceImageConsumer);
        } catch (IOException e) {
            System.out.println("IOException in method run: " + e.getMessage());
        } finally {
            printWriter.close();
        }
    }

    public static void main(String[] args) {
        String cascade = "./src/main/resources/cascade.json";

//        String facesPhotosDirectory = "./src/main/resources/photos/faces";
//        String notFacesPhotosDirectory = "./src/main/resources/photos/notFaces";
        String facesPhotosDirectory = "C:\\Users\\GlAz\\Desktop\\faceDetection\\faces";
        String notFacesPhotosDirectory = "C:\\Users\\GlAz\\Desktop\\faceDetection\\notfaces";

        String output = "./src/main/output.txt";

        FSUtil util;

        try {
            util = new FSUtil(facesPhotosDirectory, output, cascade);
            util.run(notFacesPhotosDirectory);

        } catch (IOException e) {
            System.out.println("IOException: cannot create file");
        }

    }

}

