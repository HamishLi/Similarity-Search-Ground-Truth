package eu.similarity.msc.data;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Logger;

import eu.similarity.msc.core_concepts.Metric;
import eu.similarity.msc.util.GistFileReader;

public class GistMetricSpace implements MetricSpaceResource<Integer, float[]> {

	private String dataFilePath;
	private String queryFilePath;
	private String gtFilePath;
	private String objectDataPath;

	public GistMetricSpace(String filePath) {
		this.dataFilePath = filePath + "data/XXX/YYY.dat";
		this.queryFilePath = filePath + "queries.obj";
		this.gtFilePath = filePath + "groundtruth_jsd.txt";
		this.objectDataPath = filePath + "extracted/";
	}

	@Override
	public Map<Integer, float[]> getData() throws IOException, ClassNotFoundException {
		Map<Integer, float[]> res = new TreeMap<>();
		for (int i = 0; i < 1000; i++) {
			res.putAll(getData(i));
		}
		return res;
	}

	public Map<Integer, float[]> getData(int fileNo) throws IOException, ClassNotFoundException {

		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.objectDataPath + fileNo + ".obj"));
		@SuppressWarnings("unchecked")
		Map<Integer, float[]> data = (Map<Integer, float[]>) ois.readObject();
		ois.close();

		return data;
	}

	/**
	 * returns a list of 100 nearest neighbour ids for each query id
	 */
	@Override
	@SuppressWarnings("boxing")
	public Map<Integer, Integer[]> getNNIds() throws IOException {
		Map<Integer, Integer[]> res = new TreeMap<>();
		LineNumberReader fr = new LineNumberReader(new FileReader(this.gtFilePath));

		boolean finished = false;
		while (!finished) {
			try {
				Scanner s = new Scanner(fr.readLine());
				s.useDelimiter("\\t");
				Integer qid = s.nextInt();
				@SuppressWarnings("unused")
				double dist = s.nextDouble(); // normally 0 but might be epsilon-y
				Integer[] nnids = getNNIdsFromNextLine(s, qid);
				res.put(qid, nnids);
				s.close();
			} catch (Exception e) {
				finished = true;
			}
		}

		fr.close();
		return res;
	}

	@SuppressWarnings("boxing")
	private static Integer[] getNNIdsFromNextLine(Scanner s, int qid) {
		Integer[] res = new Integer[100];
		res[0] = qid;
		for (int i = 1; i < 100; i++) {
			res[i] = s.nextInt();
			@SuppressWarnings("unused")
			double f = s.nextDouble();
		}
		return res;
	}

	private static double[] getThresholdsFromNextLine(Scanner s, double d0) {
		double[] res = new double[100];
		res[0] = d0;
		for (int i = 1; i < 100; i++) {
			@SuppressWarnings("unused")
			int chuck = s.nextInt();
			res[i] = s.nextDouble();
		}
		return res;
	}

	@Override
	public Map<Integer, float[]> getQueries() throws IOException, ClassNotFoundException {

		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.queryFilePath));
		@SuppressWarnings("unchecked")
		Map<Integer, float[]> qs = (Map<Integer, float[]>) ois.readObject();
		ois.close();
		return qs;
	}

	@SuppressWarnings({ "boxing", "unused" })
	private void setQueries() throws IOException, ClassNotFoundException {
		Map<Integer, Integer[]> nnids = this.getNNIds();
		Map<Integer, float[]> queries = new TreeMap<>();
		Map<Integer, float[]> data = this.getData();
		for (int qid : nnids.keySet()) {
			float[] q = data.get(qid);
			queries.put(qid, q);
		}
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.queryFilePath));
		oos.writeObject(queries);
		oos.close();
	}

	@Override
	public Map<Integer, double[]> getThresholds() throws IOException {
		Map<Integer, double[]> res = new TreeMap<>();
		LineNumberReader fr = new LineNumberReader(new FileReader(this.gtFilePath));

		boolean finished = false;
		while (!finished) {
			try {
				Scanner s = new Scanner(fr.readLine());
				s.useDelimiter("\\t");
				Integer qid = s.nextInt();
				@SuppressWarnings("unused")
				double dist = s.nextDouble(); // normally 0 but might be epsilon-y
				double[] nnids = getThresholdsFromNextLine(s, dist);
				res.put(qid, nnids);
				s.close();
			} catch (Exception e) {
				finished = true;
			}
		}

		fr.close();
		return res;
	}

	@SuppressWarnings("boxing")
	public void writeObjectDataFiles() throws IOException {
		for (int i = 0; i < 1000; i++) {
			Map<Integer, float[]> data = new HashMap<>();
			String fb = this.objectDataPath + i + ".obj";
			FileOutputStream fos = new FileOutputStream(fb);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			for (int j = 0; j < 1000; j++) {
				final int fid = i * 1000 + j;
				float[] gist = getGist(fid);

				data.put(fid, l1_normalise(gist));
			}
			oos.writeObject(data);
			oos.close();
		}
	}

	private static float[] l1_normalise(float[] gist) {
		float[] res = new float[gist.length];
		float acc = 0;
		for (float f : gist) {
			acc += f;
		}
		for (int i = 0; i < res.length; i++) {
			res[i] = gist[i] / acc;
		}
		return res;
	}

	private float[] getGist(int fid) throws IOException {
		String f = this.dataFilePath.replace("XXX", "" + fid / 10000).replace("YYY", "" + fid);
		GistFileReader gfr = new GistFileReader(f);
		float[] gist = gfr.getGistValues();
		return gist;
	}

	@Override
	public Metric<float[]> getMetric() {
		/*
		 * data in this context is normalised, but high dim floats so likely to have
		 * rounding errors in this calculation
		 */
		return new Metric<float[]>() {

			@Override
			public double distance(float[] x, float[] y) {

				double accumulator = 0;
				int ptr = 0;
				for (double d1 : x) {
					double d2 = y[ptr++];

					if (d1 != 0 && d2 != 0) {
						accumulator -= xLogx(d1);
						accumulator -= xLogx(d2);
						accumulator += xLogx(d1 + d2);
					}

				}
				/*
				 * allow for rounding errors, if this goes over 1.0 very bad things might
				 * happen!
				 */
				final double acc = Math.min(accumulator / (Math.log(2) * 2), 1);

				final double res = Math.sqrt(1 - acc);
				if (Double.isNaN(res)) {
					Logger.getLogger("jensen shannon in GIST").severe("nan result: acc = " + acc);
					throw new RuntimeException("nan result: acc = " + acc);
				}
				return res;
			}

			private double xLogx(double d) {
				return d * Math.log(d);
			}

			@Override
			public String getMetricName() {
				return "jsd";
			}
		};
	}
}
