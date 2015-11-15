import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.function.Consumer;

public final class FSUtil {
    private static final int SCANNING_WINDOW_SIZE = 60;
    private static final int STEP = SCANNING_WINDOW_SIZE / 3;
    private static final String DELIMITER = "\t";

    private static FileFilter fileFilter = new FileFilter() {
        private final List<String> extensions = Arrays.asList(ImageIO.getReaderFormatNames());

        public boolean accept(@NotNull final File pathname) {
            final String extension = FilenameUtils.getExtension(pathname.getName());
            return !(!pathname.isFile() || !pathname.exists() || !extensions.contains(extension));

        }
    };
    private final List<Feature> features;
    private final File directory;
    private int imageId = 0;


    //Q: почеу cascade может быть null?
    public FSUtil(@NotNull final String directory, @NotNull final InputStream cascade) throws IOException {
        this.directory = new File(directory);

        if (!this.directory.exists() || !this.directory.isDirectory()) {
            throw new IllegalArgumentException();
        }

        features = FeatureManager.readCascade(cascade);
    }

    public static void main(String[] args) throws IOException {
        final String cascade = "./src/main/resources/features.json";

//        String facesImagesDirectory = "./src/main/resources/photos/faces";
//        String notFacesImagesDirectory = "./src/main/resources/photos/notFaces";

        final String output = "./src/main/output.fv";
//        final String facesImagesDirectory = "C:\\Users\\GlAz\\Desktop\\faceDetection\\adjustedFaces";
        final String facesImagesDirectory = "/Users/vkokarev/studenst/FaceDetection/dataset/faces";
        final String notFacesImagesDirectory = "/Users/vkokarev/studenst/FaceDetection/dataset/objects";
//        final String notFacesImagesDirectory = "C:\\Users\\GlAz\\Desktop\\faceDetection\\adjustedObjects";

        FSUtil util;

        try(final PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(output, false)))) {
            //по хорошему - надо закрыть стрим
            util = new FSUtil(facesImagesDirectory, FSUtil.class.getResourceAsStream("features.json"));
            util.writeFeatureVectors(pw, ImageType.FACES);

            util = new FSUtil(notFacesImagesDirectory, FSUtil.class.getResourceAsStream("features.json"));
            util.writeFeatureVectors(pw, ImageType.NOT_CLASSIFIED);
        }
    }

    public void forEachImage(@NotNull final Consumer<BufferedImage> consumer) throws IOException {
        for (final File f : directory.listFiles(fileFilter)) {
            consumer.accept(ImageIO.read(f));
        }
    }

    private void writeFeatureVector(@NotNull final PrintWriter pw, @NotNull final BufferedImage image, @NotNull final ImageType imageType) {
        final StringJoiner joiner = new StringJoiner(DELIMITER);
        joiner.add("1"); // id
        joiner.add("" + imageType.getNumber()); //fastest way to convert number->string
        joiner.add(""); //uri
        joiner.add("1"); //group
        getFeatureVector(image).forEach(d -> joiner.add(String.format(Locale.ENGLISH, "%.2f", d)));
        pw.println(joiner.toString());
    }

    private List<Double> getFeatureVector(@NotNull final BufferedImage image) {
        final IntegralImage integralImage = new IntegralImage(image);
        final List<Double> result = new ArrayList<>();
        features
            .stream()
            .map(f -> f.scale(SCANNING_WINDOW_SIZE))
            .map(f -> handleCascade(f, integralImage))
            .forEach(result::addAll);
        return result;
    }

    private List<Double> handleCascade(@NotNull final Feature scaledFeature, @NotNull final IntegralImage integralImage) {
        final List<Double> result = new ArrayList<>();
        final int cascadeWidth = (int) scaledFeature.getWidth();
        final int cascadeHeight = (int) scaledFeature.getHeight();

        for (int i = 0; i < (integralImage.getHeight() - cascadeHeight) / STEP; i++) {
            for (int j = 0; j < (integralImage.getWidth() - cascadeWidth) / STEP; j++) {
                final Vector shift = new Vector(j * STEP, i * STEP);
                result.add(integralImage.handleCascade(scaledFeature, shift));
            }
        }
        return result;
    }

    private void writeFeatureVectors(@NotNull final PrintWriter pw, @NotNull final ImageType imageType) {
        try {
            forEachImage(img -> writeFeatureVector(pw, img, imageType));
        } catch (IOException e) {
            System.out.println("IOException in method writeFeatureVectors: " + e.getMessage());
            //лучше гордо умереть, чем стыдливо такое замолчать
            throw new RuntimeException("can't continue execution due to fatal error", e);
        }
    }

//    public void replaceFiles(@NotNull final ImageType imageType) throws IOException {
//        replaceFile(directory, output, imageType);
//        imageId = 0;
//    }

//    private void replaceFile(@NotNull final File filepath, @NotNull final File output, @NotNull final ImageType imageType) throws IOException {
//        if (filepath.isDirectory()) {
//            for (File f : filepath.listFiles(fileFilter)) {
//                replaceFile(f, output, imageType);
//            }
//        } else {
//            String newPath = output.getAbsolutePath() +
//                    File.separator + imageType.name().toLowerCase() +
//                    imageId++ + FilenameUtils.getExtension(filepath.getName());
//            Files.copy(filepath.toPath(), new File(newPath).toPath());
//        }
//    }

//    public void adjustImages(@NotNull final ImageType imageType, final int height, final int width) throws IllegalArgumentException {
//
//        if (height <= 0 || width <= 0)
//            throw new IllegalArgumentException();
//
//        imageId = 0;
//        String templatePath = output.getPath() + File.separator + imageType.name().toLowerCase();
//        Consumer<BufferedImage> consumer = img -> {
//            try {
//                adjustImage(img, templatePath, height, width);
//            } catch (IOException e) {
//                System.out.println("IOException in adjustImage(): " + e.getMessage());
//            }
//        };
//
//        try {
//            forEachImage(consumer);
//        } catch (IOException e) {
//            System.out.println("IOException in adjust(...): " + e.getMessage());
//        }
//    }

//    private void adjustImage(@NotNull final BufferedImage img, @NotNull final String templatePath, final int height, final int width) throws IOException {
//
//        final int WHITE = -1;
//
//        // check if file with such name is already exists
//        File newFile;
//        do {
//            newFile = new File(templatePath + imageId++ + ".jpg");
//        } while (newFile.exists());
//
//        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//
//        final int offsetX;
//        final int offsetY;
//
//        if (width > img.getWidth())
//            offsetX = 0;
//        else
//            offsetX = (img.getWidth() - width) / 2;
//
//        if (height > img.getHeight())
//            offsetY = 0;
//        else
//            offsetY = (img.getHeight() - height) / 2;
//
//        for (int i = 0; i < height; i++) {
//            for (int j = 0; j < width; j++) {
//                if (offsetY + i < img.getHeight() && offsetX + j < img.getWidth())
//                    newImage.setRGB(j, i, img.getRGB(offsetX + j, offsetY + i));
//                else
//                    newImage.setRGB(j, i, WHITE);
//            }
//        }
//
//        newFile.createNewFile();
//        ImageIO.write(newImage, "jpg", newFile);
//    }
}

