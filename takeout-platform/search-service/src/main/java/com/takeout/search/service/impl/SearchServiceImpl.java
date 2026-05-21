package com.takeout.search.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeout.search.dto.SearchRequest;
import com.takeout.search.dto.SearchResult;
import com.takeout.search.entity.SearchDish;
import com.takeout.search.entity.SearchMerchant;
import com.takeout.search.mapper.SearchDishMapper;
import com.takeout.search.mapper.SearchMerchantMapper;
import com.takeout.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final SearchMerchantMapper searchMerchantMapper;
    private final SearchDishMapper searchDishMapper;

    @Override
    public SearchResult searchMerchants(SearchRequest request) {
        Page<SearchMerchant> page = new Page<>(request.getPage(), request.getSize());
        LambdaQueryWrapper<SearchMerchant> wrapper = new LambdaQueryWrapper<>();

        // Only search active merchants
        wrapper.eq(SearchMerchant::getStatus, 1);

        // Keyword search - match name, address, description, categories
        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            wrapper.and(w -> w
                    .like(SearchMerchant::getName, request.getKeyword())
                    .or().like(SearchMerchant::getAddress, request.getKeyword())
                    .or().like(SearchMerchant::getDescription, request.getKeyword())
                    .or().like(SearchMerchant::getCategories, request.getKeyword()));
        }

        // Category filter
        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            wrapper.like(SearchMerchant::getCategories, request.getCategory());
        }

        // Sorting
        applyMerchantSort(wrapper, request.getSortBy());

        IPage<SearchMerchant> merchantPage = searchMerchantMapper.selectPage(page, wrapper);

        // If location is provided, calculate distance and optionally re-sort by distance
        List<SearchMerchant> merchants = merchantPage.getRecords();
        if (request.getLongitude() != null && request.getLatitude() != null
                && "distance".equals(request.getSortBy())) {
            merchants.sort(Comparator.comparingDouble(m ->
                    haversine(request.getLatitude().doubleValue(), request.getLongitude().doubleValue(),
                            m.getLatitude() != null ? m.getLatitude().doubleValue() : 0,
                            m.getLongitude() != null ? m.getLongitude().doubleValue() : 0)));
        }

        SearchResult result = new SearchResult();
        result.setMerchants(merchants);
        result.setTotalMerchants((int) merchantPage.getTotal());
        result.setDishes(new ArrayList<>());
        result.setTotalDishes(0);
        return result;
    }

    @Override
    public SearchResult searchDishes(SearchRequest request) {
        Page<SearchDish> page = new Page<>(request.getPage(), request.getSize());
        LambdaQueryWrapper<SearchDish> wrapper = new LambdaQueryWrapper<>();

        // Only search active dishes
        wrapper.eq(SearchDish::getStatus, 1);

        // Keyword search - match name, description, category, merchantName
        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            wrapper.and(w -> w
                    .like(SearchDish::getName, request.getKeyword())
                    .or().like(SearchDish::getDescription, request.getKeyword())
                    .or().like(SearchDish::getCategory, request.getKeyword())
                    .or().like(SearchDish::getMerchantName, request.getKeyword()));
        }

        // Category filter
        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            wrapper.eq(SearchDish::getCategory, request.getCategory());
        }

        // Sorting
        applyDishSort(wrapper, request.getSortBy());

        IPage<SearchDish> dishPage = searchDishMapper.selectPage(page, wrapper);

        SearchResult result = new SearchResult();
        result.setMerchants(new ArrayList<>());
        result.setTotalMerchants(0);
        result.setDishes(dishPage.getRecords());
        result.setTotalDishes((int) dishPage.getTotal());
        return result;
    }

    @Override
    public void syncMerchant(SearchMerchant merchant) {
        if (merchant.getMerchantId() == null) {
            log.warn("Sync merchant failed: merchantId is null");
            return;
        }
        // Check if merchant already exists
        SearchMerchant existing = searchMerchantMapper.selectOne(
                new LambdaQueryWrapper<SearchMerchant>()
                        .eq(SearchMerchant::getMerchantId, merchant.getMerchantId()));
        if (existing != null) {
            merchant.setId(existing.getId());
            searchMerchantMapper.updateById(merchant);
            log.info("Updated search merchant: merchantId={}", merchant.getMerchantId());
        } else {
            searchMerchantMapper.insert(merchant);
            log.info("Inserted search merchant: merchantId={}", merchant.getMerchantId());
        }
    }

    @Override
    public void syncDish(SearchDish dish) {
        if (dish.getDishId() == null) {
            log.warn("Sync dish failed: dishId is null");
            return;
        }
        // Check if dish already exists
        SearchDish existing = searchDishMapper.selectOne(
                new LambdaQueryWrapper<SearchDish>()
                        .eq(SearchDish::getDishId, dish.getDishId()));
        if (existing != null) {
            dish.setId(existing.getId());
            searchDishMapper.updateById(dish);
            log.info("Updated search dish: dishId={}", dish.getDishId());
        } else {
            searchDishMapper.insert(dish);
            log.info("Inserted search dish: dishId={}", dish.getDishId());
        }
    }

    @Override
    public List<SearchMerchant> getNearbyMerchants(BigDecimal longitude, BigDecimal latitude, double radiusKm) {
        // Fetch all active merchants and filter by distance in memory
        List<SearchMerchant> allMerchants = searchMerchantMapper.selectList(
                new LambdaQueryWrapper<SearchMerchant>()
                        .eq(SearchMerchant::getStatus, 1));

        double lat = latitude.doubleValue();
        double lon = longitude.doubleValue();

        List<SearchMerchant> nearby = new ArrayList<>();
        for (SearchMerchant m : allMerchants) {
            if (m.getLatitude() == null || m.getLongitude() == null) {
                continue;
            }
            double dist = haversine(lat, lon, m.getLatitude().doubleValue(), m.getLongitude().doubleValue());
            if (dist <= radiusKm) {
                nearby.add(m);
            }
        }

        nearby.sort(Comparator.comparingDouble(m ->
                haversine(lat, lon, m.getLatitude().doubleValue(), m.getLongitude().doubleValue())));

        return nearby;
    }

    @Override
    public List<SearchMerchant> getPopularMerchants(int limit) {
        return searchMerchantMapper.selectList(
                new LambdaQueryWrapper<SearchMerchant>()
                        .eq(SearchMerchant::getStatus, 1)
                        .orderByDesc(SearchMerchant::getMonthlySales)
                        .last("LIMIT " + limit));
    }

    /**
     * Apply sort to merchant query wrapper
     */
    private void applyMerchantSort(LambdaQueryWrapper<SearchMerchant> wrapper, String sortBy) {
        if (sortBy == null) {
            sortBy = "default";
        }
        switch (sortBy) {
            case "rating":
                wrapper.orderByDesc(SearchMerchant::getRating);
                break;
            case "sales":
                wrapper.orderByDesc(SearchMerchant::getMonthlySales);
                break;
            case "distance":
                // Distance sorting is done in memory after query (needs user location)
                wrapper.orderByDesc(SearchMerchant::getRating);
                break;
            default:
                // Default: sort by monthly sales descending
                wrapper.orderByDesc(SearchMerchant::getMonthlySales);
                break;
        }
    }

    /**
     * Apply sort to dish query wrapper
     */
    private void applyDishSort(LambdaQueryWrapper<SearchDish> wrapper, String sortBy) {
        if (sortBy == null) {
            sortBy = "default";
        }
        switch (sortBy) {
            case "rating":
                wrapper.orderByDesc(SearchDish::getRating);
                break;
            case "sales":
                wrapper.orderByDesc(SearchDish::getSales);
                break;
            case "price":
                wrapper.orderByAsc(SearchDish::getPrice);
                break;
            default:
                // Default: sort by sales descending
                wrapper.orderByDesc(SearchDish::getSales);
                break;
        }
    }

    /**
     * Calculate distance between two coordinates using the Haversine formula.
     * Returns distance in kilometers.
     */
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double EARTH_RADIUS_KM = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
}
