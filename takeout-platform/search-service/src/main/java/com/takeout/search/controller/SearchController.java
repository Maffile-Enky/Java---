package com.takeout.search.controller;

import com.takeout.common.core.annotation.RequireRole;
import com.takeout.common.core.domain.Result;
import com.takeout.common.core.enums.RoleEnum;
import com.takeout.search.dto.SearchRequest;
import com.takeout.search.dto.SearchResult;
import com.takeout.search.entity.SearchDish;
import com.takeout.search.entity.SearchMerchant;
import com.takeout.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * Search merchants by keyword, category, sort, pagination
     */
    @GetMapping("/merchants")
    public Result<SearchResult> searchMerchants(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal longitude,
            @RequestParam(required = false) BigDecimal latitude,
            @RequestParam(defaultValue = "default") String sortBy,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        SearchRequest request = new SearchRequest();
        request.setKeyword(keyword);
        request.setCategory(category);
        request.setLongitude(longitude);
        request.setLatitude(latitude);
        request.setSortBy(sortBy);
        request.setPage(page);
        request.setSize(size);
        SearchResult result = searchService.searchMerchants(request);
        return Result.success(result);
    }

    /**
     * Search dishes by keyword, sort, pagination
     */
    @GetMapping("/dishes")
    public Result<SearchResult> searchDishes(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "default") String sortBy,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        SearchRequest request = new SearchRequest();
        request.setKeyword(keyword);
        request.setCategory(category);
        request.setSortBy(sortBy);
        request.setPage(page);
        request.setSize(size);
        SearchResult result = searchService.searchDishes(request);
        return Result.success(result);
    }

    /**
     * Get popular/hot merchants
     */
    @GetMapping("/hot")
    public Result<List<SearchMerchant>> getHotMerchants(
            @RequestParam(defaultValue = "10") Integer limit) {
        List<SearchMerchant> merchants = searchService.getPopularMerchants(limit);
        return Result.success(merchants);
    }

    /**
     * Get nearby merchants
     */
    @GetMapping("/nearby")
    public Result<List<SearchMerchant>> getNearbyMerchants(
            @RequestParam BigDecimal longitude,
            @RequestParam BigDecimal latitude,
            @RequestParam(defaultValue = "3.0") Double radius) {
        List<SearchMerchant> merchants = searchService.getNearbyMerchants(longitude, latitude, radius);
        return Result.success(merchants);
    }

    /**
     * Sync merchant data (internal API for other services to push data)
     */
    @PostMapping("/sync/merchant")
    @RequireRole(RoleEnum.ADMIN)
    public Result<Void> syncMerchant(@RequestBody SearchMerchant merchant) {
        searchService.syncMerchant(merchant);
        return Result.success(null);
    }

    /**
     * Sync dish data (internal API for other services to push data)
     */
    @PostMapping("/sync/dish")
    @RequireRole(RoleEnum.ADMIN)
    public Result<Void> syncDish(@RequestBody SearchDish dish) {
        searchService.syncDish(dish);
        return Result.success(null);
    }
}
