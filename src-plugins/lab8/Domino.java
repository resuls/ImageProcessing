package lab8;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Line;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.morphology.BinaryMorphologyFilter;
import imagingbook.pub.regions.Contour;
import imagingbook.pub.regions.RegionContourLabeling;
import imagingbook.pub.regions.RegionLabeling;
import imagingbook.pub.threshold.global.OtsuThresholder;
import lab7.lib.Utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static lab5.Bounding_Box.drawBox;
import static lab5.Bounding_Box.getBoundingBox;
import static lab5.Moment.getCentralMoments;
import static lab7.Rectify_Selection.makeTransformationMatrix;

public class Domino implements PlugInFilter
{
    private static final int B_WIDTH = 70;
    private static final int B_HEIGHT = 100;

    public int setup(String arg0, ImagePlus im)
    {
        return DOES_8G + DOES_RGB + NO_CHANGES;
    }

    public void run(ImageProcessor ip)
    {
        // Threshold
        ByteProcessor bp = ip.convertToByteProcessor();
        new OtsuThresholder().threshold(bp);
        bp.invert();

        // Closing
        BinaryMorphologyFilter filter = new BinaryMorphologyFilter.Disk(3);
        filter.applyTo(bp, BinaryMorphologyFilter.OpType.Close);
//        new ImagePlus("closing", bp).show();

        // Segmentation and Major Axis
        RegionContourLabeling segmenter = new RegionContourLabeling(bp);

        // This returns a list of regions, sorted by size:
        List<RegionLabeling.BinaryRegion> regions = segmenter.getRegions(true);

        ColorProcessor cp = bp.convertToColorProcessor();
        cp.setColor(Color.BLACK);

        // visit and color the pixels inside of each region:
        for (RegionLabeling.BinaryRegion R : regions)
        {
            // Skip if it is not a domino brick
            if (R.getSize() < 1000)
                continue;

            Point[] corners = getBoundingBox(R);

            getEyes(R, cp);

            drawBox(cp, corners);
        }

        new ImagePlus("Final", cp).show();
    }

    int i = 1;
    private void getEyes(RegionLabeling.BinaryRegion R, ColorProcessor cp)
    {
        // Get angle
        double[] centralMoments = getCentralMoments(R);
        double angle = 0.5 * Math.atan2(2 * centralMoments[0], centralMoments[1] - centralMoments[2]);

        Line halfLine = getHalfLine(R, angle);

        double k = (halfLine.y2d - halfLine.y1d) / (halfLine.x2d - halfLine.x1d);
        double d = halfLine.y1d - k * halfLine.x1d;

        ArrayList<Point> left = new ArrayList<>();
        ArrayList<Point> right = new ArrayList<>();

        if (k == Double.POSITIVE_INFINITY || k == Double.NEGATIVE_INFINITY)
        {
            for (Point p: R)
            {
                if (p.x < halfLine.x1)
                    left.add(p);
                else
                    right.add(p);
            }
        }
        else
        {
            for (Point p: R)
            {
                if (p.x < (p.y - d) / k)
                    left.add(p);
                else
                    right.add(p);
            }
        }

        cp.setColor(Color.MAGENTA);
        for (Point p : left)
        {
            cp.drawPixel(p.x, p.y);
        }

        cp.setColor(Color.CYAN);
        for (Point p : right)
        {
            cp.drawPixel(p.x, p.y);
        }

        cp.setColor(Color.yellow);
        cp.drawLine(halfLine.x1, halfLine.y1, halfLine.x2, halfLine.y2);

        int leftEyes = 0;
        int rightEyes = 0;

        List<Contour> contours = R.getInnerContours();
        for (Contour c : contours)
        {
            int l = 0;
            int r = 0;

            for (Point p: c)
            {
                if (left.contains(p))
                    l++;
                else
                    r++;
            }

            if (l == c.getLength())
                leftEyes++;
            else if (r == c.getLength())
                rightEyes++;
        }

        cp.setColor(Color.WHITE);
        cp.drawString(Integer.toString(i));

        IJ.log(i++ + ": " + leftEyes + " " + rightEyes);
    }

    private Line getHalfLine(RegionLabeling.BinaryRegion R, double angle)
    {
        angle -= Math.PI / 2;

        Point center = new Point((int) R.getCenterPoint().getX(), (int) R.getCenterPoint().getY());

        Point start = new Point((int) (center.getX() + B_WIDTH / 2 * Math.cos(angle)),
                (int) (center.getY() +  B_WIDTH / 2 * Math.sin(angle)));

        Point end = new Point((int) (start.getX() + B_WIDTH * Math.cos(angle + Math.PI)),
                (int) (start.getY() +  B_WIDTH * Math.sin(angle + Math.PI)));

        return new Line(start.x, start.y, end.x, end.y);
    }
}

