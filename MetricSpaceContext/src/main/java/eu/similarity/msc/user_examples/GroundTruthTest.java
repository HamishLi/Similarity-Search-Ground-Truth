package eu.similarity.msc.user_examples;

import eu.similarity.msc.data.DataListView;
import eu.similarity.msc.data.MfAlexMetricSpace;

import java.io.*;
import java.util.*;

public class GroundTruthTest {
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        //MfAlexMetricSpace mf = new MfAlexMetricSpace("Volumes/Data/mf_fc6_full_euc/", "Volumes/Data/mf_fc6_full_euc/mf_fc6_raw/");
        /*final List<DataListView.IdDatumPair> data0 = DataListView.convert(mf.getData(0));
        final List<DataListView.IdDatumPair> data = DataListView.convert(mf.getData());
        final List<DataListView.IdDatumPair> queries = DataListView.convert(mf.getQueries());
        final Map<Integer, double[]> thresholds = mf.getThresholds();
        final CountedMetric<DataListView.IdDatumPair> cm = DataListView.convert(mf.getMetric());


        DataListView.IdDatumPair query = data0.get(0);
        double threshold = 900;
        VPTree<DataListView.IdDatumPair> vpt = new VPTree<>(data, DataListView.convert(mf.getMetric()));
        final List<DataListView.IdDatumPair> res = vpt.search(query, threshold);

        for (int i = 0; i < res.size(); i++) {
            System.out.println(res.get(i).id);
        }
        System.out.println(res.size());*/
        /*final Map<Integer, double[]> thresholds = mf.getThresholds();
        final double[] ts = thresholds.get(4443);
        final double threshold = ts[50 - 1];
        System.out.println(threshold);*/

        /*int count = 0;
        for (int i = 0; i < 1000000; i++) {
            if (thresholds.get(i) != null) {
                System.out.println(i);
                count++;
            }
        }

        System.out.println(count);*/

        /*Map<Integer, List<DataListView.IdDistancePair>> groundTruth = getGroundTruthWithTransform(3.0);
        System.out.println(groundTruth.size());
        for (int i : groundTruth.keySet()) {
            List<DataListView.IdDistancePair> groundTruth0 = groundTruth.get(i);
            System.out.println(i);
            for (int j = 0; j < groundTruth0.size(); j++) {
                System.out.print(groundTruth0.get(j).id + ", " + groundTruth0.get(j).distance + ", ");
            }
            System.out.println();
        }*/

            List<DataListView.IdDistancePair> groundTruth0 = getGroundTruthWithTransform(3.5, 788);
            for (int j = 0; j < groundTruth0.size(); j++) {
                System.out.print(groundTruth0.get(j).imageId + ", " + groundTruth0.get(j).distance + ", ");

            System.out.println();
        }
    }

    public static  List<DataListView.IdDistancePair> getGroundTruth(int imageId) throws IOException {
        Map<Integer, List<DataListView.IdDistancePair>> groundTruth = new TreeMap<>();
        int fileNumber = imageId / 1000;
        final FileReader fr = new FileReader("Volumes/Data/mf_fc6_full_euc/ground_truth/" + fileNumber + ".txt");
        LineNumberReader lnr = new LineNumberReader(fr);
        for (String line = lnr.readLine(); line != null; line = lnr.readLine()) {
            Scanner s = new Scanner(line);
            List<DataListView.IdDistancePair> idp = new ArrayList<>();
            int id_target = s.nextInt();
            for (int gt = 0; gt < 100; gt++) {
                int id = s.nextInt();
                double distance = s.nextDouble();
                idp.add(new DataListView.IdDistancePair(id, distance));
            }
            groundTruth.put(id_target, idp);
            s.close();
        }
        lnr.close();

        List<DataListView.IdDistancePair> idDistancePairs = groundTruth.get(imageId);
        return idDistancePairs;

    }

    public static List<DataListView.IdDistancePair> getGroundTruthWithTransform(double pow, int imageId) throws IOException {
        Map<Integer, List<DataListView.IdDistancePair>> groundTruth = new TreeMap<>();

        final FileReader fr = new FileReader("Volumes/Data/mf_fc6_full_euc/groundTruth/ground_truth_pow_" + pow + ".txt");
        LineNumberReader lnr = new LineNumberReader(fr);
        for (String line = lnr.readLine(); line != null; line = lnr.readLine()) {
            Scanner s = new Scanner(line);
            List<DataListView.IdDistancePair> idp = new ArrayList<>();
            int id_target = s.nextInt();
            int nnIds = s.nextInt();
            for (int gt = 0; gt < nnIds; gt++) {
                int id = s.nextInt();
                double distance = s.nextDouble();
                idp.add(new DataListView.IdDistancePair(id, distance));
            }
            groundTruth.put(id_target, idp);
            s.close();
        }
        lnr.close();

        List<DataListView.IdDistancePair> idDistancePairs = groundTruth.get(imageId);
        return idDistancePairs;
    }
}
