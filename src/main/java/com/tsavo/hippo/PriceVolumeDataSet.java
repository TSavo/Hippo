package com.tsavo.hippo;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.knowm.xchange.dto.marketdata.Trade;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PriceVolumeDataSet implements SortedSet<PriceVolumeData> {
	TreeSet<PriceVolumeData> data = new TreeSet<>();
	public Duration duration;

	public PriceVolumeDataSet(Duration aDuration) {
		duration = aDuration;
	}

	
	
	public PriceVolumeDataSet(SortedSet<Trade> someTrades, Duration aRollup, PriceVolumeData.PriceName aName) {
		
		DateTime nextDate = new DateTime(someTrades.first().getTimestamp().getTime()).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
		NavigableMap<Long, DateTime> dateMap = new TreeMap<>();
		
		do {
			dateMap.put(nextDate.getMillis(), nextDate);
			nextDate = nextDate.plus(aRollup);
		} while (nextDate.isBeforeNow());
		
		
		Map<DateTime, TreeSet<Trade>> tradesByDateRollup = new HashMap<>();
		
		someTrades.forEach(x -> {
			DateTime when = dateMap.floorEntry(x.getTimestamp().getTime()).getValue();
			if(!tradesByDateRollup.containsKey(when)){
				tradesByDateRollup.put(when, new TreeSet<Trade>());
			}
			tradesByDateRollup.get(when).add(x);
		});
		tradesByDateRollup.entrySet().forEach(x -> data.add(new PriceVolumeData(x.getKey(), aRollup, x.getValue(), aName)));
		duration = aRollup;
	}

	public void forEach(Consumer<? super PriceVolumeData> action) {
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


	public Iterator<PriceVolumeData> iterator() {
		return data.iterator();
	}

	public Comparator<? super PriceVolumeData> comparator() {
		return data.comparator();
	}

	public Object[] toArray() {
		return data.toArray();
	}

	public SortedSet<PriceVolumeData> subSet(PriceVolumeData fromElement, PriceVolumeData toElement) {
		return data.subSet(fromElement, toElement);
	}

	public <T> T[] toArray(T[] a) {
		return data.toArray(a);
	}

	public SortedSet<PriceVolumeData> headSet(PriceVolumeData toElement) {
		return data.headSet(toElement);
	}

	public boolean add(PriceVolumeData e) {
		return data.add(e);
	}

	public SortedSet<PriceVolumeData> tailSet(PriceVolumeData fromElement) {
		return data.tailSet(fromElement);
	}

	public boolean remove(Object o) {
		return data.remove(o);
	}

	public PriceVolumeData first() {
		return data.first();
	}

	public PriceVolumeData last() {
		return data.last();
	}

	public Spliterator<PriceVolumeData> spliterator() {
		return data.spliterator();
	}

	public boolean containsAll(Collection<?> c) {
		return data.containsAll(c);
	}

	public boolean addAll(Collection<? extends PriceVolumeData> c) {
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

	public boolean removeIf(Predicate<? super PriceVolumeData> filter) {
		return data.removeIf(filter);
	}

	public Stream<PriceVolumeData> stream() {
		return data.stream();
	}

	public Stream<PriceVolumeData> parallelStream() {
		return data.parallelStream();
	}

	public PriceVolumeDataSet average(AverageFunctionFactory aFunction) {
		AverageFunction price = aFunction.getAverageFunction();
		PriceVolumeDataSet average = new PriceVolumeDataSet(duration);
		for (PriceVolumeData item : data) {
			price.addSample(new WeightedSample(item.price, item.volume));
			
			average.add(new PriceVolumeData(item.startDate, duration, price.getAverage(), item.volume, item.priceName));
		}
		return average;
	}

	public PriceVolumeDataSet difference() {
		PriceVolumeData first = data.first();
		BigDecimal price = first.price, volume = first.volume;

		PriceVolumeDataSet diff = new PriceVolumeDataSet(duration);
		for (PriceVolumeData item : data) {
			diff.add(new PriceVolumeData(item.startDate, item.length, item.price.subtract(price), item.volume.subtract(volume), item.priceName));
			price = item.price;
			volume = item.volume;
		}
		return diff;
	}
}
