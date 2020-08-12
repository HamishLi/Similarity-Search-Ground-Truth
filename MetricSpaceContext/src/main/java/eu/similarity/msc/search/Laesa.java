package eu.similarity.msc.search;

import java.util.ArrayList;
import java.util.List;

import eu.similarity.msc.core_concepts.Metric;

public class Laesa<T> {

	protected List<T> data;
	protected Metric<T> metric;
	protected List<T> refPoints;
	protected double[][] refDists;

	public Laesa(List<T> data, List<T> refPoints, Metric<T> metric) {
		this.data = data;
		this.metric = metric;
		this.refPoints = refPoints;
		this.refDists = new double[data.size()][refPoints.size()];
		int datPtr = 0;
		for (T dPoint : data) {
			int refPtr = 0;
			for (T rPoint : refPoints) {
				this.refDists[datPtr][refPtr] = this.metric.distance(rPoint, dPoint);
				refPtr++;
			}
			datPtr++;
		}
	}

	public List<T> search(T query, double t) {
		List<T> res = new ArrayList<>();
		double[] qDists = new double[this.refPoints.size()];
		int refPtr = 0;
		for (T rPoint : this.refPoints) {
			final double d = this.metric.distance(rPoint, query);
			qDists[refPtr++] = d;
			if (d <= t) {
				res.add(rPoint);
			}
		}
		int dPtr = 0;
		for (T datum : this.data) {
			if (!canExclude(qDists, this.refDists[dPtr++], t)) {
				if (this.metric.distance(query, datum) <= t) {
					res.add(datum);
				}
			}
		}

		return res;
	}

	@SuppressWarnings("static-method")
	protected boolean canExclude(double[] qDists, double[] rDists, double t) {
		boolean excluded = false;
		int i = 0;
		while (i < qDists.length && !excluded)
			if (!excluded) {
				final double pq = qDists[i];
				final double ps = rDists[i];
				excluded = Math.abs(pq - ps) > t;
				i++;
			}
		return excluded;
	}
}
