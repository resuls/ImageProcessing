package lab1;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * @author R. Saparov
 */
public class Image_Statistics implements PlugInFilter
{

    public int setup(String args, ImagePlus image)
    {
        return DOES_8G;
    }

    public void run(ImageProcessor ip)
    {
        int height = ip.getHeight();
        int width = ip.getWidth();

        int max;
        int min = max = ip.getPixel(0, 0);
        long sum = 0;

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                int px = ip.getPixel(x, y);
                if (px > max)
                    max = px;
                if (px < min)
                    min = px;
                sum += px;
            }
        }
        double avg = (double) sum / (height * width);

        double variance = 0;
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                int px = ip.getPixel(x, y);

                variance += Math.pow(px - avg, 2);
            }
        }
        variance /= (width * height);

        IJ.log(String.format("Max: %d\nMin: %d\nAvg: %.4f", max, min, avg));
        IJ.log(String.format("Variance: %.2f", variance));
    }
}
