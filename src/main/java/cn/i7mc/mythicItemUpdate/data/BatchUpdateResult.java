package cn.i7mc.mythicItemUpdate.data;

/**
 * 批量更新结果
 * 
 * @author i7mc
 * @version 1.0
 */
public class BatchUpdateResult {
    
    private final boolean success;
    private final String message;
    private final BatchUpdateStatistics statistics;
    private final long timestamp;
    
    public BatchUpdateResult(boolean success, String message, BatchUpdateStatistics statistics) {
        this.success = success;
        this.message = message;
        this.statistics = statistics;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * 获取是否成功
     * 
     * @return 是否成功
     */
    public boolean isSuccess() {
        return success;
    }
    
    /**
     * 获取消息
     * 
     * @return 消息
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * 获取统计信息
     * 
     * @return 统计信息
     */
    public BatchUpdateStatistics getStatistics() {
        return statistics;
    }
    
    /**
     * 获取时间戳
     * 
     * @return 时间戳
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * 检查是否有统计信息
     * 
     * @return 是否有统计信息
     */
    public boolean hasStatistics() {
        return statistics != null;
    }
    
    @Override
    public String toString() {
        return String.format("BatchUpdateResult{success=%s, message='%s', hasStatistics=%s, timestamp=%d}",
            success, message, hasStatistics(), timestamp);
    }
}
