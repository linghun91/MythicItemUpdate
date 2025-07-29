package cn.i7mc.mythicItemUpdate;

import cn.i7mc.mythicItemUpdate.listener.ReloadEventListener;
import cn.i7mc.mythicItemUpdate.listener.UpdateEventHandler;
import cn.i7mc.mythicItemUpdate.manager.ConfigManager;
import cn.i7mc.mythicItemUpdate.manager.MessageManager;
import cn.i7mc.mythicItemUpdate.mythic.ItemDetector;
import cn.i7mc.mythicItemUpdate.updater.BatchUpdateManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * MythicItemUpdate 主插件类
 * 负责插件的启动、关闭和管理器协调
 *
 * @author i7mc
 * @version 1.0
 */
public final class MythicItemUpdate extends JavaPlugin {

    // 管理器实例
    private ConfigManager configManager;
    private MessageManager messageManager;
    private ItemDetector itemDetector;
    private BatchUpdateManager batchUpdateManager;

    // 事件监听器实例
    private ReloadEventListener reloadEventListener;
    private UpdateEventHandler updateEventHandler;

    @Override
    public void onEnable() {
        try {
            getLogger().info("正在启用 MythicItemUpdate...");

            // 初始化管理器
            if (!initializeManagers()) {
                getLogger().severe("管理器初始化失败，插件将被禁用");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }

            // 初始化事件监听器
            if (!initializeEventListeners()) {
                getLogger().severe("事件监听器初始化失败，插件将被禁用");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }

            // 发送启动成功消息
            messageManager.sendStartupMessage("enabled");
            getLogger().info("MythicItemUpdate 已成功启用！");

        } catch (Exception e) {
            getLogger().severe("启用插件时发生错误: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        try {
            getLogger().info("正在禁用 MythicItemUpdate...");

            // 关闭事件监听器
            shutdownEventListeners();

            // 关闭管理器
            shutdownManagers();

            // 发送关闭消息
            if (messageManager != null) {
                messageManager.sendStartupMessage("disabled");
            }

            getLogger().info("MythicItemUpdate 已成功禁用！");

        } catch (Exception e) {
            getLogger().severe("禁用插件时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 初始化所有管理器
     *
     * @return 是否初始化成功
     */
    private boolean initializeManagers() {
        try {
            // 初始化配置管理器
            configManager = new ConfigManager(this);
            if (!configManager.enable()) {
                getLogger().severe("配置管理器初始化失败");
                return false;
            }

            // 初始化消息管理器
            messageManager = new MessageManager(this);
            if (!messageManager.enable()) {
                getLogger().severe("消息管理器初始化失败");
                return false;
            }

            // 初始化物品检测器
            itemDetector = new ItemDetector(this);
            if (!itemDetector.enable()) {
                getLogger().severe("物品检测器初始化失败");
                return false;
            }

            // 初始化批量更新管理器
            batchUpdateManager = new BatchUpdateManager(this);
            if (!batchUpdateManager.enable()) {
                getLogger().severe("批量更新管理器初始化失败");
                return false;
            }

            getLogger().info("所有管理器初始化成功");
            return true;

        } catch (Exception e) {
            getLogger().severe("初始化管理器时发生错误: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 初始化事件监听器
     *
     * @return 是否初始化成功
     */
    private boolean initializeEventListeners() {
        try {
            // 初始化重载事件监听器
            reloadEventListener = new ReloadEventListener(this);
            if (!reloadEventListener.enable()) {
                getLogger().severe("重载事件监听器初始化失败");
                return false;
            }

            // 初始化更新事件处理器
            updateEventHandler = new UpdateEventHandler(this);
            if (!updateEventHandler.enable()) {
                getLogger().severe("更新事件处理器初始化失败");
                return false;
            }

            getLogger().info("所有事件监听器初始化成功");
            return true;

        } catch (Exception e) {
            getLogger().severe("初始化事件监听器时发生错误: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 关闭所有管理器
     */
    private void shutdownManagers() {
        try {
            if (batchUpdateManager != null) {
                batchUpdateManager.disable();
                batchUpdateManager = null;
            }

            if (itemDetector != null) {
                itemDetector.disable();
                itemDetector = null;
            }

            if (messageManager != null) {
                messageManager.disable();
                messageManager = null;
            }

            if (configManager != null) {
                configManager.disable();
                configManager = null;
            }

            getLogger().info("所有管理器已关闭");

        } catch (Exception e) {
            getLogger().severe("关闭管理器时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 关闭事件监听器
     */
    private void shutdownEventListeners() {
        try {
            if (updateEventHandler != null) {
                updateEventHandler.disable();
                updateEventHandler = null;
            }

            if (reloadEventListener != null) {
                reloadEventListener.disable();
                reloadEventListener = null;
            }

            getLogger().info("所有事件监听器已关闭");

        } catch (Exception e) {
            getLogger().severe("关闭事件监听器时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== 管理器获取方法 ====================

    /**
     * 获取配置管理器
     *
     * @return 配置管理器实例
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * 获取消息管理器
     *
     * @return 消息管理器实例
     */
    public MessageManager getMessageManager() {
        return messageManager;
    }

    /**
     * 获取物品检测器
     *
     * @return 物品检测器实例
     */
    public ItemDetector getItemDetector() {
        return itemDetector;
    }

    /**
     * 获取批量更新管理器
     *
     * @return 批量更新管理器实例
     */
    public BatchUpdateManager getBatchUpdateManager() {
        return batchUpdateManager;
    }

    /**
     * 获取重载事件监听器
     *
     * @return 重载事件监听器实例
     */
    public ReloadEventListener getReloadEventListener() {
        return reloadEventListener;
    }

    /**
     * 获取更新事件处理器
     *
     * @return 更新事件处理器实例
     */
    public UpdateEventHandler getUpdateEventHandler() {
        return updateEventHandler;
    }
}
