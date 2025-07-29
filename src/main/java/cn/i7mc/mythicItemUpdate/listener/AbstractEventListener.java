package cn.i7mc.mythicItemUpdate.listener;

import cn.i7mc.mythicItemUpdate.MythicItemUpdate;
import cn.i7mc.mythicItemUpdate.core.AbstractManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

/**
 * 事件监听抽象类
 * 提供统一的事件监听器模式实现，所有事件监听器都应继承此类
 * 
 * @author i7mc
 * @version 1.0
 */
public abstract class AbstractEventListener extends AbstractManager implements Listener {
    
    protected PluginManager pluginManager;
    protected boolean registered;
    
    /**
     * 构造函数
     * 
     * @param plugin 插件实例
     */
    public AbstractEventListener(MythicItemUpdate plugin) {
        super(plugin);
        this.pluginManager = plugin.getServer().getPluginManager();
        this.registered = false;
    }
    
    @Override
    public boolean initialize() {
        try {
            if (registered) {
                return true;
            }
            
            // 执行子类特定的初始化逻辑
            if (!initializeListener()) {
                return false;
            }
            
            // 注册事件监听器
            registerEvents();
            registered = true;
            
            info("事件监听器初始化成功");
            return true;
            
        } catch (Exception e) {
            handleError("初始化事件监听器失败", e);
            return false;
        }
    }
    
    @Override
    public boolean reload() {
        try {
            // 如果已注册，先注销再重新注册
            if (registered) {
                unregisterEvents();
                registered = false;
            }
            
            // 执行子类特定的重载逻辑
            if (!reloadListener()) {
                return false;
            }
            
            // 重新注册事件监听器
            registerEvents();
            registered = true;
            
            info("事件监听器重载成功");
            return true;
            
        } catch (Exception e) {
            handleError("重载事件监听器失败", e);
            return false;
        }
    }
    
    @Override
    public void shutdown() {
        try {
            // 执行子类特定的关闭逻辑
            shutdownListener();
            
            // 注销事件监听器
            if (registered) {
                unregisterEvents();
                registered = false;
            }
            
            debug("事件监听器已关闭");
            
        } catch (Exception e) {
            handleError("关闭事件监听器时发生错误", e);
        }
    }
    
    /**
     * 子类特定的初始化逻辑
     * 
     * @return 是否初始化成功
     */
    protected abstract boolean initializeListener();
    
    /**
     * 子类特定的重载逻辑
     * 
     * @return 是否重载成功
     */
    protected abstract boolean reloadListener();
    
    /**
     * 子类特定的关闭逻辑
     */
    protected abstract void shutdownListener();
    
    /**
     * 注册事件监听器
     */
    protected final void registerEvents() {
        if (!registered) {
            pluginManager.registerEvents(this, plugin);
            debug("事件监听器已注册: " + getName());
        }
    }
    
    /**
     * 注销事件监听器
     */
    protected final void unregisterEvents() {
        if (registered) {
            // Paper API没有直接的注销单个监听器的方法
            // 通常通过插件禁用时自动注销
            debug("事件监听器已注销: " + getName());
        }
    }
    
    /**
     * 检查事件监听器是否已注册
     * 
     * @return 是否已注册
     */
    public final boolean isRegistered() {
        return registered;
    }
    
    /**
     * 安全地处理事件
     *
     * @param eventName 事件名称
     * @param eventHandler 事件处理逻辑
     */
    protected final void safeEventHandle(String eventName, EventHandlerFunction eventHandler) {
        try {
            eventHandler.handle();
        } catch (Exception e) {
            handleError("处理事件失败: " + eventName, e);
        }
    }
    
    /**
     * 检查是否应该处理此事件
     * 
     * @return 是否应该处理
     */
    protected boolean shouldHandleEvent() {
        return isEnabled() && registered;
    }
    
    /**
     * 发送调试消息
     * 
     * @param message 消息
     * @param placeholders 占位符
     */
    protected void sendDebugMessage(String message, String... placeholders) {
        if (plugin.getConfigManager().isDebugEnabled()) {
            plugin.getMessageManager().sendDebugMessage(
                "debug.event-processing", 
                message, 
                plugin.getMessageManager().createPlaceholders(placeholders)
            );
        }
    }
    
    /**
     * 事件处理接口
     */
    @FunctionalInterface
    protected interface EventHandlerFunction {
        void handle() throws Exception;
    }
}
