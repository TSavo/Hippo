package com.tsavo.hippo;

import com.mongodb.*;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.Trade;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by evilg on 7/6/2017.
 */
public class MongoTickerDatabase implements TickerDatabase {
    static DB db;

    static {
        MongoCredential credential = MongoCredential.createScramSha1Credential(System.getenv("HIPPO_DB_USERNAME"), System.getenv("HIPPO_DB_NAME"), System.getenv("HIPPO_DB_PASSWORD").toCharArray());
        db = new MongoClient(new ServerAddress(System.getenv("HIPPO_DB_URL")), Arrays.asList(credential)).getDB(System.getenv("HIPPO_DB_NAME"));

    }

    public final String exchange;

    public MongoTickerDatabase(String anExchangeName) {
        this.exchange = anExchangeName;
    }

    public static DBObject toDBOject(Trade aTrade) {
        return new BasicDBObject("_id", aTrade.getId())
                .append("price", aTrade.getPrice().setScale(8, BigDecimal.ROUND_HALF_EVEN).doubleValue())
                .append("amount", aTrade.getTradableAmount().setScale(8, BigDecimal.ROUND_HALF_EVEN).doubleValue())
                .append("type", aTrade.getType().toString())
                .append("timestamp", aTrade.getTimestamp())
                .append("currencyPair", aTrade.getCurrencyPair().toString());
    }

    public static Trade toTrade(DBObject anObject) {
        return new Trade.Builder().id((String) anObject.get("_id"))
                .currencyPair(new CurrencyPair((String) anObject.get("currencyPair")))
                .price(new BigDecimal(((Double) anObject.get("price"))))
                .timestamp((Date) anObject.get("timestamp"))
                .tradableAmount(new BigDecimal(((Double) anObject.get("amount"))))
                .type(((String) anObject.get("type")) == "ASK" ? Order.OrderType.ASK : Order.OrderType.BID)
                .build();
    }

    @Override
    public void addTradeListener(CurrencyPair aPair, TradeListener aListener) {
        throw new RuntimeException("Not Implemented");
    }

    public void putAll(CurrencyPair aPair, Set<Trade> someTrades) {
        DBCollection collection = db.getCollection(exchange + "-" + aPair);
        someTrades.forEach(x -> {
            collection.update(new BasicDBObject("_id", x.getId()), new BasicDBObject("$set", toDBOject(x)), true, false);
        });
    }

    @Override
    public void put(CurrencyPair aPair, Trade anOrder) {
        DBCollection collection = db.getCollection(exchange + "-" + aPair);
        DBObject trade = toDBOject(anOrder);
        collection.update(new BasicDBObject("_id", anOrder.getId()), trade, true, false);
    }

    @Override
    public Set<Trade> get(CurrencyPair aPair) {
        DBCollection collection = db.getCollection(exchange + "-" + aPair);
        DBCursor cursor = collection.find();
        HashSet<Trade> trades = new HashSet<>();
        cursor.forEach(x -> trades.add(toTrade(x)));
        return trades;
    }
}
