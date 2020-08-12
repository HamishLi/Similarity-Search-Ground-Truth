package eu.similarity.msc.data;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * @author newrichard
 * in progress not yet ready for use
 */
public class Mpeg7MetricSpace {

	@SuppressWarnings("boxing")
	static Map<Integer, int[]> getMpeg7(File f) throws IOException {
		Map<Integer, int[]> res = new HashMap<>();
		LineNumberReader fr = new LineNumberReader(new FileReader(f));
		boolean finished = false;
		while (!finished) {
			try {
				int id = getIdFromNextLine(fr);
//				System.out.println("getting " + id);
				fr.readLine();
				int[] data = getDataFromNextLines(fr);
				res.put(id, data);
			} catch (Exception e) {
				System.out.println("finshed: " + e.getMessage());
				finished = true;
			}
		}

		fr.close();
		return res;
	}

	private static int[] getDataFromNextLines(LineNumberReader fr) throws Exception {
		int[] data = new int[282];
		int dim = 0;
		int linesRead = 0;
		while (linesRead < 5) {
//		for (int i = 0; i < 5; i++) {
			Scanner s1 = new Scanner(fr.readLine());
			if (linesRead == 4) {
				if (s1.findInLine("[0-9]\\.[0-9]") != null) {
					s1.close();
					s1 = new Scanner(fr.readLine());
				}
			}
			s1.useDelimiter("[,;]\\s");

			while (s1.hasNextInt()) {
				int n = s1.nextInt();
				data[dim++] = n;
			}

			s1.close();
			linesRead++;
		}
		if (dim < 282) {
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

	static void printLines(File f) throws IOException {
		LineNumberReader fr = new LineNumberReader(new FileReader(f));
		for (int i = 0; i < 1000; i++) {
			System.out.println(fr.readLine());
		}
		fr.close();
	}

	public static void main(String[] args) throws IOException {
//		String fname = "/Volumes/Data/MPEG7_mu/queryset-cophir-1000_sapir2.data";
//		String fname = "/Volumes/Data/MPEG7_mu/pivots-2560-random.data";
		String fname = "/Volumes/Data/MPEG7_mu/1Mdata";

		File f = new File(fname);

//		printLines(f);
		Map<Integer, int[]> data = getMpeg7(f);
		System.out.println(data.size());
		Set<Integer> keys = data.keySet();
		int noDone = 0;
		for (int key : keys) {
			if (noDone < 5) {
				System.out.print(key);
				for (int i : data.get(key)) {
					System.out.print("\t" + i);
				}
				System.out.println();
				noDone++;
			}
		}
	}
}
