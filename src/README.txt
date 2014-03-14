Samantha Luber
sluber@usc.edu
9111089098
CSCI576: Assignment 2

Program Overview:
Program driver is implemented in ImageCompression class. DCTCoder class performs actual DCT, IDCT, quantization, and de-quantization computations. To abstract the work needed to translate between image blocks and a BufferedImage, I implemented the RGBBlockImage class that wraps an ArrayList of  RGBBlock class objects ("image blocks") and a BufferedImage object ("output image"). To display the input original image and output decoded image, I implemented the DoubleImageDisplay class.

In DCTCoder, I have two implementations of DCT/IDCT. The first implementation is dctBlockNaiveMethod, which uses no pre-computed values and calculates using the FDCT and IDCT equations from the class lecture notes/JPEG paper directly. The second implementation dctBlock uses pre-computed coefficient values for DCT/IDCT (and 8x8 matrix). For the second implementation, I compute the transform of 8x8 block A using the following matrix equation: B = U * A * U_transpose, where U is the pre-computed value matrix I generate at the beginning of my program. Both implementations provide the same results, but I feel that this second implementation is both more efficient and an overall cleaner solution. By default, my program uses the second implementation.

Note:
ImageCompression uses third-party implementation of a Matrix class (included in april package).
As April package is licensed for use under GNU GPL, I imported only the necessary classes from April lib and cited the source and author of these classes at the top of each class's java file).

Compiling instructions:
javac *.java april/*.java

Execution instructions:
java ImageCompression <rgb image file path> <quantization factor (int: 0-7)> <delivery mode (1, 2, or 3)> <latency (in ms)>


