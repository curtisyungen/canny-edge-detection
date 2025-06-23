package com.example.open_cv_project

import android.graphics.Bitmap
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.android.Utils as OpenCVUtils

class ImageUtils {
    // Applies Canny edge detection to crop padding from left and right sides of images
    // 1. Convert images to black and white and apply slight blur
    // 2. Apply Canny edge detection
    // 3. Sum columns of pixels to determine strong and weak edges
    // 4. Scan inward from left and right sides to determine boundaries
    // 5. Crop the image between left/right boundaries
    fun cropEdges(bitmap: Bitmap, edgeThreshold1: Double = 50.0, edgeThreshold2: Double = 150.0): Bitmap {
        val mat = Mat()
        OpenCVUtils.bitmapToMat(bitmap, mat)

        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2GRAY)
        Imgproc.GaussianBlur(mat, mat, Size(5.0, 5.0), 0.0)

        val edges = Mat()
        Imgproc.Canny(mat, edges, edgeThreshold1, edgeThreshold2)

        val edgeSums = IntArray(edges.cols())
        for (x in 0 until edges.cols()) {
            var sum = 0
            val topScan = (edges.rows() * TOP_SCAN_RATIO).toInt()
            for (y in 0 until topScan) {
                sum += edges.get(y, x)[0].toInt()
            }
            edgeSums[x] = sum
        }

        var left = 0
        while (left < edgeSums.size / 2 && edgeSums[left] < EDGE_THRESHOLD) {
            left++
        }

        var right = edgeSums.size - 1
        while (right > edgeSums.size / 2 && edgeSums[right] < EDGE_THRESHOLD) {
            right--
        }

        // left + 1 because there was an extra pixel remaining on the left side after crop
        val cropStartX = (left + 1).coerceAtMost(bitmap.width - 2)
        val cropWidth = (right - left - 1).coerceAtLeast(1)

        return Bitmap.createBitmap(bitmap, cropStartX, 0, cropWidth, bitmap.height)
    }

    companion object {
        // threshold column sum needs to exceed for it to be considered an edge
        private const val EDGE_THRESHOLD = 1000
        // fraction of image height used for scanning for edges in case border is partial height (e.g. image_3.png)
        private const val TOP_SCAN_RATIO = 0.3
    }
}
