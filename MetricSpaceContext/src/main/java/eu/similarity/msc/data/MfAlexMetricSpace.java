package eu.similarity.msc.data;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Logger;

import eu.similarity.msc.core_concepts.Metric;
import eu.similarity.msc.metrics.floats.Euclidean;

public class MfAlexMetricSpace extends IncrementalBuildMetricSpace {

	private String rawDataFilePath;

	public MfAlexMetricSpace(String filePath, String rawDataFilePath) {
		super(filePath);
		this.rawDataFilePath = rawDataFilePath;
	}

	public MfAlexMetricSpace(String filePath) {
		super(filePath);
	}

	@Override
	public Metric<float[]> getMetric() {
		return new Euclidean();
	}

	@Override
	protected Map<Integer, float[]> getRawDataHunk(int fileNumber) {

		Map<Integer, float[]> hunk = new TreeMap<>();
		try {
			final FileReader fr = new FileReader(this.rawDataFilePath + fileNumber + ".txt");
			LineNumberReader lnr = new LineNumberReader(fr);
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
			Logger.getLogger(this.getClass().getName())
					.severe("can't read from file " + this.rawDataFilePath + fileNumber + ".txt");
			throw new RuntimeException(this.getClass().getName());
		}

		return hunk;
	}

}
