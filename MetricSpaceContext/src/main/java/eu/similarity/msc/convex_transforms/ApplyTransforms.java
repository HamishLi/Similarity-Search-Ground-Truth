package eu.similarity.msc.convex_transforms;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import eu.similarity.msc.core_concepts.CountedMetric;
import eu.similarity.msc.data.DataListView;
import eu.similarity.msc.data.DataListView.IdDatumPair;
import eu.similarity.msc.data.EucMetricSpace;
import eu.similarity.msc.data.MetricSpaceResource;
import eu.similarity.msc.data.MfAlexMetricSpace;
import eu.similarity.msc.data.SiftMetricSpace;
import eu.similarity.msc.search.Laesa;

public class ApplyTransforms {

	public static void main(String[] args) throws ClassNotFoundException, IOException {
//		MetricSpaceResource<Integer, float[]> sift = new SiftMetricSpace("/Volumes/Data/SIFT_mu/");
		MetricSpaceResource<Integer, float[]> mfAlex = new MfAlexMetricSpace("Volumes/Data/mf_fc6_full_euc/");
//		MetricSpaceResource<Integer, float[]> euc28 = new EucMetricSpace("/Volumes/Data/euc28/", 28, false);

		runTransformTest(mfAlex, 50);
	}

	@SuppressWarnings("boxing")
	private static void runTransformTest(MetricSpaceResource<Integer, float[]> space, int nn)
			throws IOException, ClassNotFoundException {
		final List<IdDatumPair> data = DataListView.convert(space.getData());
		final List<IdDatumPair> queries = DataListView.convert(space.getQueries());
//		final List<IdDatumPair> referencePoints = DataListView.removeRandom(data, 256);
//		final Map<Integer, Integer[]> nnIds = sift.getNNIds();
		final Map<Integer, double[]> thresholds = space.getThresholds();

		final CountedMetric<IdDatumPair> cm = DataListView.convert(space.getMetric());
//		LaesaWithTransform<IdDatumPair> lae = new LaesaWithTransform<>(data, referencePoints, cm);
		VptWithTransform<IdDatumPair> vpt = new VptWithTransform<>(data, cm);
		cm.reset();

		for (IdDatumPair query : queries) {
			final double[] ts = thresholds.get(query.id);
			final double threshold = ts[nn - 1];

			System.out.print(query.id + "\t" + threshold);
			int trueResults = 0;
			for (float f = 2f; f < 3.5; f += 0.2) {
				final double pow = f;
				vpt.setTransform((x) -> Math.pow(x, pow));
				final List<IdDatumPair> res = vpt.search(query, threshold);
				if (f == 1) {
					trueResults = res.size();
					System.out.print("\t" + trueResults);
				}
				System.out.print("\t" + (float) res.size() / trueResults + "\t" + cm.reset());
			}
			System.out.println();
		}
	}
}
