package lab7;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.lib.math.Matrix;
import lab7.lib.Utils;

import java.awt.*;
import java.awt.geom.Point2D;

public class Rotate_Image implements PlugInFilter
{
    static double RotationAngle = 30.0;    // rotation angle (in degrees, clockwise)
    private ImagePlus im = null;

    public int setup(String arg0, ImagePlus im)
    {
        this.im = im;
        return DOES_8G + DOES_RGB + NO_CHANGES;
    }

    public void run(ImageProcessor source)
    {
        double[][] A = makeTransformationMatrix(RotationAngle);
        IJ.log("A = \n" + Matrix.toString(A));
        ImageProcessor target = source.duplicate();
        Utils.mapPixels(source, target, A);
        new ImagePlus(im.getShortTitle() + "-rotated", target).show();
    }

    // -------------------------------------------------------------------
    // METHODS TO COMPLETE
    // -------------------------------------------------------------------

    public Point2D getCenterPoint()
    {
        Point2D c = new Point();
        c.setLocation(im.getWidth() / 2.0, im.getHeight() / 2.0);
        IJ.log(c.getX() + " --- " + c.getY());
        return c;
    }

    /**
     * Calculates a 3x3 projective transformation matrix A
     * to rotate the image by 'angle' (given in degrees)
     * about the image center.
     *
     * @param angle The rotation angle (in degrees).
     * @return The transformation matrix.
     */
    public double[][] makeTransformationMatrix(double angle)
    {
        //center of the target image
        Point2D c = getCenterPoint();
        angle = angle * Math.PI / 180;
        return new double[][]
        {
            {Math.cos(angle), -Math.sin(angle), c.getX() * (1 - Math.cos(angle)) + c.getY() * Math.sin(angle)},
            {Math.sin(angle), Math.cos(angle), c.getY() * (1 - Math.cos(angle)) - c.getX() * Math.sin(angle)},
            {0, 0, 1}
        };
    }
}
