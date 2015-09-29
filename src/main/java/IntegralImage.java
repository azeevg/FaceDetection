import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class IntegralImage {
    private final double[][] integralImage;

    public IntegralImage(final BufferedImage bufferedImage) {
        if (bufferedImage == null)
            throw new IllegalArgumentException();

        double[][] matrixOfBrightness = new double[bufferedImage.getHeight()][bufferedImage.getWidth()];

        for (int i = 0; i < bufferedImage.getHeight(); i++) {
            for (int j = 0; j < bufferedImage.getWidth(); j++) {
                matrixOfBrightness[i][j] = getBrightness(bufferedImage.getRGB(j, i));
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

    private static double getBrightness(final int rgb) {
        Color color = new Color(rgb);
        return (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255.0;
    }

    private double getIntegralImage(final int xOffset, final int yOffset, final int xBound, final int yBound) {
        if (xOffset < 0 || yOffset < 0 || yBound > integralImage.length || xBound > integralImage[0].length ||
                (xOffset > xBound || yOffset > yBound))
            throw new IllegalArgumentException();

        double result = integralImage[yBound][xBound];

        if (yOffset > 0)
            result -= integralImage[yOffset - 1][xBound];
        if (xOffset > 0)
            result -= integralImage[yBound][xOffset - 1];
        if (yOffset > 0 && xOffset > 0)
            result += integralImage[yOffset - 1][xOffset - 1];

        return result;
    }
/*
    private void printMatrix() {
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
