package cn.i7mc.mythicItemUpdate.manager;

import cn.i7mc.mythicItemUpdate.MythicItemUpdate;
import cn.i7mc.mythicItemUpdate.core.AbstractManager;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * 配置管理器
 * 负责管理插件的配置文件
 * 
 * @author i7mc
 * @version 1.0
 */
public class ConfigManager extends AbstractManager {
    
    private FileConfiguration config;
    
    public ConfigManager(MythicItemUpdate plugin) {
        super(plugin);
    }
    
    @Override
    public boolean initialize() {
        try {
            // 保存默认配置文件
            plugin.saveDefaultConfig();
            
            // 加载配置
            plugin.reloadConfig();
            config = plugin.getConfig();
            
            // 验证配置
            if (!validateConfig()) {
                warning("配置文件验证失败，使用默认值");
            }
            
            info("配置管理器初始化成功");
            return true;
            
        } catch (Exception e) {
            handleError("初始化配置管理器失败", e);
            return false;
        }
    }
    
    @Override
    public boolean reload() {
        try {
            plugin.reloadConfig();
            config = plugin.getConfig();
            
            if (!validateConfig()) {
                warning("配置文件验证失败，使用默认值");
            }
            
            info("配置已重载");
            return true;
            
        } catch (Exception e) {
            handleError("重载配置失败", e);
            return false;
        }
    }
    
    @Override
    public void shutdown() {
        config = null;
        debug("配置管理器已关闭");
    }
    
    @Override
    public String getName() {
        return "ConfigManager";
    }
    
    /**
     * 验证配置文件
     * 
     * @return 配置是否有效
     */
    private boolean validateConfig() {
        if (config == null) {
            return false;
        }
        
        // 检查必要的配置项
        boolean valid = true;
        
        if (!config.contains("settings.enabled")) {
            config.set("settings.enabled", true);
            valid = false;
        }
        
        if (!config.contains("settings.debug")) {
            config.set("settings.debug", false);
            valid = false;
        }
        
        if (!config.contains("settings.item-update.auto-update")) {
            config.set("settings.item-update.auto-update", true);
            valid = false;
        }
        

        
        if (!valid) {
            plugin.saveConfig();
        }
        
        return valid;
    }
    
    /**
     * 获取配置值
     * 
     * @param path 配置路径
     * @param defaultValue 默认值
     * @return 配置值
     */
    public <T> T getConfigValue(String path, T defaultValue) {
        if (config == null) {
            return defaultValue;
        }
        
        Object value = config.get(path, defaultValue);
        try {
            @SuppressWarnings("unchecked")
            T result = (T) value;
            return result;
        } catch (ClassCastException e) {
            warning(String.format("配置项 %s 类型错误，使用默认值", path));
            return defaultValue;
        }
    }
    
    /**
     * 设置配置值
     *
     * @param path 配置路径
     * @param value 配置值
     */
    public void setConfigValue(String path, Object value) {
        if (config != null) {
            config.set(path, value);
            plugin.saveConfig();
        }
    }

    /**
     * 安全地将配置值转换为long类型
     *
     * @param value 配置值
     * @param defaultValue 默认值
     * @return long类型的值
     */
    private long convertToLong(Object value, long defaultValue) {
        if (value == null) {
            return defaultValue;
        }

        try {
            if (value instanceof Long) {
                return (Long) value;
            } else if (value instanceof Integer) {
                return ((Integer) value).longValue();
            } else if (value instanceof Number) {
                return ((Number) value).longValue();
            } else if (value instanceof String) {
                return Long.parseLong((String) value);
            } else {
                warning(String.format("无法将配置值 %s 转换为long类型，使用默认值 %d", value, defaultValue));
                return defaultValue;
            }
        } catch (Exception e) {
            warning(String.format("转换配置值 %s 为long类型时发生错误，使用默认值 %d: %s", value, defaultValue, e.getMessage()));
            return defaultValue;
        }
    }
    
    /**
     * 检查插件是否启用
     * 
     * @return 是否启用
     */
    public boolean isPluginEnabled() {
        return getConfigValue("settings.enabled", true);
    }
    
    /**
     * 检查是否启用调试模式
     * 
     * @return 是否启用调试
     */
    public boolean isDebugEnabled() {
        return getConfigValue("settings.debug", false);
    }
    
    /**
     * 检查是否启用自动更新
     * 
     * @return 是否启用自动更新
     */
    public boolean isAutoUpdateEnabled() {
        return getConfigValue("settings.item-update.auto-update", true);
    }
    

    
    /**
     * 检查是否更新玩家背包
     * 
     * @return 是否更新背包
     */
    public boolean isUpdateInventoryEnabled() {
        return getConfigValue("settings.item-update.update-inventory", true);
    }
    
    /**
     * 检查是否更新末影箱
     * 
     * @return 是否更新末影箱
     */
    public boolean isUpdateEnderChestEnabled() {
        return getConfigValue("settings.item-update.update-enderchest", true);
    }
    
    /**
     * 检查是否更新掉落物品
     * 
     * @return 是否更新掉落物品
     */
    public boolean isUpdateDroppedItemsEnabled() {
        return getConfigValue("settings.item-update.update-dropped-items", true);
    }
    
    /**
     * 检查是否更新容器
     *
     * @return 是否更新容器
     */
    public boolean isUpdateContainersEnabled() {
        return getConfigValue("settings.item-update.update-containers", true);
    }

    // ==================== 事件处理配置 ====================

    /**
     * 检查是否启用玩家加入时更新
     *
     * @return 是否启用
     */
    public boolean isUpdateOnJoinEnabled() {
        return getConfigValue("events.update-on-join", true);
    }

    /**
     * 获取玩家加入后的更新延迟
     *
     * @return 延迟时间（tick）
     */
    public long getJoinUpdateDelay() {
        Object value = getConfigValue("events.join-update-delay", 40);
        return convertToLong(value, 40L);
    }

    /**
     * 检查是否启用打开背包时更新
     *
     * @return 是否启用
     */
    public boolean isUpdateOnInventoryOpenEnabled() {
        return getConfigValue("events.update-on-inventory-open", true);
    }

    /**
     * 检查是否启用点击时更新
     *
     * @return 是否启用
     */
    public boolean isUpdateOnClickEnabled() {
        return getConfigValue("events.update-on-click", true);
    }

    /**
     * 检查是否启用拾取时更新
     *
     * @return 是否启用
     */
    public boolean isUpdateOnPickupEnabled() {
        return getConfigValue("events.update-on-pickup", true);
    }

    /**
     * 检查是否向管理员发送通知
     *
     * @return 是否发送通知
     */
    public boolean isNotifyAdminsEnabled() {
        return getConfigValue("events.notify-admins", true);
    }
}
