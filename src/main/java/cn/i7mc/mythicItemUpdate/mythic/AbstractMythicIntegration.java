package cn.i7mc.mythicItemUpdate.mythic;

import cn.i7mc.mythicItemUpdate.MythicItemUpdate;
import cn.i7mc.mythicItemUpdate.core.AbstractManager;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.core.items.MythicItem;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

/**
 * MythicMobs API集成抽象类
 * 提供与MythicMobs插件的基础集成功能
 * 
 * @author i7mc
 * @version 1.0
 */
public abstract class AbstractMythicIntegration extends AbstractManager {

    protected MythicBukkit mythicBukkit;
    protected Plugin mythicPlugin;
    protected boolean mythicAvailable;
    
    public AbstractMythicIntegration(MythicItemUpdate plugin) {
        super(plugin);
        this.mythicAvailable = false;
    }
    
    @Override
    public boolean initialize() {
        try {
            // 检查MythicMobs插件是否存在
            mythicPlugin = Bukkit.getPluginManager().getPlugin("MythicMobs");
            if (mythicPlugin == null || !mythicPlugin.isEnabled()) {
                warning("MythicMobs插件未找到或未启用");
                return false;
            }
            
            // 获取MythicMobs实例
            mythicBukkit = MythicBukkit.inst();
            if (mythicBukkit == null) {
                warning("无法获取MythicBukkit实例");
                return false;
            }
            
            // 检查API版本兼容性
            if (!checkAPICompatibility()) {
                warning("MythicMobs API版本不兼容");
                return false;
            }
            
            mythicAvailable = true;
            info("MythicMobs集成初始化成功");
            return initializeIntegration();
            
        } catch (Exception e) {
            handleError("初始化MythicMobs集成失败", e);
            return false;
        }
    }
    
    @Override
    public boolean reload() {
        try {
            if (!mythicAvailable) {
                return initialize();
            }
            
            return reloadIntegration();
            
        } catch (Exception e) {
            handleError("重载MythicMobs集成失败", e);
            return false;
        }
    }
    
    @Override
    public void shutdown() {
        try {
            shutdownIntegration();
            mythicBukkit = null;
            mythicPlugin = null;
            mythicAvailable = false;
            debug("MythicMobs集成已关闭");
            
        } catch (Exception e) {
            handleError("关闭MythicMobs集成时发生错误", e);
        }
    }
    
    /**
     * 子类特定的初始化逻辑
     * 
     * @return 是否初始化成功
     */
    protected abstract boolean initializeIntegration();
    
    /**
     * 子类特定的重载逻辑
     * 
     * @return 是否重载成功
     */
    protected abstract boolean reloadIntegration();
    
    /**
     * 子类特定的关闭逻辑
     */
    protected abstract void shutdownIntegration();
    
    /**
     * 检查MythicMobs是否可用
     * 
     * @return 是否可用
     */
    public final boolean isMythicAvailable() {
        return mythicAvailable && mythicPlugin != null && mythicPlugin.isEnabled();
    }
    
    /**
     * 获取MythicBukkit实例
     *
     * @return MythicBukkit实例
     */
    public final MythicBukkit getMythicBukkit() {
        return mythicBukkit;
    }
    
    /**
     * 获取MythicMobs插件实例
     * 
     * @return 插件实例
     */
    public final Plugin getMythicPlugin() {
        return mythicPlugin;
    }
    
    /**
     * 检查物品是否为MythicMobs物品
     * 
     * @param item 要检查的物品
     * @return 是否为MythicMobs物品
     */
    public boolean isMythicItem(ItemStack item) {
        if (!isMythicAvailable() || item == null) {
            return false;
        }
        
        try {
            return mythicBukkit.getItemManager().isMythicItem(item);
        } catch (Exception e) {
            debug("检查MythicMobs物品时发生错误: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 获取MythicMobs物品的内部名称
     * 
     * @param item 物品
     * @return 内部名称，如果不是MythicMobs物品则返回null
     */
    public String getMythicItemInternalName(ItemStack item) {
        if (!isMythicItem(item)) {
            return null;
        }
        
        try {
            return mythicBukkit.getItemManager().getMythicTypeFromItem(item);
        } catch (Exception e) {
            debug("获取MythicMobs物品内部名称时发生错误: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 根据内部名称获取MythicMobs物品
     * 
     * @param internalName 内部名称
     * @return MythicItem实例，如果不存在则返回null
     */
    public MythicItem getMythicItemByName(String internalName) {
        if (!isMythicAvailable() || internalName == null || internalName.trim().isEmpty()) {
            return null;
        }
        
        try {
            Optional<MythicItem> mythicItem = mythicBukkit.getItemManager().getItem(internalName);
            return mythicItem.orElse(null);
        } catch (Exception e) {
            debug("根据名称获取MythicMobs物品时发生错误: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 生成MythicMobs物品
     * 
     * @param internalName 内部名称
     * @param amount 数量
     * @return 生成的物品，如果失败则返回null
     */
    public ItemStack generateMythicItem(String internalName, int amount) {
        if (!isMythicAvailable() || internalName == null || internalName.trim().isEmpty()) {
            return null;
        }
        
        try {
            MythicItem mythicItem = getMythicItemByName(internalName);
            if (mythicItem == null) {
                debug("未找到MythicMobs物品: " + internalName);
                return null;
            }
            
            io.lumine.mythic.api.adapters.AbstractItemStack abstractItem = mythicItem.generateItemStack(amount);
            ItemStack item = io.lumine.mythic.bukkit.BukkitAdapter.adapt(abstractItem);
            debug("成功生成MythicMobs物品: " + internalName + " x" + amount);
            return item;
            
        } catch (Exception e) {
            debug("生成MythicMobs物品时发生错误: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 检查API版本兼容性
     * 
     * @return 是否兼容
     */
    private boolean checkAPICompatibility() {
        try {
            String version = mythicPlugin.getDescription().getVersion();
            debug("检测到MythicMobs版本: " + version);
            
            // 检查最低版本要求（5.0+）
            if (version.startsWith("4.") || version.startsWith("3.") || version.startsWith("2.")) {
                warning("MythicMobs版本过低，需要5.0或更高版本");
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            debug("检查API兼容性时发生错误: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 安全地执行MythicMobs API调用
     * 
     * @param operation 操作描述
     * @param apiCall API调用
     * @param defaultValue 默认返回值
     * @param <T> 返回类型
     * @return 操作结果
     */
    protected <T> T safeMythicCall(String operation, MythicAPICall<T> apiCall, T defaultValue) {
        if (!isMythicAvailable()) {
            debug("MythicMobs不可用，跳过操作: " + operation);
            return defaultValue;
        }
        
        try {
            return apiCall.call();
        } catch (Exception e) {
            debug("执行MythicMobs API调用失败 [" + operation + "]: " + e.getMessage());
            return defaultValue;
        }
    }
    
    /**
     * MythicMobs API调用接口
     * 
     * @param <T> 返回类型
     */
    @FunctionalInterface
    protected interface MythicAPICall<T> {
        T call() throws Exception;
    }
}
