# Static Resources Specification

This document lists all static image assets required by the Meituan-style frontend redesign.
Place all files in `takeout-frontend/public/images/` following the directory structure below.

## Directory Structure

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
│   ├── avatar-default.png
│   └── loading-food.gif
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

## Asset Details

### Logo (`logo/`)

| Filename | Size | Format | Description |
|----------|------|--------|-------------|
| logo.svg | 120×40 | SVG | Header logo — "美团外卖" wordmark, yellow text on transparent background |
| logo-icon.svg | 32×32 | SVG | Small icon-only logo for mobile header and favicon |

### Category Icons (`categories/`)

All icons: 48×48 SVG, flat style, consistent color palette (yellow/orange tones on transparent).

| Filename | Label | Description |
|----------|-------|-------------|
| all.svg | 全部 | Grid/all-items icon |
| chinese.svg | 中餐 | Chinese food (rice bowl or wok) |
| fastfood.svg | 快餐 | Fast food (burger or box) |
| dessert.svg | 甜点 | Dessert (cake or cupcake) |
| drinks.svg | 饮品 | Drinks (cup with straw) |
| bbq.svg | 烧烤 | BBQ (skewer or grill) |
| hotpot.svg | 火锅 | Hot pot (pot with steam) |
| japanese.svg | 日料 | Japanese food (sushi or bento) |
| western.svg | 西餐 | Western food (pizza or steak) |
| fruit.svg | 水果 | Fruits (apple or basket) |
| grocery.svg | 超市 | Grocery (shopping bag) |
| breakfast.svg | 早餐 | Breakfast (bread or egg) |

### Placeholders (`placeholders/`)

| Filename | Size | Format | Description |
|----------|------|--------|-------------|
| merchant-default.png | 200×150 | PNG | Default merchant cover image — neutral food/restaurant illustration |
| dish-default.png | 200×200 | PNG | Default dish image — generic plate/food icon |
| avatar-default.png | 80×80 | PNG | Default user avatar — person silhouette on light gray circle |
| loading-food.gif | 200×200 | GIF | Loading animation — food-themed (e.g., bouncing dots, spinning fork) |

### Empty States (`empty-states/`)

All illustrations: 240×240 SVG, light/minimal style, muted colors, with subtle humor.

| Filename | Used In | Description |
|----------|---------|-------------|
| empty-cart.svg | CartView | Empty shopping bag or cart with sad face |
| empty-orders.svg | OrdersView | Empty receipt or clipboard |
| empty-restaurants.svg | RestaurantsView | Empty restaurant/store front |
| empty-search.svg | Search results | Magnifying glass with "nothing found" |

### Icons (`icons/`)

All icons: 24×24 SVG (except arrow-right 16×16, search 20×20), single-color (#666 or #999), consistent stroke width.

| Filename | Description |
|----------|-------------|
| delivery.svg | Delivery scooter/bicycle |
| clock.svg | Clock face (delivery time) |
| star.svg | Filled star (rating) |
| star-empty.svg | Outlined star (empty rating) |
| location.svg | Map pin |
| phone.svg | Phone handset |
| cart.svg | Shopping cart |
| order.svg | Clipboard/receipt |
| user.svg | Person silhouette |
| arrow-right.svg | Chevron right |
| search.svg | Magnifying glass |

### Banners (`banners/`)

All banners: 600×200 PNG, promotional style with text overlays.

| Filename | Description |
|----------|-------------|
| banner-1.png | 新用户立减 — new user discount promotion |
| banner-2.png | 满减优惠 — spend & save promotion |
| banner-3.png | 品质精选 — premium restaurant selection |

## Notes

- SVG icons should use `currentColor` or a neutral gray (#666/#999) so they can be styled via CSS
- Category icons should have a consistent visual style (same stroke width, corner radius, etc.)
- Placeholder images should be tasteful but clearly indicate "no image available"
- Banners can contain Chinese text overlays — use a clean sans-serif font
