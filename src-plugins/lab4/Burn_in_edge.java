package lab4;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.filter.RankFilters;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.awt.*;


public class Burn_in_edge implements PlugInFilter
{
    public int setup(String arg, ImagePlus im)
    {
        return DOES_8G + DOES_RGB;
    }

    public void run(ImageProcessor ip)
    {
        ColorProcessor cp = (ColorProcessor) ip.duplicate();
        ByteProcessor bp = ip.convertToByteProcessor();
        FloatProcessor normalized = edgeOperator(bp);
        new ImagePlus("normalized", normalized).show();

        FloatProcessor soft = getSoftened(normalized);

        double radius = 5;
        RankFilters rf = new RankFilters();
        rf.rank(cp, radius, RankFilters.MEDIAN);

        new ImagePlus("soft", soft).show();

        int[] RGB = new int[3];
        for (int y = 0; y < ip.getHeight(); y++)
        {
            for (int x = 0; x < ip.getWidth(); x++)
            {
                cp.getPixel(x, y, RGB);
                double burnin = soft.getf(x, y);
                RGB[0] *= burnin;
                RGB[1] *= burnin;
                RGB[2] *= burnin;
                cp.putPixel(x,y, RGB);
            }
        }

        new ImagePlus("result", cp).show();
    }

    private FloatProcessor getSoftened(FloatProcessor fp)
    {
        FloatProcessor soft = fp.convertToFloatProcessor();
        for (int y = 0; y < fp.getHeight(); y++)
        {
            for (int x = 0; x < fp.getWidth(); x++)
            {
                soft.putPixelValue(x, y, softThreshold(fp.getf(x, y)));
            }
        }
        return soft;
    }

    private double softThreshold(float E)
    {
        double a = 0.05, b = 0.3;

        if (E < a)
            return 1;
        else if (E > b)
            return 0;
        else
            return 0.5 * (1 + Math.cos(Math.PI * (E - a) / (b - a)));
    }

    private FloatProcessor edgeOperator(ImageProcessor ip)
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
            }
        }

        res.multiply(1 / res.getMax());
        res.resetMinAndMax();

        return res;
    }
}
