package com.tsavo.hippo;

import com.firebase.client.*;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.okcoin.FuturesContract;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;

public class FutureTickerDatabase {

	static Firebase firebase = new Firebase("https://cryptomarkets.firebaseio.com/");
	static volatile boolean running = true;
	Firebase map;
	public String exchangeName;

	public FutureTickerDatabase(String anExchangeName) {
		exchangeName = anExchangeName;
		map = firebase.child(anExchangeName).child("Futures");
	}

	public void put(FuturesContract aContract, CurrencyPair aPair, Trade anOrder) {
		Firebase order = map.child(aContract.getName()).child(aPair.toString()).child(anOrder.getId());
		order.child("id").setValue(anOrder.getId());
		order.child("amount").setValue(anOrder.getTradableAmount().setScale(8, RoundingMode.HALF_DOWN).stripTrailingZeros().floatValue());
		order.child("price").setValue(anOrder.getPrice().setScale(8, RoundingMode.HALF_DOWN).stripTrailingZeros().floatValue());
		order.child("timestamp").setValue(anOrder.getTimestamp().getTime());
		order.child("type").setValue(anOrder.getType() == null ? null : anOrder.getType().toString().toLowerCase());
		System.out.println("Committed: " + exchangeName + " " + anOrder);
	}

	public SortedSet<Trade> get(FuturesContract aContract, CurrencyPair aPair) {
		TreeSet<Trade> trades = new TreeSet<>();
		Semaphore semaphore = new Semaphore(0);
		map.child(aContract.getName()).child(aPair.toString()).addListenerForSingleValueEvent(new ValueEventListener() {

			@Override
			public void onDataChange(DataSnapshot aTrade) {
				aTrade.getChildren().forEach(x -> {
					String id = x.getKey();
					BigDecimal tradableAmount = new BigDecimal(x.child("amount").getValue().toString());
					BigDecimal price = new BigDecimal(x.child("price").getValue().toString());
					Date timestamp = new Date(Long.parseLong(x.child("timestamp").getValue().toString()));
					Order.OrderType type = x.child("type").getValue() == null ? null : x.child("type").getValue().toString().equals("ask") ? Order.OrderType.ASK : Order.OrderType.BID;
					trades.add(new Trade(type, tradableAmount, aPair, price, timestamp, id));
				});
				semaphore.release();
			}

			@Override
			public void onCancelled(FirebaseError arg0) {
				System.out.println("canceled");
			}
		});
		try {
			semaphore.acquire();
			semaphore.release();
		} catch (InterruptedException e) {
			return new TreeSet<Trade>();
		}
		return trades;
	}

	public void addTradeListener(FuturesContract aContract, CurrencyPair aPair, TradeListener aListener) {
		Firebase pair = map.child(aContract.getName()).child(aPair.toString());
		pair.addChildEventListener(new ChildEventListener() {

			@Override
			public void onChildRemoved(DataSnapshot arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onChildChanged(DataSnapshot x, String arg1) {
				if (x.getChildrenCount() < 5) {
					return;
				}
				String id = x.getKey();
				BigDecimal tradableAmount = new BigDecimal(x.child("amount").getValue().toString());
				BigDecimal price = new BigDecimal(x.child("price").getValue().toString());
				Date timestamp = new Date(Long.parseLong(x.child("timestamp").getValue().toString()));
				Order.OrderType type = x.child("type").getValue() == null ? null : x.child("type").getValue().toString().equals("ask") ? Order.OrderType.ASK : Order.OrderType.BID;
				aListener.handleTrade(new Trade(type, tradableAmount, aPair, price, timestamp, id));

			}

			@Override
			public void onChildAdded(DataSnapshot x, String arg1) {
			}

			@Override
			public void onCancelled(FirebaseError arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void putAll(FuturesContract aContract, SortedSet<Trade> aList) {
		aList.forEach(trade -> {
			Firebase pair = map.child(aContract.getName()).child(trade.getCurrencyPair().toString());
			Firebase order = pair.child(trade.getId());
			order.child("id").setValue(trade.getId());
			order.child("amount").setValue(trade.getTradableAmount().setScale(8, RoundingMode.HALF_DOWN).stripTrailingZeros().floatValue());
			order.child("price").setValue(trade.getPrice().setScale(8, RoundingMode.HALF_DOWN).stripTrailingZeros().floatValue());
			order.child("timestamp").setValue(trade.getTimestamp().getTime());
			order.child("type").setValue(trade.getType() == null ? null : trade.getType().toString().toLowerCase());

		});
		System.out.println("Committed to " + exchangeName + ": " + aList.size() + " orders.");
	}

	public static void close() {
		running = false;
	}

}
