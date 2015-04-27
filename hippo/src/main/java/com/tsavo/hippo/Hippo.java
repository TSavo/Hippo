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
		ExchangeSpecification cryptsy = new ExchangeSpecification(CryptsyExchange.class);
		cryptsy.setApiKey("1948367b66763024000812b257c1c5907e1e36fb");
		cryptsy.setSecretKey("9c5baae0e58978fd7daa317ce2418980aae3ee0ed2dc623dbd78dfdd5ef319ff78b0f80a5a1a9178");

		Exchange btce = ExchangeFactory.INSTANCE.createExchange(BTCEExchange.class.getName());
		ExchangeSpecification btceSpec = btce.getDefaultExchangeSpecification();
		btceSpec.setApiKey("OIW7IED5-XTPBARNQ-78G66VT3-NOX4NY4T-CBPV070X");
		btceSpec.setSecretKey("ccba84ce0908586fd2baa360e7da98d9cd5be4109a11cb8bab4269025652b834");

		Exchange okcoin = ExchangeFactory.INSTANCE.createExchange(OkCoinExchange.class.getName());
		ExchangeSpecification okcoinSpec = okcoin.getDefaultExchangeSpecification();
		okcoinSpec.setExchangeSpecificParametersItem("Use_Intl", true);
		okcoinSpec.setApiKey("65e10714-24a8-4587-a79e-ae9e4579fe29");
		okcoinSpec.setSecretKey("692338766D348C051F91B189674D6D3E");

		exchanges.add(ExchangeFactory.INSTANCE.createExchange(okcoinSpec));

		Exchange bfx = ExchangeFactory.INSTANCE.createExchange(BitfinexExchange.class.getName());

		ExchangeSpecification bitfinexSpec = bfx.getDefaultExchangeSpecification();

		bitfinexSpec.setApiKey("fqnmXJypz1WV5qGdxf9qPLEqYuhJ1l0BOVzJOBgz5y9");
		bitfinexSpec.setSecretKey("7LaUvSp90XOoYkDM9mOf3vr7iwwhFuTcqfQs0VQxDdm");

		Exchange bter = ExchangeFactory.INSTANCE.createExchange(BTERExchange.class.getName());
		bter.getExchangeSpecification().setApiKey("6B8C5A04-2FBF-4643-AE67-616D0066A412");
		bter.getExchangeSpecification().setSecretKey("63d5c7892f0831d8d04a9af8ddaefeb498ec0822edcf8b47073689f3783d2ee8");

		exchanges.add(ExchangeFactory.INSTANCE.createExchange(bitfinexSpec));
		exchanges.add(bter);
		exchanges.add(ExchangeFactory.INSTANCE.createExchange(btceSpec));
		Exchange referenceExchange = ExchangeFactory.INSTANCE.createExchange(cryptsy);
		exchanges.add(referenceExchange);
		
		Exchange yacuna = ExchangeFactory.INSTANCE.createExchange(YacunaExchange.class.getName());
		yacuna.getExchangeSpecification().setApiKey("AAEAAAgfi0NutxlS0JuXBlez5MeK6PsB6b1-afrMV5iyAT6461D079U2");
		yacuna.getExchangeSpecification().setSecretKey("36422983b180fbc37095bd9530869bc9");

		//exchanges.add(yacuna); Not Implemented yet
		ExchangeSpecification bitstamp = ExchangeFactory.INSTANCE.createExchange(BitstampExchange.class.getName()).getDefaultExchangeSpecification();
		bitstamp.setUserName("67954");
		bitstamp.setApiKey("sfk8Ie2VbEkQdz0sqzE3DCoZ9LkTGCcy");
		bitstamp.setSecretKey("JqF632TD3I3fhLvDPqZPjWWO7ZyWXTFT");
		
		exchanges.add(ExchangeFactory.INSTANCE.createExchange(bitstamp));
		
		Exchange kraken = ExchangeFactory.INSTANCE.createExchange(KrakenExchange.class.getName());
		kraken.getExchangeSpecification().setApiKey("U65ezt/UHp1l61CNTRchqz9gP8ApTGBonTK53M7xBh5CEp2FrCLxsAWE");
		kraken.getExchangeSpecification().setSecretKey("y7Xv5rfE+bQ9hLy+Xd76sPIDibl38e4BYiC4iRRam+9aSr78CBAdToMgOPGukED0MJ3DleacCcmznVySGjvsPQ==");

		//exchanges.add(kraken); Not working right :(
		
		ExchangeSpecification coinbaseex = ExchangeFactory.INSTANCE.createExchange(CoinbaseExExchange.class.getName()).getDefaultExchangeSpecification();
		coinbaseex.setApiKey("fb0ce60e6e91177a4a440f47c4a023f9");
		coinbaseex.setSecretKey("MwtmETnIwxcR5zXPlMgzh80gSGtQX8w937XLJVwbHKIXKTADP3TqJEENmI8Tjl7/X1b7m11f32Sel2pGLefzvQ==");
		coinbaseex.setPassword("zabbas");
		//exchanges.add(ExchangeFactory.INSTANCE.createExchange(coinbaseex));
		
		
		ExchangeSpecification bittrex = ExchangeFactory.INSTANCE.createExchange(BittrexExchange.class.getName()).getDefaultExchangeSpecification();
		bittrex.setApiKey("8b4e28e1d39641d0bc9209250a02cfa5");
		bittrex.setSecretKey("521ac34799844fb3b7cdaa77bd9ea4b6");
		exchanges.add(ExchangeFactory.INSTANCE.createExchange(bittrex));
		
		List<LiveTickerWriter> tickers = new ArrayList<>();
		exchanges.forEach(exchange -> tickers.add(new LiveTickerWriter(exchange)));
		PeriodFormatter periodFormat = new PeriodFormatterBuilder()
	     .printZeroRarelyLast()
	     .appendYears()
	     .appendSuffix(" year ", " years ")
	     .printZeroRarelyLast()
	     .appendMonths()
	     .appendSuffix(" month ", " months ")
	     .appendDays()
	     .appendSuffix(" day ", " days ")
	     .appendHours()
	     .appendSuffix(" hour ", " hours ")
	     .appendSeparator("and ")
	     .appendMinutes()
	     .appendSuffix(" minute ", " minutes ")
	     .toFormatter();
		while (true) {
//			System.out.println("------");
//			tickers.forEach(ticker -> {
//				try {
//					System.out.println(ticker.db.exchangeName);
//					ticker.exchange.getPollingMarketDataService().getExchangeSymbols().forEach(pair -> System.out.println(pair + " : " + periodFormat.print(ticker.getAvailableDataLength(pair))));
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//			});

			Thread.sleep(60000);
		}
	}

}
