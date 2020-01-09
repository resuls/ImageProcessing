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
        float[] gradient = {-1, 0, 1};
        float[] falloff = {3, 10, 3};

//        float[] hx = {0, 3, 10, -3, 0, 3, -10, -3, 0};
//        float[] hy = {10, -3, 0, -3, 0, 3, 0, 3, 10};
//
//        FloatProcessor ipx = ip.convertToFloatProcessor();
//        ipx.convolve(hx, 3, 3);
//        ipx.resetMinAndMax();
//
//        FloatProcessor ipy = ip.convertToFloatProcessor();
//        ipy.convolve(hy, 3, 3);
//        ipy.resetMinAndMax();

        FloatProcessor ipx = getEdgeMap(ip, gradient, falloff);
        FloatProcessor ipy = getEdgeMap(ip, falloff, gradient);

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
            }
        }

        res.multiply(1 / res.getMax());
        res.resetMinAndMax();

        return new FloatProcessor[] {ipx, ipy, res};
    }

    private FloatProcessor getEdgeMap(ImageProcessor ip, float[] gradient, float[] falloff)
    {
        FloatProcessor ipx = ip.convertToFloatProcessor();
        ipx.convolve(falloff, 1, 3);
        ipx.convolve(gradient, 3, 1);
        ipx.resetMinAndMax();
        return ipx;
    }
}
