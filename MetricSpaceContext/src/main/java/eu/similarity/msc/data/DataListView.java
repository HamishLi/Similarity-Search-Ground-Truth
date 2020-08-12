package eu.similarity.msc.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import eu.similarity.msc.core_concepts.CountedMetric;
import eu.similarity.msc.core_concepts.Metric;

public class DataListView {

    public static class IdDatumPair {
        public int id;
        public float[] datum;

        public IdDatumPair(int id, float[] datum) {
            this.id = id;
            this.datum = datum;
        }
    }

    public static <T> List<T> removeRandom(List<T> data, int i) {
        Random rand = new Random(0);
        List<T> res = new ArrayList<>();
        while (res.size() < i) {
            res.add(data.remove(rand.nextInt(data.size())));
        }
        return res;
    }

    public static List<IdDatumPair> convert(Map<Integer, float[]> data) {
        List<IdDatumPair> res = new ArrayList<>();
        for (int i : data.keySet()) {
            IdDatumPair idp = new IdDatumPair(i, data.get(i));
            res.add(idp);
        }
        return res;
    }

    public static CountedMetric<IdDatumPair> convert(final Metric<float[]> m) {
        Metric<IdDatumPair> met = new Metric<IdDatumPair>() {

            @Override
            public double distance(IdDatumPair x, IdDatumPair y) {
                return m.distance(x.datum, y.datum);
            }

            @Override
            public String getMetricName() {
                return m.getMetricName();
            }
        };
        return new CountedMetric<>(met);
    }

    public static class IdDistancePair implements Comparable<IdDistancePair> {
        public int imageId;
        public double distance;

        public IdDistancePair(int imageId, double distance) {
            this.imageId = imageId;
            this.distance = distance;
        }

        @Override
        public int compareTo(IdDistancePair that) {
            return Double.compare(this.distance, that.distance);
        }
    }

    public static class SimilarImage {
        public int imageId;
        public double distance;
        public String imageURL;

        public SimilarImage(IdDistancePair idp) {
            this.imageId = idp.imageId;
            this.distance = idp.distance;
            this.imageURL = getImageURL(imageId);
        }
    }

    public static class IdImageURLPair {
        public int imageId;
        public String imageURL;

        public IdImageURLPair(int imageId) {
            this.imageId = imageId;
            this.imageURL = getImageURL(imageId);
        }
    }

    public static String getImageURL(int id) {
        StringBuffer sb = new StringBuffer();
        sb.append("https://similarity.cs.st-andrews.ac.uk/mirflkr/images/");
        sb.append(id / 10000);
        sb.append("/");
        sb.append(id);
        sb.append(".jpg");
        return sb.toString();
    }

}
