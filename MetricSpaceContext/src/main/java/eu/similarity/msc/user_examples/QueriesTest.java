package eu.similarity.msc.user_examples;

import eu.similarity.msc.data.DataListView;
import eu.similarity.msc.data.MetricSpaceResource;
import eu.similarity.msc.data.MfAlexMetricSpace;

import java.io.IOException;
import java.util.*;

public class QueriesTest {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        MfAlexMetricSpace mf = new MfAlexMetricSpace("Volumes/Data/mf_fc6_full_euc/", "Volumes/Data/mf_fc6_full_euc/mf_fc6_raw/");
        final List<DataListView.IdDatumPair> queries = DataListView.convert(mf.getQueries());
        int max = 999, min = 0;
        int randomIndex = (int) (Math.random() * (max - min) + min);
        int randomId = queries.get(randomIndex).id;
        System.out.println(randomId);




    }


}
