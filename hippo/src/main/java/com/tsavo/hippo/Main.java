package com.tsavo.hippo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.ExchangeSpecification;
import com.xeiam.xchange.bitfinex.v1.BitfinexExchange;
import com.xeiam.xchange.btce.v3.BTCEExchange;
import com.xeiam.xchange.bter.BTERExchange;
import com.xeiam.xchange.cryptsy.CryptsyExchange;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.okcoin.OkCoinExchange;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		List<Exchange> exchanges = new ArrayList<>();
		
		List<LiveTicker> tickers = new ArrayList<>();
		exchanges.forEach(exchange -> tickers.add(new LiveTicker(exchange)));
		while (true) {
			Date when = new Date(new Date().getTime() - 1000000000);
			when.setHours(0);
			when.setMinutes(0);
			when.setSeconds(0);
			tickers.forEach(ticker -> {
				System.out.println(ticker.db.exchangeName + ":");
				ticker.getDataForTimeframe(new CurrencyPair("BTC", "USD"), when, new Date(new Date().getTime() + 1000000), 1000 * 60 * 60).forEach(x -> System.out.println(x));
				;
			});

			Thread.sleep(10000);
		}
	}

}
