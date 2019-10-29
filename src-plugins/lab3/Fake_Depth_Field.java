package lab3;

import java.awt.Point;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;


public class Fake_Depth_Field implements PlugInFilter
{
    private ImagePlus im;

    public int setup(String arg, ImagePlus im)
    {
        this.im = im;
        return DOES_8G + ROI_REQUIRED + NO_CHANGES;
    }

    public void run(ImageProcessor ip)
    {
        Roi roi = im.getRoi();
        if (roi == null)
        {
            IJ.error("ROI required!");
            return;
        }

        int width = ip.getWidth();
        int height = ip.getHeight();

        float[] kernel = makeGaussKernel1D(0.003 * width);

        ImageProcessor blurredIp = ip.duplicate();
        gaussianBlur(blurredIp, kernel);
        ImagePlus im = new ImagePlus("Blurred_image", blurredIp);
        im.show();

        FloatProcessor mask = makeRoiMask(roi, width, height);
        im = new ImagePlus("Mask", mask.duplicate());
        im.show();

        kernel = makeGaussKernel1D(0.05 * width);
        gaussianBlur(mask, kernel);
        im = new ImagePlus("Blurred_mask", mask.duplicate());
        im.show();

        ImageProcessor newImage = ip.duplicate();

        float R;
        int I, Ii, val;
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                R = mask.getPixelValue(x, y);
                I = ip.getPixel(x, y);
                Ii = blurredIp.getPixel(x, y);
                val = (int) (R * I + (1 - R) * Ii);
                newImage.putPixel(x, y, val);
            }
        }

        im = new ImagePlus("Result", newImage);
        im.show();
    }

    /**
     * Creates a mask image from a given ROI. Mask pixels are set to 1.0
     * if inside the ROI and 0.0 outside. Works for any type of ROI.
     *
     * @param roi    user-selected region of interest
     * @param width  mask image width
     * @param height mask image height
     * @return the mask image
     */
    private FloatProcessor makeRoiMask(Roi roi, int width, int height)
    {
        FloatProcessor R = new FloatProcessor(width, height);
        R.setValue(0.0);
        R.fill();
        for (Point p : roi)
        {
            R.setf(p.x, p.y, 1.0f);
        }
        return R;
    }

    private float[] makeGaussKernel1D(double sigma)
    {
        int center = (int) (6 * sigma);
        int size = 2 * center + 1;
        float[] h = new float[size];

        double sigma2 = sigma * sigma;

        for (int i = 0; i < h.length; i++)
        {
            double r = center - i;
            h[i] = (float) Math.exp(-0.5 * (r * r) / sigma2);
        }

        return h;
    }

    private void gaussianBlur(ImageProcessor ip, float[] kernel)
    {
        ip.convolve(kernel, kernel.length, 1);
        ip.convolve(kernel, 1, kernel.length);
    }
}
