package eu.similarity.msc.metrics.floats;

import eu.similarity.msc.core_concepts.Metric;

public class Euclidean implements Metric<float[]> {

	@Override
	public double distance(float[] x, float[] y) {
		double acc = 0;
		for (int i = 0; i < x.length; i++) {
			double diff = x[i] - y[i];
			acc += diff * diff;
		}
		return Math.sqrt(acc);
	}

	@Override
	public String getMetricName() {
		return "Euc";
	}

}
