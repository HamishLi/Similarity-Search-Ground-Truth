package eu.similarity.msc.data;

import java.io.IOException;
import java.util.Map;

import eu.similarity.msc.core_concepts.Metric;

/**
 * @author newrichard Interface to generalise over fetching data
 */
public interface MetricSpaceResource<IdType, DataRep> {

	/**
	 * @return all the data from the collection, indexed by integer
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Map<IdType, DataRep> getData() throws IOException, ClassNotFoundException;

	/**
	 * @return the metric which governs the space
	 */
	public Metric<DataRep> getMetric();

	/**
	 * @return all the mapped nnids for a given query. nb the query id space may be
	 *         different from the data, or may be the same, depending on the
	 *         collection
	 * @throws IOException
	 */
	public Map<IdType, IdType[]> getNNIds() throws IOException;

	/**
	 * @return the queries, indexed by integer. this may overlap with the data or
	 *         may be disjoint, query and data ids should not be mixed
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Map<IdType, DataRep> getQueries() throws IOException, ClassNotFoundException;

	/**
	 * @return a map from query id to the distances of all mapped nearest neighbours
	 * @throws IOException
	 */
	public Map<IdType, double[]> getThresholds() throws IOException;

}
