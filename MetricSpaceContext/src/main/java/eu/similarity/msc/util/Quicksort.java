package eu.similarity.msc.util;

public class Quicksort {

	/**
	 * places the ith element of the vector in the correct location relative to the
	 * others
	 * 
	 * @param v
	 * @param toPlace
	 */
	public static <T> void placeOrdinal(ObjectWithDistance<T>[] v, int toPlace) {
		partitionToPivotPoint(v, 0, v.length - 1, toPlace);
	}

	/**
	 * places the median value at either the middle, or to the right of middle for
	 * even sized collections; values to left and right are not sorted, but
	 * guaranteed to be less or greater respectively than the median
	 * 
	 * @param v
	 */
	public static <T> void placeMedian(ObjectWithDistance<T>[] v) {
		placeOrdinal(v, v.length / 2);
	}

	public static <T> void sort(ObjectWithDistance<T>[] v) {
		partitionSort(v, 0, v.length - 1);
	}

	/**
	 * 
	 * @param v
	 * @param from
	 * @param to
	 */
	public static <T> void partitionSort(ObjectWithDistance<T>[] v, int from, int to) {

		ObjectWithDistance<T> pivot = v[to];

		int upTo = from;
		int pivotPos = to;
		/*
		 * now, run into the middle of the vector from both ends, until upTo and
		 * pivotPos meet
		 */
		while (pivotPos != upTo) {
			if (v[upTo].compareTo(pivot) == 1) {
				swap(v, upTo, pivotPos - 1);
				swap(v, pivotPos - 1, pivotPos--);
			} else {
				upTo++;
			}
		}

		/*
		 * here is the quicksort code if we want more than the median value
		 */
		if (pivotPos > from) {
			partitionSort(v, from, pivotPos - 1);
		}
		if (pivotPos < to) {
			partitionSort(v, pivotPos + 1, to);
		}
	}

	public static <T> void partitionToPivotPoint(ObjectWithDistance<T>[] v, int from, int to, int finalPivotPos) {

		ObjectWithDistance<T> pivot = v[to];

		int upTo = from;
		int pivotPos = to;
		/*
		 * now, run into the middle of the vector from both ends, until upTo and
		 * pivotPos meet
		 */
		while (pivotPos != upTo) {
			if (v[upTo].compareTo(pivot) > 0) {
				swap(v, upTo, pivotPos - 1);
				swap(v, pivotPos - 1, pivotPos--);
			} else {
				upTo++;
			}
		}

		/*
		 * this code only places the finalPivotPos value correctly, does not sort the
		 * rest of the array
		 */
		if (pivotPos > finalPivotPos && pivotPos > from) {
			partitionToPivotPoint(v, from, pivotPos - 1, finalPivotPos);
		} else if (pivotPos < finalPivotPos && pivotPos < to) {
			partitionToPivotPoint(v, pivotPos + 1, to, finalPivotPos);
		}
	}

	private static <T> void swap(T[] v, int from, int to) {
		if (from != to) {
			T temp = v[to];
			v[to] = v[from];
			v[from] = temp;
		}
	}

}
