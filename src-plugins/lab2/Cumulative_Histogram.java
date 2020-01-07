package lab2;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

import static lab2.Histogram.getCumulativeHistogram;
import static lab2.Histogram.showHistogram;

public class Cumulative_Histogram implements PlugInFilter
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

        int[] cH = getCumulativeHistogram(h);

        showHistogram(h, "a");
        // show the result:
        showHistogram(cH, "hist_" + im.getTitle());
    }
}
