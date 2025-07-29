package cn.i7mc.mythicItemUpdate.listener;

import cn.i7mc.mythicItemUpdate.MythicItemUpdate;
import io.lumine.mythic.bukkit.events.MythicReloadedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

/**
 * MythicMobs重载事件监听器
 * 监听MythicMobs的重载事件，触发物品更新流程
 * 
 * @author i7mc
 * @version 1.0
 */
public class ReloadEventListener extends AbstractEventListener {
    
    public ReloadEventListener(MythicItemUpdate plugin) {
        super(plugin);
    }
    
    @Override
    protected boolean initializeListener() {
        info("MythicMobs重载事件监听器初始化成功");
        return true;
    }
    
    @Override
    protected boolean reloadListener() {
        info("MythicMobs重载事件监听器重载成功");
        return true;
    }
    
    @Override
    protected void shutdownListener() {
        debug("MythicMobs重载事件监听器已关闭");
    }
    
    @Override
    public String getName() {
        return "ReloadEventListener";
    }
    
    /**
     * 监听MythicMobs重载完成事件
     * 
     * @param event MythicMobs重载事件
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onMythicReloaded(MythicReloadedEvent event) {
        safeEventHandle("MythicReloadedEvent", () -> {
            if (!shouldHandleEvent()) {
                return;
            }
            
            sendDebugMessage("检测到MythicMobs重载事件");
            
            // 检查是否启用了自动更新功能
            if (!plugin.getConfigManager().isAutoUpdateEnabled()) {
                sendDebugMessage("自动更新功能已禁用，跳过物品更新");
                return;
            }
            
            // 立即执行更新
            handleMythicReload();
        });
    }
    
    /**
     * 处理MythicMobs重载后的物品更新逻辑
     */
    private void handleMythicReload() {
        try {
            sendDebugMessage("开始处理MythicMobs重载后的物品更新");
            
            // 检查MythicMobs是否可用
            if (!plugin.getItemDetector().isMythicAvailable()) {
                warning("MythicMobs不可用，无法执行物品更新");
                return;
            }
            
            // 重载物品检测器
            if (!plugin.getItemDetector().reload()) {
                warning("重载物品检测器失败");
                return;
            }
            
            // 触发批量更新管理器
            if (plugin.getBatchUpdateManager() != null) {
                plugin.getBatchUpdateManager().triggerGlobalUpdate();
                sendDebugMessage("已触发全局物品更新");
            } else {
                warning("批量更新管理器未初始化");
            }

        } catch (Exception e) {
            handleError("处理MythicMobs重载事件时发生错误", e);
            sendReloadFailureMessage(e.getMessage());
        }
    }

    /**
     * 发送重载失败消息
     * 
     * @param error 错误信息
     */
    private void sendReloadFailureMessage(String error) {
        if (plugin.getConfigManager().isNotifyAdminsEnabled()) {
            plugin.getMessageManager().broadcastToAdmins(
                "messages.reload-failure",
                plugin.getMessageManager().createPlaceholders(
                    "error", error
                )
            );
        }
    }
}
