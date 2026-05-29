package com.takeout.merchant.service;

import com.takeout.merchant.dto.InventoryDTO;

public interface InventoryService {

    /**
     * 扣减库存（支持分布式事务）
     * @param inventoryDTO 库存扣减信息
     * @return 是否扣减成功
     */
    boolean deductStock(InventoryDTO inventoryDTO);

    /**
     * 返还库存（用于取消订单时）
     * @param inventoryDTO 库存返还信息
     * @return 是否返还成功
     */
    boolean revertStock(InventoryDTO inventoryDTO);

    /**
     * 检查库存是否充足
     * @param dishId 菜品ID
     * @param quantity 数量
     * @return 是否充足
     */
    boolean checkStock(Long dishId, Integer quantity);
}
