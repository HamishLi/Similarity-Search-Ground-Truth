package eu.similarity.msc.convex_transforms;

import java.util.List;
import java.util.function.Function;

import eu.similarity.msc.core_concepts.Metric;
import eu.similarity.msc.data.DataListView;
import eu.similarity.msc.metrics.floats.Euclidean;
import eu.similarity.msc.search.VPTree;

public class VptWithTransform<T> extends VPTree<T> {

	private Function<Double, Double> transform;

	public VptWithTransform(List<T> data, Metric<T> metric) {
		super(data, metric);
	}

	@SuppressWarnings("boxing")
	@Override
	protected boolean canExclude(double a, double b, double c) {
		final Double aa = this.transform.apply(a);
		final Double bb = this.transform.apply(b);
		final Double cc = this.transform.apply(c);
		return bb >= aa + cc;
	}

	public void setTransform(Function<Double, Double> transform) {
		this.transform = transform;
	}

	public double getDistance(float[] x, float[] y) {
		Euclidean euc = new Euclidean();
		double d = euc.distance(x, y);
		return this.transform.apply(d);
	}



}
