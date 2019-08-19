package violajones.visualizer;

import org.jetbrains.annotations.NotNull;
import violajones.Feature;
import violajones.FeatureHandler;
import violajones.Oracle;
import violajones.Utils;
import violajones.common.Point;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FramesDrawer {
    private static final int MIN_SCANNING_WINDOW_SIZE = 60;
    private static final int STEP = MIN_SCANNING_WINDOW_SIZE / 4;
    private static final int LINE_WIDTH = 2;
//    private static final int LINE_COLOR = -256;
    //    private static final int WINDOW_SIZE = 60;

    private static final int MAX_SCANNING_LEVEL = 5;
    private static final double SCALE_COEFFICIENT = 1.25;
    private final Oracle oracle;

    public FramesDrawer(@NotNull final Oracle oracle) {
        this.oracle = oracle;
    }

    public BufferedImage selectFaces(@NotNull final BufferedImage originalImage, @NotNull final List<Feature> features)
            throws Exception {

        int scanningWindowSize = MIN_SCANNING_WINDOW_SIZE;
        Collection<Frame> frames = new ArrayList<>();

        for (int scanningLevel = 0; scanningLevel < MAX_SCANNING_LEVEL; ++scanningLevel) {
            FeatureHandler featureHandler = new FeatureHandler(features, scanningWindowSize);

            if (scanningWindowSize > originalImage.getHeight() || scanningWindowSize > originalImage.getWidth())
                throw new Exception("Image is smaller then scanning window");

            for (int i = 0; i < (originalImage.getHeight() - scanningWindowSize) / STEP; i++) {
                for (int j = 0; j < (originalImage.getWidth() - scanningWindowSize) / STEP; j++) {
                    final Point topLeft = new Point(j * STEP, i * STEP);
                    final Point bottomRight = new Point(j * STEP + scanningWindowSize, i * STEP + scanningWindowSize);
                    final Frame scanningWindow = new Frame(topLeft, bottomRight);

                    if (oracle.verifyFace(featureHandler.getFeatureVector(originalImage, scanningWindow)) == 1.0) {
                        frames.add(scanningWindow);
                    }
                }
            }
            scanningWindowSize *= SCALE_COEFFICIENT;
        }

        return drawFrames(originalImage, frames);
    }

    private BufferedImage drawFrames(@NotNull final BufferedImage originalImage, @NotNull final Collection<Frame> frames) {
        if (frames.isEmpty())
            return originalImage;

        BufferedImage image = Utils.cloneBufferedImage(originalImage);

        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.YELLOW);

        for (Frame f : frames) {
            Point topLeft = f.getTopLeftCorner();
            Point bottomRight = f.getBottomRightCorner();

            g2d.fill(new Rectangle2D.Float((float) topLeft.getX(), (float) topLeft.getY(),
                    LINE_WIDTH, (float) (bottomRight.getY() - topLeft.getY() + LINE_WIDTH)));

            g2d.fill(new Rectangle2D.Float((float) topLeft.getX(), (float) topLeft.getY(),
                    (float) (bottomRight.getX() - topLeft.getX() + LINE_WIDTH), LINE_WIDTH));

            g2d.fill(new Rectangle2D.Float((float) bottomRight.getX(), (float) topLeft.getY(),
                    LINE_WIDTH, (float) (bottomRight.getY() - topLeft.getY() + LINE_WIDTH)));

            g2d.fill(new Rectangle2D.Float((float) topLeft.getX(), (float) bottomRight.getY(),
                    (float) (bottomRight.getX() - topLeft.getX() + LINE_WIDTH), LINE_WIDTH));
        }

        g2d.dispose();

        return image;
    }

//    public static void main(String[] args) throws Exception {
//        final String input = "./src/main/resources/photos/tmp/not_classified1.jpg";
//        final String cascade = "./src/main/resources/features.json";
//
//        FramesDrawer drawer = new FramesDrawer(new PseudoOracle());
//        try {
//            BufferedImage image = drawer.selectFaces(ImageIO.read(new File(input)),
//                    FeatureManager.readCascade(FramesDrawer.class.getResourceAsStream("features.json")));
//
//            File newFile = new File("./src/main/resources/photos/tmp/meow.jpg");
//
//            newFile.createNewFile();
//            ImageIO.write(image, "png", newFile);
//
//        } catch (Exception e) {
//            System.out.println("Something goes wrong.");
//            throw e;
//        }
//    }
}
