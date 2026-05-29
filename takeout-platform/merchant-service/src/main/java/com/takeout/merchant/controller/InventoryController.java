package com.takeout.merchant.controller;

import com.takeout.common.core.domain.Result;
import com.takeout.merchant.dto.InventoryDTO;
import com.takeout.merchant.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Tag(name = "库存管理", description = "库存扣减、返还、检查接口")
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/deduct")
    @Operation(summary = "扣减库存", description = "订单创建时扣减菜品库存，支持幂等性")
    public Result<Boolean> deductStock(@Parameter(description = "库存操作DTO") @RequestBody InventoryDTO inventoryDTO) {
        boolean success = inventoryService.deductStock(inventoryDTO);
        return Result.success(success);
    }

    @PostMapping("/revert")
    @Operation(summary = "返还库存", description = "订单取消时返还菜品库存")
    public Result<Boolean> revertStock(@Parameter(description = "库存操作DTO") @RequestBody InventoryDTO inventoryDTO) {
        boolean success = inventoryService.revertStock(inventoryDTO);
        return Result.success(success);
    }

    @GetMapping("/check")
    @Operation(summary = "检查库存", description = "检查菜品库存是否充足")
    public Result<Boolean> checkStock(
            @Parameter(description = "菜品ID") @RequestParam Long dishId,
            @Parameter(description = "需要的数量") @RequestParam Integer quantity) {
        boolean sufficient = inventoryService.checkStock(dishId, quantity);
        return Result.success(sufficient);
    }
}
