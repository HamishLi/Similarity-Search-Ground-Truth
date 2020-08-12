package eu.similarity.msc.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import eu.similarity.msc.core_concepts.Metric;

public class SiftMetricSpace implements MetricSpaceResource<Integer, float[]> {

	private static float[] getDataFromNextLine(LineNumberReader fr) throws Exception {
		float[] data = new float[128];
		int dim = 0;
		Scanner s1 = new Scanner(fr.readLine());
		s1.useDelimiter(",");

		while (s1.hasNextFloat()) {
			float n = s1.nextFloat();
			data[dim++] = n;
		}

		s1.close();

		if (dim < 128) {
			throw new Exception(dim + " is not enough data");
		}
		return data;
	}

	private static int getIdFromNextLine(LineNumberReader fr) throws Exception {
		final String line = fr.readLine();
		Scanner s = new Scanner(line);
		if (!"#objectKey".equals(s.next())) {
			s.close();
			if (line != null) {
				System.out.println("last line: " + line);
			}
			throw new Exception("no more data");
		}
		s.next();
		int id = s.nextInt();
		s.close();
		return id;
	}

	@SuppressWarnings("boxing")
	private static Integer[] getNNIdsFromNextLine(String line) {
		Integer[] res = new Integer[100];
		Scanner s = new Scanner(line);
		s.useDelimiter("[:,]\\s");
		for (int i = 0; i < 100; i++) {
			s.nextFloat();
			res[i] = s.nextInt();
		}
		s.close();
		return res;
	}

	private static int getQid(String line) {
		Scanner s = new Scanner(line);
		@SuppressWarnings("unused")
		String x = s.findInLine("\\(Q");
		String y = s.findInLine("[0-9]+");
		s.close();
		return Integer.parseInt(y);
	}

	private static int getQueryIdFromNextLine(LineNumberReader fr) throws Exception {
		final String line = fr.readLine();
		Scanner s = new Scanner(line);
		if (!"#objectKey".equals(s.next())) {
			s.close();
			if (line != null) {
				System.out.println("last line: " + line);
			}
			throw new Exception("no more data");
		}
		s.next();
		String id = s.next();

		s.close();
		if (id.charAt(0) != 'Q') {
			throw new Exception("query id not found");
		}
		return Integer.parseInt(id.substring(1));
	}

	private static double[] getThresholdsFromNextLine(String line) {
		double[] res = new double[100];
		Scanner s = new Scanner(line);
		s.useDelimiter("[:,]\\s");
		for (int i = 0; i < 100; i++) {
			res[i] = s.nextDouble();
			s.nextInt();
		}
		s.close();
		return res;
	}

	private static void writeObjectDataFile(Map<Integer, float[]> data, Iterator<Integer> keyIt, FileOutputStream fos)
			throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		Map<Integer, float[]> m = new TreeMap<>();
		for (int i = 0; i < 1000; i++) {
			final Integer id = keyIt.next();
			float[] f = data.get(id);
			m.put(id, f);
			if (i == 999) {
				System.out.println("writing " + id);
			}
		}
		oos.writeObject(m);
	}

	@SuppressWarnings("boxing")
	static Map<Integer, float[]> getSiftTextData(File f, boolean queryFile) throws IOException {
		Map<Integer, float[]> res = new TreeMap<>();
		LineNumberReader fr = new LineNumberReader(new FileReader(f));
		boolean finished = false;
		while (!finished) {
			try {
				int id = queryFile ? getQueryIdFromNextLine(fr) : getIdFromNextLine(fr);
				if (id % 1000 == 0) {
					// System.out.println("getting " + id);
				}

				float[] data = getDataFromNextLine(fr);
				res.put(id, data);
			} catch (Exception e) {
				finished = true;
			}
		}

		fr.close();
		return res;
	}

	private String dataFilePath;
	private String queryFilePath;
	private String gtFilePath;
	private String objectDataPath;

	public SiftMetricSpace(String filePath) {
		this.dataFilePath = filePath + "sift-1M.data";
		this.queryFilePath = filePath + "queryset-sift-1000.data";
		this.gtFilePath = filePath + "ground-truth-1000000_my.txt";
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

	/**
	 * returns a list of 100 nearest neighbour ids for each query id
	 */
	@Override
	@SuppressWarnings("boxing")
	public Map<Integer, Integer[]> getNNIds() throws IOException {
		Map<Integer, Integer[]> res = new TreeMap<>();
		LineNumberReader fr = new LineNumberReader(new FileReader(this.gtFilePath));
		fr.readLine();
		fr.readLine();

		boolean finished = false;
		while (!finished) {
			try {
				fr.readLine();
				int qid = getQid(fr.readLine());
				Integer[] nnids = getNNIdsFromNextLine(fr.readLine());
				res.put(qid, nnids);
			} catch (Exception e) {
				finished = true;
			}
		}

		fr.close();
		return res;
	}

	@Override
	public Map<Integer, float[]> getQueries() throws IOException, ClassNotFoundException {
		File f = new File(this.queryFilePath);
		return getSiftTextData(f, true);
	}

	@SuppressWarnings("boxing")
	@Override
	public Map<Integer, double[]> getThresholds() throws IOException {

		Map<Integer, double[]> res = new TreeMap<>();
		LineNumberReader fr = new LineNumberReader(new FileReader(this.gtFilePath));
		fr.readLine();// throw out two header lines
		fr.readLine();

		boolean finished = false;
		while (!finished) {
			try {
				fr.readLine(); // throw out first of every three lines
				int qid = getQid(fr.readLine());

				double[] thresh = getThresholdsFromNextLine(fr.readLine());

				res.put(qid, thresh);
			} catch (Exception e) {
				finished = true;
			}
		}
		fr.close();
		if (res.size() != 1000) {
//			throw new RuntimeException("no threshold file");
		}

		return res;
	}

	public void writeObjectDataFiles() throws IOException {
		File f = new File(this.dataFilePath);
		if (f.exists()) {
			//
		} else {
			f.mkdir();
		}
		Map<Integer, float[]> data = getSiftTextData(f, false);
		Iterator<Integer> keyIt = data.keySet().iterator();
		for (int file = 0; file < 1000; file++) {
			FileOutputStream fos = new FileOutputStream(this.objectDataPath + file + ".objX");
			writeObjectDataFile(data, keyIt, fos);
			fos.close();
		}
	}
}
