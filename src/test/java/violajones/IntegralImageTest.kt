package violajones

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Percentage
import org.junit.Test
import org.junit.jupiter.api.TestInstance
import violajones.common.Point
import violajones.common.Vector
import violajones.grayScale.ConversionMethod
import violajones.grayScale.GrayScaleConvertersFactory
import java.awt.Color
import java.io.File
import javax.imageio.ImageIO

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IntegralImageTest {

    @Test
    fun withGleamConverter() {
        val converter = GrayScaleConvertersFactory.create(ConversionMethod.GLEAM)
        val image = ImageIO.read(File("src\\test\\resources\\test.jpg"))
        val integralImage = IntegralImage(image, converter)

        var smallerRectangle = 0.0;
        for (y in 0..2) {
            for (x in 0..5) {
                val c = Color(image.getRGB(x, y))
                val intensity = converter.convert(c.rgb)
                assertThat(intensity).isLessThan(255.0)
                assertThat(intensity).isGreaterThanOrEqualTo(0.0)
                smallerRectangle += intensity;
            }
        }

        assertThat(integralImage.getBrightness(5, 2)).isCloseTo(smallerRectangle, Percentage.withPercentage(0.01))

        var fullImage = 0.0;

        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                val c = Color(image.getRGB(x, y))
                fullImage += converter.convert(c.rgb);
            }
        }
        assertThat(integralImage.getBrightness(9, 4)).isCloseTo(fullImage, Percentage.withPercentage(0.01))

        val manuallyCalculatedIntensity = fullImage - smallerRectangle;
        val rectangle = Region(
                Point(6.0, 3.0),
                Point(9.0, 3.0),
                Point(9.0, 4.0),
                Point(6.0, 4.0));
        val integralImageIntensity = integralImage.getBrightness(rectangle, Vector(0.0, 0.0))
        assertThat(integralImageIntensity).isGreaterThan(0.0)
        assertThat(integralImageIntensity).isCloseTo(manuallyCalculatedIntensity, Percentage.withPercentage(0.01))
    }
}