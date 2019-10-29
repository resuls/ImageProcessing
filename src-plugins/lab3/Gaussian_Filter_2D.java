package lab3;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Gaussian_Filter_2D implements PlugInFilter
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

        float[] kernel = makeGaussKernel2D();

        ip.convolve(kernel, size, size);
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

    private float[] makeGaussKernel2D()
    {
        int center = (int) (6 * sigma);
        size = 2 * center + 1;
        float[] h = new float[size * size];

        double sigma2 = sigma * sigma;

        int ind = 0;

        for (int i = 0; i < size; i++)
        {
            int x = i - center;
            for (int j = 0; j < size; j++)
            {
                int y = j - center;
                h[ind++] = (float) Math.exp(-0.5 * (x * x + y * y) / (sigma2));
            }
        }
        return h;
    }
}