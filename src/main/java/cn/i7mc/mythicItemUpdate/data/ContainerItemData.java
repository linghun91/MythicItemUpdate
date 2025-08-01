package cn.i7mc.mythicItemUpdate.data;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * 容器物品数据
 * 
 * @author i7mc
 * @version 1.0
 */
public class ContainerItemData {
    
    private final Location location;
    private final String containerType;
    private final List<MythicItemData> items;
    
    public ContainerItemData(Location location, String containerType, List<MythicItemData> items) {
        this.location = location;
        this.containerType = containerType;
        this.items = new ArrayList<>(items);
    }
    
    /**
     * 获取容器位置
     * 
     * @return 容器位置
     */
    public Location getLocation() {
        return location;
    }
    
    /**
     * 获取容器类型
     * 
     * @return 容器类型
     */
    public String getContainerType() {
        return containerType;
    }
    
    /**
     * 获取物品列表
     * 
     * @return 物品列表
     */
    public List<MythicItemData> getItems() {
        return new ArrayList<>(items);
    }
    
    /**
     * 获取物品数量
     * 
     * @return 物品数量
     */
    public int getItemCount() {
        return items.size();
    }
    
    /**
     * 检查是否有物品
     * 
     * @return 是否有物品
     */
    public boolean hasItems() {
        return !items.isEmpty();
    }
    
    /**
     * 获取需要更新的物品数量
     * 
     * @return 需要更新的物品数量
     */
    public int getUpdateCount() {
        return (int) items.stream().filter(MythicItemData::needsUpdate).count();
    }
    
    /**
     * 检查是否有需要更新的物品
     * 
     * @return 是否有需要更新的物品
     */
    public boolean hasUpdates() {
        return getUpdateCount() > 0;
    }
    
    @Override
    public String toString() {
        return String.format("ContainerItemData{type='%s', location=%s, items=%d, updates=%d}",
            containerType, location, getItemCount(), getUpdateCount());
    }
}
