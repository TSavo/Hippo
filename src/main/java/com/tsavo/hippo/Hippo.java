package com.tsavo.hippo;

import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.bitbay.BitbayExchange;
import org.knowm.xchange.bitfinex.v1.BitfinexExchange;
import org.knowm.xchange.bitmarket.BitMarketExchange;
import org.knowm.xchange.bitstamp.BitstampExchange;
import org.knowm.xchange.bittrex.v1.BittrexExchange;
import org.knowm.xchange.cexio.CexIOExchange;
import org.knowm.xchange.coinbase.CoinbaseExchange;
import org.knowm.xchange.gdax.GDAXExchange;
import org.knowm.xchange.gemini.v1.GeminiExchange;
import org.knowm.xchange.hitbtc.HitbtcExchange;
import org.knowm.xchange.itbit.v1.ItBitExchange;
import org.knowm.xchange.kraken.KrakenExchange;
import org.knowm.xchange.okcoin.OkCoinExchange;
import org.knowm.xchange.poloniex.PoloniexExchange;
import org.knowm.xchange.yobit.YoBitExchange;

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

		Exchange bitfinexExchange = new BitfinexExchange();
		ExchangeSpecification bitfinexSpec = bitfinexExchange.getDefaultExchangeSpecification();
		bitfinexExchange.applySpecification(bitfinexSpec);
		exchanges.add(bitfinexExchange);

		Exchange bittrex = new BittrexExchange();
		ExchangeSpecification bittrexSpec = bittrex.getDefaultExchangeSpecification();
		bittrex.applySpecification(bittrexSpec);
		exchanges.add(bittrex);


		Exchange gdax = new GDAXExchange();
		ExchangeSpecification gdaxSpec = gdax.getDefaultExchangeSpecification();
		gdax.applySpecification(gdaxSpec);
		exchanges.add(gdax);

		Exchange cexio = new CexIOExchange();
		ExchangeSpecification cexioSpec = cexio.getDefaultExchangeSpecification();
		cexio.applySpecification(cexioSpec);
		exchanges.add(cexio);

		Exchange gemini = new GeminiExchange();
		ExchangeSpecification geminiSpec = gemini.getDefaultExchangeSpecification();
		gemini.applySpecification(geminiSpec);
		exchanges.add(gemini);

		Exchange hitbtc = new HitbtcExchange();
		hitbtc.applySpecification(hitbtc.getDefaultExchangeSpecification());
		exchanges.add(hitbtc);

		Exchange bitstamp = new BitstampExchange();
		bitstamp.applySpecification(bitstamp.getDefaultExchangeSpecification());
		exchanges.add(bitstamp);

		Exchange bitmarket = new BitMarketExchange();
		bitmarket.applySpecification(bitmarket.getDefaultExchangeSpecification());
		exchanges.add(bitmarket);

		Exchange yobit = new YoBitExchange();
		yobit.applySpecification(yobit.getDefaultExchangeSpecification());
		exchanges.add(yobit);



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
