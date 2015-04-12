package com.tsavo.hippo;

import com.xeiam.xchange.dto.marketdata.Trade;

public interface TradeListener {

	public void handleTrade(Trade trade);
}
