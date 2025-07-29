package cn.i7mc.mythicItemUpdate.core;

import cn.i7mc.mythicItemUpdate.MythicItemUpdate;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 抽象管理器基类
 * 提供统一的管理器模式实现，所有管理器都应继承此类
 * 
 * @author i7mc
 * @version 1.0
 */
public abstract class AbstractManager {
    
    protected final MythicItemUpdate plugin;
    protected boolean enabled;
    
    /**
     * 构造函数
     * 
     * @param plugin 插件实例
     */
    public AbstractManager(MythicItemUpdate plugin) {
        this.plugin = plugin;
        this.enabled = false;
    }
    
    /**
     * 初始化管理器
     * 子类应重写此方法实现具体的初始化逻辑
     * 
     * @return 是否初始化成功
     */
    public abstract boolean initialize();
    
    /**
     * 重载管理器
     * 子类应重写此方法实现具体的重载逻辑
     * 
     * @return 是否重载成功
     */
    public abstract boolean reload();
    
    /**
     * 关闭管理器
     * 子类应重写此方法实现具体的关闭逻辑
     */
    public abstract void shutdown();
    
    /**
     * 获取管理器名称
     * 子类应重写此方法返回具体的管理器名称
     * 
     * @return 管理器名称
     */
    public abstract String getName();
    
    /**
     * 启用管理器
     * 
     * @return 是否启用成功
     */
    public final boolean enable() {
        if (enabled) {
            return true;
        }
        
        try {
            if (initialize()) {
                enabled = true;
                onEnable();
                return true;
            }
        } catch (Exception e) {
            handleError("启用管理器时发生错误", e);
        }
        
        return false;
    }
    
    /**
     * 禁用管理器
     */
    public final void disable() {
        if (!enabled) {
            return;
        }
        
        try {
            onDisable();
            shutdown();
            enabled = false;
        } catch (Exception e) {
            handleError("禁用管理器时发生错误", e);
        }
    }
    
    /**
     * 检查管理器是否已启用
     * 
     * @return 是否已启用
     */
    public final boolean isEnabled() {
        return enabled;
    }
    
    /**
     * 获取插件实例
     * 
     * @return 插件实例
     */
    public final MythicItemUpdate getPlugin() {
        return plugin;
    }
    
    /**
     * 管理器启用时的回调方法
     * 子类可重写此方法实现启用后的额外逻辑
     */
    protected void onEnable() {
        // 默认实现为空，子类可重写
    }
    
    /**
     * 管理器禁用时的回调方法
     * 子类可重写此方法实现禁用前的额外逻辑
     */
    protected void onDisable() {
        // 默认实现为空，子类可重写
    }
    
    /**
     * 统一的错误处理方法
     * 
     * @param message 错误消息
     * @param throwable 异常对象
     */
    protected final void handleError(String message, Throwable throwable) {
        plugin.getLogger().severe(String.format("[%s] %s: %s", 
            getName(), message, throwable.getMessage()));
        
        if (plugin.getConfig().getBoolean("settings.debug", false)) {
            throwable.printStackTrace();
        }
    }
    
    /**
     * 记录调试信息
     * 
     * @param message 调试消息
     */
    protected final void debug(String message) {
        if (plugin.getConfig().getBoolean("settings.debug", false)) {
            plugin.getLogger().info(String.format("[DEBUG][%s] %s", getName(), message));
        }
    }
    
    /**
     * 记录信息
     * 
     * @param message 信息消息
     */
    protected final void info(String message) {
        plugin.getLogger().info(String.format("[%s] %s", getName(), message));
    }
    
    /**
     * 记录警告
     * 
     * @param message 警告消息
     */
    protected final void warning(String message) {
        plugin.getLogger().warning(String.format("[%s] %s", getName(), message));
    }
}
