package com.example.projectcolor


/**
 * PixelData is a data class representing the color information for a single pixel in an RGB format.
 *
 * It includes:
 *
 * - `red`: A `Float` value representing the red component of the pixel color, ranging from 0.0 to 1.0.
 * - `green`: A `Float` value representing the green component of the pixel color, ranging from 0.0 to 1.0.
 * - `blue`: A `Float` value representing the blue component of the pixel color, ranging from 0.0 to 1.0.
 *
 * Instances of this class are used within an RGB matrix to define the color of each pixel, enabling precise
 * control over the display and manipulation of pixel data within the application.
 */
data class PixelData(
    val red: Float,
    val green: Float,
    val blue: Float,
)

/**
 * RGBMatrix is a class that represents a 2D matrix of pixels, specifically designed for managing and manipulating
 * RGB color data. It is used to store and retrieve color information for a grid of pixels.
 *
 * The class includes:
 *
 * - `width`: The width of the pixel grid (number of columns).
 * - `height`: The height of the pixel grid (number of rows).
 * - `data`: A private array of `PixelData` objects that stores the RGB color information for each pixel in the grid.
 *   The array is initialized with black pixels (0.0 for red, green, and blue components).
 *
 * The key methods provided are:
 *
 * - `getPixel(x: Int, y: Int): PixelData`: Retrieves the `PixelData` for the pixel located at the specified
 *   `(x, y)` coordinates. Throws an `IndexOutOfBoundsException` if the coordinates are outside the matrix bounds.
 *
 * - `setPixel(x: Int, y: Int, pixel: PixelData)`: Sets the color of the pixel at the specified `(x, y)` coordinates
 *   to the given `PixelData`. Throws an `IndexOutOfBoundsException` if the coordinates are outside the matrix bounds.
 *
 * The class also includes a private helper method:
 *
 * - `isValidIndex(x: Int, y: Int): Boolean`: Checks if the given `(x, y)` coordinates are within the valid range
 *   of the matrix dimensions. Returns `true` if the coordinates are valid, otherwise returns `false`.
 *
 * RGBMatrix is central to the application's functionality, enabling detailed control and manipulation of a grid
 * of pixels, which can be used for various purposes such as rendering, animation, or transmission over Bluetooth.
 */
class RGBMatrix (
    val width: Int,
    val height: Int,
    ) {

    private val data: Array<PixelData> = Array(height * width) { PixelData(0f, 0f, 0f) }

    fun getPixel(x: Int, y: Int): PixelData {
        if(!isValidIndex(x, y)) {
            throw IndexOutOfBoundsException("Index ($x, $y) out of bounds for RGBMatrix ($width, $height")
        }
        val index = y * width + x
        return data[index]
    }

    fun setPixel(x: Int, y: Int, pixel: PixelData) {
        if(!isValidIndex(x, y)) {
            throw IndexOutOfBoundsException("Index ($x, $y) out of bounds for RGBMatrix ($width, $height")
        }
        val index = y * width + x
        data[index] = pixel
    }

    private fun isValidIndex(x: Int, y: Int): Boolean {
        return x in 0 until width && y in 0 until height
    }
}