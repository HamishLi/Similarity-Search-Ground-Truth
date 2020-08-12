package eu.similarity.msc.data;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import eu.similarity.msc.core_concepts.Metric;
import eu.similarity.msc.metrics.floats.Euclidean;

public class EucMetricSpace extends IncrementalBuildMetricSpace {

	private int dimension;
	private boolean gaussian;

	public EucMetricSpace(String filePath, int dimension, boolean gaussian) {
		super(filePath);
		this.dimension = dimension;
		this.gaussian = gaussian;
	}
	
	public EucMetricSpace(String filePath) {
		super(filePath);
	}

	@Override
	public Metric<float[]> getMetric() {
		return new Euclidean();
	}

	@SuppressWarnings("boxing")
	@Override
	protected Map<Integer, float[]> getRawDataHunk(int fileNumber) {
		Random rand = new Random(fileNumber);
		Map<Integer, float[]> res = new TreeMap<>();

		for (int i = 0; i < 1000; i++) {
			float[] fs = new float[this.dimension];
			for (int dim = 0; dim < this.dimension; dim++) {
				fs[dim] = (float) (this.gaussian ? rand.nextGaussian() : rand.nextFloat());
			}
			res.put(fileNumber * 1000 + i, fs);
		}
		return res;
	}

}
