package eu.similarity.msc.data;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import eu.similarity.msc.core_concepts.Metric;
import eu.similarity.msc.util.ObjectWithDistance;
import eu.similarity.msc.util.Quicksort;

public class MfAlexMetricSpace_old implements MetricSpaceResource<Integer, float[]> {

	@SuppressWarnings({ "boxing", "unused" })
	private static Set<Integer> getPivotIds() {
		Set<Integer> queries = getQueryIds();
		Set<Integer> res = new TreeSet<>();
		// nb Random is seeded so result is deterministic
		Random rand = new Random(0);
		while (res.size() < 1000) {
			int next = rand.nextInt(1000 * 1000);
			if (!queries.contains(next)) {
				res.add(next);
			}
		}
		return res;
	}

	/**
	 * @return the set of query ids
	 */
	@SuppressWarnings("boxing")
	private static Set<Integer> getQueryIds() {
		Set<Integer> res = new TreeSet<>();
		// nb Random is seeded so result is deterministic
		Random rand = new Random(0);
		while (res.size() < 1000) {
			int next = rand.nextInt(1000 * 1000);
			res.add(next);
		}
		return res;
	}

	private String filePath;
	private String dataFilePath;
	private String queryFilePath;
	private String gtFilePath;
	private String objectDataPath;
	private Logger logger;

	@Deprecated
	public MfAlexMetricSpace_old(String filePath) {
		this.logger = Logger.getLogger(this.getClass().getName());
		this.filePath = filePath;
		this.dataFilePath = filePath + "mf_fc6_raw/";
		this.queryFilePath = filePath + "queries.obj";
		this.gtFilePath = filePath + "nnInfo.txt";
		this.objectDataPath = filePath + "extracted/";

		checkExtractedDir();
	}

	@Override
	public Map<Integer, float[]> getData() {
		Map<Integer, float[]> res = new TreeMap<>();
		for (int f = 0; f < 1000; f++) {
			res.putAll(this.getData(f));
		}
		return res;
	}

	/**
	 * return a single data file of 1k values from the object format. If this
	 * doesn't exist, warn the user and create it
	 * 
	 * @param fileNumber the file to fetch, between 0 and 999 inc.
	 * @return the data from this file
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Map<Integer, float[]> getData(int fileNumber) {
		final String fileName = this.objectDataPath + fileNumber + ".obj";

		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));

			try {
				@SuppressWarnings("unchecked")
				Map<Integer, float[]> res = (Map<Integer, float[]>) ois.readObject();
				ois.close();
				return res;
			} catch (ClassNotFoundException e) {
				ois.close();
				this.logger.severe(
						"cannot read data from " + fileName + ", may be corrupted: try deleting and re-running");
				throw new RuntimeException(this.getClass().getName());
			}
		} catch (FileNotFoundException e) {
			this.logger.info("can't find file " + fileName + "; trying to create it");
			writeObjectDataFile(fileNumber);
			return getData(fileNumber);

		} catch (IOException e) {
			this.logger.severe("cannot open " + fileName + " as input stream, please check file permissions");
			throw new RuntimeException(this.getClass().getName());
		}
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
	public Map<Integer, Integer[]> getNNIds() {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.filePath + "gtNNids.obj"));
			@SuppressWarnings("unchecked")
			Map<Integer, int[]> nnids = (Map<Integer, int[]>) ois.readObject();
			ois.close();
			Map<Integer, Integer[]> res = new TreeMap<>();
			for (int nnid : nnids.keySet()) {
				int[] nns = nnids.get(nnid);
				Integer[] x = new Integer[nns.length];
				for (int i = 0; i < nns.length; i++) {
					x[i] = nns[i];
				}
				res.put(nnid, x);
			}
			return res;
		} catch (ClassNotFoundException | IOException e) {
			fatalError(e, "couldn't open nnid file correctly");
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Integer, float[]> getQueries() {

		try {
			FileInputStream fis = null;
			ObjectInputStream ois = null;
			Map<Integer, float[]> res = null;

			fis = new FileInputStream(this.queryFilePath);
			ois = new ObjectInputStream(fis);
			res = (Map<Integer, float[]>) ois.readObject();
			ois.close();

			return res;
		} catch (FileNotFoundException e) {
			this.logger.info("query data file is not present, going to create it");
			this.logger.info("(this may take a while)");
			writeQueries();
			return getQueries();
		} catch (ClassCastException | IOException e) {
			fatalError(e, "couldn't open nnid file correctly");
			return null;
		} catch (ClassNotFoundException e) {
			fatalError(e, "couldn't open nnid file correctly - no class header");
			return null;
		}
	}

	@Override
	public Map<Integer, double[]> getThresholds() {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.filePath + "thesholds.obj"));
			@SuppressWarnings("unchecked")
			Map<Integer, double[]> res = (Map<Integer, double[]>) ois.readObject();
			ois.close();
			return res;
		} catch (FileNotFoundException e) {
			this.logger.info("couldn't open file " + this.filePath + "thesholds.obj");
			this.logger.info("creating new file - may take some time");
			writeThresholdFile();
			return getThresholds();
		} catch (ClassNotFoundException e) {
			fatalError(e, "couldn't open nnid file correctly - no class header");
			return null;
		} catch (IOException e) {
			fatalError(e, "couldn't open nnid file correctly - I/O error");
			return null;
		}

	}

	private void checkExtractedDir() {
		File extracted = new File(this.objectDataPath);
		if (!extracted.exists()) {
			this.logger.info(this.filePath + "extracted/  does not exist; "
					+ (extracted.mkdir() ? " created now" : "unable to create now"));
		}
	}

	private void fatalError(Exception e, String message) {
		this.logger.severe(message);
		throw new RuntimeException(e.toString());
	}

	/**
	 * generate ground truth file, each line is qid followed by repeated nnid
	 * distance in a tab-separated file
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings({ "boxing", "unused" })
	private void setNNinfo() throws IOException, ClassNotFoundException {
		// may have to do this is several batches so don't overwrite existing!
		PrintWriter pw = new PrintWriter(this.gtFilePath.replace(".txt", "X.txt"));
		PrintWriter pw1k = new PrintWriter(this.gtFilePath.replace(".txt", "X1k.txt"));

		Map<Integer, float[]> data = this.getData();
		for (int qid : getQueryIds()) {
			@SuppressWarnings("unchecked")
			ObjectWithDistance<Integer>[] dists = new ObjectWithDistance[data.size()];
			float[] query = data.get(qid);
			int ptr = 0;
			for (int d : data.keySet()) {
				double dist = this.getMetric().distance(query, data.get(d));
				dists[ptr++] = new ObjectWithDistance<>(d, dist);
			}
			Quicksort.placeOrdinal(dists, 1000);
			Quicksort.partitionSort(dists, 0, 1000);

			pw.print(qid);
			pw1k.print(qid);
			for (int i = 0; i < 1000; i++) {
				if (i < 100) {
					pw.print("\t" + dists[i].getValue() + "\t" + dists[i].getDistance());
				}
				pw1k.print("\t" + dists[i].getValue() + "\t" + dists[i].getDistance());
			}
			pw.println();
			pw.flush();
			pw1k.println();
			pw1k.flush();
		}

		pw.close();
		pw1k.close();
	}

	/**
	 * used once to access externally created ground truth files which are not a
	 * part of the public release
	 * 
	 * @param dirName
	 * @param fileext
	 * @param nnidsPerQuery
	 * @throws IOException
	 */
	@SuppressWarnings({ "unused", "boxing" })
	private void writeGroundTruth(String dirName, String fileext, int nnidsPerQuery) throws IOException {
		Map<Integer, int[]> nnids = new TreeMap<>();

		for (int fileNo = 0; fileNo < 1000; fileNo++) {
			Set<Integer> qids = getQueryIds();
			LineNumberReader lnr = new LineNumberReader(
					new FileReader(this.filePath + dirName + "/" + fileNo + "." + fileext));
			for (String line = lnr.readLine(); line != null; line = lnr.readLine()) {
				Scanner s = new Scanner(line);
				int qid = s.nextInt();
				if (qids.contains(qid)) {
					boolean finished = false;
					int[] theseNNs = new int[nnidsPerQuery];
					int ptr = 0;
					while (!finished) {
						try {
							theseNNs[ptr++] = s.nextInt();
							@SuppressWarnings("unused")
							double dist = s.nextDouble();
						} catch (RuntimeException e) {
							// should only be generated if the next thing isn't there to read
							finished = true;
						}
					}
					nnids.put(qid, theseNNs);
				}
				s.close();
			}
			lnr.close();
		}
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.filePath + "gtNNids.obj"));
		oos.writeObject(nnids);
		oos.close();
	}

	@SuppressWarnings("boxing")
	private void writeObjectDataFile(int fileNumber) {
		try {
			final FileReader fr = new FileReader(this.dataFilePath + fileNumber + ".txt");
			LineNumberReader lnr = new LineNumberReader(fr);
			Map<Integer, float[]> hunk = new TreeMap<>();
			try {
				for (String line = lnr.readLine(); line != null; line = lnr.readLine()) {
					Scanner s = new Scanner(line);
					float[] data = new float[4096];
					int id = s.nextInt();
					for (int fl = 0; fl < 4096; fl++) {
						data[fl] = s.nextFloat();
					}
					hunk.put(id, data);
					s.close();
				}
				lnr.close();
			} catch (IOException o) {
				this.logger.severe("can't read from file " + this.objectDataPath + fileNumber + ".obj");
				throw new RuntimeException(this.getClass().getName());
			}

			try {
				ObjectOutputStream oos = new ObjectOutputStream(
						new FileOutputStream(this.objectDataPath + fileNumber + ".obj"));
				oos.writeObject(hunk);
				oos.close();
			} catch (FileNotFoundException e) {
				this.logger.severe("can't open file " + this.objectDataPath + fileNumber + ".obj");
				throw new RuntimeException(this.getClass().getName());
			} catch (IOException e) {
				this.logger.severe("can't write to file " + this.objectDataPath + fileNumber + ".obj");
				throw new RuntimeException(this.getClass().getName());
			}
		} catch (FileNotFoundException e) {
			this.logger
					.severe("can't open file " + this.dataFilePath + fileNumber + ".txt, can't do much without that!");
			throw new RuntimeException(this.getClass().getName());
		}

	}

	@SuppressWarnings("unused")
	private void writeObjectDataFiles() throws IOException {
		for (int i = 0; i < 1000; i++) {
			writeObjectDataFile(i);
		}
	}

	@SuppressWarnings("boxing")
	private Map<Integer, float[]> writeQueries() {
		try {
			Map<Integer, float[]> queries = new TreeMap<>();
			Map<Integer, float[]> data = getData();
			Set<Integer> queryIds = getQueryIds();
			for (int qid : queryIds) {
				queries.put(qid, data.get(qid));
			}

			final FileOutputStream fos = new FileOutputStream(this.queryFilePath);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(queries);
			oos.close();

			return queries;
		} catch (FileNotFoundException e) {
			fatalError(e, "couldn't create file output stream " + this.queryFilePath);
			return null;
		} catch (IOException e) {
			fatalError(e, "I/O error in writing file " + this.queryFilePath);
			return null;
		}

	}

	@SuppressWarnings("boxing")
	private void writeThresholdFile() {
		try {
			Map<Integer, double[]> res = new TreeMap<>();
			Map<Integer, Integer[]> nnids = this.getNNIds();
			Map<Integer, float[]> data = this.getData();
			Map<Integer, float[]> queries = this.getQueries();
			final Metric<float[]> metric = this.getMetric();
			for (int qid : queries.keySet()) {
				float[] query = queries.get(qid);
				Integer[] nns = nnids.get(qid);
				double[] dists = new double[nnids.size()];
				int ptr = 0;
				for (int nn : nns) {
					dists[ptr++] = metric.distance(query, data.get(nn));
				}
				res.put(qid, dists);
			}

			final FileOutputStream fos = new FileOutputStream(this.filePath + "thesholds.obj");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(res);
			oos.close();
		} catch (FileNotFoundException e) {
			fatalError(e, "couldn't create file output stream " + this.filePath + "thesholds.obj");
		} catch (IOException e) {
			fatalError(e, "I/O error in writing file " + this.filePath + "thesholds.obj");
		}
	}

}
