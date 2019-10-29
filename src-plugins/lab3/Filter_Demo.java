package lab3;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * Demonstrates the use of {@link ImageProcessor}'s {@code convolve()} method
 * for implementing linear filters.
 *
 * @author WB
 */
public class Filter_Demo implements PlugInFilter
{

    public int setup(String arg, ImagePlus im)
    {
        return DOES_ALL;    // works for any type of image!
    }

    public void run(ImageProcessor ip)
    {
        float[] H = {        // filter kernel - this is a 1D array!
                1, 2, 1,
                2, 4, 2,
                1, 2, 1
        };
        ip.convolve(H, 3, 3);    // apply H as a 3 x 3 kernel
    }
}