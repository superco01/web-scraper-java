import model.Product;
import utils.TokopediaScraperUtil;

import java.io.IOException;
import java.util.List;

public class Application {

    public static void main(String[] args) throws IOException {

        List<Product> resultList = TokopediaScraperUtil.extract(100);
        TokopediaScraperUtil.export(resultList, "storage/Tokopedia_Phone.csv");
    }
}