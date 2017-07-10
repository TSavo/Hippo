package com.tsavo.hippo;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Trade;

import java.util.Set;

/**
 * Created by evilg on 6/29/2017.
 */
public interface TickerDatabase {


    void put(CurrencyPair aPair, Trade anOrder);

    Set<Trade> get(CurrencyPair aPair);

    void addTradeListener(CurrencyPair aPair, TradeListener aListener);

    void putAll(CurrencyPair aPair, Set<Trade> aList);
}
