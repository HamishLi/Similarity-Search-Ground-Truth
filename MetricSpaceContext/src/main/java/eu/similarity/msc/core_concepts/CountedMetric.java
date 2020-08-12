package eu.similarity.msc.core_concepts;

public class CountedMetric<T> implements Metric<T> {
    private Metric<T> metric;
    private int count;

    public CountedMetric(Metric<T> m) {
        this.metric = m;
        this.count = 0;
    }

    @Override
    public double distance(T x, T y) {
        this.count++;
        return this.metric.distance(x, y);
    }

    @Override
    public String getMetricName() {
        return this.metric.getMetricName();
    }

    public int reset() {
        int res = this.count;
        this.count = 0;
        return res;
    }
}