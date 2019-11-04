package lab5;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.regions.RegionContourLabeling;
import imagingbook.pub.regions.RegionLabeling;

import java.awt.*;
import java.util.List;

import static lab5.Moment.getCentralMoments;

public class Major_Axes implements PlugInFilter
{
    public int setup(String arg, ImagePlus im)
    {
        return DOES_8G + NO_CHANGES;
    }

    public void run(ImageProcessor ip)
    {
        RegionContourLabeling segmenter = new RegionContourLabeling((ByteProcessor) ip);

        // This returns a list of regions, sorted by size:
        List<RegionLabeling.BinaryRegion> regions = segmenter.getRegions(true);

        ColorProcessor cp = ip.convertToColorProcessor();

        // visit and color the pixels inside of each region:
        for (RegionLabeling.BinaryRegion R : regions)
        {
            double[] centralMoments = getCentralMoments(R);
            // Get angle
            double angle = 0.5 * Math.atan2(2 * centralMoments[0], centralMoments[1] - centralMoments[2]);
            drawMajorAxis(cp, R, angle);
        }

        new ImagePlus("MajorAxis", cp).show();
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
}
