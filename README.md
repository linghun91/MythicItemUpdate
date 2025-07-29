# MythicItemUpdate

> **MythicMobs 自定义物品动态更新插件**

一个专为 Minecraft 服务器设计的插件，能够自动检测并更新玩家背包、末影箱和容器中的 MythicMobs 自定义物品，确保物品始终保持最新状态。

## 🌟 核心功能

### 🔄 自动物品更新
- **智能检测**：自动检测服务器中所有的 MythicMobs 自定义物品
- **实时更新**：当 MythicMobs 配置文件修改并重载后，自动更新所有相关物品
- **全面覆盖**：支持更新玩家背包、末影箱、掉落物品和容器中的物品

### 📦 支持的物品位置
- ✅ **玩家背包**：自动更新玩家背包中的 MythicMobs 物品
- ✅ **末影箱**：自动更新玩家末影箱中的 MythicMobs 物品  
- ✅ **掉落物品**：自动更新掉落在地面上的 MythicMobs 物品
- ✅ **容器物品**：自动更新箱子、漏斗等容器中的 MythicMobs 物品

### ⚡ 事件触发更新
- **MythicMobs 重载时**：执行 `/mm reload` 后自动触发全局物品更新
- **玩家加入服务器**：玩家进入游戏时自动检查并更新背包物品
- **打开背包时**：玩家打开背包界面时检查物品更新
- **物品交互时**：点击或拾取物品时实时检查更新

## 🚀 快速开始

### 系统要求
- **Minecraft 版本**：1.20.1+
- **服务端**：Paper/Spigot
- **前置插件**：MythicMobs

### 安装步骤

1. **下载插件**
   - 将 `MythicItemUpdate.jar` 放入服务器的 `plugins` 文件夹

2. **重启服务器**
   - 重启服务器以加载插件

3. **验证安装**
   - 查看控制台是否显示插件成功启用信息
   - 确认 MythicMobs 插件已正确加载

### 基本使用

插件安装后即可自动工作，无需额外配置：

1. **修改 MythicMobs 物品配置**
   ```yaml
   # 例如修改物品的 Lore、属性等
   MyCustomSword:
     Id: DIAMOND_SWORD
     Display: '&6神圣之剑'
     Lore:
     - '&7一把充满神圣力量的剑'
     - '&c攻击力: +10'  # 新增的属性
   ```

2. **重载 MythicMobs**
   ```
   /mm reload
   ```

3. **自动更新完成**
   - 插件会自动检测并更新所有相关物品
   - 玩家背包中的旧版本物品会立即更新为新版本

## ⚙️ 配置说明

### 主配置文件 (config.yml)

```yaml
# 基础设置
settings:
  debug: false          # 调试模式
  enabled: true         # 启用插件
  
  item-update:
    auto-update: true           # 自动更新
    update-inventory: true      # 更新背包
    update-enderchest: true     # 更新末影箱
    update-dropped-items: true  # 更新掉落物品
    update-containers: true     # 更新容器

# 事件处理设置
events:
  update-on-join: true              # 玩家加入时更新
  join-update-delay: 40             # 加入延迟(tick)
  update-on-inventory-open: true    # 打开背包时更新
  update-on-click: true             # 点击物品时更新
  update-on-pickup: true            # 拾取物品时更新
  notify-admins: true               # 通知管理员

# 性能设置
performance:
  max-concurrent-players: 10  # 最大并发处理玩家数
  timeout: 30                 # 超时时间(秒)

# 日志设置
logging:
  log-statistics: true    # 记录统计信息
  log-details: false      # 记录详细过程
  show-progress: true     # 显示进度
```

### 配置项详解

#### 物品更新设置
- `auto-update`: 是否启用自动更新功能
- `update-inventory`: 是否更新玩家背包中的物品
- `update-enderchest`: 是否更新玩家末影箱中的物品
- `update-dropped-items`: 是否更新掉落在地面的物品
- `update-containers`: 是否更新容器（箱子等）中的物品

#### 事件触发设置
- `update-on-join`: 玩家加入服务器时是否检查更新背包物品
- `join-update-delay`: 玩家加入后延迟多久开始更新（以 tick 为单位，20tick = 1秒）
- `update-on-inventory-open`: 玩家打开背包时是否检查物品更新
- `update-on-click`: 玩家点击物品时是否检查更新
- `update-on-pickup`: 玩家拾取物品时是否检查更新
- `notify-admins`: 是否向管理员发送重载通知消息

#### 性能优化设置
- `max-concurrent-players`: 同时处理的最大玩家数量，避免服务器过载
- `timeout`: 单个物品更新的最大超时时间（秒）

#### 日志记录设置
- `log-statistics`: 是否在控制台记录更新统计信息
- `log-details`: 是否记录详细的更新过程（调试用）
- `show-progress`: 是否在控制台显示更新进度

## 🎯 使用场景

### 场景一：物品属性调整
当你需要调整 MythicMobs 物品的属性时：
1. 修改 MythicMobs 配置文件中的物品属性
2. 执行 `/mm reload`
3. 所有玩家背包中的该物品会自动更新

### 场景二：物品外观更新
当你需要更新物品的显示名称或 Lore 时：
1. 在 MythicMobs 配置中修改 `Display` 和 `Lore`
2. 重载 MythicMobs
3. 玩家手中和背包中的物品外观立即更新

### 场景三：新玩家加入
当新玩家加入服务器时：
1. 插件自动检查玩家背包中的 MythicMobs 物品
2. 如果发现过时的物品版本，自动更新为最新版本
3. 确保新玩家获得的物品都是最新状态

## 🔧 故障排除

### 常见问题

**Q: 物品没有自动更新？**
A: 请检查：
- MythicMobs 插件是否正常运行
- 配置文件中 `auto-update` 是否为 `true`
- 对应的更新选项（如 `update-inventory`）是否启用

**Q: 服务器出现卡顿？**
A: 请调整性能设置：
- 降低 `max-concurrent-players` 的值
- 关闭不必要的事件触发更新
- 启用 `debug` 模式查看详细信息

**Q: 容器中的物品没有更新？**
A: 请确认：
- `update-containers` 设置为 `true`
- 容器中确实存在 MythicMobs 物品
- 物品配置确实发生了变化

### 调试模式
启用调试模式可以获得更多信息：
```yaml
settings:
  debug: true
```

启用后，控制台会显示详细的处理过程，帮助定位问题。

## 📋 权限说明

插件目前不需要特殊权限配置，所有功能对玩家自动生效。

管理员通知功能需要以下权限之一：
- `mythicitemupdate.admin`
- OP 权限

## 🔄 更新日志

### v1.0
- ✅ 实现自动物品检测和更新功能
- ✅ 支持背包、末影箱、掉落物品和容器更新
- ✅ 添加多种事件触发机制
- ✅ 完善的配置系统和性能优化
- ✅ 静默运行模式，减少控制台输出
