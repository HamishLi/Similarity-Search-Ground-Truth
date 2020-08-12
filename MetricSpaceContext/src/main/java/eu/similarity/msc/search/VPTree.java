package eu.similarity.msc.search;

import java.util.ArrayList;
import java.util.List;

import eu.similarity.msc.core_concepts.Metric;
import eu.similarity.msc.util.ObjectWithDistance;
import eu.similarity.msc.util.Quicksort;

public class VPTree<T> {

	public final class VPTreeNode<T1> {
		private T1 pivot;
		private double mu;
		private VPTreeNode<T1> left;
		private VPTreeNode<T1> right;

		VPTreeNode(List<T1> data, Metric<T1> metric) {
			if (data.size() == 0) {
				// do nothing
			} else if (data.size() == 1) {
				this.pivot = data.get(0);
			} else {
				this.pivot = data.get(0);
				@SuppressWarnings("unchecked")
				ObjectWithDistance<T1>[] objs = new ObjectWithDistance[data.size() - 1];
				int ptr = 0;
				for (T1 datum : data.subList(1, data.size())) {
					objs[ptr++] = new ObjectWithDistance<>(datum, metric.distance(this.pivot, datum));
				}
				Quicksort.placeMedian(objs);

				List<T1> leftList = new ArrayList<>();
				for (int i = 0; i < objs.length / 2; i++) {
					leftList.add(objs[i].getValue());
				}
				this.mu = objs[objs.length / 2].getDistance();
				List<T1> rightList = new ArrayList<>();
				for (int i = objs.length / 2; i < objs.length; i++) {
					rightList.add(objs[i].getValue());
				}
				if (leftList.size() > 0) {
					this.left = new VPTreeNode<>(leftList, metric);
				}
				if (rightList.size() > 0) {
					this.right = new VPTreeNode<>(rightList, metric);
				}
			}
		}

		public void search(T1 query, double t, List<T1> results, Metric<T1> metric) {
			double pq = metric.distance(this.pivot, query);
			if (pq <= t) {
				results.add(this.pivot);
			}
			if (this.left != null && !canExclude(t, pq, this.mu)) {
				this.left.search(query, t, results, metric);
			}
			if (this.right != null && !canExclude(pq, this.mu, t)) {
				this.right.search(query, t, results, metric);
			}
		}

	}

	@SuppressWarnings("static-method")
	protected boolean canExclude(double a, double b, double c) {
		return b >= c + a;
	}

	private VPTreeNode<T> index;
	private Metric<T> metric;

	public VPTree(List<T> data, Metric<T> metric) {
		this.metric = metric;
		this.index = new VPTreeNode<>(data, metric);
	}

	public List<T> search(T query, double threshold) {
		List<T> res = new ArrayList<>();
		this.index.search(query, threshold, res, this.metric);
		return res;
	}

}
