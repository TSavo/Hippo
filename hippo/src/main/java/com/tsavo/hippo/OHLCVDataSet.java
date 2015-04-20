package com.tsavo.hippo;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import com.xeiam.xchange.dto.marketdata.Trade;

public class OHLCVDataSet implements SortedSet<OHLCVData> {
	SortedSet<OHLCVData> data = new TreeSet<>();
	public Duration duration;

	public OHLCVDataSet(Duration aDuration) {
		duration = aDuration;
	}

	public OHLCVDataSet(SortedSet<Trade> someTrades, Duration aRollup) {
		DateTime nextDate = new DateTime(someTrades.first().getTimestamp().getTime()).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
		do {
			data.add(new OHLCVData(nextDate, aRollup, someTrades));
			nextDate = nextDate.plus(aRollup);
			if (data.last().volume == null) {
				data.remove(data.last());
			}
		} while (nextDate.isBeforeNow());
		duration = aRollup;
	}

	public void forEach(Consumer<? super OHLCVData> action) {
		data.forEach(action);
	}

	public int size() {
		return data.size();
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public boolean contains(Object o) {
		return data.contains(o);
	}

	public Iterator<OHLCVData> iterator() {
		return data.iterator();
	}

	public Comparator<? super OHLCVData> comparator() {
		return data.comparator();
	}

	public Object[] toArray() {
		return data.toArray();
	}

	public SortedSet<OHLCVData> subSet(OHLCVData fromElement, OHLCVData toElement) {
		return data.subSet(fromElement, toElement);
	}

	public <T> T[] toArray(T[] a) {
		return data.toArray(a);
	}

	public SortedSet<OHLCVData> headSet(OHLCVData toElement) {
		return data.headSet(toElement);
	}

	public boolean add(OHLCVData e) {
		return data.add(e);
	}

	public SortedSet<OHLCVData> tailSet(OHLCVData fromElement) {
		return data.tailSet(fromElement);
	}

	public boolean remove(Object o) {
		return data.remove(o);
	}

	public OHLCVData first() {
		return data.first();
	}

	public OHLCVData last() {
		return data.last();
	}

	public Spliterator<OHLCVData> spliterator() {
		return data.spliterator();
	}

	public boolean containsAll(Collection<?> c) {
		return data.containsAll(c);
	}

	public boolean addAll(Collection<? extends OHLCVData> c) {
		return data.addAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return data.retainAll(c);
	}

	public boolean removeAll(Collection<?> c) {
		return data.removeAll(c);
	}

	public void clear() {
		data.clear();
	}

	public boolean equals(Object o) {
		return data.equals(o);
	}

	public int hashCode() {
		return data.hashCode();
	}

	public boolean removeIf(Predicate<? super OHLCVData> filter) {
		return data.removeIf(filter);
	}

	public Stream<OHLCVData> stream() {
		return data.stream();
	}

	public Stream<OHLCVData> parallelStream() {
		return data.parallelStream();
	}

	public OHLCVDataSet average(AverageFunctionFactory aFunction) {
		AverageFunction open = aFunction.getAverageFunction(), high = aFunction.getAverageFunction(), low = aFunction.getAverageFunction(), close = aFunction
				.getAverageFunction(), volume = aFunction.getAverageFunction();
		OHLCVDataSet average = new OHLCVDataSet(duration);
		for (OHLCVData item : data) {
			open.addSample(new WeightedSample(item.open, item.volume));
			high.addSample(new WeightedSample(item.high, item.volume));
			low.addSample(new WeightedSample(item.low, item.volume));
			close.addSample(new WeightedSample(item.close, item.volume));
			volume.addSample(new WeightedSample(item.volume, BigDecimal.ONE));
			average.add(new OHLCVData(item.startDate, duration, open.getAverage(), high.getAverage(), low.getAverage(), close.getAverage(), volume.getAverage()));
		}
		return average;
	}

	public OHLCVDataSet difference() {
		OHLCVData first = data.first();
		BigDecimal open = first.open, high = first.high, low = first.low, close = first.close, volume = first.volume;

		OHLCVDataSet diff = new OHLCVDataSet(duration);
		for (OHLCVData item : data) {
			diff.add(new OHLCVData(item.startDate, item.length, item.open.subtract(open), item.high.subtract(high), item.low.subtract(low), item.close.subtract(close), item.volume
					.subtract(volume)));
			open = item.open;
			high = item.high;
			low = item.low;
			close = item.close;
			volume = item.volume;
		}
		return diff;
	}
}
