package lab5;

import ij.IJ;
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
import static lab5.Moment.getEccentricity;

public class Shape_Classification implements PlugInFilter
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
            double E = getEccentricity(R);

            // nuts
            if (E < 2)
                cp.setColor(Color.RED);
            // bolts
            else
                cp.setColor(Color.blue);

            for (Point p : R)
            {
                cp.drawDot(p.x, p.y);
            }
        }

        new ImagePlus("Shapes", cp).show();
    }
}
