//package eu.similarity.msc.convex_transforms;
//
///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.io.LineNumberReader;
//import java.io.PrintWriter;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.NoSuchElementException;
//import java.util.Scanner;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import eu.similarity.msc.core_concepts.CountedMetric;
//import eu.similarity.msc.core_concepts.Metric;
//import eu.similarity.msc.search.VPTree;
//import messif.objects.impl.MetaObjectSAPIRWeightedDist3;
//import messif.objects.util.AbstractObjectList;
//import messif.objects.util.StreamGenericAbstractObjectIterator;
//
///**
// *
// * @author xmic
// */
//public class Mpeg7Brno2 {
//
//	/**
//	 *
//	 * @param filePath path to the file with the MPEG7 descriptors in a messif
//	 *                 textual format
//	 * @param count    number of descriptors to read. If negative, all objects from
//	 *                 the file are loaded.
//	 * @return list of MPEG7 descriptors
//	 */
//	public static List<MetaObjectSAPIRWeightedDist3> getMPEG7DescriptorsAsList(String filePath, int count) {
//		Iterator<MetaObjectSAPIRWeightedDist3> it = getIterator(MetaObjectSAPIRWeightedDist3.class, filePath);
//		return new AbstractObjectList<>(it, count);
//	}
//
//	/**
//	 * Example how to evaluate the distance between two MPEG7 descriptors.
//	 *
//	 * @param desc1 first MPEG7 descriptor
//	 * @param desc2 sescond MPEG7 descriptor
//	 * @return their distance
//	 */
//	public static float getDistanceOfTwoMPEG7Descriprtors(MetaObjectSAPIRWeightedDist3 desc1,
//			MetaObjectSAPIRWeightedDist3 desc2) {
//		return desc1.getDistance(desc2);
//	}
//
//	private static Metric<MetaObjectSAPIRWeightedDist3> getMetric(float power) {
//		return new Metric<MetaObjectSAPIRWeightedDist3>() {
//
//			@Override
//			public double distance(MetaObjectSAPIRWeightedDist3 x, MetaObjectSAPIRWeightedDist3 y) {
//				return Math.pow(x.getDistance(y), power);
//			}
//
//			@Override
//			public String getMetricName() {
//				return "MessifMPEG7";
//			}
//		};
//	}
//
//	private static StreamGenericAbstractObjectIterator getIterator(Class clazz, String filePath) {
//		try {
//			File file = new File(filePath);
//			if (!file.exists()) {
//				return null;
//			}
//			return new StreamGenericAbstractObjectIterator(clazz, filePath);
//		} catch (IOException ex) {
//			Logger.getLogger("ballsed up").log(Level.SEVERE, null, ex);
//		}
//		return null;
//	}
//
//	public static void main(String[] args) throws IOException {
//
//		Map<Integer, float[]> m = getNNDists();
////		float[] powers = { 1, 1.2f, 1.4f, 1.6f, 1.8f, 2, 2.2f, 2.4f, 2.6f, 2.8f, 3.0f, 3.2f, 3.4f };
//		float[] powers = { 3.4f };
//
//		List<MetaObjectSAPIRWeightedDist3> data = getMPEG7DescriptorsAsList("/Volumes/Data/MPEG7_mu/1Mdata.gz", -1);
//		List<MetaObjectSAPIRWeightedDist3> queries = getMPEG7DescriptorsAsList(
//				"/Volumes/Data/MPEG7_mu/queryset-cophir-1000_sapir2.data", -1);
//
//		for (float f : powers) {
//			doTestQuery(data, queries, m, f);
//		}
//	}
//
//	private static void doTestQuery(List<MetaObjectSAPIRWeightedDist3> data, List<MetaObjectSAPIRWeightedDist3> queries,
//			Map<Integer, float[]> m, float power) throws FileNotFoundException {
//
//		PrintWriter pw = new PrintWriter(
//				"/Users/newrichard/Dropbox/Apps/Overleaf/2019_Pattern_recognition_letters/results/mpeg7_vpt/mpeg_vpt_"
//						+ power + ".csv");
//
//		final Metric<MetaObjectSAPIRWeightedDist3> metric = getMetric(power);
//		CountedMetric<MetaObjectSAPIRWeightedDist3> cm = new CountedMetric<>(metric);
//		VPTree<MetaObjectSAPIRWeightedDist3> vpt = new VPTree<>(data, cm);
//		cm.reset();
//
//		pw.println("Pivots,Exp,Recall,Distance computations");
//
//		for (MetaObjectSAPIRWeightedDist3 query : queries) {
//			int qid = Integer.parseInt(query.getLocatorURI());
//			double threshold = Math.pow(m.get(qid)[99] + 0.0000001f, power);
//			List<MetaObjectSAPIRWeightedDist3> res = vpt.search(query, threshold);
//			pw.println("-," + power + "," + res.size() + "," + cm.reset());
//			pw.flush();
//		}
//
//		pw.close();
//	}
//
//	@SuppressWarnings("boxing")
//	public static Map<Integer, float[]> getNNDists() throws IOException {
//		Map<Integer, float[]> res = new HashMap<>();
//		LineNumberReader lnr = new LineNumberReader(
//				new FileReader("/Volumes/Data/MPEG7_mu/groundtruth_3-1M-q1000.txt"));
//		boolean finished = false;
//		while (!finished) {
//			try {
//				int id = getIdFromLine(lnr.readLine());// will throw NullPointer with empty line
//
//				lnr.readLine();// timing info
//
//				float[] vals = getFloatsFromLine(lnr.readLine());
//
//				res.put(id, vals);
//			} catch (NullPointerException e) {
//				finished = true;
//			}
//		}
//		lnr.close();
//		return res;
//	}
//
//	private static float[] getFloatsFromLine(String line2) {
//		Scanner s2 = new Scanner(line2);
//		float[] vals = new float[100];
//		boolean fin = false;
//		int ptr = 0;
//		while (!fin) {
//			try {
//				String dist = s2.next("[0-9\\.]+:");
//				float f = Float.parseFloat(dist.substring(0, dist.length() - 1));
//				vals[ptr++] = f;
//				@SuppressWarnings("unused")
//				String nnId = s2.next("[0-9]+,?");
//			} catch (NoSuchElementException e) {
//				// fails if either patter fails to match
//				fin = true;
//			}
//		}
//		s2.close();
//		return vals;
//	}
//
//	private static int getIdFromLine(String line1) {
//		Scanner s1 = new Scanner(line1);// will throw NullPointerException if line1 is null
//		s1.next("[A-Za-z]*");
//		s1.next("<[A-Za-z]*");
//		String idToken = s1.next("\\([0-9]+\\),");
//		String[] idBits = idToken.split("[\\(\\)]");
//		int id = Integer.parseInt(idBits[1]);
//		s1.close();
//		return id;
//	}
//}
