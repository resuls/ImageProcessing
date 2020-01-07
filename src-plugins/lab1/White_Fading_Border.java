package lab1;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

import java.util.ArrayList;

public class White_Fading_Border implements PlugInFilter
{

    public int setup(String args, ImagePlus image)
    {
        return DOES_8G;
    }

    public void run(ImageProcessor ip)
    {
        int height = ip.getHeight();
        int width = ip.getWidth();

        ip.invert();
        double d = width * .2;

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                int px = ip.getPixel(x, y);
                int npx = (int) (px * window_function(x, width, d));
                ip.putPixel(x, y, npx);

                int npy = (int) (npx * window_function(y, width, d));
                ip.putPixel(x, y, npy);
            }
        }
        ip.invert();
    }

    private double window_function(int px, int size, double d)
    {
        if (px >= 0 && px < d)
        {
            return cosfunc(px / d);
        }
        else if (px >= size - d && px <= size)
        {
            /*
            NewValue = (((OldValue - OldMin) * (NewMax - NewMin)) / (OldMax - OldMin)) + NewMin
             */
            return cosfunc((px - size + d) / d + 1);
        }
        else return 1;
    }

    private double cosfunc(double x)
    {
        return (1 - Math.cos(Math.PI * x)) / 2;
    }
}
