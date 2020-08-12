package eu.similarity.msc.convex_transforms;

import eu.similarity.msc.core_concepts.CountedMetric;
import eu.similarity.msc.data.DataListView;
import eu.similarity.msc.data.MetricSpaceResource;
import eu.similarity.msc.data.MfAlexMetricSpace;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class GroundTruthWithTransform {

    private String filePath;
    private String groundTruthFilePath;
    private String dataFilePath;
    private String queryFilePath;
    private String gtFilePath;
    private String objectDataPath;
    private Logger logger;

    public GroundTruthWithTransform() {
        this.filePath = "Volumes/Data/mf_fc6_full_euc/";
        this.groundTruthFilePath = filePath + "groundTruth/";
        this.dataFilePath = filePath + "mf_fc6_raw/";
        this.queryFilePath = filePath + "queries.obj";
        this.gtFilePath = filePath + "nnInfo.txt";
        this.objectDataPath = filePath + "extracted/";
        this.logger = Logger.getLogger(this.getClass().getName());

        checkGroundTruthDir(groundTruthFilePath);
    }

    private void checkGroundTruthDir(String groundTruthFilePath) {
        File extracted = new File(groundTruthFilePath);
        if (!extracted.exists()) {
            this.logger.info(this.filePath + "groundTruth/  does not exist; "
                    + (extracted.mkdir() ? " created now" : "unable to create now"));
        }
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException {
        GroundTruthWithTransform gt = new GroundTruthWithTransform();
        MetricSpaceResource<Integer, float[]> mf = new MfAlexMetricSpace(gt.filePath);

        double pow = 2.5;

        Map<Integer, List<DataListView.IdDistancePair>> groundTruth = getTransformedGroundTruth(mf, 50, pow);
        writeGroundTruth(gt.groundTruthFilePath, groundTruth, pow);
    }


    @SuppressWarnings("boxing")
    private static Map<Integer, List<DataListView.IdDistancePair>> getTransformedGroundTruth(MetricSpaceResource<Integer, float[]> space, int nn, double pow)
            throws IOException, ClassNotFoundException {
        final List<DataListView.IdDatumPair> data = DataListView.convert(space.getData());
        final List<DataListView.IdDatumPair> queries = DataListView.convert(space.getQueries());
        final Map<Integer, double[]> thresholds = space.getThresholds();

        final CountedMetric<DataListView.IdDatumPair> cm = DataListView.convert(space.getMetric());
        VptWithTransform<DataListView.IdDatumPair> vpt = new VptWithTransform<>(data, cm);
        cm.reset();

        final Map<Integer, List<DataListView.IdDistancePair>> groundTruth = new TreeMap<>();

        int count = 0;

        for (DataListView.IdDatumPair query : queries) {
            final double[] ts = thresholds.get(query.id);
            final double threshold = ts[nn - 1];

            count++;

            System.out.print(query.id + "\t" + threshold + "\t");

            vpt.setTransform((x) -> Math.pow(x, pow));
            final List<DataListView.IdDatumPair> res = vpt.search(query, threshold);
            final List<DataListView.IdDistancePair> idp = new ArrayList<>();

            System.out.print("\t" + res.size() + count + "\n");

            for (int i = 0; i < res.size(); i++) {
                double distance = vpt.getDistance(query.datum, res.get(i).datum);
                idp.add(new DataListView.IdDistancePair(res.get(i).id, distance));
            }
            Collections.sort(idp);

            for (int i = 0; i < idp.size(); i++) {
                System.out.print("\t" + idp.get(i).imageId + "\t" + idp.get(i).distance + "\t");
            }
            System.out.println();

            groundTruth.put(query.id, idp);
        }
        return groundTruth;
    }


    @SuppressWarnings("boxing")
    private static void writeGroundTruth(String groundTruthFilePath, Map<Integer, List<DataListView.IdDistancePair>> groundTruth, double pow) throws IOException {
//        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(groundTruthFilePath + "ground_truth_pow_" + pow + ".obj"));
//        oos.writeObject(groundTruth);
//        oos.close();

        PrintWriter pw = new PrintWriter(groundTruthFilePath + "ground_truth_pow_" + pow + ".txt");
        for (int gtid : groundTruth.keySet()) {
            pw.print(gtid + "\t" + groundTruth.get(gtid).size());
            for (int i = 0; i < groundTruth.get(gtid).size(); i++) {
                pw.print("\t" + groundTruth.get(gtid).get(i).imageId + "\t" + groundTruth.get(gtid).get(i).distance);
            }
            pw.println();
            pw.flush();
        }

        pw.close();

    }

}
