package com.tsavo.hippo;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.knowm.xchange.dto.marketdata.Trade;

import java.math.BigDecimal;
import java.util.SortedSet;

public class PriceVolumeData implements Comparable<PriceVolumeData> {

	public DateTime startDate;
	public Duration length;
	public BigDecimal price;
	public BigDecimal volume;
	public PriceName priceName;

	public static enum PriceName {
		OPEN, HIGH, LOW, CLOSE;
	}

	public PriceVolumeData(DateTime startDate, Duration length, BigDecimal price, BigDecimal volume, PriceName priceName) {
		super();
		this.startDate = startDate;
		this.length = length;
		this.price = price;
		this.volume = volume;
		this.priceName = priceName;
	}

	public PriceVolumeData(DateTime aDate, Duration aLength, SortedSet<Trade> dbData, PriceName aPriceName) {
		startDate = aDate;
		length = aLength;
		priceName = aPriceName;
		DateTime endDate = startDate.plus(aLength);
		switch (aPriceName) {
		case OPEN:
			price = dbData.stream().min((x, y) -> x.getTimestamp().compareTo(y.getTimestamp())).get().getPrice();
			break;
		case CLOSE:
			price = dbData.stream().max((x, y) -> x.getTimestamp().compareTo(y.getTimestamp())).get().getPrice();
			break;
		case LOW:
			price = dbData.stream().min((x, y) -> x.getPrice().compareTo(y.getPrice())).get().getPrice();
			break;
		case HIGH:
			price = dbData.stream().max((x, y) -> x.getPrice().compareTo(y.getPrice())).get().getPrice();
			break;
		}
		volume = dbData.stream().map((x) -> x.getTradableAmount()).reduce((x, y) -> x.add(y)).get();
	}

	@Override
	public int compareTo(PriceVolumeData o) {
		return startDate.compareTo(o.startDate);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("Date", startDate).append("Length", length).append("Price name", priceName).append("Price", price).append("Volume", volume)
				.toString();
	}
}
