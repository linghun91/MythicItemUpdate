package cn.i7mc.mythicItemUpdate.data;

import org.bukkit.inventory.ItemStack;

/**
 * MythicMobs物品数据
 * 
 * @author i7mc
 * @version 1.0
 */
public class MythicItemData {
    
    private final String internalName;
    private final ItemStack originalItem;
    private final int slot;
    private final ItemLocation location;
    private ItemStack updatedItem;
    private boolean needsUpdate;
    
    public MythicItemData(String internalName, ItemStack originalItem, int slot, ItemLocation location) {
        this.internalName = internalName;
        this.originalItem = originalItem;
        this.slot = slot;
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
     * 获取槽位
     * 
     * @return 槽位
     */
    public int getSlot() {
        return slot;
    }
    
    /**
     * 获取位置类型
     * 
     * @return 位置类型
     */
    public ItemLocation getLocation() {
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
        return String.format("MythicItemData{name='%s', slot=%d, location=%s, needsUpdate=%s}",
            internalName, slot, location, needsUpdate);
    }
    
    /**
     * 物品位置枚举
     */
    public enum ItemLocation {
        INVENTORY("背包"),
        ENDERCHEST("末影箱"),
        CONTAINER("容器");
        
        private final String displayName;
        
        ItemLocation(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
}
