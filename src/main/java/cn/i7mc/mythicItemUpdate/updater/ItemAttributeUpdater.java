package cn.i7mc.mythicItemUpdate.updater;

import cn.i7mc.mythicItemUpdate.MythicItemUpdate;
import cn.i7mc.mythicItemUpdate.data.DroppedItemData;
import cn.i7mc.mythicItemUpdate.data.MythicItemData;
import cn.i7mc.mythicItemUpdate.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.CompletableFuture;

/**
 * 物品属性更新器
 * 负责更新物品的具体属性和数据
 * 
 * @author i7mc
 * @version 1.0
 */
public class ItemAttributeUpdater extends AbstractItemUpdater {
    
    public ItemAttributeUpdater(MythicItemUpdate plugin) {
        super(plugin);
    }
    
    @Override
    protected boolean initializeUpdater() {
        info("物品属性更新器初始化成功");
        return true;
    }
    
    @Override
    protected boolean reloadUpdater() {
        info("物品属性更新器重载成功");
        return true;
    }
    
    @Override
    protected void shutdownUpdater() {
        debug("物品属性更新器已关闭");
    }
    
    @Override
    public String getName() {
        return "ItemAttributeUpdater";
    }
    
    @Override
    public UpdateResult updateItem(MythicItemData itemData, Player player) {
        return safeUpdate("更新物品: " + itemData.getInternalName(), () -> {

            // 检查物品数据有效性
            if (itemData == null || itemData.getOriginalItem() == null) {
                return createFailureResult("物品数据无效", null);
            }

            ItemStack originalItem = itemData.getOriginalItem();

            // 检查是否需要更新
            if (!needsUpdate(originalItem)) {
                sendDebugMessage("物品无需更新: " + itemData.getInternalName());
                return createSkippedResult("物品无需更新");
            }

            // 获取更新后的物品
            ItemStack updatedItem = getUpdatedItem(originalItem);
            if (updatedItem == null || updatedItem.equals(originalItem)) {
                return createSkippedResult("物品更新后无变化");
            }

            // 设置更新后的物品
            itemData.setUpdatedItem(updatedItem);

            // 根据位置类型执行不同的更新逻辑
            switch (itemData.getLocation()) {
                case INVENTORY:
                    return updateInventoryItem(itemData, player);
                case ENDERCHEST:
                    return updateEnderChestItem(itemData, player);
                case CONTAINER:
                    return updateContainerItem(itemData);
                default:
                    return createFailureResult("未知的物品位置类型", null);
            }
        });
    }
    
    @Override
    public UpdateResult updateDroppedItem(DroppedItemData droppedData) {
        return safeUpdate("更新掉落物品: " + droppedData.getInternalName(), () -> {

            // 检查掉落物品数据有效性
            if (droppedData == null || droppedData.getOriginalItem() == null) {
                return createFailureResult("掉落物品数据无效", null);
            }

            ItemStack originalItem = droppedData.getOriginalItem();

            // 检查是否需要更新
            if (!needsUpdate(originalItem)) {
                sendDebugMessage("掉落物品无需更新: " + droppedData.getInternalName());
                return createSkippedResult("掉落物品无需更新");
            }

            // 获取更新后的物品
            ItemStack updatedItem = getUpdatedItem(originalItem);
            if (updatedItem == null || updatedItem.equals(originalItem)) {
                return createSkippedResult("掉落物品更新后无变化");
            }

            // 设置更新后的物品
            droppedData.setUpdatedItem(updatedItem);

            // 更新世界中的掉落物品实体
            return updateDroppedItemEntity(droppedData);
        });
    }
    
    /**
     * 更新背包中的物品
     *
     * @param itemData 物品数据
     * @param player 玩家实例
     * @return 更新结果
     */
    private UpdateResult updateInventoryItem(MythicItemData itemData, Player player) {
        try {
            // 检查玩家是否在线
            if (player == null || !player.isOnline()) {
                return createFailureResult("玩家不在线", null);
            }

            // 检查槽位是否有效
            int slot = itemData.getSlot();
            if (slot < 0 || slot >= player.getInventory().getSize()) {
                return createFailureResult("槽位索引超出范围: " + slot, null);
            }

            // 在主线程中执行物品更新
            CompletableFuture<UpdateResult> future = new CompletableFuture<>();

            Utils.runOnMainThread(() -> {
                try {
                    // 获取更新后的物品
                    ItemStack updatedItem = itemData.getUpdatedItem();
                    if (updatedItem == null) {
                        future.complete(createFailureResult("更新后的物品为空", null));
                        return;
                    }

                    // 更新玩家背包中的物品
                    player.getInventory().setItem(slot, updatedItem);

                    sendDebugMessage("成功更新背包物品",
                        "player", player.getName(),
                        "item", itemData.getInternalName(),
                        "slot", String.valueOf(slot));

                    future.complete(createSuccessResult("背包物品更新成功"));

                } catch (Exception e) {
                    future.complete(createFailureResult("更新背包物品失败: " + e.getMessage(), e));
                }
            });

            return future.get();

        } catch (Exception e) {
            return createFailureResult("更新背包物品时发生异常: " + e.getMessage(), e);
        }
    }
    
    /**
     * 更新末影箱中的物品
     *
     * @param itemData 物品数据
     * @param player 玩家实例
     * @return 更新结果
     */
    private UpdateResult updateEnderChestItem(MythicItemData itemData, Player player) {
        try {
            // 检查玩家是否在线
            if (player == null || !player.isOnline()) {
                return createFailureResult("玩家不在线", null);
            }

            // 检查槽位是否有效
            int slot = itemData.getSlot();
            if (slot < 0 || slot >= player.getEnderChest().getSize()) {
                return createFailureResult("末影箱槽位索引超出范围: " + slot, null);
            }

            CompletableFuture<UpdateResult> future = new CompletableFuture<>();

            Utils.runOnMainThread(() -> {
                try {
                    // 获取更新后的物品
                    ItemStack updatedItem = itemData.getUpdatedItem();
                    if (updatedItem == null) {
                        future.complete(createFailureResult("更新后的物品为空", null));
                        return;
                    }

                    // 更新玩家末影箱中的物品
                    player.getEnderChest().setItem(slot, updatedItem);

                    sendDebugMessage("成功更新末影箱物品",
                        "player", player.getName(),
                        "item", itemData.getInternalName(),
                        "slot", String.valueOf(slot));

                    future.complete(createSuccessResult("末影箱物品更新成功"));

                } catch (Exception e) {
                    future.complete(createFailureResult("更新末影箱物品失败: " + e.getMessage(), e));
                }
            });

            return future.get();

        } catch (Exception e) {
            return createFailureResult("更新末影箱物品时发生异常: " + e.getMessage(), e);
        }
    }
    
    /**
     * 更新容器中的物品
     * 
     * @param itemData 物品数据
     * @return 更新结果
     */
    private UpdateResult updateContainerItem(MythicItemData itemData) {
        try {
            CompletableFuture<UpdateResult> future = new CompletableFuture<>();
            
            Utils.runOnMainThread(() -> {
                try {
                    sendDebugMessage("更新容器物品", "item", itemData.getInternalName(), "slot", String.valueOf(itemData.getSlot()));
                    future.complete(createSuccessResult("容器物品更新成功"));
                    
                } catch (Exception e) {
                    future.complete(createFailureResult("更新容器物品失败: " + e.getMessage(), e));
                }
            });
            
            return future.get();
            
        } catch (Exception e) {
            return createFailureResult("更新容器物品时发生异常: " + e.getMessage(), e);
        }
    }
    
    /**
     * 更新掉落物品实体
     * 
     * @param droppedData 掉落物品数据
     * @return 更新结果
     */
    private UpdateResult updateDroppedItemEntity(DroppedItemData droppedData) {
        try {
            CompletableFuture<UpdateResult> future = new CompletableFuture<>();
            
            Utils.runOnMainThread(() -> {
                try {
                    // 查找对应的掉落物品实体
                    Entity entity = Bukkit.getEntity(droppedData.getEntityId());
                    
                    if (entity == null || !(entity instanceof Item)) {
                        future.complete(createFailureResult("掉落物品实体不存在或类型错误", null));
                        return;
                    }
                    
                    Item itemEntity = (Item) entity;
                    
                    // 更新物品实体的ItemStack
                    itemEntity.setItemStack(droppedData.getUpdatedItem());
                    
                    sendDebugMessage("更新掉落物品实体", "item", droppedData.getInternalName(), "entityId", droppedData.getEntityId().toString());
                    future.complete(createSuccessResult("掉落物品实体更新成功"));
                    
                } catch (Exception e) {
                    future.complete(createFailureResult("更新掉落物品实体失败: " + e.getMessage(), e));
                }
            });
            
            return future.get();
            
        } catch (Exception e) {
            return createFailureResult("更新掉落物品实体时发生异常: " + e.getMessage(), e);
        }
    }
    
    /**
     * 更新玩家背包中指定槽位的物品
     * 
     * @param player 玩家
     * @param slot 槽位
     * @param newItem 新物品
     * @return 更新结果
     */
    public UpdateResult updatePlayerInventorySlot(Player player, int slot, ItemStack newItem) {
        return safeUpdate("更新玩家背包槽位", () -> {
            if (player == null || !player.isOnline()) {
                return createFailureResult("玩家不在线", null);
            }
            
            Inventory inventory = player.getInventory();
            if (slot < 0 || slot >= inventory.getSize()) {
                return createFailureResult("槽位索引超出范围", null);
            }
            
            inventory.setItem(slot, newItem);
            sendDebugMessage("更新玩家背包槽位", "player", player.getName(), "slot", String.valueOf(slot));
            
            return createSuccessResult("玩家背包槽位更新成功");
        });
    }
    
    /**
     * 更新玩家末影箱中指定槽位的物品
     * 
     * @param player 玩家
     * @param slot 槽位
     * @param newItem 新物品
     * @return 更新结果
     */
    public UpdateResult updatePlayerEnderChestSlot(Player player, int slot, ItemStack newItem) {
        return safeUpdate("更新玩家末影箱槽位", () -> {
            if (player == null || !player.isOnline()) {
                return createFailureResult("玩家不在线", null);
            }
            
            Inventory enderChest = player.getEnderChest();
            if (slot < 0 || slot >= enderChest.getSize()) {
                return createFailureResult("槽位索引超出范围", null);
            }
            
            enderChest.setItem(slot, newItem);
            sendDebugMessage("更新玩家末影箱槽位", "player", player.getName(), "slot", String.valueOf(slot));
            
            return createSuccessResult("玩家末影箱槽位更新成功");
        });
    }
}
