package net.donotturnoff.imagesegmentor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Segmentor {
	private final double[][] data;
	private final int k;
	private final int iterations;
	private List<Cluster> clusters;
	
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
	
	private List<Integer> shuffleIndices() {
		List<Integer> indexWrappers = new ArrayList<>();
		for (int i = 0; i < data.length; i++) {
			indexWrappers.add(i);
		}
		Collections.shuffle(indexWrappers);
		return indexWrappers;
	}
	
	private void generateClusters() {
		List<Integer> indices = shuffleIndices();
		clusters = new ArrayList<>();
		for (int i = 0; i < k; i++) {
			int index = indices.get(i);
			clusters.add(new Cluster(data[index]));
		}
	}
	
	private void distributeData() {
		for (double[] datum: data) {
			Cluster closest = clusters.get(0);
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
	
	public List<Cluster> getClusters() {
		return clusters;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (Cluster cluster: clusters) {
			result.append(cluster.toString()).append("\n");
		}
		result = new StringBuilder(result.substring(0, result.length() - 1));
		return result.toString();
	}
}
