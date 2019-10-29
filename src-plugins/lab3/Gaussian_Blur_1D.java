package lab3;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * Demonstrates the basic use of the {@link GenericDialog} class
 * to query user input.
 *
 * @author WB
 */
public class Gaussian_Blur_1D implements PlugInFilter
{
    private static double sigma = 2; // filter radius (static field keeps value between invocations)
    private static int size = 0;

    public int setup(String arg, ImagePlus imp)
    {
        return DOES_ALL;
    }

    public void run(ImageProcessor ip)
    {
        if (!getUserInput())
            return;    // dialog was canceled

        float[] kernel = makeGaussKernel1D();

        ip.convolve(kernel, size, 1);
        ip.convolve(kernel, 1, size);
    }

    private boolean getUserInput()
    {
        GenericDialog gd = new GenericDialog("Set filter parameters");
        gd.addNumericField("Radius = (0.5, ..., 20)", sigma, 1);
        gd.showDialog();
        if (gd.wasCanceled())
        {
            return false;
        }
        sigma = gd.getNextNumber();
        sigma = Math.min(Math.max(sigma, 0.5), 20);    // limit to 1,...,50
        return true;
    }

    private float[] makeGaussKernel1D()
    {
        int center = (int) (6 * sigma);
        size = 2 * center + 1;
        float[] h = new float[size];

        double sigma2 = sigma * sigma;

        for (int i = 0; i < h.length; i++)
        {
            double r = center - i;
            h[i] = (float) Math.exp(-0.5 * (r * r) / sigma2);
        }

        return h;
    }
}