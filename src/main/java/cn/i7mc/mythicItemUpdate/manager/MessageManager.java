package cn.i7mc.mythicItemUpdate.manager;

import cn.i7mc.mythicItemUpdate.MythicItemUpdate;
import cn.i7mc.mythicItemUpdate.core.AbstractManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息管理器
 * 负责管理插件的消息配置和发送
 * 
 * @author i7mc
 * @version 1.0
 */
public class MessageManager extends AbstractManager {
    
    private FileConfiguration messageConfig;
    private File messageFile;
    
    public MessageManager(MythicItemUpdate plugin) {
        super(plugin);
    }
    
    @Override
    public boolean initialize() {
        try {
            // 创建消息配置文件
            messageFile = new File(plugin.getDataFolder(), "message.yml");
            
            if (!messageFile.exists()) {
                plugin.saveResource("message.yml", false);
            }
            
            // 加载消息配置
            messageConfig = YamlConfiguration.loadConfiguration(messageFile);
            
            // 加载默认消息配置
            InputStream defaultStream = plugin.getResource("message.yml");
            if (defaultStream != null) {
                YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
                messageConfig.setDefaults(defaultConfig);
            }
            
            info("消息管理器初始化成功");
            return true;
            
        } catch (Exception e) {
            handleError("初始化消息管理器失败", e);
            return false;
        }
    }
    
    @Override
    public boolean reload() {
        try {
            if (messageFile.exists()) {
                messageConfig = YamlConfiguration.loadConfiguration(messageFile);
                
                // 重新加载默认配置
                InputStream defaultStream = plugin.getResource("message.yml");
                if (defaultStream != null) {
                    YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
                    messageConfig.setDefaults(defaultConfig);
                }
            }
            
            info("消息配置已重载");
            return true;
            
        } catch (Exception e) {
            handleError("重载消息配置失败", e);
            return false;
        }
    }
    
    @Override
    public void shutdown() {
        messageConfig = null;
        messageFile = null;
        debug("消息管理器已关闭");
    }
    
    @Override
    public String getName() {
        return "MessageManager";
    }
    
    /**
     * 获取消息
     * 
     * @param path 消息路径
     * @param defaultMessage 默认消息
     * @return 格式化后的消息
     */
    public String getMessage(String path, String defaultMessage) {
        if (messageConfig == null) {
            return formatMessage(defaultMessage);
        }
        
        String message = messageConfig.getString(path, defaultMessage);
        return formatMessage(message);
    }
    
    /**
     * 获取消息并替换占位符
     * 
     * @param path 消息路径
     * @param defaultMessage 默认消息
     * @param placeholders 占位符映射
     * @return 格式化后的消息
     */
    public String getMessage(String path, String defaultMessage, Map<String, String> placeholders) {
        String message = getMessage(path, defaultMessage);
        
        if (placeholders != null && !placeholders.isEmpty()) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        
        return message;
    }
    
    /**
     * 发送消息给命令发送者
     * 
     * @param sender 命令发送者
     * @param path 消息路径
     * @param defaultMessage 默认消息
     */
    public void sendMessage(CommandSender sender, String path, String defaultMessage) {
        String message = getMessage(path, defaultMessage);
        sender.sendMessage(message);
    }
    
    /**
     * 发送消息给命令发送者（带占位符）
     * 
     * @param sender 命令发送者
     * @param path 消息路径
     * @param defaultMessage 默认消息
     * @param placeholders 占位符映射
     */
    public void sendMessage(CommandSender sender, String path, String defaultMessage, 
                           Map<String, String> placeholders) {
        String message = getMessage(path, defaultMessage, placeholders);
        sender.sendMessage(message);
    }
    
    /**
     * 发送调试消息
     * 
     * @param path 消息路径
     * @param defaultMessage 默认消息
     */
    public void sendDebugMessage(String path, String defaultMessage) {
        if (plugin.getConfig().getBoolean("settings.debug", false)) {
            String message = getMessage(path, defaultMessage);
            plugin.getLogger().info(message);
        }
    }
    

    
    /**
     * 格式化消息（处理颜色代码）
     * 
     * @param message 原始消息
     * @return 格式化后的消息
     */
    private String formatMessage(String message) {
        if (message == null) {
            return "";
        }
        
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    /**
     * 创建占位符映射的便捷方法
     * 
     * @param key 键
     * @param value 值
     * @return 占位符映射
     */
    public Map<String, String> createPlaceholders(String key, String value) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put(key, value);
        return placeholders;
    }
    
    /**
     * 创建多个占位符映射的便捷方法
     * 
     * @param keyValuePairs 键值对（偶数个参数，奇数位置为键，偶数位置为值）
     * @return 占位符映射
     */
    public Map<String, String> createPlaceholders(String... keyValuePairs) {
        Map<String, String> placeholders = new HashMap<>();
        
        if (keyValuePairs.length % 2 != 0) {
            warning("占位符参数数量必须为偶数");
            return placeholders;
        }
        
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            placeholders.put(keyValuePairs[i], keyValuePairs[i + 1]);
        }
        
        return placeholders;
    }
    
    // 常用消息的便捷方法
    
    public void sendStartupMessage(String type) {
        String message = getMessage("startup." + type, "&a[MythicItemUpdate] " + type);
        plugin.getLogger().info(ChatColor.stripColor(message));
    }
    
    public void sendReloadMessage(String type) {
        String message = getMessage("reload." + type, "&a[MythicItemUpdate] " + type);
        plugin.getLogger().info(ChatColor.stripColor(message));
    }
    
    public void sendErrorMessage(String type, String error) {
        Map<String, String> placeholders = createPlaceholders("error", error);
        String message = getMessage("errors." + type, "&c[MythicItemUpdate] 错误: " + error, placeholders);
        plugin.getLogger().severe(ChatColor.stripColor(message));
    }

    /**
     * 向所有在线管理员广播消息
     *
     * @param messageKey 消息键
     * @param placeholders 占位符
     */
    public void broadcastToAdmins(String messageKey, Map<String, String> placeholders) {
        String message = getMessage(messageKey, "&a[MythicItemUpdate] 管理员消息", placeholders);

        plugin.getServer().getOnlinePlayers().forEach(player -> {
            if (player.hasPermission("mythicitemupdate.admin") || player.isOp()) {
                player.sendMessage(colorize(message));
            }
        });

        // 同时记录到控制台
        plugin.getLogger().info(ChatColor.stripColor(message));
    }

    /**
     * 向所有在线管理员广播消息（无占位符）
     *
     * @param messageKey 消息键
     */
    public void broadcastToAdmins(String messageKey) {
        broadcastToAdmins(messageKey, new HashMap<>());
    }

    /**
     * 发送调试消息（新版本，支持配置管理器）
     *
     * @param messageKey 消息键
     * @param debugMessage 调试消息内容
     * @param placeholders 占位符
     */
    public void sendDebugMessage(String messageKey, String debugMessage, Map<String, String> placeholders) {
        if (plugin.getConfigManager().isDebugEnabled()) {
            Map<String, String> debugPlaceholders = new HashMap<>(placeholders);
            debugPlaceholders.put("message", debugMessage);

            String message = getMessage(messageKey, "&7[DEBUG] " + debugMessage, debugPlaceholders);
            plugin.getLogger().info(ChatColor.stripColor(message));
        }
    }

    /**
     * 颜色化消息
     *
     * @param message 原始消息
     * @return 颜色化后的消息
     */
    private String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
