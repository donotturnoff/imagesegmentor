package net.donotturnoff.imagesegmentor;

import java.util.ArrayList;
import java.util.Arrays;

public class Cluster {
	private double[] mean;
	private ArrayList<double[]> data = new ArrayList<>();
	
	public Cluster(double[] mean) {
		this.mean = mean;
	}
	
	public double distanceFrom(double[] datum) {
		double sumOfSquares = 0;
		for (int i = 0; i < mean.length; i++) {
			sumOfSquares += (mean[i]-datum[i])*(mean[i]-datum[i]);
		}
		return Math.sqrt(sumOfSquares);
	}
	
	public void addDatum(double[] datum) {
		data.add(datum);
	}
	
	public void updateMean() {
		mean = new double[mean.length];
		for (double[] datum: data) {
			for (int i = 0; i < mean.length; i++) {
				mean[i] += datum[i];
			}
		}
		for (int i = 0; i < mean.length; i++) {
			mean[i] /= data.size();
		}
	}
	
	public void clearData() {
		data = new ArrayList<>();
	}
	
	public double[] getMean() {
		return mean;
	}
	
	public double[][] getData() {
		double[][] dataArray = new double[data.size()][mean.length];
		return data.toArray(dataArray);
	}
	
	public String toString() {
		return "Cluster[mean=" + Arrays.toString(mean) + ", data=" + Arrays.deepToString(getData()) + "]";
	}
}
