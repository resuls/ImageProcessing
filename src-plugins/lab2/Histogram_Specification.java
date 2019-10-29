package lab2;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

import static lab2.Histogram.*;

public class Histogram_Specification implements PlugInFilter
{
    ImagePlus im = null; // keep a reference to the associated ImagePlus object

    public int setup(String args, ImagePlus im)
    {
        this.im = im;
        return DOES_8G + STACK_REQUIRED;
    }

    public void run(ImageProcessor ignored)
    {
        ImageStack stack = im.getImageStack();
        int K = stack.getSize();

        ImageProcessor ref = stack.getProcessor(K / 2 + 1);
        showHistogram(getCDF(ref.getHistogram()), "Reference");

        for (int k = 1; k <= K; k++)
        { // NOTE: stack slices are numbered from 1,...,K!!
            ImageProcessor target = stack.getProcessor(k);

            matchHistograms(ref, target);
            showHistogram(getCDF(target.getHistogram()), "target_" + k);
        }
    }

    private void matchHistograms(ImageProcessor reference, ImageProcessor target)
    {
        double[] pA = getCDF(target.getHistogram());
        double[] pR = getCDF(reference.getHistogram());
        int[] f = new int[256];

        for (int i = 0; i < 256; i++)
        {
            int j = 255;
            do
            {
                f[i] = j;
                j--;
            } while (j >= 0 && pA[i] <= pR[j]);
        }
        target.applyTable(f);
    }
}
