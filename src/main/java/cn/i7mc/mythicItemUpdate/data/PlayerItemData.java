package cn.i7mc.mythicItemUpdate.data;

import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

/**
 * 玩家物品数据
 * 
 * @author i7mc
 * @version 1.0
 */
public class PlayerItemData {

    private final String playerName;
    private final Player player;
    private final List<MythicItemData> inventoryItems;
    private final List<MythicItemData> enderChestItems;

    public PlayerItemData(String playerName) {
        this.playerName = playerName;
        this.player = null;
        this.inventoryItems = new ArrayList<>();
        this.enderChestItems = new ArrayList<>();
    }

    public PlayerItemData(Player player) {
        this.playerName = player.getName();
        this.player = player;
        this.inventoryItems = new ArrayList<>();
        this.enderChestItems = new ArrayList<>();
    }
    
    /**
     * 获取玩家名称
     *
     * @return 玩家名称
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * 获取玩家实例
     *
     * @return 玩家实例
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * 获取背包物品列表
     * 
     * @return 背包物品列表
     */
    public List<MythicItemData> getInventoryItems() {
        return inventoryItems;
    }
    
    /**
     * 获取末影箱物品列表
     * 
     * @return 末影箱物品列表
     */
    public List<MythicItemData> getEnderChestItems() {
        return enderChestItems;
    }
    
    /**
     * 获取所有物品列表
     * 
     * @return 所有物品列表
     */
    public List<MythicItemData> getAllItems() {
        List<MythicItemData> allItems = new ArrayList<>();
        allItems.addAll(inventoryItems);
        allItems.addAll(enderChestItems);
        return allItems;
    }
    
    /**
     * 获取背包物品数量
     * 
     * @return 背包物品数量
     */
    public int getInventoryItemCount() {
        return inventoryItems.size();
    }
    
    /**
     * 获取末影箱物品数量
     * 
     * @return 末影箱物品数量
     */
    public int getEnderChestItemCount() {
        return enderChestItems.size();
    }
    
    /**
     * 获取总物品数量
     * 
     * @return 总物品数量
     */
    public int getTotalItemCount() {
        return getInventoryItemCount() + getEnderChestItemCount();
    }
    
    /**
     * 检查是否有物品
     * 
     * @return 是否有物品
     */
    public boolean hasItems() {
        return getTotalItemCount() > 0;
    }
    
    @Override
    public String toString() {
        return String.format("PlayerItemData{player='%s', inventory=%d, enderChest=%d, total=%d}",
            playerName, getInventoryItemCount(), getEnderChestItemCount(), getTotalItemCount());
    }
}
