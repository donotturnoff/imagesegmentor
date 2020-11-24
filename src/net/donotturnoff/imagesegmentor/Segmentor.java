package net.donotturnoff.imagesegmentor;

import java.util.ArrayList;
import java.util.Collections;

public class Segmentor {
	private double[][] data;
	private int k;
	private int iterations;
	private Cluster[] clusters;
	
	public Segmentor(double[][] data, int k, int iterations) throws IllegalArgumentException {
		if (k > data.length) {
			throw new IllegalArgumentException("Cannot split " + data.length + " data points into " + k + " clusters");
		} else {
			this.data = data;
			this.k = k;
			this.iterations = iterations;
		}
	}
	
	public void segment() {
		generateClusters();
		
		for (int i = 0; i < iterations; i++) {
			clearDataFromClusters();
			distributeData();
			updateClusterMeans();
		}
	}
	
	private int[] shuffleIndices() {
		ArrayList<Integer> indexWrappers = new ArrayList<Integer>();
		for (int i = 0; i < data.length; i++) {
			indexWrappers.add(new Integer(i));
		}
		Collections.shuffle(indexWrappers);
		int[] indices = new int[indexWrappers.size()];
		for (int i = 0; i < data.length; i++) {
			indices[i] = indexWrappers.get(i);
		}
		return indices;
	}
	
	private void generateClusters() {
		int[] indices = shuffleIndices();
		clusters = new Cluster[k];
		for (int i = 0; i < k; i++) {
			int index = indices[i];
			clusters[i] = new Cluster(data[index]);
		}
	}
	
	private void distributeData() {
		for (double[] datum: data) {
			Cluster closest = clusters[0];
			for (Cluster cluster: clusters) {
				if (cluster.distanceFrom(datum) < closest.distanceFrom(datum)) {
					closest = cluster;
				}
			}
			closest.addDatum(datum);
		}
	}
	
	private void updateClusterMeans() {
		for (Cluster cluster: clusters) {
			cluster.updateMean();
		}
	}
	
	private void clearDataFromClusters() {
		for (Cluster cluster: clusters) {
			cluster.clearData();
		}
	}
	
	public Cluster[] getClusters() {
		return clusters;
	}
	
	public String toString() {
		String result = "";
		for (Cluster cluster: clusters) {
			result += cluster.toString() + "\n";
		}
		result = result.substring(0, result.length() - 1);
		return result;
	}
}
