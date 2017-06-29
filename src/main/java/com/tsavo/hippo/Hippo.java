package com.tsavo.hippo;

import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.bittrex.v1.BittrexExchange;
import org.knowm.xchange.okcoin.OkCoinExchange;
import org.knowm.xchange.poloniex.Poloniex;
import org.knowm.xchange.poloniex.PoloniexExchange;

import java.util.ArrayList;
import java.util.List;

public class Hippo {

	public static void main(String[] args) throws InterruptedException {
		List<Exchange> exchanges = new ArrayList<>();
		//ExchangeSpecification cryptsy = new ExchangeSpecification(CryptsyExchange.class);


		Exchange okcoin = new OkCoinExchange();
		ExchangeSpecification okcoinSpec = okcoin.getDefaultExchangeSpecification();
		okcoinSpec.setExchangeSpecificParametersItem("Use_Intl", true);

		Exchange okcoinFutures = new OkCoinExchange();
		ExchangeSpecification okCoinFutureSpec = okcoinFutures.getDefaultExchangeSpecification();
		okCoinFutureSpec.setExchangeSpecificParametersItem("Use_Intl", true);
		okCoinFutureSpec.setExchangeSpecificParametersItem("Use_Futures", true);

		exchanges.add(ExchangeFactory.INSTANCE.createExchange(okcoinSpec));
		//exchanges.add(ExchangeFactory.INSTANCE.createExchange(okCoinFutureSpec));


		Exchange poloniex = new PoloniexExchange();
		ExchangeSpecification poloniexSpec = poloniex.getDefaultExchangeSpecification();
		poloniex.applySpecification(poloniexSpec);
		exchanges.add(poloniex);


		Exchange bittrex = new BittrexExchange();
		ExchangeSpecification bittrexSpec = bittrex.getDefaultExchangeSpecification();
		bittrex.applySpecification(bittrexSpec);
		exchanges.add(bittrex);

		List<LiveTickerWriter> tickers = new ArrayList<>();
//		List<LiveFutureTickerWriter> futureTickers = new ArrayList<>();
//		futureTickers.add(new LiveFutureTickerWriter(ExchangeFactory.INSTANCE.createExchange(okCoinFutureSpec)));

		exchanges.forEach(exchange -> tickers.add(new LiveTickerWriter(exchange)));
		PeriodFormatter periodFormat = new PeriodFormatterBuilder().printZeroRarelyLast().appendYears().appendSuffix(" year ", " years ").printZeroRarelyLast().appendMonths()
				.appendSuffix(" month ", " months ").appendDays().appendSuffix(" day ", " days ").appendHours().appendSuffix(" hour ", " hours ").appendSeparator("and ")
				.appendMinutes().appendSuffix(" minute ", " minutes ").toFormatter();
		while (true) {

			Thread.sleep(60000);
		}
	}

}
