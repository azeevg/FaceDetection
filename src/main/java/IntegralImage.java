import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;

public class IntegralImage {
    private final double[][] integralImage;

    public IntegralImage(@NotNull final BufferedImage bufferedImage) {

        double[][] matrixOfBrightness = new double[bufferedImage.getHeight()][bufferedImage.getWidth()];

        for (int i = 0; i < bufferedImage.getHeight(); i++) {
            for (int j = 0; j < bufferedImage.getWidth(); j++) {
                matrixOfBrightness[i][j] = calculateBrightness(bufferedImage.getRGB(j, i));
            }
        }
//        printMatrix(matrixOfBrightness);
//        System.out.println("\n\n");
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

//        printMatrix(integralImage);
    }

    private static double calculateBrightness(final int rgb) {
        Color color = new Color(rgb);
        return (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255.0;
    }

    private double getBrightness(final int x, final int y) {
        return integralImage[y][x];
    }

    private double getBrightness(@NotNull final Primitive primitive, @NotNull final Vector shift) {
        // it works only with rectangles, which edges are parallel to the axes
        List<Point> points = primitive.getVertexes();

        Point point = points.get(2);
        double result = getBrightness((int) (point.getX() + shift.getX()), (int) (point.getY() + shift.getY()));
        point = points.get(0);
        result -= getBrightness((int) (point.getX() + shift.getX()), (int) (point.getY() + shift.getY()));

        return result;
    }

    private double getTotalBrightness(@NotNull final Collection<Primitive> primitives, @NotNull final Vector shift) {
        double result = 0;

        for (Primitive p : primitives) {
            result += handlePrimitive(p, shift);
        }
        return result;
    }

    public double handleCascade(@NotNull final Cascade cascade, @NotNull final Vector shift) {
        double white = getTotalBrightness(cascade.getWhitePrimitives(), shift);
        double black = getTotalBrightness(cascade.getBlackPrimitives(), shift);

        return white - black;
    }

    private double handlePrimitive(@NotNull final Primitive primitive, @NotNull final Vector shift) {
        return getBrightness(primitive, shift);
    }

    public int getHeight() {
        return integralImage.length;
    }

    public int getWidth() {
        return integralImage[0].length;
    }


    private void printMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.printf("%8.1f ", matrix[i][j]);
            }
            System.out.println();
        }
    }
/*

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
