package com.tsavo.hippo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.marketdata.Trade;
import com.xeiam.xchange.exceptions.ExchangeException;
import com.xeiam.xchange.exceptions.NotAvailableFromExchangeException;
import com.xeiam.xchange.exceptions.NotYetImplementedForExchangeException;

public class LiveTicker {

	public TickerDatabase db;
	private Thread updaterThread;
	public volatile boolean running = true;
	private Map<CurrencyPair, SortedSet<Trade>> lastTrades = new HashMap<>();
	private List<TradeListener> listeners = new ArrayList<TradeListener>();

	public LiveTicker(final Exchange anExchange) {
		db = new TickerDatabase(anExchange.getExchangeSpecification().getExchangeName());
		try {
			for (CurrencyPair pair : anExchange.getPollingTradeService().getExchangeSymbols().stream().filter(x -> {
				if (x.counterSymbol.equals("USD")) {
					if (x.baseSymbol.equals("BTC") || x.baseSymbol.equals("LTC") || x.baseSymbol.equals("DRK")) {
						return true;
					}
					return false;
				}
				if (x.counterSymbol.equals("BTC")) {
					if (x.baseSymbol.equals("LTC") || x.baseSymbol.equals("DRK")) {
						return true;
					}
				}
				return false;
			}).collect(Collectors.<CurrencyPair> toList())) {
				TickerDatabase db = new TickerDatabase(anExchange.getExchangeSpecification().getExchangeName());
				lastTrades.put(pair, db.get(pair) != null ? db.get(pair) : new TreeSet<Trade>());
			}
		} catch (ExchangeException | NotAvailableFromExchangeException | NotYetImplementedForExchangeException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		updaterThread = new Thread("LiveTicker updater for " + anExchange.getExchangeSpecification().getExchangeName()) {
			@Override
			public void run() {
				while (running) {
					try {
						for (CurrencyPair pair : anExchange.getPollingTradeService().getExchangeSymbols().stream().filter(x -> {
							if (x.counterSymbol.equals("USD")) {
								if (x.baseSymbol.equals("BTC") || x.baseSymbol.equals("LTC") || x.baseSymbol.equals("DRK")) {
									return true;
								}
								return false;
							}
							if (x.counterSymbol.equals("BTC")) {
								if (x.baseSymbol.equals("LTC") || x.baseSymbol.equals("DRK")) {
									return true;
								}
							}
							return false;
						}).collect(Collectors.<CurrencyPair> toList())) {
							SortedSet<Trade> newTrades = new TreeSet<>(anExchange.getPollingMarketDataService().getTrades(pair).getTrades());
							SortedSet<Trade> notSeen = new TreeSet<>(newTrades);
							notSeen.removeAll(lastTrades.get(pair));
							lastTrades.put(pair, newTrades);
							db.putAll(notSeen);
							listeners.forEach(listener -> notSeen.forEach(trade -> listener.handleTrade(trade)));
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								return;
							}
						}
					} catch (ExchangeException | NotAvailableFromExchangeException | NotYetImplementedForExchangeException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		updaterThread.setDaemon(true);
		updaterThread.start();
	}

	public SortedSet<OHLCVData> getDataForTimeframe(CurrencyPair aPair, Date aStartDate, Date anEndDate, long aRollUp) {
		SortedSet<OHLCVData> data = new TreeSet<>();
		Date nextDate = aStartDate;
		SortedSet<Trade> dbData = db.get(aPair);
		if (dbData == null) {
			return data;
		}
		dbData = new TreeSet<>(db.get(aPair));
		while (nextDate.before(anEndDate)) {
			data.add(new OHLCVData(nextDate, aRollUp, dbData));
			nextDate = new Date(nextDate.getTime() + aRollUp);
			if (data.last().volume == null) {
				data.remove(data.last());
			}
		}
		return data;
	}

	public void stop() {
		running = false;
	}

	public void addListener(TradeListener aListener) {
		listeners.add(aListener);
	}

	@Override
	protected void finalize() throws Throwable {
		stop();
	}
}
