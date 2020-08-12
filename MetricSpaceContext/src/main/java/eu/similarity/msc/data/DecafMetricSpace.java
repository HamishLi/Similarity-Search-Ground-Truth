package eu.similarity.msc.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Logger;

import eu.similarity.msc.core_concepts.Metric;

public class DecafMetricSpace implements MetricSpaceResource<Integer, float[]> {

	private static class Pair<Key, Value> {
		Key key;
		Value value;

		Pair(Key k, Value v) {
			this.key = k;
			this.value = v;
		}

		public Key getKey() {
			return this.key;
		}

		public Value getValue() {
			return this.value;
		}
	}

	@SuppressWarnings({ "boxing" })
	private static Pair<Integer, float[]> getPair(String a) {
		float[] vals = new float[4096];
		String[] b = a.split("decaf\\_float\\\"\\:\\[");
		String[] c = b[1].split("\\\"_id\\\"\\:\\\"");
		String[] d = c[1].split("\\\"");

		Scanner s = new Scanner(b[1]);
		s.useDelimiter(",");
		int dim = 0;
		while (s.hasNextFloat()) {
			vals[dim] = s.nextFloat();
			dim++;
		}
		String last = s.next();
		vals[dim] = Float.parseFloat(last.substring(0, last.length() - 1));

		Pair<Integer, float[]> pair = new Pair<>(Integer.parseInt(d[0]), vals);
		s.close();
		return pair;
	}

	private String mainFilePath;
	private String dataFilePath;
	private String queryFilePath;
	private String gtFilePath;

	private String objectDataPath;

	public DecafMetricSpace(String filePath) {
		this.mainFilePath = filePath;
		this.dataFilePath = filePath + "JSON_objects_with_sketches.data";
		this.queryFilePath = filePath + "JSON_objects_with_sketches_queries.data";
		this.gtFilePath = filePath + "groundtruth-profineural-1M-q1000.txt";
		this.objectDataPath = filePath + "extracted/";
	}

	@Override
	public Map<Integer, float[]> getData() throws IOException, ClassNotFoundException {
		return getData(1000);
	}

	public Map<Integer, float[]> getData(int noOfFiles) throws IOException, ClassNotFoundException {
		Map<Integer, float[]> m = new TreeMap<>();
		for (int file = 0; file < noOfFiles; file++) {
			FileInputStream fis = new FileInputStream(this.objectDataPath + file + ".obj");
			ObjectInputStream ois = new ObjectInputStream(fis);

			@SuppressWarnings("unchecked")
			Map<Integer, float[]> m1 = (Map<Integer, float[]>) ois.readObject();
			ois.close();
			m.putAll(m1);
		}
		return m;
	}

	@Override
	public Metric<float[]> getMetric() {
		return new Metric<float[]>() {

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
				return "euc";
			}
		};
	}

	@SuppressWarnings("boxing")
	@Override
	public Map<Integer, Integer[]> getNNIds() throws IOException {
		Map<Integer, Integer[]> res = new TreeMap<>();
		String filename = this.gtFilePath;
		LineNumberReader lnr = new LineNumberReader(new FileReader(filename));

		for (int i = 0; i < 1000; i++) {
			String idLine = lnr.readLine();
			String[] idLineSplit = idLine.split("=");

			String nnLine = lnr.readLine();
			String[] spl = nnLine.split(",");
			Integer[] nnIds = new Integer[spl.length];
			for (int nn = 0; nn < spl.length; nn++) {
				String nnBit = spl[nn]; // each bit should look like: " 49.658: 0000927805" which is distance: id
				String[] flBit = nnBit.split(":");
				// so you can't apply parseInt if there are leading spaces!
				String s = flBit[1];
				while (s.indexOf(' ') == 0) {
					s = s.substring(1);
				}
				nnIds[nn] = Integer.parseInt(s);
			}

			/*
			 * add the query id and the distance to the nnth nearest-neighbour
			 */
			res.put(Integer.parseInt(idLineSplit[1]), nnIds);
		}

		lnr.close();
		return res;
	}

	@Override
	public Map<Integer, float[]> getQueries() throws IOException, ClassNotFoundException {

		LineNumberReader lnr = new LineNumberReader(new FileReader(this.queryFilePath));
		Map<Integer, float[]> res = new TreeMap<>();

		for (String line = lnr.readLine(); line != null; line = lnr.readLine()) {
			Pair<Integer, float[]> pair = getPair(line);
			res.put(pair.getKey(), pair.getValue());
		}
		lnr.close();

		return res;
	}

	@SuppressWarnings("boxing")
	@Override
	public Map<Integer, double[]> getThresholds() throws IOException {
		Map<Integer, double[]> res = new TreeMap<>();
		String filename = this.gtFilePath;
		LineNumberReader lnr = new LineNumberReader(new FileReader(filename));

		for (int i = 0; i < 1000; i++) {
			String idLine = lnr.readLine();
			String[] idLineSplit = idLine.split("=");

			String nnLine = lnr.readLine();
			String[] spl = nnLine.split(",");
			double[] nnDists = new double[spl.length];
			for (int nn = 0; nn < spl.length; nn++) {
				String nnBit = spl[nn]; // each bit should look like: " 49.658: 0000927805" which is distance: id
				String[] flBit = nnBit.split(":");
				nnDists[nn] = Float.parseFloat(flBit[0]);
			}

			/*
			 * add the query id and the distance to the nnth nearest-neighbour
			 */
			res.put(Integer.parseInt(idLineSplit[1]), nnDists);
		}

		lnr.close();
		return res;
	}

	public void writeObjectDataFiles() throws IOException {
		File extracted = new File(this.mainFilePath + "extracted");
		if (extracted.exists()) {
			logFatal("directory" + extracted.getPath() + " already exists, won't overwrite");
		} else {
			extracted.mkdir();
		}
		LineNumberReader lnr = new LineNumberReader(new FileReader(this.dataFilePath));
		writeObjectBatches(lnr);
		lnr.close();
	}

	private void logFatal(String msg) {
		Logger.getLogger(this.getClass().getName()).severe(msg);
		throw new RuntimeException(msg);
	}

	private void writeObjectBatches(LineNumberReader lnr) throws IOException, FileNotFoundException {
		int batch = 0;
		int id = 0;
		ObjectOutputStream oos = null;

		Map<Integer, float[]> vals = null;

		for (String line = lnr.readLine(); line != null; line = lnr.readLine()) {
			if (id % 1000 == 0) {
				if (batch != 0) {
					oos.writeObject(vals);
					oos.close();
					System.out.println("done batch " + (batch - 1));
				}
				FileOutputStream fos = new FileOutputStream(this.objectDataPath + batch + ".obj");
				oos = new ObjectOutputStream(fos);
				vals = new TreeMap<>();
				batch++;
			}
			Pair<Integer, float[]> pair = getPair(line);
			id++;
			vals.put(pair.getKey(), pair.getValue());
		}
		oos.writeObject(vals);
		oos.close(); // only for the last ever printwriter
		System.out.println("done");
	}

}
