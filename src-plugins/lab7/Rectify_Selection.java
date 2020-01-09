package lab7;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.lib.math.Matrix;
import lab7.lib.Utils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * This ImageJ plugin transforms an image selection
 * (specified by four corner points) to a new image
 * with A4 proportions.
 */
public class Rectify_Selection implements PlugInFilter
{
    static final int A4_width = 210;
    static final int A4_height = 297;    // A4 paper size in mm

    ImagePlus im = null;

    public int setup(String args, ImagePlus im)
    {
        this.im = im;
        return DOES_8G + DOES_RGB + NO_CHANGES + ROI_REQUIRED;
    }

    public void run(ImageProcessor source)
    {
        Roi roi = im.getRoi();
        if (!(roi instanceof PolygonRoi) || roi.getPolygon().npoints < 4)
        {
            IJ.error("Polygon selection with at least 4 points required!");
            return;
        }

        // Get the 4 source points:
        Point2D[] P = Utils.getPoints(roi.getPolygon());

        // Get the 4 target points (corners of A4 paper):
        int wt = A4_width * 5;    // scale size as needed
        int ht = A4_height * 5;
        Point2D[] Q = {
                new Point(0, 0),
                new Point(wt - 1, 0),
                new Point(wt - 1, ht - 1),
                new Point(0, ht - 1)
        };

        double[][] A = makeTransformationMatrix(P, Q);
        IJ.log("A = \n" + Matrix.toString(A));

        // Map the source image to a new target image:
        ImageProcessor target = source.createProcessor(wt, ht);
        Utils.mapPixels(source, target, A);
        (new ImagePlus("target", target)).show();
    }

    // -------------------------------------------------------------------
    // METHODS TO COMPLETE
    // -------------------------------------------------------------------

    /**
     * Calculates a 3x3 projective transformation matrix A
     * which maps the given source points P to the target points Q,
     * such that q_i = A * p_i (in homogeneous coordinates).
     *
     * @param p source image points (p_0,...,p_3)
     * @param q target image points (q_0,...,q_3)
     * @return the linear transformation that maps points P to Q.
     */
    public static double[][] makeTransformationMatrix(Point2D[] p, Point2D[] q)
    {
        if (p.length < 4 || q.length < 4)
            throw new IllegalArgumentException("At least 4 point pairs are required!");

        //Matrix M
        double[][] M = getMatrixM(p, q);

        //Vector x
        double[] x = getVectorX(q);

        //Solving the 8 linear equations for the unknown vector a
        double[] a = Matrix.solve(M, x);

        //Calculating projective matrix A
        return new double[][]
                {
                    {a[0], a[1], a[2]},
                    {a[3], a[4], a[5]},
                    {a[6], a[7], 1}
                };
    }


    public static double[][] getMatrixM(Point2D[] p, Point2D[] q)
    {
        return new double[][]
        {
            {p[0].getX(), p[0].getY(), 1, 0, 0, 0, -p[0].getX() * q[0].getX(), -p[0].getY() * q[0].getX()},
            {0, 0, 0, p[0].getX(), p[0].getY(), 1, -p[0].getX() * q[0].getY(), -p[0].getY() * q[0].getY()},

            {p[1].getX(), p[1].getY(), 1, 0, 0, 0, -p[1].getX() * q[1].getX(), -p[1].getY() * q[1].getX()},
            {0, 0, 0, p[1].getX(), p[1].getY(), 1, -p[1].getX() * q[1].getY(), -p[1].getY() * q[1].getY()},

            {p[2].getX(), p[2].getY(), 1, 0, 0, 0, -p[2].getX() * q[2].getX(), -p[2].getY() * q[2].getX()},
            {0, 0, 0, p[2].getX(), p[2].getY(), 1, -p[2].getX() * q[2].getY(), -p[2].getY() * q[2].getY()},

            {p[3].getX(), p[3].getY(), 1, 0, 0, 0, -p[3].getX() * q[3].getX(), -p[3].getY() * q[3].getX()},
            {0, 0, 0, p[3].getX(), p[3].getY(), 1, -p[3].getX() * q[3].getY(), -p[3].getY() * q[3].getY()},
        };
    }

    public static double[] getVectorX(Point2D[] q)
    {
        double[] b = new double[8];
        int i = 0;

        for (Point2D p : q)
        {
            b[i++] = p.getX();
            b[i++] = p.getY();
        }

        return b;
    }
}
