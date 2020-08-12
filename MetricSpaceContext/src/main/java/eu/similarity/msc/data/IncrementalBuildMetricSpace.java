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
import java.io.PrintWriter;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

import eu.similarity.msc.core_concepts.Metric;
import eu.similarity.msc.util.ObjectWithDistance;
import eu.similarity.msc.util.Quicksort;

public abstract class IncrementalBuildMetricSpace implements MetricSpaceResource<Integer, float[]> {

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
	private String queryFilePath;
	private String gtFilePath;
	private String objectDataPath;
	private Logger logger;

	public IncrementalBuildMetricSpace(String filePath) {
		this.logger = Logger.getLogger(this.getClass().getName());
		this.filePath = filePath;
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
	public abstract Metric<float[]> getMetric();

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
			this.logger.info("can't find file " + this.filePath + "gtNNids.obj; trying to create it");
			createNNidsFile();
			return getNNIds();
		}
	}

	@SuppressWarnings("boxing")
	private void createNNidsFile() {
		Map<Integer, float[]> data = this.getData();
		Map<Integer, float[]> queries = this.getQueries();
		final Metric<float[]> metric = this.getMetric();
		Map<Integer, int[]> nnids = new TreeMap<>();
		for (int query : queries.keySet()) {
			@SuppressWarnings("unchecked")
			ObjectWithDistance<Integer>[] dists = new ObjectWithDistance[data.size()];
			int ptr = 0;
			for (int datum : data.keySet()) {
				final double distance = metric.distance(queries.get(query), data.get(datum));
				dists[ptr++] = new ObjectWithDistance<>(datum, distance);
			}

			Quicksort.placeOrdinal(dists, 100);
			Quicksort.partitionSort(dists, 0, 100);
			int[] newNnids = new int[100];
			for (int i = 0; i < 100; i++) {
				newNnids[i] = dists[i].getValue();
			}
			nnids.put(query, newNnids);
		}

		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.filePath + "gtNNids.obj"));
			oos.writeObject(nnids);
			oos.close();
		} catch (FileNotFoundException e) {
			fatalError(e, "couldn't create file: " + this.filePath + "gtNNids.obj");
		} catch (IOException e) {
			fatalError(e, "couldn't write to file: " + this.filePath + "gtNNids.obj");
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
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.filePath + "thresholds.obj"));
			@SuppressWarnings("unchecked")
			Map<Integer, double[]> res = (Map<Integer, double[]>) ois.readObject();
			ois.close();
			return res;
		} catch (FileNotFoundException e) {
			this.logger.info("couldn't open file " + this.filePath + "thresholds.obj");
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

	private void writeObjectDataFile(int fileNumber) {
		Map<Integer, float[]> hunk = getRawDataHunk(fileNumber);

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
				double[] dists = new double[100];
				int ptr = 0;
				for (int nn : nns) {
					dists[ptr++] = metric.distance(query, data.get(nn));
				}
				res.put(qid, dists);
			}

			final FileOutputStream fos = new FileOutputStream(this.filePath + "thresholds.obj");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(res);
			oos.close();
		} catch (FileNotFoundException e) {
			fatalError(e, "couldn't create file output stream " + this.filePath + "thresholds.obj");
		} catch (IOException e) {
			fatalError(e, "I/O error in writing file " + this.filePath + "thresholds.obj");
		}
	}

	protected abstract Map<Integer, float[]> getRawDataHunk(int fileNumber);

}
