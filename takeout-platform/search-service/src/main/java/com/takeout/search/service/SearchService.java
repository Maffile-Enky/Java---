package com.takeout.search.service;

import com.takeout.search.dto.SearchRequest;
import com.takeout.search.dto.SearchResult;
import com.takeout.search.entity.SearchDish;
import com.takeout.search.entity.SearchMerchant;

import java.math.BigDecimal;
import java.util.List;

public interface SearchService {

    /**
     * Search merchants by keyword, category, and location
     */
    SearchResult searchMerchants(SearchRequest request);

    /**
     * Search dishes by keyword
     */
    SearchResult searchDishes(SearchRequest request);

    /**
     * Sync (insert or update) merchant data from merchant-service
     */
    void syncMerchant(SearchMerchant merchant);

    /**
     * Sync (insert or update) dish data from merchant-service
     */
    void syncDish(SearchDish dish);

    /**
     * Get nearby merchants within a given radius (km)
     */
    List<SearchMerchant> getNearbyMerchants(BigDecimal longitude, BigDecimal latitude, double radiusKm);

    /**
     * Get popular/hot merchants ordered by monthly sales
     */
    List<SearchMerchant> getPopularMerchants(int limit);
}
