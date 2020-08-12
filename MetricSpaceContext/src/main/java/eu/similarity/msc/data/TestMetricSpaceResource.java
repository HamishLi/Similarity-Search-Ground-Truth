package eu.similarity.msc.data;

import java.io.IOException;
import java.util.Map;

public class TestMetricSpaceResource {

	@SuppressWarnings("boxing")
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		MetricSpaceResource<Integer, float[]> gist = new MfAlexMetricSpace_old("/Volumes/Data/mf_fc6_full_euc/");
		MetricSpaceResource<Integer, float[]> euc28 = new EucMetricSpace("/Volumes/Data/euc28Gaussian/");
		testResource(euc28);
	}

	public static void getDataInfo(GistMetricSpace gist) throws IOException, ClassNotFoundException {

		Map<Integer, float[]> dat = gist.getData(0);
		for (float[] d1 : dat.values()) {
			System.out.print(d1.length + "\t");
			boolean pos = true;
			float acc = 0;
			float acc2 = 0;
			for (float f : d1) {
				pos &= f >= 0;
				acc += f;
				acc2 += f * f;
			}
			System.out.println((pos ? "pos" : "not pos") + "\t" + acc + "\t" + Math.sqrt(acc2));
		}
//		Map<Integer, Integer[]> nnids = gist.getNNIds();
//		Map<Integer, double[]> thresholds = gist.getThresholds();
//		dat.putAll(gist.getData(5300));
	}

	public static void testAllSpaces() throws ClassNotFoundException, IOException {
		MetricSpaceResource<Integer, float[]> gsd = new SiftMetricSpace("/Volumes/Data/SIFT_mu/");
		testResource(gsd);
		MetricSpaceResource<Integer, float[]> dec = new DecafMetricSpace("/Volumes/Data/profiset/");
		testResource(dec);
		MetricSpaceResource<Integer, float[]> gist = new GistMetricSpace("/Volumes/Data/mf_gist/");
		testResource(gist);
	}

	public static <Index, Representation> void testResource(MetricSpaceResource<Index, Representation> resource)
			throws IOException, ClassNotFoundException {
		System.out.println("testing " + resource.getClass().getName());

		Map<Index, Representation> queries = resource.getQueries();
		System.out.println(queries.size() + " queries");

		Map<Index, double[]> nnThresholds = resource.getThresholds();
		System.out.println(nnThresholds.size() + " nnThreshold arrays, each of size "
				+ nnThresholds.values().iterator().next().length);
		Map<Index, Index[]> nnids = resource.getNNIds();
		System.out.println(nnids.size() + " nnId arrays, each of size " + nnids.values().iterator().next().length);

		Map<Index, Representation> data = resource.getData();
		System.out.println(data.size() + " data");

		for (Index qId : queries.keySet()) {

			final double[] ts = nnThresholds.get(qId);
			final double topThreshold = ts[ts.length - 1];

			System.out.print("query " + qId);
			Index nnId = nnids.get(qId)[1];
			System.out.print(" nn: " + nnId);
			System.out.print(" dist should be: " + nnThresholds.get(qId)[1] + " and less than " + topThreshold);
			double dist = resource.getMetric().distance(queries.get(qId), data.get(nnId));
			System.out.println(" and is " + dist);
		}
	}
}
