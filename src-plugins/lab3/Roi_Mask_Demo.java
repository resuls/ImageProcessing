package lab3;

import java.awt.Point;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

/**
 * Demonstrates how to create a binary mask image from a user-selected
 * region of interest (ROI).
 *
 * @author WB
 */
public class Roi_Mask_Demo implements PlugInFilter
{

    private ImagePlus im;

    public int setup(String arg, ImagePlus im)
    {
        this.im = im;
        return DOES_ALL + ROI_REQUIRED + NO_CHANGES;
    }

    public void run(ImageProcessor ip)
    {
        Roi roi = im.getRoi();
        if (roi == null)
        {
            IJ.error("ROI required!");
            return;
        }

        String title = im.getShortTitle();
        ImageProcessor R = makeRoiMask(roi, ip.getWidth(), ip.getHeight());
        new ImagePlus(title + "-mask", R.duplicate()).show();
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
}
