package cn.i7mc.mythicItemUpdate.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 工具类
 * 提供通用的工具方法
 * 
 * @author i7mc
 * @version 1.0
 */
public class Utils {
    
    /**
     * 检查字符串是否为空或null
     * 
     * @param str 要检查的字符串
     * @return 是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * 检查字符串是否不为空
     * 
     * @param str 要检查的字符串
     * @return 是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    /**
     * 安全地获取字符串，如果为null则返回默认值
     * 
     * @param str 原字符串
     * @param defaultValue 默认值
     * @return 安全的字符串
     */
    public static String safeString(String str, String defaultValue) {
        return str != null ? str : defaultValue;
    }
    
    /**
     * 检查ItemStack是否有效（不为null且不为AIR）
     * 
     * @param item 要检查的物品
     * @return 是否有效
     */
    public static boolean isValidItem(ItemStack item) {
        return item != null && item.getType() != Material.AIR && item.getAmount() > 0;
    }
    
    /**
     * 安全地获取ItemMeta
     * 
     * @param item 物品
     * @return ItemMeta或null
     */
    public static ItemMeta safeGetItemMeta(ItemStack item) {
        return isValidItem(item) ? item.getItemMeta() : null;
    }
    
    /**
     * 检查物品是否有自定义名称
     * 
     * @param item 物品
     * @return 是否有自定义名称
     */
    public static boolean hasCustomName(ItemStack item) {
        ItemMeta meta = safeGetItemMeta(item);
        return meta != null && meta.hasDisplayName();
    }
    
    /**
     * 安全地获取物品显示名称
     * 
     * @param item 物品
     * @return 显示名称
     */
    public static String getItemDisplayName(ItemStack item) {
        if (!isValidItem(item)) {
            return "无效物品";
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            return meta.getDisplayName();
        }
        
        return item.getType().name();
    }
    
    /**
     * 检查物品是否有Lore
     * 
     * @param item 物品
     * @return 是否有Lore
     */
    public static boolean hasLore(ItemStack item) {
        ItemMeta meta = safeGetItemMeta(item);
        return meta != null && meta.hasLore() && meta.getLore() != null && !meta.getLore().isEmpty();
    }
    
    /**
     * 安全地获取物品Lore
     * 
     * @param item 物品
     * @return Lore列表
     */
    public static List<String> getItemLore(ItemStack item) {
        ItemMeta meta = safeGetItemMeta(item);
        if (meta != null && meta.hasLore()) {
            return meta.getLore();
        }
        return new ArrayList<>();
    }
    
    /**
     * 检查玩家是否在线
     * 
     * @param playerName 玩家名称
     * @return 是否在线
     */
    public static boolean isPlayerOnline(String playerName) {
        if (isEmpty(playerName)) {
            return false;
        }
        
        Player player = Bukkit.getPlayer(playerName);
        return player != null && player.isOnline();
    }
    
    /**
     * 安全地获取在线玩家
     * 
     * @param playerName 玩家名称
     * @return 玩家对象或null
     */
    public static Player getOnlinePlayer(String playerName) {
        if (isEmpty(playerName)) {
            return null;
        }
        
        Player player = Bukkit.getPlayer(playerName);
        return (player != null && player.isOnline()) ? player : null;
    }
    
    /**
     * 获取所有在线玩家列表
     * 
     * @return 在线玩家列表
     */
    public static List<Player> getOnlinePlayers() {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }
    
    /**
     * 格式化时间（毫秒转换为可读格式）
     * 
     * @param milliseconds 毫秒
     * @return 格式化的时间字符串
     */
    public static String formatTime(long milliseconds) {
        if (milliseconds < 1000) {
            return milliseconds + "ms";
        }
        
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds);
        if (seconds < 60) {
            return seconds + "s";
        }
        
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        if (minutes < 60) {
            return minutes + "m " + (seconds % 60) + "s";
        }
        
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
        return hours + "h " + (minutes % 60) + "m " + (seconds % 60) + "s";
    }
    
    /**
     * 创建带超时的CompletableFuture
     * 
     * @param timeoutMillis 超时时间（毫秒）
     * @param <T> 返回类型
     * @return CompletableFuture
     */
    public static <T> CompletableFuture<T> createTimeoutFuture(long timeoutMillis) {
        CompletableFuture<T> future = new CompletableFuture<>();
        
        Bukkit.getScheduler().runTaskLater(
            Bukkit.getPluginManager().getPlugin("MythicItemUpdate"),
            () -> future.completeExceptionally(new RuntimeException("操作超时")),
            timeoutMillis / 50 // 转换为tick
        );
        
        return future;
    }
    
    /**
     * 在主线程中执行任务
     * 
     * @param task 要执行的任务
     */
    public static void runOnMainThread(Runnable task) {
        if (Bukkit.isPrimaryThread()) {
            task.run();
        } else {
            Bukkit.getScheduler().runTask(
                Bukkit.getPluginManager().getPlugin("MythicItemUpdate"), 
                task
            );
        }
    }
    
    /**
     * 在异步线程中执行任务
     * 
     * @param task 要执行的任务
     */
    public static void runAsync(Runnable task) {
        Bukkit.getScheduler().runTaskAsynchronously(
            Bukkit.getPluginManager().getPlugin("MythicItemUpdate"), 
            task
        );
    }
    
    /**
     * 延迟执行任务
     * 
     * @param task 要执行的任务
     * @param delayTicks 延迟tick数
     */
    public static void runLater(Runnable task, long delayTicks) {
        Bukkit.getScheduler().runTaskLater(
            Bukkit.getPluginManager().getPlugin("MythicItemUpdate"), 
            task, 
            delayTicks
        );
    }
    
    /**
     * 延迟执行任务（毫秒）
     * 
     * @param task 要执行的任务
     * @param delayMillis 延迟毫秒数
     */
    public static void runLaterMillis(Runnable task, long delayMillis) {
        runLater(task, delayMillis / 50); // 转换为tick
    }
    
    /**
     * 检查版本兼容性
     * 
     * @param requiredVersion 需要的版本
     * @param currentVersion 当前版本
     * @return 是否兼容
     */
    public static boolean isVersionCompatible(String requiredVersion, String currentVersion) {
        if (isEmpty(requiredVersion) || isEmpty(currentVersion)) {
            return false;
        }
        
        try {
            String[] required = requiredVersion.split("\\.");
            String[] current = currentVersion.split("\\.");
            
            int maxLength = Math.max(required.length, current.length);
            
            for (int i = 0; i < maxLength; i++) {
                int req = i < required.length ? Integer.parseInt(required[i]) : 0;
                int cur = i < current.length ? Integer.parseInt(current[i]) : 0;
                
                if (cur > req) {
                    return true;
                } else if (cur < req) {
                    return false;
                }
            }
            
            return true; // 版本相等
            
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
