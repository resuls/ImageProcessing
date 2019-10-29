package lab1;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * @author R. Saparov
 */
public class Histogram_stats implements PlugInFilter
{

    public int setup(String args, ImagePlus image)
    {
        return DOES_8G;
    }

    public void run(ImageProcessor ip)
    {
        int[] hist = ip.getHistogram();

        int max, min;
        max = -1;
        min = 256;

        for (int i = 0; i < hist.length; i++)
        {
            if (hist[i] > 0)
            {
                if (i < min)
                    min = i;
                if (i > max)
                    max = i;
            }
        }

//        for (int i = 0; i < hist.length; i++)
//        {
//            if (hist[i] > 0)
//            {
//                min = i;
//                break;
//            }
//        }
//        for (int i = hist.length - 1; i >= 0; i--)
//        {
//            if (hist[i] > 0)
//            {
//                max = i;
//                break;
//            }
//        }

        double avg = 0;
        int area = ip.getWidth() * ip.getHeight();
        for (int i = 0; i < hist.length; i++)
        {
            avg += i * hist[i];
        }
        avg /= area;

        double variance = 0;
        for (int i = 0; i < hist.length; i++)
        {
            variance += (Math.pow(i - avg, 2) * hist[i]);
        }
        variance /= area;

        IJ.log(String.format("Max: %d\nMin: %d\nAvg: %.4f", max, min, avg));
        IJ.log(String.format("Variance: %.2f", variance));
    }
}
