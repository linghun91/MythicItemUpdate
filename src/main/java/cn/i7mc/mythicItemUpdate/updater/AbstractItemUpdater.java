package cn.i7mc.mythicItemUpdate.updater;

import cn.i7mc.mythicItemUpdate.MythicItemUpdate;
import cn.i7mc.mythicItemUpdate.core.AbstractManager;
import cn.i7mc.mythicItemUpdate.data.*;
import cn.i7mc.mythicItemUpdate.util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 物品更新抽象类
 * 提供物品更新的基础功能和统一接口
 * 
 * @author i7mc
 * @version 1.0
 */
public abstract class AbstractItemUpdater extends AbstractManager {
    
    protected final AtomicInteger totalProcessed;
    protected final AtomicInteger totalUpdated;
    protected final AtomicInteger totalFailed;
    
    public AbstractItemUpdater(MythicItemUpdate plugin) {
        super(plugin);
        this.totalProcessed = new AtomicInteger(0);
        this.totalUpdated = new AtomicInteger(0);
        this.totalFailed = new AtomicInteger(0);
    }
    
    @Override
    public boolean initialize() {
        resetCounters();
        return initializeUpdater();
    }
    
    @Override
    public boolean reload() {
        return reloadUpdater();
    }
    
    @Override
    public void shutdown() {
        shutdownUpdater();
        resetCounters();
    }
    
    /**
     * 子类特定的初始化逻辑
     * 
     * @return 是否初始化成功
     */
    protected abstract boolean initializeUpdater();
    
    /**
     * 子类特定的重载逻辑
     * 
     * @return 是否重载成功
     */
    protected abstract boolean reloadUpdater();
    
    /**
     * 子类特定的关闭逻辑
     */
    protected abstract void shutdownUpdater();
    
    /**
     * 更新单个物品
     *
     * @param itemData 物品数据
     * @param player 玩家实例（可能为null，用于掉落物品等情况）
     * @return 更新结果
     */
    public abstract UpdateResult updateItem(MythicItemData itemData, Player player);

    /**
     * 更新掉落物品
     *
     * @param droppedData 掉落物品数据
     * @return 更新结果
     */
    public abstract UpdateResult updateDroppedItem(DroppedItemData droppedData);
    
    /**
     * 检查物品是否需要更新
     * 
     * @param item 物品
     * @return 是否需要更新
     */
    protected boolean needsUpdate(ItemStack item) {
        if (!Utils.isValidItem(item)) {
            return false;
        }
        
        // 使用物品检测器检查是否需要更新
        if (plugin.getItemDetector() != null) {
            return plugin.getItemDetector().needsUpdate(item);
        }
        
        return false;
    }
    
    /**
     * 获取更新后的物品
     * 
     * @param item 原物品
     * @return 更新后的物品
     */
    protected ItemStack getUpdatedItem(ItemStack item) {
        if (!Utils.isValidItem(item)) {
            return item;
        }
        
        // 使用物品检测器获取更新后的物品
        if (plugin.getItemDetector() != null) {
            return plugin.getItemDetector().getUpdatedItem(item);
        }
        
        return item;
    }
    
    /**
     * 重置计数器
     */
    protected void resetCounters() {
        totalProcessed.set(0);
        totalUpdated.set(0);
        totalFailed.set(0);
    }
    
    /**
     * 增加处理计数
     */
    protected void incrementProcessed() {
        totalProcessed.incrementAndGet();
    }
    
    /**
     * 增加更新计数
     */
    protected void incrementUpdated() {
        totalUpdated.incrementAndGet();
    }
    
    /**
     * 增加失败计数
     */
    protected void incrementFailed() {
        totalFailed.incrementAndGet();
    }
    
    /**
     * 获取处理总数
     * 
     * @return 处理总数
     */
    public int getTotalProcessed() {
        return totalProcessed.get();
    }
    
    /**
     * 获取更新总数
     * 
     * @return 更新总数
     */
    public int getTotalUpdated() {
        return totalUpdated.get();
    }
    
    /**
     * 获取失败总数
     * 
     * @return 失败总数
     */
    public int getTotalFailed() {
        return totalFailed.get();
    }
    
    /**
     * 获取统计信息
     * 
     * @return 统计信息
     */
    public UpdateStatistics getStatistics() {
        return new UpdateStatistics(
            getTotalProcessed(),
            getTotalUpdated(),
            getTotalFailed()
        );
    }
    
    /**
     * 创建成功的更新结果
     * 
     * @param message 消息
     * @return 更新结果
     */
    protected UpdateResult createSuccessResult(String message) {
        incrementProcessed();
        incrementUpdated();
        return new UpdateResult(true, message, null);
    }
    
    /**
     * 创建失败的更新结果
     * 
     * @param message 错误消息
     * @param error 异常
     * @return 更新结果
     */
    protected UpdateResult createFailureResult(String message, Throwable error) {
        incrementProcessed();
        incrementFailed();
        return new UpdateResult(false, message, error);
    }
    
    /**
     * 创建跳过的更新结果
     * 
     * @param message 消息
     * @return 更新结果
     */
    protected UpdateResult createSkippedResult(String message) {
        incrementProcessed();
        return new UpdateResult(true, message, null, true);
    }
    
    /**
     * 安全地执行更新操作
     * 
     * @param operation 操作描述
     * @param updateOperation 更新操作
     * @return 更新结果
     */
    protected UpdateResult safeUpdate(String operation, UpdateOperation updateOperation) {
        try {
            return updateOperation.execute();
        } catch (Exception e) {
            handleError("执行更新操作失败: " + operation, e);
            return createFailureResult("更新失败: " + e.getMessage(), e);
        }
    }
    
    
    /**
     * 更新操作接口
     */
    @FunctionalInterface
    protected interface UpdateOperation {
        UpdateResult execute() throws Exception;
    }
    
    /**
     * 更新结果类
     */
    public static class UpdateResult {
        private final boolean success;
        private final String message;
        private final Throwable error;
        private final boolean skipped;
        
        public UpdateResult(boolean success, String message, Throwable error) {
            this(success, message, error, false);
        }
        
        public UpdateResult(boolean success, String message, Throwable error, boolean skipped) {
            this.success = success;
            this.message = message;
            this.error = error;
            this.skipped = skipped;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public Throwable getError() {
            return error;
        }
        
        public boolean isSkipped() {
            return skipped;
        }
        
        public boolean hasError() {
            return error != null;
        }
        
        @Override
        public String toString() {
            return String.format("UpdateResult{success=%s, skipped=%s, message='%s', hasError=%s}",
                success, skipped, message, hasError());
        }
    }
    
    /**
     * 更新统计信息类
     */
    public static class UpdateStatistics {
        private final int processed;
        private final int updated;
        private final int failed;
        
        public UpdateStatistics(int processed, int updated, int failed) {
            this.processed = processed;
            this.updated = updated;
            this.failed = failed;
        }
        
        public int getProcessed() {
            return processed;
        }
        
        public int getUpdated() {
            return updated;
        }
        
        public int getFailed() {
            return failed;
        }
        
        public int getSkipped() {
            return processed - updated - failed;
        }
        
        public double getSuccessRate() {
            return processed > 0 ? (double) updated / processed * 100 : 0;
        }
        
        @Override
        public String toString() {
            return String.format("UpdateStatistics{processed=%d, updated=%d, failed=%d, skipped=%d, successRate=%.1f%%}",
                processed, updated, failed, getSkipped(), getSuccessRate());
        }
    }
}
