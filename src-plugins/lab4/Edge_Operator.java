package lab4;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.awt.*;


public class Edge_Operator implements PlugInFilter
{
    public int setup(String arg, ImagePlus im)
    {
        return DOES_8G;
    }

    public void run(ImageProcessor ip)
    {
        FloatProcessor[] results = edgeOperator(ip);

        new ImagePlus("gradient X", results[0]).show();
        new ImagePlus("gradient Y", results[1]).show();
        new ImagePlus("Edge Magnitude", results[2]).show();
    }

    private FloatProcessor[] edgeOperator(ImageProcessor ip)
    {
        FloatProcessor ipx = ip.convertToFloatProcessor();
        FloatProcessor ipy = ip.convertToFloatProcessor();

        float[] gradient = {-1, 0, 1};
        float[] falloff = {3, 10, 3};

        ipx.convolve(falloff, 1, 3);
        ipx.convolve(gradient, 3, 1);
        ipx.resetMinAndMax();

        ipy.convolve(falloff, 3, 1);
        ipy.convolve(gradient, 1, 3);
        ipy.resetMinAndMax();

        int width = ip.getWidth();
        int height = ip.getHeight();
        FloatProcessor res = new FloatProcessor(width, height);

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                double px = ipx.getf(x, y);
                double py = ipy.getf(x, y);

                res.setf(x, y, (float) Math.sqrt(px * px + py * py));
//                res.setf(x, y, (float) Math.atan2(py, px));
            }
        }

        res.multiply(1 / res.getMax());
        res.resetMinAndMax();

        return new FloatProcessor[] {ipx, ipy, res};
    }
}
