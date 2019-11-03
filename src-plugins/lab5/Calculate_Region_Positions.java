package lab5;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.regions.Contour;
import imagingbook.pub.regions.RegionContourLabeling;
import imagingbook.pub.regions.RegionLabeling;

import java.awt.*;
import java.util.List;

/**
 * This plugin assumes as input a binary image with values
 * 0 (for background pixels) and >1 (for foreground pixels).
 * The input image is labeled and all detected regions are
 * processed.
 * It uses the binary region segmentation features provided
 * by the 'imagingbook' library (installed in initial project).
 *
 * @author WB
 */
public class Calculate_Region_Positions implements PlugInFilter
{

    private ImagePlus im;

    public int setup(String arg, ImagePlus im)
    {
        this.im = im;
        return DOES_8G + NO_CHANGES;
    }

    public void run(ImageProcessor ip)
    {
        RegionContourLabeling segmenter = new RegionContourLabeling((ByteProcessor) ip);

        // This returns a list of regions, sorted by size:
        List<RegionLabeling.BinaryRegion> regions = segmenter.getRegions(true);
        IJ.log("Number of regions found: " + regions.size());

        ColorProcessor cp = ip.convertToColorProcessor();

        // visit and color the pixels inside of each region:
        for (RegionLabeling.BinaryRegion R : regions)
        {
            Point centroid = new Point();

            for (Point p : R)
            {
                centroid.x += p.x;
                centroid.y += p.y;
            }

            centroid.x /= R.getSize();
            centroid.y /= R.getSize();

            drawCross(cp, centroid, 5);

            IJ.log("Centroid: " + centroid.getX() + ", " + centroid.getY()
                    + "\nCentral: " + R.getCenterPoint().toString() + "\n");
        }

        new ImagePlus(im.getShortTitle() + "-centroid", cp).show();
    }

    private void drawCross(ColorProcessor cp, Point centroid, int lineSize)
    {
        cp.setColor(Color.RED);

        cp.drawLine(centroid.x - lineSize, centroid.y + lineSize,
                centroid.x + lineSize, centroid.y - lineSize);
        cp.drawLine(centroid.x + lineSize, centroid.y + lineSize,
                centroid.x - lineSize, centroid.y - lineSize);
    }
}