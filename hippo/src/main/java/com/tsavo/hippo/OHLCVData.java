package com.tsavo.hippo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import com.xeiam.xchange.dto.marketdata.Trade;

public class OHLCVData implements Comparable<OHLCVData> {

	public DateTime startDate;
	public Duration length;

	public BigDecimal open;
	public BigDecimal high;
	public BigDecimal low;
	public BigDecimal close;
	public BigDecimal volume;

	public OHLCVData(DateTime aDate, Duration aLength, BigDecimal aOpen, BigDecimal aHigh, BigDecimal aLow, BigDecimal aClose, BigDecimal aVolume){
		startDate = aDate;
		length = aLength;
		open = aOpen;
		high = aHigh;
		low = aLow;
		close = aClose;
		volume = aVolume;
	}
	
	public OHLCVData(DateTime aDate, Duration aLength, SortedSet<Trade> dbData) {
		startDate = aDate;
		length = aLength;
		DateTime endDate = startDate.plus(aLength);
		List<Trade> trades = dbData.stream().filter(x -> new DateTime(x.getTimestamp().getTime()).isAfter(startDate) && new DateTime(x.getTimestamp()).isBefore(endDate)).collect(Collectors.toList());
		if (trades.size() == 0) {
			return;
		}
		open = trades.stream().min((x, y) -> x.getTimestamp().compareTo(y.getTimestamp())).get().getPrice();
		close = trades.stream().max((x, y) -> x.getTimestamp().compareTo(y.getTimestamp())).get().getPrice();
		low = trades.stream().min((x, y) -> x.getPrice().compareTo(y.getPrice())).get().getPrice();
		high = trades.stream().max((x, y) -> x.getPrice().compareTo(y.getPrice())).get().getPrice();
		volume = trades.stream().map((x) -> x.getTradableAmount()).reduce((x, y) -> x.add(y)).get();
	}

	@Override
	public int compareTo(OHLCVData o) {
		return startDate.compareTo(o.startDate);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("Date", startDate).append("Length", length).append("Open", open).append("High", high).append("Low", low).append("Close", close)
				.append("Volume", volume).toString();
	}
}
