# Canny Edge Detection

This uses Canny edge detection to crop padding from the left and right sides of images.

Canny detects edges by finding areas with strong changes in color or brightness.
After finding edges, we sum edge strengths in each column of pixels.
Columns with values below the threshold are considered padding and are cropped.

<img src="cannyEdgeDetectionDemo.gif" alt="Demo" width="350"/>
