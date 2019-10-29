package lab2;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

import static lab2.Histogram.*;

public class Cumulative_Distribution implements PlugInFilter
{
    ImagePlus im = null; // keep a reference to the associated ImagePlus object

    public int setup(String arg, ImagePlus im)
    {
        this.im = im;
        return DOES_8G + NO_CHANGES;
    }

    public void run(ImageProcessor ip)
    {
        // get the ordinary histogram:
        int[] h = ip.getHistogram();

        double[] cdf = getCDF(h);

        // show the result:
        showHistogram(cdf, "cdf_" + im.getTitle());
    }
}
