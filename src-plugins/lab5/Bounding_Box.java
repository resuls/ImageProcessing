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

public class Bounding_Box implements PlugInFilter
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
            Point[] box = getBoundingBox(R);
            drawBox(cp, box);
        }

        new ImagePlus("Bounding_box", cp).show();
    }

    private Point[] getBoundingBox(RegionLabeling.BinaryRegion R)
    {
        double a_min, a_max, b_min, b_max;

        double[] centralMoments = getCentralMoments(R);
        double angle = 0.5 * Math.atan2(2 * centralMoments[0], centralMoments[1] - centralMoments[2]);

        double[] ea = {Math.cos(angle), Math.sin(angle)};
        double[] eb = {Math.sin(angle), -Math.cos(angle)};

        a_min = b_min = Integer.MAX_VALUE;
        a_max = b_max = Integer.MIN_VALUE;

        for (Point p : R)
        {
            double a = p.x * ea[0] + p.y * ea[1];
            a_min = Math.min(a_min, a);
            a_max = Math.max(a_max, a);

            double b = p.x * eb[0] + p.y * eb[1];
            b_min = Math.min(b_min, b);
            b_max = Math.max(b_max, b);
        }

        // A B C D
        Point[] res = new Point[4];
        res[0] = addVectors(multiplyVectorTo(ea, a_min), multiplyVectorTo(eb, b_min));
        res[1] = addVectors(multiplyVectorTo(ea, a_min), multiplyVectorTo(eb, b_max));
        res[2] = addVectors(multiplyVectorTo(ea, a_max), multiplyVectorTo(eb, b_max));
        res[3] = addVectors(multiplyVectorTo(ea, a_max), multiplyVectorTo(eb, b_min));

        return res;
    }

    private Point multiplyVectorTo(double[] vector, double c)
    {
        return new Point((int) (vector[0] * c), (int) (vector[1] * c));
    }

    private Point addVectors(Point v1, Point v2)
    {
        return new Point(v1.x + v2.x, v1.y + v2.y);
    }

    private void drawBox(ColorProcessor cp, Point[] b)
    {
        cp.setColor(Color.RED);
        for (int i = 0; i < 4; i++)
        {
            cp.drawLine(b[i].x, b[i].y, b[(i + 1) % 4].x, b[(i + 1) % 4].y);
        }
    }
}
