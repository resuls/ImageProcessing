package lab5;

import imagingbook.pub.regions.RegionLabeling;

import java.awt.*;

public class Moment
{
    public static double[] getCentralMoments(RegionLabeling.BinaryRegion R)
    {
        // u11, u20, u02
        double[] centralMoments = new double[3];
        centralMoments[0] = calculateCentralMoments(R, 1, 1);
        centralMoments[1] = calculateCentralMoments(R, 2, 0);
        centralMoments[2] = calculateCentralMoments(R, 0, 2);
        return centralMoments;
    }

    private static int calculateCentralMoments(RegionLabeling.BinaryRegion R,
                                        int p, int q)
    {
        int centralMoment = 0;
        for (Point point : R)
        {
            centralMoment += Math.pow(point.getX() - R.getCenterPoint().getX(), p)
                    * Math.pow(point.getY() - R.getCenterPoint().getY(), q);
        }
        return centralMoment;
    }
}
