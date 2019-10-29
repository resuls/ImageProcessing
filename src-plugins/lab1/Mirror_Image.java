package lab1;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * @author R. Saparov
 */
public class Mirror_Image implements PlugInFilter
{

    public int setup(String args, ImagePlus image)
    {
        return DOES_8G;
    }

    public void run(ImageProcessor ip)
    {
        int height = ip.getHeight();
        int width = ip.getWidth();

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width / 2; x++)
            {
                int left = ip.getPixel(x, y);
                int right = ip.getPixel(width - x - 1, y);

                ip.putPixel(x, y, right);
                ip.putPixel(width - 1 - x, y, left);
            }
        }
    }
}
