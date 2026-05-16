package com.takeout.search.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SearchRequest {

    private String keyword;

    private String category;

    private BigDecimal longitude;

    private BigDecimal latitude;

    /**
     * Sort type: default / distance / rating / sales / price
     */
    private String sortBy = "default";

    private Integer page = 1;

    private Integer size = 10;
}
