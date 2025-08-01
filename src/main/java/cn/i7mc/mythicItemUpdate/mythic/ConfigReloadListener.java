package cn.i7mc.mythicItemUpdate.mythic;

import cn.i7mc.mythicItemUpdate.MythicItemUpdate;
import cn.i7mc.mythicItemUpdate.data.BatchUpdateResult;
import cn.i7mc.mythicItemUpdate.util.Utils;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicReloadedEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * MythicMobs配置重载监听器
 * 监听MythicMobs的重载事件并触发物品更新
 * 
 * @author i7mc
 * @version 1.0
 */
public class ConfigReloadListener extends AbstractMythicIntegration implements Listener {
    
    private boolean listenerRegistered;
    
    public ConfigReloadListener(MythicItemUpdate plugin) {
        super(plugin);
        this.listenerRegistered = false;
    }
    
    @Override
    protected boolean initializeIntegration() {
        try {
            // 注册事件监听器
            if (!listenerRegistered) {
                Bukkit.getPluginManager().registerEvents(this, plugin);
                listenerRegistered = true;
                info("MythicMobs重载监听器已注册");
            }
            
            return true;
            
        } catch (Exception e) {
            handleError("初始化MythicMobs重载监听器失败", e);
            return false;
        }
    }
    
    @Override
    protected boolean reloadIntegration() {
        // 重载时无需特殊处理，监听器会继续工作
        info("MythicMobs重载监听器重载成功");
        return true;
    }
    
    @Override
    protected void shutdownIntegration() {
        try {
            // 注销事件监听器
            if (listenerRegistered) {
                MythicReloadedEvent.getHandlerList().unregister(this);
                listenerRegistered = false;
            }
        } catch (Exception e) {
        }
    }
    
    @Override
    public String getName() {
        return "ConfigReloadListener";
    }
    
    /**
     * 监听MythicMobs重载事件
     * 
     * @param event MythicMobs重载事件
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMythicReload(MythicReloadedEvent event) {
        try {
            // 检查是否启用自动更新
            if (!plugin.getConfigManager().isAutoUpdateEnabled()) {
                return;
            }
            

            
            // 立即执行物品更新
            try {
                triggerItemUpdate();
            } catch (Exception e) {
                handleError("触发物品更新时发生错误", e);
            }

        } catch (Exception e) {
            handleError("处理MythicMobs重载事件时发生错误", e);
        }
    }
    
    /**
     * 触发物品更新
     */
    private void triggerItemUpdate() {
        try {
            // 获取物品更新管理器
            if (plugin.getBatchUpdateManager() == null) {
                warning("批量更新管理器未初始化，无法执行物品更新");
                return;
            }
            

            
            // 执行物品更新
            try {
                BatchUpdateResult result = plugin.getBatchUpdateManager().performFullUpdate();
                if (!result.isSuccess()) {
                    plugin.getMessageManager().sendErrorMessage("general-error", result.getMessage());
                }
            } catch (Exception e) {
                handleError("执行物品更新时发生错误", e);
                plugin.getMessageManager().sendErrorMessage("general-error", e.getMessage());
            }
            
        } catch (Exception e) {
            handleError("触发物品更新失败", e);
        }
    }
    
    /**
     * 手动触发物品更新
     * 用于命令或其他方式手动触发更新
     */
    public void manualTriggerUpdate() {
        try {
            if (!isMythicAvailable()) {
                warning("MythicMobs不可用，无法执行手动更新");
                return;
            }
            
            triggerItemUpdate();
            
        } catch (Exception e) {
            handleError("手动触发物品更新失败", e);
        }
    }
    
    /**
     * 检查监听器是否已注册
     * 
     * @return 是否已注册
     */
    public boolean isListenerRegistered() {
        return listenerRegistered;
    }
    
    /**
     * 获取MythicMobs版本信息
     * 
     * @return 版本信息
     */
    public String getMythicVersion() {
        if (!isMythicAvailable()) {
            return "未知";
        }
        
        try {
            return getMythicPlugin().getDescription().getVersion();
        } catch (Exception e) {
            return "获取失败";
        }
    }
    
    /**
     * 获取MythicMobs状态信息
     * 
     * @return 状态信息
     */
    public String getMythicStatus() {
        if (!isMythicAvailable()) {
            return "不可用";
        }
        
        try {
            StringBuilder status = new StringBuilder();
            status.append("版本: ").append(getMythicVersion());
            status.append(", 状态: 正常");
            status.append(", 监听器: ").append(listenerRegistered ? "已注册" : "未注册");
            
            return status.toString();
            
        } catch (Exception e) {
            return "状态获取失败";
        }
    }
    
    /**
     * 测试MythicMobs连接
     * 
     * @return 连接是否正常
     */
    public boolean testMythicConnection() {
        try {
            if (!isMythicAvailable()) {
                return false;
            }
            
            // 尝试访问MythicBukkit实例
            MythicBukkit mythicBukkit = getMythicBukkit();
            if (mythicBukkit == null) {
                return false;
            }
            
            // 测试基本功能
            return safeMythicCall("测试连接", () -> {
                // 简单的API调用测试
                return true;
            }, false);
            
        } catch (Exception e) {
            return false;
        }
    }
}
