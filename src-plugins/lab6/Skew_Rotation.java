package lab6;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.morphology.BinaryMorphologyFilter;
import imagingbook.pub.regions.RegionContourLabeling;
import imagingbook.pub.regions.RegionLabeling;
import imagingbook.pub.threshold.global.OtsuThresholder;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static lab5.Moment.getCentralMoments;
import static lab5.Moment.getEccentricity;
import static lab6.Outlier.eliminateOutliers;

public class Skew_Rotation implements PlugInFilter
{
    public int setup(String arg, ImagePlus im)
    {
        return DOES_8G + DOES_RGB + NO_CHANGES;
    }

    public void run(ImageProcessor ip)
    {
        // Threshold
        ByteProcessor bp = ip.convertToByteProcessor();
        new OtsuThresholder().threshold(bp);
        bp.invert();
        new ImagePlus("Threshold", bp.duplicate()).show();

        // Dilate
        BinaryMorphologyFilter filter = new BinaryMorphologyFilter.Disk(3);
        filter.applyTo(bp, BinaryMorphologyFilter.OpType.Dilate);
        new ImagePlus("Dilate", bp.duplicate()).show();


        RegionContourLabeling segmenter = new RegionContourLabeling((ByteProcessor) bp);
        List<RegionLabeling.BinaryRegion> regions = segmenter.getRegions(true);

        ColorProcessor cp = bp.convertToColorProcessor();
        // visit and color the pixels inside of each region:
        cp.setColor(Color.RED);
        ArrayList<Double> angles = new ArrayList<>();
        for (RegionLabeling.BinaryRegion R : regions)
        {
            double[] centralMoments = getCentralMoments(R);
            double angle = 0.5 * Math.atan2(2 * centralMoments[0], centralMoments[1] - centralMoments[2]);
            angles.add(angle);

            double E = getEccentricity(R);
            if (E > 2)
            {
                cp.setColor(Color.GRAY);
                for (Point p : R)
                {
                    cp.drawDot(p.x, p.y);
                }
                drawMajorAxis(cp, R, angle);
            }
        }
        new ImagePlus("With Outliers", cp).show();

        cp = bp.convertToColorProcessor();
        angles = eliminateOutliers(angles, 2);
        double averageSkew = getGlobalSkewMean(angles);
        double degrees = -averageSkew * 180 / Math.PI;
        IJ.log(degrees + "");

        double max = -1;
        double min = 9999;

        for (double a : angles)
        {
            if (a > max)
                max = a;
            if (a < min)
                min = a;
        }

        for (RegionLabeling.BinaryRegion R : regions)
        {
            double[] centralMoments = getCentralMoments(R);
            double angle = 0.5 * Math.atan2(2 * centralMoments[0], centralMoments[1] - centralMoments[2]);

            if (angle < max && angle > min)
                drawMajorAxis(cp, R, angle);
        }
        new ImagePlus("Without Outliers", cp).show();

        cp = ip.convertToColorProcessor();
        cp.rotate(degrees);

        new ImagePlus("Rotated", cp).show();
    }

    private void drawMajorAxis(ColorProcessor cp, RegionLabeling.BinaryRegion R, double angle)
    {
        final int lineSize = 50 ;
        final int diameter = 10;
        final int hdiameter = diameter / 2;
        cp.setColor(Color.RED);
        Point start = new Point((int) R.getCenterPoint().getX(),(int) R.getCenterPoint().getY());
        Point end = new Point((int) (start.getX() + lineSize * Math.cos(angle)),
                (int) (start.getY() + lineSize * Math.sin(angle)));

        cp.drawLine(start.x, start.y, end.x, end.y);
        cp.drawOval(end.x - hdiameter, end.y - hdiameter, diameter, diameter);
    }

    private double getGlobalSkewMean(ArrayList<Double> angles)
    {
        double cos = 0;
        double sin = 0;
        for (double angle : angles)
        {
            cos += Math.cos(angle);
            sin += Math.sin(angle);
        }

        cos /= angles.size();
        sin /= angles.size();

        return Math.atan2(sin, cos);
    }
}
