import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;

public class IntegralImage {
    private final double[][] integralImage;

    public IntegralImage(@NotNull final BufferedImage bufferedImage) {

        double[][] matrixOfBrightness = new double[bufferedImage.getHeight()][bufferedImage.getWidth()];

        for (int i = 0; i < bufferedImage.getHeight(); i++) {
            for (int j = 0; j < bufferedImage.getWidth(); j++) {
                matrixOfBrightness[i][j] = calculateBrightness(bufferedImage.getRGB(j, i));
            }
        }

        integralImage = matrixOfBrightness;

        for (int i = 0; i < bufferedImage.getHeight(); i++) {
            for (int j = 0; j < bufferedImage.getWidth(); j++) {
                if (j - 1 >= 0)
                    integralImage[i][j] += integralImage[i][j - 1];
                if (i - 1 >= 0)
                    integralImage[i][j] += integralImage[i - 1][j];
                if (i - 1 >= 0 && j - 1 >= 0)
                    integralImage[i][j] -= integralImage[i - 1][j - 1];
            }
        }

//        printMatrix();
    }

    private static double calculateBrightness(final int rgb) {
        Color color = new Color(rgb);
        return (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255.0;
    }

    private double getBrightness(@NotNull final Point point) {
        return integralImage[point.getY()][point.getX()];
    }

    private double getBrightness(@NotNull final Primitive primitive) {
        // it works only with rectangles, which edges are parallel to the axes
        ArrayList<Point> points = (ArrayList<Point>) primitive.getVertexes();
        double result = getBrightness(points.get(2)) - getBrightness(points.get(0));

        return result;
    }

    private double getTotalBrightness(@NotNull final Collection<Primitive> primitives) {
        double result = 0;

        for (Primitive p : primitives) {
            result += handlePrimitive(p);
        }
        return result;
    }

    public double handleCascade(@NotNull final Cascade cascade) {
        double white = getTotalBrightness(cascade.getWhitePrimitives());
        double black = getTotalBrightness(cascade.getBlackPrimitives());

        return white - black;
    }

    private double handlePrimitive(@NotNull final Primitive primitive) {
        return getBrightness(primitive);
    }

    public int getHeight() {
        return integralImage.length;
    }

    public int getWidth() {
        return integralImage[0].length;
    }



/*    private void printMatrix() {
        for (int i = 0; i < integralImage.length; i++) {
            for (int j = 0; j < integralImage[0].length; j++) {
                System.out.printf("%8.1f ", integralImage[i][j]);
            }
            System.out.println();
        }
    }


    public static void main(String[] args) {
        String filename = "src/main/resources/test.jpg";
        BufferedImage bufferedImage;

        try {
            bufferedImage = ImageIO.read(new File(filename));
            IntegralImage integralImage = new IntegralImage(bufferedImage);
            System.out.println(integralImage.getIntegralImage(0, 0, bufferedImage.getWidth() - 1, bufferedImage.getHeight() - 1));
        } catch (IOException e) {
            System.out.println("File opening error: " + filename);
        } catch (IllegalArgumentException e) {
            System.out.println("bufferedImage == null");
        }

    }*/
}
