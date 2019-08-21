package violajones.grayScale

sealed class GrayScaleConvertersFactory {
    companion object {
        fun create(method: ConversionMethod): GrayScaleConverter = when (method) {
            ConversionMethod.GLEAM -> GleamConverter()
        }
    }
}