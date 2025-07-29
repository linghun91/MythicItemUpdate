package cn.i7mc.mythicItemUpdate.updater;

import cn.i7mc.mythicItemUpdate.MythicItemUpdate;
import cn.i7mc.mythicItemUpdate.core.AbstractManager;
import cn.i7mc.mythicItemUpdate.data.*;
import cn.i7mc.mythicItemUpdate.util.Utils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 批量物品更新管理器
 * 负责协调和管理大规模的物品更新操作
 * 
 * @author i7mc
 * @version 1.0
 */
public class BatchUpdateManager extends AbstractManager {
    
    private ItemAttributeUpdater itemUpdater;
    private boolean updateInProgress;
    private final AtomicInteger currentBatch;
    private final AtomicInteger totalBatches;
    
    public BatchUpdateManager(MythicItemUpdate plugin) {
        super(plugin);
        this.updateInProgress = false;
        this.currentBatch = new AtomicInteger(0);
        this.totalBatches = new AtomicInteger(0);
    }
    
    @Override
    public boolean initialize() {
        try {
            // 初始化物品属性更新器
            itemUpdater = new ItemAttributeUpdater(plugin);
            if (!itemUpdater.enable()) {
                warning("物品属性更新器初始化失败");
                return false;
            }
            
            info("批量更新管理器初始化成功");
            return true;
            
        } catch (Exception e) {
            handleError("初始化批量更新管理器失败", e);
            return false;
        }
    }
    
    @Override
    public boolean reload() {
        try {
            if (itemUpdater != null) {
                itemUpdater.reload();
            }
            
            info("批量更新管理器重载成功");
            return true;
            
        } catch (Exception e) {
            handleError("重载批量更新管理器失败", e);
            return false;
        }
    }
    
    @Override
    public void shutdown() {
        try {
            if (itemUpdater != null) {
                itemUpdater.disable();
                itemUpdater = null;
            }
            
            updateInProgress = false;
            debug("批量更新管理器已关闭");
            
        } catch (Exception e) {
            handleError("关闭批量更新管理器时发生错误", e);
        }
    }
    
    @Override
    public String getName() {
        return "BatchUpdateManager";
    }
    
    /**
     * 执行完整的物品更新
     *
     * @return 更新结果
     */
    public BatchUpdateResult performFullUpdate() {
        if (updateInProgress) {
            return new BatchUpdateResult(false, "更新已在进行中", null);
        }

        updateInProgress = true;
        long startTime = System.currentTimeMillis();

        try {
            // 重置计数器
            currentBatch.set(0);
            totalBatches.set(0);

            // 检测所有MythicMobs物品
            DetectionResult detection = plugin.getItemDetector().detectAllMythicItems();

            if (detection.hasError()) {
                return new BatchUpdateResult(false, "物品检测失败: " + detection.getError(), null);
            }

            if (detection.isEmpty()) {
                return new BatchUpdateResult(true, "未找到需要更新的物品", new BatchUpdateStatistics());
            }

            // 执行批量更新
            BatchUpdateStatistics statistics = performBatchUpdate(detection);

            return new BatchUpdateResult(true, "更新完成", statistics);

        } catch (Exception e) {
            handleError("执行完整更新时发生错误", e);
            return new BatchUpdateResult(false, "更新失败: " + e.getMessage(), null);

        } finally {
            updateInProgress = false;
        }
    }
    
    /**
     * 执行批量更新
     * 
     * @param detection 检测结果
     * @return 更新统计信息
     */
    private BatchUpdateStatistics performBatchUpdate(DetectionResult detection) throws Exception {
        BatchUpdateStatistics statistics = new BatchUpdateStatistics();
        
        // 更新玩家物品
        if (!detection.getPlayerData().isEmpty()) {
            updatePlayerItems(detection.getPlayerData(), statistics);
        }
        
        // 更新掉落物品
        if (!detection.getDroppedItems().isEmpty()) {
            updateDroppedItems(detection.getDroppedItems(), statistics);
        }
        
        // 更新容器物品
        if (!detection.getContainerData().isEmpty()) {
            updateContainerItems(detection.getContainerData(), statistics);
        }
        
        return statistics;
    }
    
    /**
     * 更新玩家物品
     *
     * @param playerDataList 玩家数据列表
     * @param statistics 统计信息
     */
    private void updatePlayerItems(List<PlayerItemData> playerDataList, BatchUpdateStatistics statistics) throws Exception {
        for (PlayerItemData playerData : playerDataList) {
            Player player = Utils.getOnlinePlayer(playerData.getPlayerName());
            if (player == null) {
                debug("玩家 " + playerData.getPlayerName() + " 不在线，跳过更新");
                continue;
            }

            // 发送处理消息
            plugin.getMessageManager().sendDebugMessage("progress.player-progress",
                "正在处理玩家: " + playerData.getPlayerName(),
                plugin.getMessageManager().createPlaceholders("player", playerData.getPlayerName()));

            List<MythicItemData> allItems = playerData.getAllItems();
            updateItemBatch(allItems, statistics, player);

            statistics.incrementPlayersProcessed();
        }
    }
    
    /**
     * 更新掉落物品
     *
     * @param droppedItems 掉落物品列表
     * @param statistics 统计信息
     */
    private void updateDroppedItems(List<DroppedItemData> droppedItems, BatchUpdateStatistics statistics) throws Exception {
        updateDroppedItemBatch(droppedItems, statistics);
    }
    
    /**
     * 更新容器物品
     *
     * @param containerDataList 容器数据列表
     * @param statistics 统计信息
     */
    private void updateContainerItems(List<ContainerItemData> containerDataList, BatchUpdateStatistics statistics) throws Exception {
        for (ContainerItemData containerData : containerDataList) {
            List<MythicItemData> items = containerData.getItems();
            updateItemBatch(items, statistics, null); // 容器物品不需要Player信息

            statistics.incrementContainersProcessed();
        }
    }
    
    /**
     * 更新物品批次
     *
     * @param batch 物品批次
     * @param statistics 统计信息
     * @param player 玩家实例（可能为null，用于容器物品等情况）
     */
    private void updateItemBatch(List<MythicItemData> batch, BatchUpdateStatistics statistics, Player player) throws Exception {
        for (MythicItemData itemData : batch) {
            AbstractItemUpdater.UpdateResult result = itemUpdater.updateItem(itemData, player);

            if (result.isSuccess()) {
                if (result.isSkipped()) {
                    statistics.incrementSkipped();
                } else {
                    statistics.incrementUpdated();
                }
            } else {
                statistics.incrementFailed();
            }
        }

        currentBatch.incrementAndGet();
        sendProgressMessage();
    }
    
    /**
     * 更新掉落物品批次
     *
     * @param batch 掉落物品批次
     * @param statistics 统计信息
     */
    private void updateDroppedItemBatch(List<DroppedItemData> batch, BatchUpdateStatistics statistics) throws Exception {
        for (DroppedItemData droppedData : batch) {
            AbstractItemUpdater.UpdateResult result = itemUpdater.updateDroppedItem(droppedData);

            if (result.isSuccess()) {
                if (result.isSkipped()) {
                    statistics.incrementSkipped();
                } else {
                    statistics.incrementUpdated();
                }
            } else {
                statistics.incrementFailed();
            }
        }

        currentBatch.incrementAndGet();
        sendProgressMessage();
    }
    

    
    /**
     * 发送进度消息
     */
    private void sendProgressMessage() {
        if (plugin.getConfigManager().getConfigValue("logging.show-progress", true)) {
            plugin.getMessageManager().sendDebugMessage("progress.batch-processing",
                "正在处理批次进度",
                plugin.getMessageManager().createPlaceholders(
                    "current", String.valueOf(currentBatch.get()),
                    "total", String.valueOf(totalBatches.get())
                ));
        }
    }
    

    
    /**
     * 检查是否正在更新
     * 
     * @return 是否正在更新
     */
    public boolean isUpdateInProgress() {
        return updateInProgress;
    }
    
    /**
     * 获取当前批次
     * 
     * @return 当前批次
     */
    public int getCurrentBatch() {
        return currentBatch.get();
    }
    
    /**
     * 获取总批次数
     *
     * @return 总批次数
     */
    public int getTotalBatches() {
        return totalBatches.get();
    }

    /**
     * 触发全局更新
     * 在主线程中执行完整的物品更新流程
     */
    public void triggerGlobalUpdate() {
        try {
            BatchUpdateResult result = performFullUpdate();

            if (!result.isSuccess()) {
                warning("全局物品更新失败: " + result.getMessage());
            }

        } catch (Exception e) {
            handleError("执行全局更新时发生错误", e);
        }
    }

    /**
     * 更新单个玩家的物品
     *
     * @param playerData 玩家数据
     * @return 更新结果
     */
    public BatchUpdateResult updatePlayerItems(PlayerItemData playerData) {
        try {
            BatchUpdateStatistics statistics = new BatchUpdateStatistics();
            List<PlayerItemData> playerList = new ArrayList<>();
            playerList.add(playerData);
            updatePlayerItems(playerList, statistics);

            return new BatchUpdateResult(true, "玩家物品更新完成", statistics);

        } catch (Exception e) {
            String playerName = playerData.getPlayer() != null ?
                playerData.getPlayer().getName() : playerData.getPlayerName();
            handleError("更新玩家物品失败: " + playerName, e);
            return new BatchUpdateResult(false, "更新失败: " + e.getMessage(), null);
        }
    }
}
