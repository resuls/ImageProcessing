package lab2;

import ij.ImagePlus;
import ij.process.ByteProcessor;

import java.awt.*;

public class Histogram
{
    private static final int HISTOGRAM_HEIGHT = 128;

    public static int[] getCumulativeHistogram(int[] h)
    {
        int[] H = new int[256];
//        for (int i = 0; i < 256; i++)
//            for (int j = 0; j <= i; j++)
//                H[i] += h[j];

        H[0] = h[0];
        for (int i = 1; i < 256; i++)
            H[i] += H[i - 1] + h[i];

        return H;
    }

    public static double[] getCDF(int[] h)
    {
        int[] H = getCumulativeHistogram(h);

        double N = H[255] * 1.0;
        double[] cdf = new double[256];
        for (int i = 0; i < 256; i++)
            cdf[i] = H[i] / N;

        return cdf;
    }

    public static void showHistogram(int[] p, String title)
    {
        int height = HISTOGRAM_HEIGHT;
        double pmax = 0;
        for (int value : p)
        {
            pmax = Math.max(pmax, value);
        }
        ByteProcessor hIp = new ByteProcessor(256, height);    // create a new image
        hIp.setColor(Color.white);
        hIp.fill();
        hIp.setColor(Color.black);
        for (int i = 0; i < p.length; i++)
        {
            int len = (int) Math.round(height * p[i] / pmax);    // scale the max value to window height
            hIp.drawLine(i, height, i, height - len);
        }
        new ImagePlus(title, hIp).show();    // show the new image on screen
    }

    public static void showHistogram(double[] p, String title)
    {
        int height = HISTOGRAM_HEIGHT;
        double pmax = 0;
        for (double value : p)
        {
            pmax = Math.max(pmax, value);
        }
        ByteProcessor hIp = new ByteProcessor(256, height);    // create a new image
        hIp.setColor(Color.white);
        hIp.fill();
        hIp.setColor(Color.black);
        for (int i = 0; i < p.length; i++)
        {
            int len = (int) Math.round(height * p[i] / pmax);    // scale the max value to window height
            hIp.drawLine(i, height, i, height - len);
        }
        new ImagePlus(title, hIp).show();    // show the new image on screen
    }
}
