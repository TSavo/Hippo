package com.tsavo.hippo;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.knowm.xchange.okcoin.FuturesContract;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class LiveFutureTickerWriter {

	public FutureTickerDatabase db;
	private Thread updaterThread;
	public volatile boolean running = true;
	private Map<FuturesContract, Map<CurrencyPair, SortedSet<Trade>>> lastTrades = new HashMap<>();
	public Exchange exchange;

	public LiveFutureTickerWriter(final Exchange anExchange) {
		exchange = anExchange;
		db = new FutureTickerDatabase(anExchange.getExchangeSpecification().getExchangeName());
		try {
			for (FuturesContract contract : FuturesContract.values()) {
				Map<CurrencyPair, SortedSet<Trade>> map = new HashMap<>();
				for (CurrencyPair pair : anExchange.getExchangeSymbols()) {
					// TickerDatabase db = new
					// TickerDatabase(anExchange.getExchangeSpecification().getExchangeName());
					map.put(pair, new TreeSet<Trade>());
				}
				lastTrades.put(contract, map);
			}
		} catch (ExchangeException | NotAvailableFromExchangeException | NotYetImplementedForExchangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		updaterThread = new Thread("LiveFutureTickerWriter for " + anExchange.getExchangeSpecification().getExchangeName()) {
			@Override
			public void run() {
				while (running) {
					try {
						for (FuturesContract contract : FuturesContract.values()) {
							for (CurrencyPair pair : anExchange.getExchangeSymbols()) {
								try {
									SortedSet<Trade> newTrades = new TreeSet<>(anExchange.getMarketDataService().getTrades(pair, contract).getTrades());
									SortedSet<Trade> notSeen = new TreeSet<>(newTrades);
									notSeen.removeAll(lastTrades.get(contract).get(pair));
									lastTrades.get(contract).put(pair, newTrades);
									db.putAll(contract, notSeen);
									try {
										Thread.sleep(10000);
									} catch (InterruptedException e) {
										return;
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					} catch (ExchangeException | NotAvailableFromExchangeException | NotYetImplementedForExchangeException e) {
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

	@Override
	protected void finalize() throws Throwable {
		stop();
	}
}
