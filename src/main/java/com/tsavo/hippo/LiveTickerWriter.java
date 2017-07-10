package com.tsavo.hippo;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.knowm.xchange.gemini.v1.dto.GeminiException;
import si.mazi.rescu.HttpStatusIOException;

import java.util.*;

public class LiveTickerWriter {

	public TickerDatabase db;
	private Thread updaterThread;
	public volatile boolean running = true;
	private Map<CurrencyPair, Set<Trade>> lastTrades = new HashMap<>();
	public Exchange exchange;

	public LiveTickerWriter(final Exchange anExchange) {
		exchange = anExchange;
		db = new MongoTickerDatabase(anExchange.getExchangeSpecification().getExchangeName());
		try {
			for (CurrencyPair pair : anExchange.getExchangeSymbols()) {
				// TickerDatabase db = new
				// TickerDatabase(anExchange.getExchangeSpecification().getExchangeName());
				lastTrades.put(pair, new TreeSet<Trade>());
			}
		} catch (ExchangeException | NotAvailableFromExchangeException | NotYetImplementedForExchangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		updaterThread = new Thread("LiveTicker updater for " + anExchange.getExchangeSpecification().getExchangeName()) {
			@Override
			public void run() {
				while (running) {
					try {
						for (CurrencyPair pair : anExchange.getExchangeSymbols()) {
							try {
								Set<Trade> newTrades = new HashSet<>(anExchange.getMarketDataService().getTrades(pair).getTrades());
								Set<Trade> notSeen = new HashSet<>(newTrades);
								notSeen.removeAll(lastTrades.get(pair));
								lastTrades.put(pair, newTrades);
								db.putAll(pair, notSeen);
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									return;
								}
							}catch(GeminiException | ExchangeException | HttpStatusIOException e) {

							} catch (Exception e) {
								e.printStackTrace();
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
