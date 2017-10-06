# Hippo

A cryptocurrency exchange recording and playback package, based on the XChange library.

## Reading Usage

To read data, you will need a database with data in it. A read only one is provided for you. You'll need to set your environment variables upon launch to the following values:
```
HIPPO_DB_URL=mongo.nefariousplan.com
HIPPO_DB_NAME=cryptomarkets
HIPPO_DB_USERNAME=hippo
HIPPO_DB_PASSWORD=hippo
```

Then, make a new MongoTickerDatabase, and get the feed for the currency pair, and turn that into a OHLCV data set:
```
TickerDatabase ticker = new MongoTickerDatabase("Bittrex");
Set<Trade> rawPriceData = ticker.get(new CurrencyPair("BTC", "USDT"));
OHLCVDataSet data = new OHLCVDataSet(rawPriceData, Duration.standardHours(1));
```

You can them stream that into any data analysis:
```
WeightedMovingAverageFunction wma = new WeightedMovingAverageFunction(10);
  data.stream().map(x -> new WeightedSample(x.volume, BigDecimal.ONE)).forEach(x -> {
  wma.addSample(x);
});
```

## Writing Usage

To write data, you will need to provide your own database and credentials in the environment variables.

Then construct your XChange exchange adapter:
```
Exchange cryptopiaExchange = new CryptopiaExchange();
cryptopiaExchange.applySpecification(cryptopiaExchange.getDefaultExchangeSpecification());
exchanges.add(cryptopiaExchange);
```

And give it to a LiveTickerWriter:
```
new LiveTickerWriter(exchange);
```

And watch the data stream in!

## Build docker image from Dockerfile 

```
docker build . -t tsavo/hippo:latest
```

---

All software is Copyright of Travis Savo <evilgenius@nefariousplan.com> and is licensed under the Apache License Version 2.0 AS IS without any guarentee that it will work or not do something stupid.
