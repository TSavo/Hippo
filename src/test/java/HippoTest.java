import com.tsavo.hippo.MongoTickerDatabase;
import com.tsavo.hippo.OHLCVDataSet;
import com.tsavo.hippo.TickerDatabase;
import org.joda.time.Duration;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Trade;

import java.util.Set;

/**
 * Created by evilg on 10/5/2017.
 */
public class HippoTest {

    public static void main(String[] args) {
        TickerDatabase ticker = new MongoTickerDatabase("Bittrex");
        Set<Trade> rawPriceData = ticker.get(new CurrencyPair("ETH", "BTC"));
        OHLCVDataSet data = new OHLCVDataSet(rawPriceData, new Duration(360000));
        data.stream().forEach(x -> System.out.println(x.volume));
    }
}

