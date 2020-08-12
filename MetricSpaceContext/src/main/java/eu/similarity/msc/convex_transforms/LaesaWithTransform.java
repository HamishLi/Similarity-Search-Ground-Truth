package eu.similarity.msc.convex_transforms;

import java.util.List;
import java.util.function.Function;

import eu.similarity.msc.core_concepts.Metric;
import eu.similarity.msc.search.Laesa;

public class LaesaWithTransform<T> extends Laesa<T> {

	private Function<Double, Double> transform;

	public LaesaWithTransform(List<T> data, List<T> refPoints, Metric<T> metric) {
		super(data, refPoints, metric);
	}

	@SuppressWarnings("boxing")
	@Override
	protected boolean canExclude(double[] qDists, double[] rDists, double t) {
		boolean excluded = false;
		int i = 0;
		while (i < qDists.length && !excluded)
			if (!excluded) {
				final double pq = this.transform.apply(qDists[i]);
				final double ps = this.transform.apply(rDists[i]);
				excluded = Math.abs(pq - ps) > this.transform.apply(t);
				i++;
			}
		return excluded;
	}

	public void setTransform(Function<Double, Double> transform) {
		this.transform = transform;
	}

}
