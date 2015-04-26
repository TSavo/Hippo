package com.tsavo.hippo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.Order.OrderType;
import com.xeiam.xchange.dto.marketdata.Trade;

public class TickerDatabase {

	static Firebase firebase = new Firebase("https://cryptomarkets.firebaseio.com/");
	static volatile boolean running = true;
	Firebase map;
	public String exchangeName;

	public TickerDatabase(String anExchangeName) {
		exchangeName = anExchangeName;
		map = firebase.child(anExchangeName);
	}

	public void put(CurrencyPair aPair, Trade anOrder) {
		map.child(aPair.toString()).runTransaction(new Transaction.Handler() {
			@Override
			public Transaction.Result doTransaction(MutableData currentData) {
				MutableData order = currentData.child(anOrder.getId());
				order.child("id").setValue(anOrder.getId());
				order.child("amount").setValue(anOrder.getTradableAmount().setScale(8, RoundingMode.HALF_DOWN).stripTrailingZeros().floatValue());
				order.child("price").setValue(anOrder.getPrice().setScale(8, RoundingMode.HALF_DOWN).stripTrailingZeros().floatValue());
				order.child("timestamp").setValue(anOrder.getTimestamp().getTime());
				order.child("type").setValue(anOrder.getType() == null ? null : anOrder.getType().toString().toLowerCase());
				return Transaction.success(currentData);
			}

			@Override
			public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot currentData) {
				System.out.println("Committed: " + committed + " " + exchangeName + " " + anOrder);
			}
		});

	}

	public SortedSet<Trade> get(CurrencyPair aPair) {
		TreeSet<Trade> trades = new TreeSet<>();
		Semaphore semaphore = new Semaphore(0);
		map.child(aPair.toString()).addListenerForSingleValueEvent(new ValueEventListener() {

			@Override
			public void onDataChange(DataSnapshot aTrade) {
				aTrade.getChildren().forEach(x -> {
					String id = x.getKey();
					BigDecimal tradableAmount = new BigDecimal(x.child("amount").getValue().toString());
					BigDecimal price = new BigDecimal(x.child("price").getValue().toString());
					Date timestamp = new Date(Long.parseLong(x.child("timestamp").getValue().toString()));
					OrderType type = x.child("type").getValue() == null ? null : x.child("type").getValue().toString().equals("ask") ? OrderType.ASK : OrderType.BID;
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

	public void addTradeListener(CurrencyPair aPair, TradeListener aListener) {
		map.child(aPair.toString()).addChildEventListener(new ChildEventListener() {

			@Override
			public void onChildRemoved(DataSnapshot arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onChildChanged(DataSnapshot arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onChildAdded(DataSnapshot x, String arg1) {
				String id = x.getKey();
				BigDecimal tradableAmount = new BigDecimal(x.child("amount").getValue().toString());
				BigDecimal price = new BigDecimal(x.child("price").getValue().toString());
				Date timestamp = new Date(Long.parseLong(x.child("timestamp").getValue().toString()));
				OrderType type = x.child("type").getValue() == null ? null : x.child("type").getValue().toString().equals("ask") ? OrderType.ASK : OrderType.BID;
				aListener.handleTrade(new Trade(type, tradableAmount, aPair, price, timestamp, id));
			}

			@Override
			public void onCancelled(FirebaseError arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void putAll(SortedSet<Trade> aList) {
		map.runTransaction(new Transaction.Handler() {
			@Override
			public Transaction.Result doTransaction(MutableData currentData) {
				aList.forEach(trade -> {
					MutableData pair = currentData.child(trade.getCurrencyPair().toString());
					MutableData order = pair.child(trade.getId());
					order.child("id").setValue(trade.getId());
					order.child("amount").setValue(trade.getTradableAmount().setScale(8, RoundingMode.HALF_DOWN).stripTrailingZeros().floatValue());
					order.child("price").setValue(trade.getPrice().setScale(8, RoundingMode.HALF_DOWN).stripTrailingZeros().floatValue());
					order.child("timestamp").setValue(trade.getTimestamp().getTime());
					order.child("type").setValue(trade.getType() == null ? null : trade.getType().toString().toLowerCase());

				});
				return Transaction.success(currentData);

			}

			@Override
			public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot currentData) {
				System.out.println("Committed: " + committed + " " + exchangeName + " " + aList.size() + " orders.");
			}
		});
		
		

	}

	public static void close() {
		running = false;
	}

}
