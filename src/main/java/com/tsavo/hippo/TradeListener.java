package com.tsavo.hippo;

import org.knowm.xchange.dto.marketdata.Trade;

public interface TradeListener {

	public void handleTrade(Trade trade);
}
