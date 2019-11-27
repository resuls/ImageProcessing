package lab7;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Image_Rotation implements PlugInFilter
{
    public int setup(String arg, ImagePlus im)
    {
        return DOES_8G + DOES_RGB + NO_CHANGES;
    }

    public void run(ImageProcessor ip)
    {

    }
}
