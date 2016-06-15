"# FISHA" 
FINDING IMAGE SIMILARITY: HISTOGRAM ANALYSIS
============================================

FISHA is a program that allows the user to find an optimal similarity index for images. FISHA can return all images "similar" to a given image in a directory; or, FISHA can seperate all images in a directory into clusters by "similarity."

There are two programs that make up this project. The Matlab program **FISHAM** and the Java program **FISHAJ**. These programs must be run sequentially and in order.

**FISHAM**

Arguments: directory holding JPG images you wish to process and directory to store output.
          
Returns: Path to output.

For each image int the directory, this program returns a horizontal and vertical gradient, an RGB histogram, and a blurred image.


**FISHAJ**

In main you must give the directory holding the JPG images, the output directory from **FISHAM** and the desired output directory for the results.

This program finds a similarity between images based on:

* texture distribution
* RGB distribution
* color coherence vectors

and uses the L1 distance metric, and DBSCAN.

Please refer to FISHA_.pdf for a full report and results.
