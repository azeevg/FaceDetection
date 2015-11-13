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

        if (cascade != null) {
            features = FeatureManager.readCascade(cascade);
            if (!this.output.exists()) {
                this.output.createNewFile();
            }
        } else {
            features = null;
            if (!this.output.exists()) {
                this.output.mkdir();
            }
        }
    }

    public static void main(String[] args) {
        String cascade = "./src/main/resources/features.json";

//        String facesImagesDirectory = "./src/main/resources/photos/faces";
//        String notFacesImagesDirectory = "./src/main/resources/photos/notFaces";

        String output = "./src/main/output.fv";
        String facesImagesDirectory = "C:\\Users\\GlAz\\Desktop\\faceDetection\\adjustedFaces";
        String notFacesImagesDirectory = "C:\\Users\\GlAz\\Desktop\\faceDetection\\adjustedObjects";

        FSUtil util;

        try {
            util = new FSUtil(facesImagesDirectory, output, cascade);
            util.writeFeatureVectors(ImageType.FACES);

            util = new FSUtil(notFacesImagesDirectory, output, cascade);
            util.writeFeatureVectors(ImageType.NOT_CLASSIFIED);

//            int height = 200;
//            int width = 200;
//
//            util = new FSUtil(facesImagesDirectory, outputFaces, null);
//            util.adjustImages(ImageType.FACES, height, width);


//            util = new FSUtil(notFacesImagesDirectory, outputObjects, null);
//            util.adjustImages(ImageType.NOT_CLASSIFIED, height, width);

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

    private void writeFeatureVectors(@NotNull final ImageType imageType) {
        Consumer<BufferedImage> consumer = img -> writeFeatureVector(img, imageType);

        try {
            printWriter = new PrintWriter(new BufferedWriter(new FileWriter(this.output, true)));
            forEachImage(consumer);
        } catch (IOException e) {
            System.out.println("IOException in method writeFeatureVectors: " + e.getMessage());
        } finally {
            printWriter.close();
        }
    }

    public void replaceFiles(@NotNull final ImageType imageType) throws IOException {
        replaceFile(directory, output, imageType);
        imageId = 0;
    }

    private void replaceFile(@NotNull final File filepath, @NotNull final File output, @NotNull final ImageType imageType) throws IOException {
        if (filepath.isDirectory()) {
            for (File f : filepath.listFiles(fileFilter)) {
                replaceFile(f, output, imageType);
            }
        } else {
            String newPath = output.getAbsolutePath() +
                    File.separator + imageType.name().toLowerCase() +
                    imageId++ + FilenameUtils.getExtension(filepath.getName());
            Files.copy(filepath.toPath(), new File(newPath).toPath());
        }
    }

    public void adjustImages(@NotNull final ImageType imageType, final int height, final int width) throws IllegalArgumentException {

        if (height <= 0 || width <= 0)
            throw new IllegalArgumentException();

        imageId = 0;
        String templatePath = output.getPath() + File.separator + imageType.name().toLowerCase();
        Consumer<BufferedImage> consumer = img -> {
            try {
                adjustImage(img, templatePath, height, width);
            } catch (IOException e) {
                System.out.println("IOException in adjustImage(): " + e.getMessage());
            }
        };

        try {
            forEachImage(consumer);
        } catch (IOException e) {
            System.out.println("IOException in adjust(...): " + e.getMessage());
        }
    }

    private void adjustImage(@NotNull final BufferedImage img, @NotNull final String templatePath, final int height, final int width) throws IOException {

        final int WHITE = -1;

        // check if file with such name is already exists
        File newFile;
        do {
            newFile = new File(templatePath + imageId++ + ".jpg");
        } while (newFile.exists());

        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        final int offsetX;
        final int offsetY;

        if (width > img.getWidth())
            offsetX = 0;
        else
            offsetX = (img.getWidth() - width) / 2;

        if (height > img.getHeight())
            offsetY = 0;
        else
            offsetY = (img.getHeight() - height) / 2;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (offsetY + i < img.getHeight() && offsetX + j < img.getWidth())
                    newImage.setRGB(j, i, img.getRGB(offsetX + j, offsetY + i));
                else
                    newImage.setRGB(j, i, WHITE);
            }
        }

        newFile.createNewFile();
        ImageIO.write(newImage, "jpg", newFile);
    }
}

