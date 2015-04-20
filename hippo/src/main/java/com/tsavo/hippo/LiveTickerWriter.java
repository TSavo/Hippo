package com.tsavo.hippo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.joda.time.Period;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.marketdata.Trade;
import com.xeiam.xchange.exceptions.ExchangeException;
import com.xeiam.xchange.exceptions.NotAvailableFromExchangeException;
import com.xeiam.xchange.exceptions.NotYetImplementedForExchangeException;

public class LiveTickerWriter {

	public TickerDatabase db;
	private Thread updaterThread;
	public volatile boolean running = true;
	private Map<CurrencyPair, SortedSet<Trade>> lastTrades = new HashMap<>();
	private List<TradeListener> listeners = new ArrayList<TradeListener>();
	public Exchange exchange;

	public LiveTickerWriter(final Exchange anExchange) {
		exchange = anExchange;
		db = new TickerDatabase(anExchange.getExchangeSpecification().getExchangeName());
		try {
			for (CurrencyPair pair : anExchange.getPollingMarketDataService().getExchangeSymbols()) {
				// TickerDatabase db = new
				// TickerDatabase(anExchange.getExchangeSpecification().getExchangeName());
				lastTrades.put(pair, new TreeSet<Trade>());
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
						for (CurrencyPair pair : anExchange.getPollingMarketDataService().getExchangeSymbols()) {
							try {
								SortedSet<Trade> newTrades = new TreeSet<>(anExchange.getPollingMarketDataService().getTrades(pair).getTrades());
								SortedSet<Trade> notSeen = new TreeSet<>(newTrades);
								notSeen.removeAll(lastTrades.get(pair));
								lastTrades.put(pair, newTrades);
								db.putAll(notSeen);
								listeners.forEach(listener -> notSeen.forEach(trade -> listener.handleTrade(trade)));
								try {
									Thread.sleep(10000);
								} catch (InterruptedException e) {
									return;
								}
							} catch (Exception e) {
								e.printStackTrace();
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
