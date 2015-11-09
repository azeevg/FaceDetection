import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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
    private final List<Feature> features;
    private final File directory;
    private PrintWriter printWriter;
    private int imageId = 0;


    public FSUtil(@NotNull final String directory, @NotNull final String output, @Nullable final String cascade) throws IOException {
        this.directory = new File(directory);

        if (!this.directory.isDirectory()) {
            throw new IllegalArgumentException();
        }

        this.output = new File(output);

        if (!this.output.exists()) {
            this.output.createNewFile();
        }

        if (cascade != null)
            features = FeatureManager.readCascade(cascade);
        else
            features = null;
    }

    public static void main(String[] args) {
        String cascade = "./src/main/resources/features.json";

//        String facesImagesDirectory = "./src/main/resources/photos/faces";
//        String notFacesImagesDirectory = "./src/main/resources/photos/notFaces";

        String facesImagesDirectory = "C:\\Users\\GlAz\\Desktop\\faceDetection\\faces";
        String notFacesImagesDirectory = "C:\\Users\\GlAz\\Desktop\\faceDetection\\notfaces";

        String output = "./src/main/Output.fv";

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
        Feature scaledFeature;
        String result = "";

        for (Feature feature : features) {
            scaledFeature = feature.scale(scanningWindowSize);
            result += handleCascade(scaledFeature, integralImage);
        }

        return result;
    }

    private String handleCascade(@NotNull final Feature scaledFeature, @NotNull final IntegralImage integralImage) {
        String result = "";
        final int step = 5;

        int cascadeWidth = (int) scaledFeature.getWidth();
        int cascadeHeight = (int) scaledFeature.getHeight();
        //DecimalFormat format = new DecimalFormat("*.##");

        Vector shift;
        for (int i = 0; i < (integralImage.getHeight() - cascadeHeight) / step; i++) {
            for (int j = 0; j < (integralImage.getWidth() - cascadeWidth) / step; j++) {
                shift = new Vector(j * step, i * step);
//                result += integralImage.handleCascade(scaledFeature, shift) + " ";
                result += String.format(Locale.ENGLISH, "%.2f", integralImage.handleCascade(scaledFeature, shift)) + " ";
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

    public void replaceFiles(@NotNull final ImageType imageType) throws IOException {
        replace(directory, output, imageType);
        imageId = 0;
    }

    private void replace(@NotNull final File filepath, @NotNull final File output, @NotNull final ImageType imageType) throws IOException {
        if (filepath.isDirectory()) {
            for (File f : filepath.listFiles(fileFilter)) {
                replace(f, output, imageType);
            }
        } else {
            String newPath = output.getAbsolutePath() +
                    File.separator + imageType.name().toLowerCase() +
                    imageId++ + FilenameUtils.getExtension(filepath.getName());
            Files.copy(filepath.toPath(), new File(newPath).toPath());
        }
    }




}

