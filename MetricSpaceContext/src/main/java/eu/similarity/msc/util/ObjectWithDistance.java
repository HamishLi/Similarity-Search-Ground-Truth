package eu.similarity.msc.util;

public class ObjectWithDistance<T> implements Comparable<ObjectWithDistance<T>> {

	private double distance;
	private T object;

	public ObjectWithDistance(T point, double distance) {
		this.object = point;
		this.distance = distance;
	}

	@Override
	public int compareTo(ObjectWithDistance<T> arg0) {
		if (Double.isNaN(this.distance) || Double.isNaN(arg0.distance)) {
			throw new RuntimeException(
					"NaN found in compareTo, quicksort won't terminate!");
		}
		if (this.distance < arg0.distance) {
			return -1;
		} else if (this.distance > arg0.distance) {
			return 1;
		} else
			return 0;
	}

	/**
	 * @return the distance
	 */
	public double getDistance() {
		return this.distance;
	}

	/**
	 * @return the object
	 */
	public T getValue() {
		return this.object;
	}

	@Override
	public String toString() {
		return this.object.toString() + " (" + this.distance + ")";
	}

}
