package lab7.lib;

import ij.process.ImageProcessor;
import imagingbook.lib.math.Matrix;

import java.awt.*;
import java.awt.geom.Point2D;


/**
 * This class defines static utility methods for geometric
 * transformations.
 * <p>
 * NO NEED TO EDIT ANYTHING HERE!
 *
 * @author WB
 */
public abstract class Utils
{
    /**
     * Applies a projective transformation to the given 2D point.
     *
     * @param A a 3x3 projective transformation matrix.
     * @param p = (x,y) the 2D point to be transformed.
     * @return the transformed point q = (x',y') = A * p.
     */
    public static Point2D transformPoint(double[][] A, Point2D p)
    {
        final double x = p.getX();
        final double y = p.getY();
        final double s = 1.0 / (A[2][0] * x + A[2][1] * y + A[2][2]);
        final double xq = s * (A[0][0] * x + A[0][1] * y + A[0][2]);
        final double yq = s * (A[1][0] * x + A[1][1] * y + A[1][2]);
        return new Point2D.Double(xq, yq);
    }

    /**
     * Transforms the source image to the target image by using target-to-source
     * mapping and the inverse transformation.
     *
     * @param source the source image
     * @param target the target image
     * @param A      the 3x3 forward transformation matrix (source to target)
     */
    public static void mapPixels(ImageProcessor source, ImageProcessor target, double[][] A)
    {
        final int wt = target.getWidth();
        final int ht = target.getHeight();
        double[][] Ai = Matrix.inverse(A);
        source.setInterpolationMethod(ImageProcessor.BICUBIC);

        // iterate over all target pixels:
        for (int ut = 0; ut < wt; ut++)
        {
            for (int vt = 0; vt < ht; vt++)
            {
                // target position pt:
                Point2D pt = new Point(ut, vt);
                // get source position, ps = Ai * pt:
                Point2D ps = transformPoint(Ai, pt);
                // get interpolated source pixel value:
                int pix = source.getPixelInterpolated(ps.getX(), ps.getY());
                // insert at target position:
                target.putPixel(ut, vt, pix);
            }
        }
    }

    /**
     * Utility method for extracting the points of an AWT polygon.
     *
     * @param poly the original polygon
     * @return the polygon's vertex points
     */
    public static Point2D[] getPoints(Polygon poly)
    {
        Point2D[] pts = new Point2D[poly.npoints];
        for (int i = 0; i < poly.npoints; i++)
        {
            pts[i] = new Point(poly.xpoints[i], poly.ypoints[i]);
        }
        return pts;
    }
}
