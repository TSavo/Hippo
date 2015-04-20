package com.tsavo.hippo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.marketdata.Trade;

public class LiveTickerReader {

	public TickerDatabase db;
	private List<TradeListener> listeners = new ArrayList<TradeListener>();

	public LiveTickerReader(final String anExchangeName) {
		db = new TickerDatabase(anExchangeName);
	}

	public SortedSet<Trade> getDataForTimeframe(CurrencyPair aPair) {
		return db.get(aPair);
	}


	public void addListener(TradeListener aListener) {
		listeners.add(aListener);
	}

}
