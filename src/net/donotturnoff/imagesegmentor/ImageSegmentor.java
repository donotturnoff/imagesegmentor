package net.donotturnoff.imagesegmentor;

import java.awt.Color;
import java.awt.image.*;
import java.util.HashMap;

public class ImageSegmentor {
	
	private Segmentor segmentor;
	private final BufferedImage in;
	private BufferedImage out;
	private HashMap<double[], double[]> map;
	private double[][] data;
	private final int k;
	private final int iterations;
	
	public ImageSegmentor(BufferedImage in, int k, int iterations) {
		this.in = in;
		this.k = k;
		this.iterations = iterations;
	}
	
	public void parse() {
		final int w = in.getWidth();
		final int h = in.getHeight();
		data = new double[w*h][3];

		final byte[] pixels = ((DataBufferByte) in.getRaster().getDataBuffer()).getData();
		final boolean hasAlphaChannel = in.getAlphaRaster() != null;
		
		final int pixelLength = (hasAlphaChannel) ? 4 : 3;
		for (int pixel = 0, i = 0; pixel < pixels.length; pixel += pixelLength) {
			data[i][2] = (int) pixels[pixel + pixelLength - 3] & 0xff; // blue
			data[i][1] = (int) pixels[pixel + pixelLength - 2] & 0xff; // green
			data[i][0] = (int) pixels[pixel + pixelLength - 1] & 0xff; // red
			i++;
		}
	}
	
	public void segment() {
		segmentor = new Segmentor(data, k, iterations);
		segmentor.segment();
	}
	
	public void map() {
		Cluster[] clusters = segmentor.getClusters();
		map = new HashMap<>();
		for (Cluster cluster : clusters) {
			double[] mean = cluster.getMean();
			for (double[] datum : cluster.getData()) {
				map.put(datum, mean);
			}
		}
	}
	
	public void update() {
		final int w = in.getWidth();
		final int h = in.getHeight();
		
		int[] outPixels = new int[w*h];
		
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				double[] datum = data[y*w + x];
				double[] mean = map.get(datum);
				int mr = (int) mean[0];
				int mg = (int) mean[1];
				int mb = (int) mean[2];
				outPixels[y*w+x] = new Color(mr, mg, mb).getRGB();
			}
		}
		
		out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB );
		final int[] a = ((DataBufferInt) out.getRaster().getDataBuffer() ).getData();
		System.arraycopy(outPixels, 0, a, 0, data.length);
	}
	
	public BufferedImage getOutput() {
		return out;
	}
}
