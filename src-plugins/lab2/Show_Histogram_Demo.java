package lab2;

import java.awt.Color;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

/**
 * This ImageJ plugin demonstrates how to normalize and display
 * a histogram in a new image. It also shows how to retrieve the
 * title of the input image (from the associated {@link ImagePlus}
 * instance) and the usage of custom methods.
 * 
 * @author WB
 *
 */
public class Show_Histogram_Demo implements PlugInFilter {
	
	static int HISTOGRAM_HEIGHT = 128;
	
	ImagePlus im = null;	// reference of the ImagePlus instance (to find its title later)
	
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_8G + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		// get the ordinary histogram:
		int[] h = ip.getHistogram();
		
		// calculate the normalized histogram:
		int N = ip.getWidth() * ip.getHeight();
		double[] p = new double[h.length];
		for (int i = 0; i < h.length; i++) {
			p[i] = h[i] / (double) N;
		}
		
		// show the result:
		showHistogram(p, "hist_" + im.getTitle());
	}
	
	void showHistogram(double[] p, String title) {
		int height = HISTOGRAM_HEIGHT;
		double pmax = 0;
		for (int i = 0; i < p.length; i++) {
			pmax = Math.max(pmax,  p[i]);
		}
		ByteProcessor hIp = new ByteProcessor(256, height);	// create a new image
		hIp.setColor(Color.white);
		hIp.fill();
		hIp.setColor(Color.black);
		for (int i = 0; i < p.length; i++) {
			int len = (int) Math.round(height * p[i]/pmax);	// scale the max value to window height
			hIp.drawLine(i, height, i, height - len);
		}
		new ImagePlus(title, hIp).show();	// show the new image on screen
	}

}
