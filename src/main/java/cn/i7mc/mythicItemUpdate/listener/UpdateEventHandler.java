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
        debug("物品更新事件处理器已关闭");
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
            
            sendDebugMessage("玩家加入，检查背包物品", "player", player.getName());
            
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
            
            sendDebugMessage("玩家打开背包，检查物品", "player", player.getName());
            
            // 异步执行更新，避免阻塞主线程
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                updateInventoryItems(event.getInventory());
            });
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
            
            sendDebugMessage("玩家点击MythicMobs物品，检查更新", 
                "player", player.getName(),
                "item", clickedItem.getType().name());
            
            // 异步检查并更新物品
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                updateSingleItem(player, clickedItem, event.getSlot());
            });
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
            
            sendDebugMessage("玩家拾取MythicMobs物品，检查更新", 
                "player", player.getName(),
                "item", item.getType().name());
            
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
            for (int i = 0; i < inventory.getSize(); i++) {
                ItemStack item = inventory.getItem(i);
                if (Utils.isValidItem(item) && plugin.getItemDetector().isMythicItem(item)) {
                    if (plugin.getItemDetector().needsUpdate(item)) {
                        ItemStack updatedItem = plugin.getItemDetector().getUpdatedItem(item);
                        if (updatedItem != null && !updatedItem.equals(item)) {
                            inventory.setItem(i, updatedItem);
                        }
                    }
                }
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
            if (plugin.getItemDetector().needsUpdate(item)) {
                ItemStack updatedItem = plugin.getItemDetector().getUpdatedItem(item);
                if (updatedItem != null && !updatedItem.equals(item)) {
                    // 在主线程中更新物品
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        player.getInventory().setItem(slot, updatedItem);
                    });
                    
                    sendDebugMessage("已更新玩家物品", 
                        "player", player.getName(),
                        "slot", String.valueOf(slot));
                }
            }
        } catch (Exception e) {
            handleError("更新单个物品失败", e);
        }
    }
}
