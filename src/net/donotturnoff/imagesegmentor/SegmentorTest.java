package net.donotturnoff.imagesegmentor;

public class SegmentorTest {
	
	public static void main(String[] args) {
		SegmentorTest test = new SegmentorTest();
		test.run();
	}
	
	private Segmentor segmentor;
	private double[][] data;
	private int k;
	private int iterations;
	
	public SegmentorTest() {
		data = new double[][]{
				{11, 10},
				{0, 0},
				{11, 13},
				{0, 1},
				{10, 12},
				{1, 0},
				{14, 12},
				{2, 2},
				{10, 10},
				{0, 2},
				{-10, 2},
				{-11, 3},
				{-12, 2},
		};
		k = 3;
		iterations = 10000;
	}
	
	public void run() {
		segmentor = new Segmentor(data, k, iterations);
		segmentor.segment();
		System.out.println(segmentor);
	}
}
