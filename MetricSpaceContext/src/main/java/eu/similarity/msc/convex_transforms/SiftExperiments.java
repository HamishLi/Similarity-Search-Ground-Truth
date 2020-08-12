package eu.similarity.msc.convex_transforms;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import eu.similarity.msc.core_concepts.CountedMetric;
import eu.similarity.msc.core_concepts.Metric;
import eu.similarity.msc.data.SiftMetricSpace;
import eu.similarity.msc.search.VPTree;

public class SiftExperiments {

	private static Random rand = new Random(1);

	public static void main(String[] args) throws IOException, ClassNotFoundException {

		final SiftMetricSpace sift = new SiftMetricSpace("/Volumes/Data/SIFT_mu/");

		Map<Integer, Integer[]> siftNNs = sift.getNNIds();
		Map<Integer, float[]> siftQueries = sift.getQueries();
		Map<Integer, float[]> siftData = sift.getData();
		List<float[]> rawData = new ArrayList<>();
		rawData.addAll(siftData.values());
		Map<Integer, double[]> thresholds = sift.getThresholds();

		generateCsvFiles(siftQueries, thresholds, rawData, sift.getMetric());

//		printTikzFigHeader();
//		printNnCoordinates(siftNNs, siftQueries, siftData, sift.getMetric());
//		printTikzFigFooter();
	}

	@SuppressWarnings("boxing")
	private static void generateCsvFiles(Map<Integer, float[]> queries, Map<Integer, double[]> thresholds,
			List<float[]> data, Metric<float[]> metric) throws FileNotFoundException {
		double[] powers = { 1, 1.2, 1.4, 1.6, 1.8, 2, 2.2, 2.4, 2.6, 2.8, 3.0, 3.2, 3.4 };
		int[] results = new int[queries.size()];

		PrintWriter pw = new PrintWriter("/Volumes/Data/nMPTs/siftAll_x.csv");
		pw.println("Pivots,Exp,Recall,Distance Computations");

		for (double power : powers) {
			Metric<float[]> m = getMetric(metric, power);
			CountedMetric<float[]> cm = new CountedMetric<>(m);
			VPTree<float[]> vpt = new VPTree<>(data, cm);
			cm.reset();

			for (int qid : queries.keySet()) {
				double[] queryThresholds = thresholds.get(qid);
				double lastThreshold = Math.pow(queryThresholds[queryThresholds.length - 1], power);
				List<float[]> res = vpt.search(queries.get(qid), lastThreshold);
				if (power == 1) {
					results[qid] = res.size();
				}
				pw.println("," + power + "," + ((float) res.size() / results[qid]) + "," + cm.reset());
				pw.flush();
			}
			pw.println();
		}
		pw.close();
	}

	private static Metric<float[]> getMetric(Metric<float[]> euc, double power) {
		Metric<float[]> m = new Metric<float[]>() {

			@Override
			public double distance(float[] x, float[] y) {
				double d = euc.distance(x, y);
				return Math.pow(d, power);
			}

			@Override
			public String getMetricName() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		return m;
	}

	private static float[] getCoordinates(double d1, double d2, double d3) {
		double d12 = d1 * d1;
		double d22 = d2 * d2;
		double d32 = d3 * d3;
		double cosTheta = (d12 + d32 - d22) / (2 * d1 * d3);
		double theta = Math.acos(cosTheta);
		double y = d3 * Math.sin(theta);
		double x = d1 - Math.sqrt(d32 - (y * y));
		float[] coords = { (float) x, (float) y };
		return coords;
	}

	private static void printNnCoordinates(Map<Integer, Integer[]> nns, Map<Integer, float[]> queries,
			Map<Integer, float[]> data, Metric<float[]> metric) throws IOException {
		int queryId = rand.nextInt(queries.size());
		float[] pivot1 = data.get(rand.nextInt(data.size()));
		float[] pivot2 = data.get(rand.nextInt(data.size()));

		float[] query = queries.get(queryId);
		final Integer[] nnIds = nns.get(queryId);

		double pqDist = metric.distance(pivot1, pivot2);
		double dqP1 = metric.distance(pivot1, query);
		double dqP2 = metric.distance(pivot2, query);
		float[] queryCoordinates = getCoordinates(pqDist, dqP1, dqP2);
		System.out.println("\\draw (0,0) circle (" + 5 + "pt);");
		System.out.println("\\draw (" + pqDist / 10 + ", 0) circle (" + 5 + "pt);");
		printCoordinates(pqDist, dqP1, dqP2, "red", 5);
		double threshold = Double.MIN_VALUE;
		double maxDist = Double.MIN_VALUE;
		for (int nnId : nnIds) {
			float[] nn = data.get(nnId);
			double d2 = metric.distance(pivot1, nn);
			double d3 = metric.distance(pivot2, nn);
			float[] xy = getCoordinates(pqDist, d2, d3);
			maxDist = Math.max(maxDist, metric.distance(queryCoordinates, xy));

			threshold = Math.max(threshold, metric.distance(nn, query));

//			System.out.println(d1 + "\t" + d2 + "\t" + d3);
			printCoordinates(pqDist, d2, d3, "red", 5);
		}
		System.out.println("\n" + "\\draw[color=red] (" + queryCoordinates[0] / 10 + "," + queryCoordinates[1] / 10
				+ ") circle (" + threshold / 10 + ");");
		System.out.println("\n" + "\\draw[dotted] (" + queryCoordinates[0] / 10 + "," + queryCoordinates[1] / 10
				+ ") circle (" + maxDist / 10 + ");");

		printRandomCoordinatesInCircle(data, pivot1, pivot2, queryCoordinates, threshold, metric);
	}

	private static <T> void printRandomCoordinates(T query, Map<Integer, T> data, Metric<T> metric) {

		final int[] array = getRandomIds(100, data.size());

		T viewPoint = data.get(0);
		double d1 = metric.distance(viewPoint, query);
		double acc = 0;
		for (int nn : array) {
			T n = data.get(nn);
			double d2 = metric.distance(viewPoint, n);
			double d3 = metric.distance(query, n);
			acc += d2;

//			System.out.println(d1 + "\t" + d2 + "\t" + d3);
			printCoordinates(d1, d2, d3, "black", 1);
		}
		System.out.println("pq dist: " + d1);
		System.out.println("mean p/s dist: " + acc / 100);
	}

	private static void printCoordinates(double d1, double d2, double d3, String colour, int size) {
		float[] coords = getCoordinates(d1, d2, d3);

		System.out.println("\\draw[color=" + colour + "] (" + (float) coords[0] / 10 + "," + (float) coords[1] / 10
				+ ") circle (" + size + "pt);");

	}

	private static void printRandomCoordinatesInCircle(Map<Integer, float[]> data, float[] pivot1, float[] pivot2,
			float[] queryCoordinates, double threshold, Metric<float[]> metric) {
		int found = 0;
		double pqDist = metric.distance(pivot1, pivot2);
		while (found < 100) {
			float[] next = data.get(rand.nextInt(data.size()));
			double d2 = metric.distance(pivot1, next);
			double d3 = metric.distance(pivot2, next);
			float[] xy = getCoordinates(pqDist, d2, d3);
			if (metric.distance(queryCoordinates, xy) < threshold) {
				printCoordinates(pqDist, d2, d3, "black", 5);
				found++;
			}
		}
	}

	private static int[] getRandomIds(int n, int range) {
		Random r = new Random(0);
		int[] res = new int[n];
		for (int i = 0; i < n; i++) {
			res[i] = r.nextInt(range);
		}
		return res;
	}

	private static void printTikzFigFooter() {
		System.out.println("\n" + "\\end{tikzpicture}\n" + "\\caption{new low dim embedding figure}\n" + "\\label{}\n"
				+ "\\end{center}\n" + "\\end{figure}");
	}

	private static void printTikzFigHeader() {
		System.out.println(
				"\n" + "\\begin{figure}[]\n" + "\\begin{center}\n" + "\n" + "\\begin{tikzpicture}[scale=0.1]\n" + "");
	}

}
