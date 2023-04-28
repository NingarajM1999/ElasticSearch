package com.example.demoProjectMultiMatch.controller;

import com.example.demoProjectMultiMatch.model.Shopping;
import com.example.demoProjectMultiMatch.service.ShoppingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
public class ShoppingController {

    @Autowired
    private ShoppingService shoppingService;

    @PostMapping("/prd")
    public ResponseEntity<String> createProduct(@RequestBody Shopping shopping) throws IOException {
        return new ResponseEntity<>(shoppingService.createProduct(shopping), HttpStatus.CREATED);
    }
    @GetMapping("/prd")
    public ResponseEntity<List<Shopping>> getByMultimatch(@RequestParam String msg) throws IOException {
        return new ResponseEntity<>(shoppingService.multiQuery(msg),HttpStatus.OK);
    }
    @GetMapping("/queryString")
    public  ResponseEntity<List<Shopping>> getQueryString(@RequestParam String msg) throws IOException {
        return new ResponseEntity<>(shoppingService.queryString(msg),HttpStatus.OK);
    }
    @GetMapping("/rangePrice")
    public ResponseEntity<List<Shopping>> rangePrice(@RequestParam String miniprice,String maxprice) throws IOException {
        return new ResponseEntity<>(shoppingService.rangePriceQuery(miniprice,maxprice),HttpStatus.OK);
    }
    @GetMapping("/rangefilter")
    public ResponseEntity<List<Shopping>>rangeFilter(@RequestParam String txt,@RequestParam String min,@RequestParam String max) throws IOException {
        return new ResponseEntity<>(shoppingService.rangeFilter(txt,min,max),HttpStatus.OK);
    }
    @GetMapping("/simpleQuery")
    public ResponseEntity<List<Shopping>> simpleString(@RequestParam String msg) throws IOException {
        return new ResponseEntity<>(shoppingService.simpleString(msg),HttpStatus.OK);
    }
    @GetMapping("/reqexp")
    public ResponseEntity<List<Shopping>> reqQuery(@RequestParam String txt) throws IOException {
        return new ResponseEntity<>(shoppingService.reqexpQuery(txt),HttpStatus.OK);
    }
    @GetMapping("/preQuery")
    public ResponseEntity<List<Shopping>>preQuery(@RequestParam String txt) throws IOException {
        return new ResponseEntity<>(shoppingService.preQuery(txt),HttpStatus.OK);
    }

}
