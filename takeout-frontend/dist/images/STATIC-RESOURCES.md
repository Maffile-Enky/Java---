# 静态资源清单

本文档列出了美团风格前端重新设计所需的全部静态图片资源。
请将所有文件放置在 `takeout-frontend/public/images/` 目录下，按照以下目录结构组织。

## 格式说明

| 目录 | 格式 | 说明 |
| --- | --- | --- |
| `logo/` | SVG | 矢量图形，支持无损缩放 |
| `categories/` | SVG | 矢量图标，48×48，黄/橙色调 |
| `empty-states/` | SVG | 矢量插画，240×240，简约风格 |
| `icons/` | SVG | 矢量图标，24×24，单色描边 |
| `placeholders/` | **PNG** | 位图，需实际图片文件 |
| `banners/` | **PNG** | 位图，600×200，含文字的促销图 |

> 注意：`placeholders/` 和 `banners/` 目录下的文件必须是 PNG 格式，前端代码已硬编码引用 `.png` 后缀。其余目录均为 SVG 矢量格式。

## 目录结构

```
public/images/
├── logo/
│   ├── logo.svg
│   └── logo-icon.svg
├── categories/
│   ├── all.svg
│   ├── chinese.svg
│   ├── fastfood.svg
│   ├── dessert.svg
│   ├── drinks.svg
│   ├── bbq.svg
│   ├── hotpot.svg
│   ├── japanese.svg
│   ├── western.svg
│   ├── fruit.svg
│   ├── grocery.svg
│   └── breakfast.svg
├── placeholders/
│   ├── merchant-default.png
│   ├── dish-default.png
│   └── avatar-default.png
├── empty-states/
│   ├── empty-cart.svg
│   ├── empty-orders.svg
│   ├── empty-restaurants.svg
│   └── empty-search.svg
├── icons/
│   ├── delivery.svg
│   ├── clock.svg
│   ├── star.svg
│   ├── star-empty.svg
│   ├── location.svg
│   ├── phone.svg
│   ├── cart.svg
│   ├── order.svg
│   ├── user.svg
│   ├── arrow-right.svg
│   └── search.svg
└── banners/
    ├── banner-1.png
    ├── banner-2.png
    └── banner-3.png
```

## 资源详情

### Logo（`logo/`）

| 文件名 | 尺寸 | 格式 | 说明 |
|--------|------|------|------|
|logo.svg | 120×40 | SVG | 顶部导航 Logo — "美团外卖" 文字标识，黄色文字，透明背景 |
| logo-icon.svg | 32×32 | SVG | 小图标 Logo，用于移动端头部和网站图标（favicon） |

### 分类图标（`categories/`）

所有图标：48×48 SVG，扁平化风格，统一配色（黄/橙色调，透明背景）。

| 文件名 | 标签 | 说明 |
|--------|------|------|
| all.svg | 全部 | 网格/全部分类图标 |
| chinese.svg | 中餐 | 中式餐饮（饭碗或炒锅） |
| fastfood.svg | 快餐 | 快餐（汉堡或餐盒） |
| dessert.svg | 甜点 | 甜品（蛋糕或纸杯蛋糕） |
| drinks.svg | 饮品 | 饮料（带吸管的杯子） |
| bbq.svg | 烧烤 | 烧烤（烤串或烤架） |
| hotpot.svg | 火锅 | 火锅（带蒸汽的锅） |
| japanese.svg | 日料 | 日式料理（寿司或便当） |
| western.svg | 西餐 | 西餐（披萨或牛排） |
| fruit.svg | 水果 | 水果（苹果或果篮） |
| grocery.svg | 超市 | 超市（购物袋） |
| breakfast.svg | 早餐 | 早餐（面包或鸡蛋） |

### 占位图（`placeholders/`）

| 文件名 | 尺寸 | 格式 | 说明 |
|--------|------|------|------|
| merchant-default.png | 200×150 | PNG | 商家默认封面图 — 通用餐饮/餐厅插画 |
| dish-default.png | 200×200 | PNG | 菜品默认图片 — 通用餐盘/食物图标 |
| avatar-default.png | 80×80 | PNG | 用户默认头像 — 浅灰色圆形上的人物剪影 |

### 空状态插画（`empty-states/`）

所有插画：240×240 SVG，简约轻盈风格，柔和配色，带轻微趣味性。

| 文件名 | 使用页面 | 说明 |
|--------|----------|------|
| empty-cart.svg | CartView（购物车） | 空购物袋或购物车，带沮丧表情 |
| empty-orders.svg | OrdersView（订单列表） | 空收据或空白剪贴板 |
| empty-restaurants.svg | RestaurantsView（商家列表） | 空荡荡的餐厅/店铺门面 |
| empty-search.svg | 搜索结果页 | 放大镜配合"未找到结果" |

### 功能图标（`icons/`）

所有图标：24×4 SVG（arrow-right 为 16×16，search 为 20×20），单色（#666 或 #999），统一描边粗细。

| 文件名 | 说明 |
|--------|------|
| delivery.svg | 配送 — 外卖电动车/自行车 |
| clock.svg | 时钟 — 配送时间 |
| star.svg | 实心星星 — 评分（已点亮） |
| star-empty.svg | 空心星星 — 评分（未点亮） |
| location.svg | 定位 — 地图图钉 |
| phone.svg | 电话 — 话筒图标 |
| cart.svg | 购物车 |
| order.svg | 订单 — 剪贴板/小票 |
| user.svg | 用户 — 人物轮廓 |
| arrow-right.svg | 箭头 — 右向箭头 |
| search.svg | 搜索 — 放大镜 |

### 轮播图（`banners/`）

所有轮播图：600×200 PNG，促销风格，可带文字叠加。

| 文件名 | 说明 |
|--------|------|
| banner-1.png | 新用户立减 — 新用户专享优惠活动 |
| banner-2.png | 满减优惠 — 满额立减促销活动 |
| banner-3.png | 品质精选 — 优质餐厅推荐 |

## 注意事项

- SVG 图标应使用 `currentColor` 或中性灰色（#666/#999），以便通过 CSS 控制颜色
- 分类图标应保持统一的视觉风格（相同的描边粗细、圆角等）
- 占位图应美观但明确表示"暂无图片"
- 轮播图可包含中文文字叠加 —— 使用简洁的无衬线字体
