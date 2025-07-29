package cn.i7mc.mythicItemUpdate.data;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 批量更新统计信息
 * 
 * @author i7mc
 * @version 1.0
 */
public class BatchUpdateStatistics {
    
    private final AtomicInteger totalProcessed;
    private final AtomicInteger totalUpdated;
    private final AtomicInteger totalSkipped;
    private final AtomicInteger totalFailed;
    private final AtomicInteger playersProcessed;
    private final AtomicInteger containersProcessed;
    
    public BatchUpdateStatistics() {
        this.totalProcessed = new AtomicInteger(0);
        this.totalUpdated = new AtomicInteger(0);
        this.totalSkipped = new AtomicInteger(0);
        this.totalFailed = new AtomicInteger(0);
        this.playersProcessed = new AtomicInteger(0);
        this.containersProcessed = new AtomicInteger(0);
    }
    
    /**
     * 增加处理总数
     */
    public void incrementProcessed() {
        totalProcessed.incrementAndGet();
    }
    
    /**
     * 增加更新数量
     */
    public void incrementUpdated() {
        totalUpdated.incrementAndGet();
        incrementProcessed();
    }
    
    /**
     * 增加跳过数量
     */
    public void incrementSkipped() {
        totalSkipped.incrementAndGet();
        incrementProcessed();
    }
    
    /**
     * 增加失败数量
     */
    public void incrementFailed() {
        totalFailed.incrementAndGet();
        incrementProcessed();
    }
    
    /**
     * 增加处理的玩家数量
     */
    public void incrementPlayersProcessed() {
        playersProcessed.incrementAndGet();
    }
    
    /**
     * 增加处理的容器数量
     */
    public void incrementContainersProcessed() {
        containersProcessed.incrementAndGet();
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
     * 获取更新数量
     * 
     * @return 更新数量
     */
    public int getTotalUpdated() {
        return totalUpdated.get();
    }
    
    /**
     * 获取跳过数量
     * 
     * @return 跳过数量
     */
    public int getTotalSkipped() {
        return totalSkipped.get();
    }
    
    /**
     * 获取失败数量
     * 
     * @return 失败数量
     */
    public int getTotalFailed() {
        return totalFailed.get();
    }
    
    /**
     * 获取处理的玩家数量
     * 
     * @return 处理的玩家数量
     */
    public int getPlayersProcessed() {
        return playersProcessed.get();
    }
    
    /**
     * 获取处理的容器数量
     *
     * @return 处理的容器数量
     */
    public int getContainersProcessed() {
        return containersProcessed.get();
    }

    /**
     * 获取处理的物品数量（别名方法）
     *
     * @return 处理的物品数量
     */
    public int getItemsProcessed() {
        return getTotalProcessed();
    }

    /**
     * 获取更新的物品数量（别名方法）
     *
     * @return 更新的物品数量
     */
    public int getItemsUpdated() {
        return getTotalUpdated();
    }

    /**
     * 获取失败的物品数量（别名方法）
     *
     * @return 失败的物品数量
     */
    public int getItemsFailed() {
        return getTotalFailed();
    }
    
    /**
     * 获取成功率
     * 
     * @return 成功率（百分比）
     */
    public double getSuccessRate() {
        int total = getTotalProcessed();
        if (total == 0) {
            return 0.0;
        }
        return (double) getTotalUpdated() / total * 100.0;
    }
    
    /**
     * 获取失败率
     * 
     * @return 失败率（百分比）
     */
    public double getFailureRate() {
        int total = getTotalProcessed();
        if (total == 0) {
            return 0.0;
        }
        return (double) getTotalFailed() / total * 100.0;
    }
    
    /**
     * 检查是否有处理的项目
     * 
     * @return 是否有处理的项目
     */
    public boolean hasProcessedItems() {
        return getTotalProcessed() > 0;
    }
    
    /**
     * 检查是否有更新的项目
     * 
     * @return 是否有更新的项目
     */
    public boolean hasUpdatedItems() {
        return getTotalUpdated() > 0;
    }
    
    /**
     * 检查是否有失败的项目
     * 
     * @return 是否有失败的项目
     */
    public boolean hasFailedItems() {
        return getTotalFailed() > 0;
    }
    
    /**
     * 重置所有统计信息
     */
    public void reset() {
        totalProcessed.set(0);
        totalUpdated.set(0);
        totalSkipped.set(0);
        totalFailed.set(0);
        playersProcessed.set(0);
        containersProcessed.set(0);
    }
    
    @Override
    public String toString() {
        return String.format("BatchUpdateStatistics{processed=%d, updated=%d, skipped=%d, failed=%d, players=%d, containers=%d, successRate=%.1f%%}",
            getTotalProcessed(),
            getTotalUpdated(),
            getTotalSkipped(),
            getTotalFailed(),
            getPlayersProcessed(),
            getContainersProcessed(),
            getSuccessRate());
    }
}
