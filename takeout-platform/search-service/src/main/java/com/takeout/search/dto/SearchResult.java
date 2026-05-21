package com.takeout.search.dto;

import com.takeout.search.entity.SearchDish;
import com.takeout.search.entity.SearchMerchant;
import lombok.Data;

import java.util.List;

@Data
public class SearchResult {

    private List<SearchMerchant> merchants;

    private List<SearchDish> dishes;

    private Integer totalMerchants;

    private Integer totalDishes;
}
