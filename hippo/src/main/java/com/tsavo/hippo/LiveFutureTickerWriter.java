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
import com.xeiam.xchange.okcoin.FuturesContract;

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
				for (CurrencyPair pair : anExchange.getPollingMarketDataService().getExchangeSymbols()) {
					// TickerDatabase db = new
					// TickerDatabase(anExchange.getExchangeSpecification().getExchangeName());
					map.put(pair, new TreeSet<Trade>());
				}
				lastTrades.put(contract, map);
			}
		} catch (ExchangeException | NotAvailableFromExchangeException | NotYetImplementedForExchangeException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		updaterThread = new Thread("LiveFutureTickerWriter for " + anExchange.getExchangeSpecification().getExchangeName()) {
			@Override
			public void run() {
				while (running) {
					try {
						for (FuturesContract contract : FuturesContract.values()) {
							for (CurrencyPair pair : anExchange.getPollingMarketDataService().getExchangeSymbols()) {
								try {
									SortedSet<Trade> newTrades = new TreeSet<>(anExchange.getPollingMarketDataService().getTrades(pair, contract).getTrades());
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

	@Override
	protected void finalize() throws Throwable {
		stop();
	}
}
