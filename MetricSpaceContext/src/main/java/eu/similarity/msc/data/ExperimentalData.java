package eu.similarity.msc.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Logger;

import eu.similarity.msc.core_concepts.Metric;
import eu.similarity.msc.data.cartesian.CartesianPoint;
import eu.similarity.msc.local_context.LocalContext;
import eu.similarity.msc.metrics.cartesian.Euclidean;
import eu.similarity.msc.util.CartesianPointFileReader;

public class ExperimentalData {

	public static enum DataSets {
		colors, nasa, euc10, euc20, sift, gist
	}

	private static enum FileType {
		data1k, data10k, pivots, queries, witness, nnInfo
	}

	// https://metric-space-data.appspot.com/dataSamples/colors/queries.dat
	private static String DATA_URL_ROOT = "https://metric-space-data.appspot.com/dataSamples/";
	private final static Logger LOG = Logger.getLogger(ExperimentalData.class.getName());
//	private static double[] colorsThresholds = { 0.052, 0.083, 0.131 };
//	private static double[] nasaThresholds = { 0.12, 0.285, 0.53 };

	private String DATA_DIR_ROOT;

	private Random rand;

	private Logger log;

	private Metric<CartesianPoint> metric;
	private int noOfRefPoints;
	private int noOfWitnessData;

	private DataSets dataset;

	public ExperimentalData(LocalContext lc, DataSets data) {
		this.log = Logger.getLogger(this.getClass().getName());
		this.dataset = data;
		this.DATA_DIR_ROOT = lc.getLocalFileRoot() + "/";
		this.rand = new Random(0);
		this.setMetric(new Euclidean<>());
	}

	public List<CartesianPoint> getData10k() throws Exception {
		checkLocalData(FileType.data10k);
		return new CartesianPointFileReader(this.DATA_DIR_ROOT + this.dataset + "/data10k.dat", false);
	}

	public List<CartesianPoint> getData1k() {
		try {
			checkLocalData(FileType.data1k);
			return new CartesianPointFileReader(this.DATA_DIR_ROOT + this.dataset + "/data1k.dat", false);
		} catch (Exception e) {
			logFatal("can't get file:"+ this.DATA_DIR_ROOT + this.dataset + "/data1k.dat");
			return null;
		}
	}

	private void checkLocalData(FileType type) {
		File f = new File(this.DATA_DIR_ROOT + this.dataset + "/" + type + ".dat");
		if (!f.exists()) {
			logInfo(this.dataset + ":" + type + " is not in local store, fetching copy");
			try {
				copyRemoteTestData(type);
			} catch (Exception e) {
				logFatal("can't create local dataset: " + e.toString());
				throw null;
			}
		}
	}

	public Metric<CartesianPoint> getMetric() {
		return this.metric;
	}

	public double[] getNNinfo(int whichNN) {
		checkLocalData(FileType.nnInfo);
		
		try {
			LineNumberReader lnr = new LineNumberReader(
					new FileReader(this.DATA_DIR_ROOT + this.dataset + "/nnInfo.dat"));
			double[] res = new double[1000];
			int ptr = 0;
			for (String line = lnr.readLine(); line != null; line = lnr.readLine()) {
				Scanner s = new Scanner(line);
				s.nextInt(); // strip id as they're currently all in order from 0
				for (int i = 0; i < whichNN; i++) {
					res[ptr] = s.nextDouble();
				}
				s.close();
				ptr++;
			}

			lnr.close();
			return res;
		} catch (Exception e) {
			logFatal("failed to open data resource " + this.DATA_DIR_ROOT + this.dataset + "/nnInfo.dat");
			throw null;
		}
	}

	public List<CartesianPoint> getPivots() throws Exception {
		checkLocalData(FileType.pivots);
		return new CartesianPointFileReader(this.DATA_DIR_ROOT + this.dataset + "/pivots.dat", false).subList(0,
				this.noOfRefPoints);
	}

	public List<CartesianPoint> getQueries() throws Exception {
		checkLocalData(FileType.queries);
		return new CartesianPointFileReader(this.DATA_DIR_ROOT + this.dataset + "/queries.dat", false);
	}

	public List<CartesianPoint> getWitnesses() throws Exception {
		checkLocalData(FileType.witness);
		return new CartesianPointFileReader(this.DATA_DIR_ROOT + this.dataset + "/witness.dat", false).subList(0,
				this.noOfWitnessData);
	}

	public void setMetric(Metric<CartesianPoint> metric) {
		this.metric = metric;
	}

	private void copyRemoteTestData(FileType type) throws FileNotFoundException {

		File f = new File(this.DATA_DIR_ROOT + this.dataset);
		if (!f.exists()) {
			logInfo("creating new data folder for " + this.dataset);
			f.mkdir();
		}
		switch (type) {
		case data1k: {
			copyRemoteFile("/data1k", "/testdata");
			break;
		}
		case data10k: {
			copyRemoteFile("/data10k", "/bigdata");
			break;
		}
		case nnInfo: {
			copyRemoteFile("/nnInfo", "/nnInfo");
			break;
		}
		case pivots: {
			copyRemoteFile("/pivots", "/pivots");
			break;
		}
		case queries: {
			copyRemoteFile("/queries", "/queries");
			break;
		}
		case witness: {
			copyRemoteFile("/witness", "/witness");
			break;
		}
		}
	}

	private void createTestData(List<CartesianPoint> data, String dataset) throws Exception, FileNotFoundException {

		File f = new File(this.DATA_DIR_ROOT + dataset);
		if (!f.exists()) {
			f.mkdir();
		}

		printDataFile(data, dataset, "/pivots", 3000);
		printDataFile(data, dataset, "/witness", 5000);
		printDataFile(data, dataset, "/data1k", 1000);
		printDataFile(data, dataset, "/queries", 1000);
		printDataFile(data, dataset, "/data10k", 10000);
	}

	private void logFatal(String s) {
		this.log.severe(s);
		throw new RuntimeException("process terminated (" + s + ")");
	}

	private void logInfo(String s) {
		this.log.info(s);
	}

	private void copyRemoteFile(String toFile, String fromFile) throws FileNotFoundException {

		try {
			URL url = new URL(DATA_URL_ROOT + this.dataset + fromFile + ".dat");
			Reader isr = new InputStreamReader(url.openStream());
			LineNumberReader lnr = new LineNumberReader(isr);
			PrintWriter pw = new PrintWriter(this.DATA_DIR_ROOT + this.dataset + toFile + ".dat");

			for (String line = lnr.readLine(); line != null; line = lnr.readLine()) {
				pw.println(line);
			}

			lnr.close();
			pw.close();

		} catch (MalformedURLException e) {
			logFatal("can't construct url: " + e.toString());
		} catch (IOException e) {
			logFatal("can't open remote file: " + e.toString());
		}
	}

	private void printDataFile(List<CartesianPoint> data, final String dataSet, final String fileType, final int number)
			throws FileNotFoundException {

		PrintWriter pw = new PrintWriter(this.DATA_DIR_ROOT + dataSet + fileType + ".dat");

		for (int i = 0; i < number; i++) {
			CartesianPoint p = data.remove(this.rand.nextInt(data.size()));
			for (double d : p.getPoint()) {
				pw.print(d + "\t");
			}
			pw.println();
		}
		pw.close();
	}
}
