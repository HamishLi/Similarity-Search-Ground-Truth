package eu.similarity.msc.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GistFileReader {

	public static final int N_SCALES = 6;
	public static final int N_ORIENTATIONS = 5;
	public static final int N_WINDOWS = 4;
	public static final int BYTES_PER_FLOAT = 4;

	public static final int GIST_SIZE = N_SCALES * N_ORIENTATIONS * N_WINDOWS * N_WINDOWS;

	public static final int GIST_SIZE_IN_BYTES = GIST_SIZE * BYTES_PER_FLOAT;

	protected static final int BUFFER_SIZE = 2048;

	private float[] gist_values;
	private double[] gist_doubles;

	public GistFileReader(String filename) throws IOException {
		FileInputStream in = new FileInputStream(filename);
		readData(in, false);
		in.close();
	} // GistFileReader

	public float[] getGistValues() {
		return this.gist_values;
	}

	private void readData(FileInputStream in, boolean normalised) throws IOException {
		byte[] bytes = new byte[BUFFER_SIZE];

		int nBytes = in.read(bytes);

		if (nBytes != GIST_SIZE_IN_BYTES) {
			throw new IOException("Wrong number of bytes");
		}

		parseBytes(bytes, normalised);
	}

	protected void parseBytes(byte[] bytes, boolean normalised) {
		this.gist_values = new float[GIST_SIZE];
		this.gist_doubles = new double[GIST_SIZE];

		ByteBuffer buf = ByteBuffer.wrap(bytes);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		int gist_idx = 0;

		for (int i = 0; i < GIST_SIZE_IN_BYTES; i += 4) {
			final float val = buf.getFloat(i);
			this.gist_values[gist_idx] = val;
			this.gist_doubles[gist_idx] = val;

			gist_idx++;
		}
	}
}
