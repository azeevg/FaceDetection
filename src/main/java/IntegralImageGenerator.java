import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class IntegralImageGenerator {
    private BufferedImage bufferedImage;
    private String path;

    public void initIntegralImageGenerator(String filename) {
        try {
            bufferedImage = ImageIO.read(new File(filename));
            path = filename;
        } catch (IOException e) {
            System.out.println("File opening error: " + path);
        }
    }

    public double[][] getMatrixOfBrightness() {

        if (bufferedImage == null) {
            return null;
        }

        double[][] matrixOfBrightness = new double[bufferedImage.getHeight()][bufferedImage.getWidth()];

        int pixel;
        int red;
        int green;
        int blue;

        for (int i = 0; i < bufferedImage.getHeight(); i++) {
            for (int j = 0; j < bufferedImage.getWidth(); j++) {
                pixel = bufferedImage.getRGB(j, i);
                red = (pixel >> 16) & 255;
                green = (pixel >> 8) & 255;
                blue = pixel & 255;

                matrixOfBrightness[i][j] = getPixelBrightness(red, green, blue);
            }
        }

        return matrixOfBrightness;
    }

    public void getInfo() {
        System.out.println("Path to file: " + path);
        System.out.println("Size: " + bufferedImage.getWidth() + " x " + bufferedImage.getHeight());
    }

    private double getPixelBrightness(int red, int green, int blue) {
        return (0.299 * red + 0.587 * green + 0.114 * blue) / 255.0;
    }

    private double[][] getIntegralImage() {

        if (bufferedImage == null)
            return null;

        double[][] integralImage = getMatrixOfBrightness();

        printMatrix(integralImage);
        System.out.println();


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

        printMatrix(integralImage);

        return integralImage;

    }

    public void printMatrix(double[][] m) {
        for (int i = 0; i < bufferedImage.getHeight(); i++) {
            for (int j = 0; j < bufferedImage.getWidth(); j++) {
                System.out.printf("%8.1f ", m[i][j]);
            }
            System.out.println();
        }
    }


    public static void main(String[] args) {
//        String input = "C:/Users/GlAz/Desktop/test.jpg";
        String input = "src/main/resources/test.jpg";
        IntegralImageGenerator generator = new IntegralImageGenerator();
        generator.initIntegralImageGenerator(input);
        generator.getInfo();
//        generator.getMatrixOfBrightness();
        generator.getIntegralImage();

    }
}
