package cn.i7mc.mythicItemUpdate.listener;

import cn.i7mc.mythicItemUpdate.MythicItemUpdate;
import cn.i7mc.mythicItemUpdate.data.MythicItemData;
import cn.i7mc.mythicItemUpdate.data.PlayerItemData;
import cn.i7mc.mythicItemUpdate.util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 物品更新事件处理器
 * 处理各种可能需要物品更新的游戏事件
 * 
 * @author i7mc
 * @version 1.0
 */
public class UpdateEventHandler extends AbstractEventListener {
    
    public UpdateEventHandler(MythicItemUpdate plugin) {
        super(plugin);
    }
    
    @Override
    protected boolean initializeListener() {
        info("物品更新事件处理器初始化成功");
        return true;
    }
    
    @Override
    protected boolean reloadListener() {
        info("物品更新事件处理器重载成功");
        return true;
    }
    
    @Override
    protected void shutdownListener() {
    }
    
    @Override
    public String getName() {
        return "UpdateEventHandler";
    }
    
    /**
     * 监听玩家加入事件
     * 检查并更新玩家背包中的MythicMobs物品
     * 
     * @param event 玩家加入事件
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        safeEventHandle("PlayerJoinEvent", () -> {
            if (!shouldHandleEvent()) {
                return;
            }
            
            Player player = event.getPlayer();
            
            // 检查是否启用了登录时更新功能
            if (!plugin.getConfigManager().isUpdateOnJoinEnabled()) {
                return;
            }
            
            
            // 延迟执行，确保玩家完全加载
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                updatePlayerInventory(player);
            }, plugin.getConfigManager().getJoinUpdateDelay());
        });
    }
    
    /**
     * 监听背包打开事件
     * 在打开背包时检查并更新物品
     * 
     * @param event 背包打开事件
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryOpen(InventoryOpenEvent event) {
        safeEventHandle("InventoryOpenEvent", () -> {
            if (!shouldHandleEvent()) {
                return;
            }
            
            if (!(event.getPlayer() instanceof Player)) {
                return;
            }
            
            Player player = (Player) event.getPlayer();
            
            // 检查是否启用了打开背包时更新功能
            if (!plugin.getConfigManager().isUpdateOnInventoryOpenEnabled()) {
                return;
            }
            
            
            // 同步执行更新，确保线程安全
            updateInventoryItems(event.getInventory());
        });
    }
    
    /**
     * 监听背包点击事件
     * 在点击物品时检查是否需要更新
     * 
     * @param event 背包点击事件
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        safeEventHandle("InventoryClickEvent", () -> {
            if (!shouldHandleEvent()) {
                return;
            }
            
            if (!(event.getWhoClicked() instanceof Player)) {
                return;
            }
            
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            
            // 检查是否启用了点击时更新功能
            if (!plugin.getConfigManager().isUpdateOnClickEnabled()) {
                return;
            }
            
            if (!Utils.isValidItem(clickedItem)) {
                return;
            }
            
            // 检查是否为MythicMobs物品
            if (!plugin.getItemDetector().isMythicItem(clickedItem)) {
                return;
            }
            
            
            // 同步检查并更新物品，避免竞态条件
            updateSingleItem(player, clickedItem, event.getSlot());
        });
    }
    
    /**
     * 监听玩家拾取物品事件
     * 在拾取MythicMobs物品时检查更新
     * 
     * @param event 拾取物品事件
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        safeEventHandle("PlayerPickupItemEvent", () -> {
            if (!shouldHandleEvent()) {
                return;
            }
            
            Player player = event.getPlayer();
            ItemStack item = event.getItem().getItemStack();
            
            // 检查是否启用了拾取时更新功能
            if (!plugin.getConfigManager().isUpdateOnPickupEnabled()) {
                return;
            }
            
            if (!Utils.isValidItem(item)) {
                return;
            }
            
            // 检查是否为MythicMobs物品
            if (!plugin.getItemDetector().isMythicItem(item)) {
                return;
            }
            
            
            // 延迟执行，确保物品已进入背包
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                updatePlayerInventory(player);
            }, 1L);
        });
    }
    
    /**
     * 更新玩家背包中的所有物品
     * 
     * @param player 玩家
     */
    private void updatePlayerInventory(Player player) {
        try {
            PlayerItemData playerData = new PlayerItemData(player);
            
            if (plugin.getBatchUpdateManager() != null) {
                plugin.getBatchUpdateManager().updatePlayerItems(playerData);
            }
            
        } catch (Exception e) {
            handleError("更新玩家背包失败: " + player.getName(), e);
        }
    }
    
    /**
     * 更新背包中的物品
     * 
     * @param inventory 背包
     */
    private void updateInventoryItems(org.bukkit.inventory.Inventory inventory) {
        try {
            ItemStack[] contents = inventory.getContents();
            boolean inventoryChanged = false;
            
            for (int i = 0; i < contents.length; i++) {
                ItemStack item = contents[i];
                if (Utils.isValidItem(item) && plugin.getItemDetector().isMythicItem(item)) {
                    if (plugin.getItemDetector().needsUpdate(item)) {
                        ItemStack updatedItem = plugin.getItemDetector().getUpdatedItem(item);
                        if (updatedItem != null && !updatedItem.equals(item)) {
                            // 验证物品在更新过程中没有被修改
                            ItemStack currentItem = inventory.getItem(i);
                            if (Utils.isValidItem(currentItem) && currentItem.equals(item)) {
                                contents[i] = updatedItem;
                                inventoryChanged = true;
                            } else {
                            }
                        }
                    }
                }
            }
            
            // 批量更新背包内容，减少潜在的同步问题
            if (inventoryChanged) {
                inventory.setContents(contents);
            }
        } catch (Exception e) {
            handleError("更新背包物品失败", e);
        }
    }
    
    /**
     * 更新单个物品
     * 
     * @param player 玩家
     * @param item 物品
     * @param slot 槽位
     */
    private void updateSingleItem(Player player, ItemStack item, int slot) {
        try {
            // 验证玩家是否在线
            if (!player.isOnline()) {
                return;
            }
            
            // 获取当前槽位的实际物品进行验证
            ItemStack currentItem = player.getInventory().getItem(slot);
            if (!Utils.isValidItem(currentItem) || !currentItem.equals(item)) {
                return;
            }
            
            if (plugin.getItemDetector().needsUpdate(item)) {
                ItemStack updatedItem = plugin.getItemDetector().getUpdatedItem(item);
                if (updatedItem != null && !updatedItem.equals(item)) {
                    // 再次验证物品没有被修改
                    ItemStack revalidateItem = player.getInventory().getItem(slot);
                    if (Utils.isValidItem(revalidateItem) && revalidateItem.equals(currentItem)) {
                        // 直接在主线程中更新物品，无需调度
                        player.getInventory().setItem(slot, updatedItem);
                        
                    } else {
                    }
                }
            }
        } catch (Exception e) {
            handleError("更新单个物品失败", e);
        }
    }
}
