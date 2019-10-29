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
public class Dialog_Demo implements PlugInFilter
{

    static double rad = 1.0; // filter radius (static field keeps value between invocations)

    public int setup(String arg, ImagePlus imp)
    {
        return DOES_ALL;
    }

    public void run(ImageProcessor ip)
    {
        if (!getUserInput())
            return;    // dialog was canceled
        IJ.log("radius = " + rad); // just to be sure
        // do the real work now ...
    }

    // ------------------------------------------------

    private boolean getUserInput()
    {
        GenericDialog gd = new GenericDialog("Set filter parameters");
        gd.addNumericField("Radius = (1,..,50)", rad, 1);
        gd.showDialog();
        if (gd.wasCanceled())
        {
            return false;
        }
        rad = gd.getNextNumber();
        rad = Math.min(Math.max(rad, 1), 50);    // limit to 1,...,50
        return true;
    }
}