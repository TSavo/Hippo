import com.tsavo.hippo.*;
import org.joda.time.Duration;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Trade;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by evilg on 10/5/2017.
 */
public class HippoTest {

    public static void main(String[] args) {
        TickerDatabase ticker = new MongoTickerDatabase("Bittrex");
        Set<Trade> rawPriceData = ticker.get(new CurrencyPair("BTC", "USDT"));
        OHLCVDataSet data = new OHLCVDataSet(rawPriceData, Duration.standardHours(1));
        WeightedMovingAverageFunction wma = new WeightedMovingAverageFunction(10);
        data.stream().map(x -> new WeightedSample(x.volume, BigDecimal.ONE)).forEach(x -> {
            wma.addSample(x);
            System.out.println(wma.getAverage());
        });

    }
}

