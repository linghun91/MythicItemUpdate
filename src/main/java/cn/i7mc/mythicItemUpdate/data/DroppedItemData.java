package cn.i7mc.mythicItemUpdate.data;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * 掉落物品数据
 * 
 * @author i7mc
 * @version 1.0
 */
public class DroppedItemData {
    
    private final String internalName;
    private final ItemStack originalItem;
    private final UUID entityId;
    private final Location location;
    private ItemStack updatedItem;
    private boolean needsUpdate;
    
    public DroppedItemData(String internalName, ItemStack originalItem, UUID entityId, Location location) {
        this.internalName = internalName;
        this.originalItem = originalItem;
        this.entityId = entityId;
        this.location = location;
        this.needsUpdate = false;
    }
    
    /**
     * 获取内部名称
     * 
     * @return 内部名称
     */
    public String getInternalName() {
        return internalName;
    }
    
    /**
     * 获取原始物品
     * 
     * @return 原始物品
     */
    public ItemStack getOriginalItem() {
        return originalItem;
    }
    
    /**
     * 获取实体ID
     * 
     * @return 实体ID
     */
    public UUID getEntityId() {
        return entityId;
    }
    
    /**
     * 获取位置
     * 
     * @return 位置
     */
    public Location getLocation() {
        return location;
    }
    
    /**
     * 获取更新后的物品
     * 
     * @return 更新后的物品
     */
    public ItemStack getUpdatedItem() {
        return updatedItem;
    }
    
    /**
     * 设置更新后的物品
     * 
     * @param updatedItem 更新后的物品
     */
    public void setUpdatedItem(ItemStack updatedItem) {
        this.updatedItem = updatedItem;
        this.needsUpdate = updatedItem != null && !originalItem.isSimilar(updatedItem);
    }
    
    /**
     * 检查是否需要更新
     * 
     * @return 是否需要更新
     */
    public boolean needsUpdate() {
        return needsUpdate;
    }
    
    /**
     * 设置是否需要更新
     * 
     * @param needsUpdate 是否需要更新
     */
    public void setNeedsUpdate(boolean needsUpdate) {
        this.needsUpdate = needsUpdate;
    }
    
    /**
     * 获取有效的物品（更新后的或原始的）
     * 
     * @return 有效的物品
     */
    public ItemStack getEffectiveItem() {
        return updatedItem != null ? updatedItem : originalItem;
    }
    
    @Override
    public String toString() {
        return String.format("DroppedItemData{name='%s', entityId=%s, location=%s, needsUpdate=%s}",
            internalName, entityId, location, needsUpdate);
    }
}
