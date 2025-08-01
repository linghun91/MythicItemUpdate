package cn.i7mc.mythicItemUpdate.data;

import java.util.ArrayList;
import java.util.List;

/**
 * 物品检测结果
 * 
 * @author i7mc
 * @version 1.0
 */
public class DetectionResult {
    
    private final List<PlayerItemData> playerData;
    private final List<DroppedItemData> droppedItems;
    private final List<ContainerItemData> containerData;
    private String error;
    private long detectionTime;
    
    public DetectionResult() {
        this.playerData = new ArrayList<>();
        this.droppedItems = new ArrayList<>();
        this.containerData = new ArrayList<>();
        this.detectionTime = System.currentTimeMillis();
    }
    
    /**
     * 添加玩家数据
     * 
     * @param data 玩家数据
     */
    public void addPlayerData(PlayerItemData data) {
        if (data != null) {
            playerData.add(data);
        }
    }
    
    /**
     * 添加掉落物品数据
     * 
     * @param data 掉落物品数据
     */
    public void addDroppedItem(DroppedItemData data) {
        if (data != null) {
            droppedItems.add(data);
        }
    }
    
    /**
     * 添加容器数据
     * 
     * @param data 容器数据
     */
    public void addContainerData(ContainerItemData data) {
        if (data != null) {
            containerData.add(data);
        }
    }
    
    /**
     * 获取玩家数据列表
     * 
     * @return 玩家数据列表
     */
    public List<PlayerItemData> getPlayerData() {
        return new ArrayList<>(playerData);
    }
    
    /**
     * 获取掉落物品列表
     * 
     * @return 掉落物品列表
     */
    public List<DroppedItemData> getDroppedItems() {
        return new ArrayList<>(droppedItems);
    }
    
    /**
     * 获取容器数据列表
     * 
     * @return 容器数据列表
     */
    public List<ContainerItemData> getContainerData() {
        return new ArrayList<>(containerData);
    }
    
    /**
     * 获取玩家物品总数
     * 
     * @return 玩家物品总数
     */
    public int getPlayerItemCount() {
        return playerData.stream().mapToInt(PlayerItemData::getTotalItemCount).sum();
    }
    
    /**
     * 获取掉落物品总数
     * 
     * @return 掉落物品总数
     */
    public int getDroppedItemCount() {
        return droppedItems.size();
    }
    
    /**
     * 获取容器物品总数
     * 
     * @return 容器物品总数
     */
    public int getContainerItemCount() {
        return containerData.stream().mapToInt(ContainerItemData::getItemCount).sum();
    }
    
    /**
     * 获取总物品数量
     * 
     * @return 总物品数量
     */
    public int getTotalItemCount() {
        return getPlayerItemCount() + getDroppedItemCount() + getContainerItemCount();
    }
    
    /**
     * 获取玩家数量
     * 
     * @return 玩家数量
     */
    public int getPlayerCount() {
        return playerData.size();
    }
    
    /**
     * 获取容器数量
     * 
     * @return 容器数量
     */
    public int getContainerCount() {
        return containerData.size();
    }
    
    /**
     * 检查是否有错误
     * 
     * @return 是否有错误
     */
    public boolean hasError() {
        return error != null;
    }
    
    /**
     * 获取错误信息
     * 
     * @return 错误信息
     */
    public String getError() {
        return error;
    }
    
    /**
     * 设置错误信息
     * 
     * @param error 错误信息
     */
    public void setError(String error) {
        this.error = error;
    }
    
    /**
     * 获取检测时间
     * 
     * @return 检测时间
     */
    public long getDetectionTime() {
        return detectionTime;
    }
    
    /**
     * 检查是否为空结果
     * 
     * @return 是否为空
     */
    public boolean isEmpty() {
        return getTotalItemCount() == 0;
    }
    
    @Override
    public String toString() {
        return String.format("DetectionResult{players=%d, playerItems=%d, droppedItems=%d, containers=%d, containerItems=%d, total=%d, hasError=%s}",
            getPlayerCount(),
            getPlayerItemCount(),
            getDroppedItemCount(),
            getContainerCount(),
            getContainerItemCount(),
            getTotalItemCount(),
            hasError());
    }
}
