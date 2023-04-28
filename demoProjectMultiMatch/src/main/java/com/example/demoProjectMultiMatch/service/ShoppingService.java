package com.example.demoProjectMultiMatch.service;

import com.example.demoProjectMultiMatch.model.Shopping;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ShoppingService {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RestHighLevelClient client;
    @Value("${elasticsearch.index.name}")
    private String index;

    public String createProduct(Shopping shopping) throws IOException {
        Map<String, Object> map = objectMapper.convertValue(shopping, Map.class);
        IndexRequest indexRequest = new IndexRequest(index).id(shopping.getProductId()).source(map);
        IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
        return response.getResult().name();
    }

    //multi match query
    public List<Shopping> multiQuery(String msg) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        String[] fileds = {"productDescription", "productCompanyName", "productName", "productPrice", "productColor"};
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery(msg, fileds));
//        searchSourceBuilder.query(QueryBuilders.multiMatchQuery(fileds.operator(Operator.AND));
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        List<Shopping> shoppingList = new ArrayList<>();
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> map = hits[0].getSourceAsMap();
            Shopping shopping = objectMapper.convertValue(map, Shopping.class);
            shoppingList.add(shopping);
        }
        return shoppingList;
    }

    //query string
    public List<Shopping> queryString(String msg) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        String[] fields = {"productDescription", "productCompanyName", "productName", "productPrice", "productColor"};
        String queryString = String.format("productDescription:%1$s OR productCompanyName:%1$s OR productName:%1$s OR productPrice:%1$s OR productColor:%1$s", msg);
        searchSourceBuilder.query(QueryBuilders.queryStringQuery(queryString).field(Arrays.toString(fields)));
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        List<Shopping> shoppingList = new ArrayList<>();
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            Shopping shopping = objectMapper.convertValue(map, Shopping.class);
            shoppingList.add(shopping);
        }
        return shoppingList;
    }

    //simple query String
    public List<Shopping> simpleString(String msg) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        String[] fields = {"productDescription", "productCompanyName", "productName", "productPrice", "productColor"};
        String queryString = String.format("productDescription:%1$s OR productCompanyName:%1$s OR productName:%1$s OR productPrice:%1$s OR productColor:%1$s", msg);
        searchSourceBuilder.query(QueryBuilders.simpleQueryStringQuery(queryString).field(Arrays.toString(fields)));
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        List<Shopping> shoppingList = new ArrayList<>();
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            Shopping shopping = objectMapper.convertValue(map, Shopping.class);
            shoppingList.add(shopping);
        }
        return shoppingList;
    }

    //range query for price
    public List<Shopping> rangePriceQuery(String miniprice, String maxprice) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.rangeQuery("productPrice").gte(miniprice).lte(maxprice));
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        ArrayList<Shopping> objects = new ArrayList<>();
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            Shopping shopping = objectMapper.convertValue(map, Shopping.class);
            objects.add(shopping);
        }
        return objects;
    }

    //range query using filter condition
    public List<Shopping> rangeFilter(String txt, String min, String max) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.boolQuery().must(QueryBuilders.multiMatchQuery(txt, "productName", "productPrice"))
                .filter(QueryBuilders.rangeQuery("productPrice").gte(min).lte(max)));
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
        ArrayList<Shopping> objects = new ArrayList<>();
        SearchHits hits = search.getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            Shopping shopping = objectMapper.convertValue(sourceAsMap, Shopping.class);
            objects.add(shopping);
        }
        return objects;
    }
    //requar experssion
    public List<Shopping> reqexpQuery(String txt) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder queryBuilder = QueryBuilders.regexpQuery("productName", ".*" + txt + ".*");
        searchSourceBuilder.query(queryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        List<Shopping> shoppingList = new ArrayList<>();
        for (SearchHit hit : hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            Shopping shopping = objectMapper.convertValue(map, Shopping.class);
            shoppingList.add(shopping);
        }
        return shoppingList;
    }

    //prefix query
    public List<Shopping> preQuery(String txt) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.prefixQuery("productName", txt));
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        ArrayList<Shopping> objects = new ArrayList<>();
        for(SearchHit hit:hits){
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            Shopping shopping = objectMapper.convertValue(sourceAsMap, Shopping.class);
            objects.add(shopping);
        }
        return objects;
    }
}

