package violajones

import java.awt.Color
import kotlin.math.pow

class GleamConverter : GrayScaleConverter {
    override fun convert(rgb: Int): Double {
        val c = Color(rgb)
        val sum = listOf(c.red, c.green, c.blue).map { (it * 1.0).pow(1 / 2.2) }.sum()
        return sum / 3.0;
    }
}