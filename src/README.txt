Samantha Luber
sluber@usc.edu
9111089098
CSCI576: Assignment 1

Program Overview:
Solution is implemented in ImageTransformer class and uses rigid body transformations to translate the original image between
the "pixel space" coordinate system and the "origin space" coordinate system, scale, and rotate the input image.
The work to read and transform images is broken into three core helper methods: readRGBImage, filterImage, and transformImage.

To display the program results, JPanels are used to display the original and transformed images side-by-side. In addition, JLabels are used
to identify that original and transformed images and to display the command-line inputs.

Note:
ImageTransformer uses third-party implementation of a Matrix class (included in april package).
As April package is licensed for use under GNU GPL, I imported only the necessary classes from April lib and cited the source and author of these classes
at the top of each class's java file).

Compiling instructions:
javac ImageTransformer.java

Execution instructions:
java ImageTransform.java <rgb image file path> <scale factor (positive floating point number)> <degrees to rotate clockwise (double number between 0 and 365 degrees inclusive)> <antialiasing flag (if set = 1, antialiasing will be performed on image)>


