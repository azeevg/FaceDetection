package violajones;

import violajones.common.Point;
import violajones.common.Vector;
import org.jetbrains.annotations.NotNull;
import violajones.grayScale.GrayScaleConverter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;

public class IntegralImage {
    private final double[][] integralImage;

    public IntegralImage(@NotNull final BufferedImage bufferedImage, GrayScaleConverter grayScaleConverter) {

        double[][] matrixOfBrightness = new double[bufferedImage.getHeight()][bufferedImage.getWidth()];

        for (int i = 0; i < bufferedImage.getHeight(); i++) {
            for (int j = 0; j < bufferedImage.getWidth(); j++) {
                matrixOfBrightness[i][j] = grayScaleConverter.convert(bufferedImage.getRGB(j, i));
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
    }

    private static double calculateBrightness(final int rgb) {
        final Color color = new Color(rgb);
        return (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255.0;
    }

    public double getBrightness(final int x, final int y) {
        return integralImage[y][x];
    }

    public double getBrightness(@NotNull final Region region, @NotNull final Vector shift) {
        // it works only with rectangles, which edges are parallel to the axes
        final List<Point> points = region.getVertexes();

        Point point = points.get(2);
        Double result = getBrightness((int) (point.getX() + shift.getX()), (int) (point.getY() + shift.getY()));
        point = points.get(0);
        result -= getBrightness((int) (point.getX() - 1 + shift.getX()), (int) (point.getY() - 1 + shift.getY()));

        return result;
    }

    private double getTotalBrightness(@NotNull final Collection<Region> regions, @NotNull final Vector shift) {
        return regions.stream().mapToDouble(p -> handlePrimitive(p, shift)).sum();
    }

    public double handleCascade(@NotNull final Feature feature, @NotNull final Vector shift) {
        double white = getTotalBrightness(feature.getWhiteRegions(), shift);
        double black = getTotalBrightness(feature.getBlackRegions(), shift);

        return white - black;
    }

    private double handlePrimitive(@NotNull final Region region, @NotNull final Vector shift) {
        return getBrightness(region, shift);
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
                System.out.printf("%8.2f ", matrix[i][j]);
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
