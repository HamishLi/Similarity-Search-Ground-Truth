package eu.similarity.msc.user_examples;

import java.util.Map;

import eu.similarity.msc.data.MfAlexMetricSpace;
import eu.similarity.msc.data.MfAlexMetricSpace_old;

public class BuildFc6Example {
    public static void main(String[] args) {

        MfAlexMetricSpace_old mf = new MfAlexMetricSpace_old("Volumes/Data/mf_fc6_full_euc/");
        Map<Integer, double[]> forceFullBuild = mf.getThresholds();
    }
}
