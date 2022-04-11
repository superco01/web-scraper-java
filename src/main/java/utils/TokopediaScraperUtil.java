package utils;

import com.opencsv.CSVWriter;
import constants.ProductConstant;
import constants.TokopediaConstant;
import model.Product;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TokopediaScraperUtil {

    public static List<Product> extract(int size) throws IOException {

        String url = TokopediaConstant.BASE_URL + TokopediaConstant.CATEGORY
                + TokopediaConstant.SUB_CATEGORY + TokopediaConstant.URL_PARAMETER;
        int pageCounter = 1;
        List<Product> productList = new ArrayList<>();
        Product product;

        while (productList.size() < size) {
            Connection connection = Jsoup.connect(url + pageCounter)
                    .referrer(TokopediaConstant.BASE_URL + TokopediaConstant.CATEGORY + TokopediaConstant.SUB_CATEGORY)
                    .userAgent(TokopediaConstant.USER_AGENT);

            Document doc = connection.get();
            Elements productElements = doc.select(TokopediaConstant.HTML_PRODUCT_LIST);

            for (Element element :
                    productElements) {
                Elements image = element.select(TokopediaConstant.HTML_IMG_TITLE);
                String decodedUrl = TokopediaConstant.BASE_URL + URLDecoder.decode
                                (element.attr(TokopediaConstant.HREF), TokopediaConstant.ENC)
                        .split(TokopediaConstant.BASE_URL)[1];
                Document docDetails = Jsoup.connect(decodedUrl).userAgent(TokopediaConstant.USER_AGENT).get();
                product = new Product();
                product.setName(image.attr(TokopediaConstant.ALT));
                product.setPrice(element.select(TokopediaConstant.HTML_PRICE).text());
                product.setMerchant(Objects.requireNonNull(element.select(TokopediaConstant.HTML_MERCHANT)
                        .select(TokopediaConstant.SPAN).last()).text());
                product.setRating(element.select(TokopediaConstant.HTML_RATING).size()
                        + TokopediaConstant.RATING_RANGE);
                product.setDescription(docDetails.select(TokopediaConstant.HTML_DESCRIPTION).text());
                product.setImageLink(image.attr(TokopediaConstant.SRC));
                productList.add(product);
                pageCounter++;
                if (productElements.size() >= size) {
                    break;
                }
            }
        }
        return productList;
    }

    public static void export(List<Product> productList, String fileName) throws IOException {

        List<String[]> list = new ArrayList<>();
        String[] header = {
                ProductConstant.NAME,
                ProductConstant.DESCRIPTION,
                ProductConstant.IMAGE_LINK,
                ProductConstant.PRICE,
                ProductConstant.RATING,
                ProductConstant.MERCHANT
        };

        list.add(header);
        String[] value;
        for (Product product : productList) {
            value = new String[]{
                    product.getName(),
                    product.getDescription(),
                    product.getImageLink(),
                    product.getPrice(),
                    product.getRating(),
                    product.getMerchant()
            };
            list.add(value);
        }

        CSVWriter writer = new CSVWriter(new FileWriter(fileName));
        writer.writeAll(list);
        writer.flush();
    }
}