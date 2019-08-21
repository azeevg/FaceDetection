package violajones.visualizer

import violajones.Feature
import violajones.FeatureHandler
import violajones.Oracle
import violajones.Utils
import violajones.common.Point
import violajones.grayScale.ConversionMethod
import violajones.grayScale.GrayScaleConverter
import violajones.grayScale.GrayScaleConvertersFactory
import java.awt.Color
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import java.util.*

class FramesDrawer(private val oracle: Oracle, conversionMethod: ConversionMethod) {
    private val grayScaleConverter: GrayScaleConverter = GrayScaleConvertersFactory.create(conversionMethod)

    @Throws(Exception::class)
    fun selectFaces(originalImage: BufferedImage, features: List<Feature>): BufferedImage {

        var scanningWindowSize = MIN_SCANNING_WINDOW_SIZE
        val frames = ArrayList<Frame>()

        for (scanningLevel in 0 until MAX_SCANNING_LEVEL) {
            val featureHandler = FeatureHandler(features, scanningWindowSize, grayScaleConverter)

            if (scanningWindowSize > originalImage.height || scanningWindowSize > originalImage.width)
                throw Exception("Image is smaller then scanning window")

            for (i in 0 until (originalImage.height - scanningWindowSize) / STEP) {
                for (j in 0 until (originalImage.width - scanningWindowSize) / STEP) {
                    val topLeft = Point((j * STEP).toDouble(), (i * STEP).toDouble())
                    val bottomRight = Point((j * STEP + scanningWindowSize).toDouble(), (i * STEP + scanningWindowSize).toDouble())
                    val scanningWindow = Frame(topLeft, bottomRight)

                    if (oracle.verifyFace(featureHandler.getFeatureVector(originalImage, scanningWindow)) == 1.0) {
                        frames.add(scanningWindow)
                    }
                }
            }
            scanningWindowSize *= SCALE_COEFFICIENT.toInt()
        }

        return drawFrames(originalImage, frames)
    }

    private fun drawFrames(originalImage: BufferedImage, frames: Collection<Frame>): BufferedImage {
        if (frames.isEmpty())
            return originalImage

        val image = Utils.cloneBufferedImage(originalImage)

        val g2d = image.createGraphics()
        g2d.color = Color.YELLOW

        for (f in frames) {
            val topLeft = f.topLeftCorner
            val bottomRight = f.bottomRightCorner

            g2d.fill(Rectangle2D.Float(topLeft.x.toFloat(), topLeft.y.toFloat(),
                    LINE_WIDTH.toFloat(), (bottomRight.y - topLeft.y + LINE_WIDTH).toFloat()))

            g2d.fill(Rectangle2D.Float(topLeft.x.toFloat(), topLeft.y.toFloat(),
                    (bottomRight.x - topLeft.x + LINE_WIDTH).toFloat(), LINE_WIDTH.toFloat()))

            g2d.fill(Rectangle2D.Float(bottomRight.x.toFloat(), topLeft.y.toFloat(),
                    LINE_WIDTH.toFloat(), (bottomRight.y - topLeft.y + LINE_WIDTH).toFloat()))

            g2d.fill(Rectangle2D.Float(topLeft.x.toFloat(), bottomRight.y.toFloat(),
                    (bottomRight.x - topLeft.x + LINE_WIDTH).toFloat(), LINE_WIDTH.toFloat()))
        }

        g2d.dispose()

        return image
    }

    companion object {
        private val MIN_SCANNING_WINDOW_SIZE = 60
        private val STEP = MIN_SCANNING_WINDOW_SIZE / 4
        private val LINE_WIDTH = 2
        private val MAX_SCANNING_LEVEL = 5
        private val SCALE_COEFFICIENT = 1.25
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
