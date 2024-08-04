package com.example.projectcolor

data class PixelData(
    val red: Float,
    val green: Float,
    val blue: Float,
)

class RGBMatrix (
    val width: Int,
    val height: Int,
    ) {

    private val data: Array<PixelData> = Array(height * width) { PixelData(0f, 0f, 0f) }

    fun getPixel(x: Int, y: Int): PixelData {
        if(!isValidIndes(x, y)) {
            throw IndexOutOfBoundsException("Index ($x, $y) out of bounds for RGBMatrix ($width, $height")
        }
        val index = y * width + x
        return data[index]
    }

    fun setPixel(x: Int, y: Int, pixel: PixelData) {
        if(!isValidIndes(x, y)) {
            throw IndexOutOfBoundsException("Index ($x, $y) out of bounds for RGBMatrix ($width, $height")
        }
        val index = y * width + x
        data[index] = pixel
    }

    private fun isValidIndes(x: Int, y: Int): Boolean {
        return x in 0 until width && y in 0 until height
    }
}