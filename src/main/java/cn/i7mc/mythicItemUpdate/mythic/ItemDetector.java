package cn.i7mc.mythicItemUpdate.mythic;

import cn.i7mc.mythicItemUpdate.MythicItemUpdate;
import cn.i7mc.mythicItemUpdate.util.Utils;
import cn.i7mc.mythicItemUpdate.data.*;
import cn.i7mc.mythicItemUpdate.data.MythicItemData.ItemLocation;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MythicMobs物品检测器
 * 负责检测服务器中所有的MythicMobs物品
 * 
 * @author i7mc
 * @version 1.0
 */
public class ItemDetector extends AbstractMythicIntegration {
    
    public ItemDetector(MythicItemUpdate plugin) {
        super(plugin);
    }
    
    @Override
    protected boolean initializeIntegration() {
        info("物品检测器初始化成功");
        return true;
    }
    
    @Override
    protected boolean reloadIntegration() {
        return true;
    }
    
    @Override
    protected void shutdownIntegration() {
    }
    
    @Override
    public String getName() {
        return "ItemDetector";
    }
    
    /**
     * 检测所有MythicMobs物品
     *
     * @return 检测结果
     */
    public DetectionResult detectAllMythicItems() {
        DetectionResult result = new DetectionResult();

        try {
            // 检测玩家物品
            if (plugin.getConfigManager().isUpdateInventoryEnabled() ||
                plugin.getConfigManager().isUpdateEnderChestEnabled()) {
                detectPlayerItems(result);
            }

            // 检测掉落物品
            if (plugin.getConfigManager().isUpdateDroppedItemsEnabled()) {
                detectDroppedItems(result);
            }

            // 检测容器物品
            if (plugin.getConfigManager().isUpdateContainersEnabled()) {
                detectContainerItems(result);
            }

        } catch (Exception e) {
            handleError("检测MythicMobs物品时发生错误", e);
            result.setError(e.getMessage());
        }

        return result;
    }

    /**
     * 检测玩家物品
     * 
     * @param result 检测结果
     */
    private void detectPlayerItems(DetectionResult result) {
        List<Player> players = Utils.getOnlinePlayers();
        
        for (Player player : players) {
            try {
                PlayerItemData playerData = new PlayerItemData(player.getName());
                
                // 检测背包物品
                if (plugin.getConfigManager().isUpdateInventoryEnabled()) {
                    detectInventoryItems(player.getInventory(), playerData.getInventoryItems());
                }
                
                // 检测末影箱物品
                if (plugin.getConfigManager().isUpdateEnderChestEnabled()) {
                    detectInventoryItems(player.getEnderChest(), playerData.getEnderChestItems());
                }
                
                if (playerData.getTotalItemCount() > 0) {
                    result.addPlayerData(playerData);
                }
                
            } catch (Exception e) {
            }
        }
    }
    
    /**
     * 检测背包中的物品
     * 
     * @param inventory 背包
     * @param itemList 物品列表
     */
    private void detectInventoryItems(Inventory inventory, List<MythicItemData> itemList) {
        ItemStack[] contents = inventory.getContents();
        
        for (int slot = 0; slot < contents.length; slot++) {
            ItemStack item = contents[slot];
            
            if (Utils.isValidItem(item) && isMythicItem(item)) {
                String internalName = getMythicItemInternalName(item);
                if (internalName != null) {
                    MythicItemData itemData = new MythicItemData(
                        internalName,
                        item.clone(),
                        slot,
                        ItemLocation.INVENTORY
                    );
                    itemList.add(itemData);
                }
            }
        }
    }
    
    /**
     * 检测掉落物品
     * 
     * @param result 检测结果
     */
    private void detectDroppedItems(DetectionResult result) {
        for (World world : Bukkit.getWorlds()) {
            try {
                for (Entity entity : world.getEntities()) {
                    if (entity instanceof Item) {
                        Item itemEntity = (Item) entity;
                        ItemStack item = itemEntity.getItemStack();
                        
                        if (Utils.isValidItem(item) && isMythicItem(item)) {
                            String internalName = getMythicItemInternalName(item);
                            if (internalName != null) {
                                DroppedItemData droppedData = new DroppedItemData(
                                    internalName,
                                    item.clone(),
                                    itemEntity.getUniqueId(),
                                    itemEntity.getLocation().clone()
                                );
                                result.addDroppedItem(droppedData);
                            }
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
    }
    
    /**
     * 检测容器物品
     * 
     * @param result 检测结果
     */
    private void detectContainerItems(DetectionResult result) {
        for (World world : Bukkit.getWorlds()) {
            try {
                for (BlockState blockState : world.getLoadedChunks()[0].getTileEntities()) {
                    if (blockState instanceof InventoryHolder) {
                        InventoryHolder holder = (InventoryHolder) blockState;
                        Inventory inventory = holder.getInventory();
                        
                        List<MythicItemData> containerItems = new ArrayList<>();
                        detectInventoryItems(inventory, containerItems);
                        
                        if (!containerItems.isEmpty()) {
                            ContainerItemData containerData = new ContainerItemData(
                                blockState.getLocation().clone(),
                                blockState.getType().name(),
                                containerItems
                            );
                            result.addContainerData(containerData);
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
    }
    
    /**
     * 检测单个物品是否需要更新
     * 
     * @param item 物品
     * @return 是否需要更新
     */
    public boolean needsUpdate(ItemStack item) {
        if (!Utils.isValidItem(item) || !isMythicItem(item)) {
            return false;
        }
        
        String internalName = getMythicItemInternalName(item);
        if (internalName == null) {
            return false;
        }
        
        // 生成新的物品进行比较
        ItemStack newItem = generateMythicItem(internalName, item.getAmount());
        if (newItem == null) {
            return false;
        }
        
        // 比较物品是否有差异
        return !item.isSimilar(newItem);
    }
    
    /**
     * 获取物品的更新版本
     * 
     * @param item 原物品
     * @return 更新后的物品，如果无需更新则返回原物品
     */
    public ItemStack getUpdatedItem(ItemStack item) {
        if (!needsUpdate(item)) {
            return item;
        }
        
        String internalName = getMythicItemInternalName(item);
        if (internalName == null) {
            return item;
        }
        
        ItemStack newItem = generateMythicItem(internalName, item.getAmount());
        return newItem != null ? newItem : item;
    }
    
    /**
     * 批量检测物品是否需要更新
     * 
     * @param items 物品列表
     * @return 需要更新的物品映射（原物品 -> 新物品）
     */
    public Map<ItemStack, ItemStack> batchCheckUpdates(List<ItemStack> items) {
        Map<ItemStack, ItemStack> updates = new HashMap<>();
        
        for (ItemStack item : items) {
            if (needsUpdate(item)) {
                ItemStack updatedItem = getUpdatedItem(item);
                if (updatedItem != item) {
                    updates.put(item, updatedItem);
                }
            }
        }
        
        return updates;
    }
}
