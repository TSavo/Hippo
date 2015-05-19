package com.tsavo.hippo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.ExchangeSpecification;
import com.xeiam.xchange.bitfinex.v1.BitfinexExchange;
import com.xeiam.xchange.bitstamp.BitstampExchange;
import com.xeiam.xchange.bittrex.v1.BittrexExchange;
import com.xeiam.xchange.btc38.Btc38;
import com.xeiam.xchange.btc38.Btc38Exchange;
import com.xeiam.xchange.btce.v3.BTCEExchange;
import com.xeiam.xchange.bter.BTERExchange;
import com.xeiam.xchange.coinbaseex.CoinbaseExExchange;
import com.xeiam.xchange.cryptsy.CryptsyExchange;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.kraken.KrakenExchange;
import com.xeiam.xchange.okcoin.OkCoinExchange;
import com.xeiam.xchange.yacuna.YacunaExchange;

public class Hippo {

	public static void main(String[] args) throws InterruptedException {
		List<Exchange> exchanges = new ArrayList<>();
		//ExchangeSpecification cryptsy = new ExchangeSpecification(CryptsyExchange.class);

		Exchange btce = ExchangeFactory.INSTANCE.createExchange(BTCEExchange.class.getName());
		ExchangeSpecification btceSpec = btce.getDefaultExchangeSpecification();

		Exchange okcoin = ExchangeFactory.INSTANCE.createExchange(OkCoinExchange.class.getName());
		ExchangeSpecification okcoinSpec = okcoin.getDefaultExchangeSpecification();
		okcoinSpec.setExchangeSpecificParametersItem("Use_Intl", true);

		Exchange okcoinFutures = ExchangeFactory.INSTANCE.createExchange(OkCoinExchange.class.getName());
		ExchangeSpecification okCoinFutureSpec = okcoinFutures.getDefaultExchangeSpecification();
		okCoinFutureSpec.setExchangeSpecificParametersItem("Use_Intl", true);
		okCoinFutureSpec.setExchangeSpecificParametersItem("Use_Futures", true);

		exchanges.add(ExchangeFactory.INSTANCE.createExchange(okcoinSpec));
		// exchanges.add(ExchangeFactory.INSTANCE.createExchange(okCoinFutureSpec));

		Exchange bfx = ExchangeFactory.INSTANCE.createExchange(BitfinexExchange.class.getName());

		ExchangeSpecification bitfinexSpec = bfx.getDefaultExchangeSpecification();


		Exchange bter = ExchangeFactory.INSTANCE.createExchange(BTERExchange.class.getName());

		exchanges.add(ExchangeFactory.INSTANCE.createExchange(bitfinexSpec));
		exchanges.add(bter);
		exchanges.add(ExchangeFactory.INSTANCE.createExchange(btceSpec));
		//Exchange referenceExchange = ExchangeFactory.INSTANCE.createExchange(cryptsy);
	//	exchanges.add(referenceExchange);

		Exchange yacuna = ExchangeFactory.INSTANCE.createExchange(YacunaExchange.class.getName());


		// exchanges.add(yacuna); Not Implemented yet
		ExchangeSpecification bitstamp = ExchangeFactory.INSTANCE.createExchange(BitstampExchange.class.getName()).getDefaultExchangeSpecification();


		exchanges.add(ExchangeFactory.INSTANCE.createExchange(bitstamp));

		Exchange kraken = ExchangeFactory.INSTANCE.createExchange(KrakenExchange.class.getName());


		ExchangeSpecification coinbaseex = ExchangeFactory.INSTANCE.createExchange(CoinbaseExExchange.class.getName()).getDefaultExchangeSpecification();
	

		ExchangeSpecification bittrex = ExchangeFactory.INSTANCE.createExchange(BittrexExchange.class.getName()).getDefaultExchangeSpecification();

		exchanges.add(ExchangeFactory.INSTANCE.createExchange(bittrex));

		List<LiveTickerWriter> tickers = new ArrayList<>();
		List<LiveFutureTickerWriter> futureTickers = new ArrayList<>();
		futureTickers.add(new LiveFutureTickerWriter(ExchangeFactory.INSTANCE.createExchange(okCoinFutureSpec)));

		exchanges.forEach(exchange -> tickers.add(new LiveTickerWriter(exchange)));
		PeriodFormatter periodFormat = new PeriodFormatterBuilder().printZeroRarelyLast().appendYears().appendSuffix(" year ", " years ").printZeroRarelyLast().appendMonths()
				.appendSuffix(" month ", " months ").appendDays().appendSuffix(" day ", " days ").appendHours().appendSuffix(" hour ", " hours ").appendSeparator("and ")
				.appendMinutes().appendSuffix(" minute ", " minutes ").toFormatter();
		while (true) {
			// System.out.println("------");
			// tickers.forEach(ticker -> {
			// try {
			// System.out.println(ticker.db.exchangeName);
			// ticker.exchange.getPollingMarketDataService().getExchangeSymbols().forEach(pair
			// -> System.out.println(pair + " : " +
			// periodFormat.print(ticker.getAvailableDataLength(pair))));
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			//
			// });

			Thread.sleep(60000);
		}
	}

}
