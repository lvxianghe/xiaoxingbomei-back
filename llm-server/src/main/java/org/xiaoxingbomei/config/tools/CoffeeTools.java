package org.xiaoxingbomei.config.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.xiaoxingbomei.vo.CoffeeOrder;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 瑞幸咖啡智能客服工具集
 * 通过Tool Calling实现智能下单、查询等业务操作
 */
@Slf4j
@Component
public class CoffeeTools
{

    // 模拟菜单数据
    private final Map<String, MenuItem> menuItems = new HashMap<>();
    // 模拟订单数据
    private final Map<String, CoffeeOrder> orders = new HashMap<>();
    // 模拟门店数据
    private final Map<String, StoreInfo> stores = new HashMap<>();
    // 模拟优惠券数据
    private final List<Coupon> coupons = new ArrayList<>();

    public CoffeeTools() {
        initializeData();
        log.info("🎯 CoffeeTools 实例已创建并初始化完成");
    }

    /**
     * 获取咖啡菜单
     * AI可以调用此工具来展示可用的咖啡产品
     */
    @Tool(name = "getCoffeeMenu", description = "获取瑞幸咖啡的完整菜单，包含所有咖啡饮品、价格和规格信息。当用户询问有什么咖啡、价格多少时调用此工具。")
    public String getCoffeeMenu() {
        log.info("🔧 [Tool Called] getCoffeeMenu() - AI正在获取咖啡菜单");
        StringBuilder menu = new StringBuilder("🌟 瑞幸咖啡菜单 🌟\n\n");
        
        menuItems.values().forEach(item -> {
            menu.append(String.format("☕ %s\n", item.name));
            menu.append(String.format("   价格: 中杯¥%.1f | 大杯¥%.1f\n", item.price, item.price + 3.0));
            menu.append(String.format("   描述: %s\n", item.description));
            menu.append(String.format("   热饮: %s | 冰饮: %s\n\n", 
                       item.availableHot ? "✅" : "❌", 
                       item.availableCold ? "✅" : "❌"));
        });
        
        String result = menu.toString();
        log.info("✅ [Tool Success] getCoffeeMenu() - 菜单获取成功，返回{}个商品", menuItems.size());
        return result;
    }

    /**
     * 创建咖啡订单
     * AI可以调用此工具来帮助用户下单
     */
    @Tool(name = "createCoffeeOrder", description = "为用户创建咖啡订单。需要提供咖啡名称、数量、规格(中杯/大杯)、温度(热/冰)。当用户明确表达要购买咖啡时调用此工具。默认门店为三里屯店。")
    public String createCoffeeOrder(String productName, int quantity, String size, String temperature) {
        log.info("🔧 [Tool Called] createCoffeeOrder() - AI正在创建咖啡订单: 产品={}, 数量={}, 规格={}, 温度={}", 
                 productName, quantity, size, temperature);
        try {
            // 安全边界检查
            if (productName == null || productName.trim().isEmpty()) {
                return "❌ 请指定要购买的咖啡产品名称。";
            }
            if (quantity <= 0 || quantity > 10) {
                return "❌ 订单数量必须在1-10之间，如需大量订购请联系门店。";
            }
            
            // 验证产品是否存在
            MenuItem item = findMenuItemByName(productName.trim());
            if (item == null) {
                return "❌ 抱歉，菜单中没有找到 \"" + productName + "\"，请重新选择咖啡。我为您展示当前可用的产品菜单。";
            }

            // 验证规格和温度
            if (size == null || !Arrays.asList("中杯", "大杯", "M", "L").contains(size.trim())) {
                return "❌ 规格必须是：中杯 或 大杯，请重新选择。";
            }
            if (temperature == null || !Arrays.asList("热", "冰", "HOT", "COLD").contains(temperature.trim())) {
                return "❌ 温度必须是：热 或 冰，请重新选择。";
            }

            // 验证温度与产品匹配
            boolean isHot = temperature.trim().equals("热") || temperature.trim().equals("HOT");
            if (isHot && !item.availableHot) {
                return "❌ " + item.name + " 不支持热饮，请选择冰饮或其他产品。";
            }
            if (!isHot && !item.availableCold) {
                return "❌ " + item.name + " 不支持冰饮，请选择热饮或其他产品。";
            }

            // 创建订单
            String orderId = "LK" + System.currentTimeMillis();
            String storeId = "store001"; // 默认门店
            String userId = "default_user"; // 默认用户
            CoffeeOrder order = new CoffeeOrder(orderId, userId, storeId);
            
            // 计算价格
            double price = item.price;
            if (size.equals("大杯") || size.equals("L")) {
                price += 3.0; // 大杯加3元
            }

            // 创建订单项
            CoffeeOrder.OrderItem orderItem = new CoffeeOrder.OrderItem(
                item.id, item.name, quantity, price
            );
            orderItem.setSize(size);
            orderItem.setTemperature(temperature);
            
            order.setItems(Arrays.asList(orderItem));
            order.setTotalAmount(price * quantity);
            order.setStoreName(stores.get(storeId).name);
            order.setEstimatedTime(LocalDateTime.now().plusMinutes(15));
            order.setStatus("CONFIRMED");

            // 保存订单
            orders.put(orderId, order);

            String result = String.format(
                "✅ 订单创建成功！\n\n" +
                "📋 订单详情：\n" +
                "订单号：%s\n" +
                "商品：%s x%d (%s, %s)\n" +
                "门店：%s\n" +
                "总金额：¥%.1f\n" +
                "预计完成时间：%s\n\n" +
                "💡 您可以随时通过订单号查询订单状态哦！",
                orderId, item.name, quantity, size, temperature,
                order.getStoreName(), order.getTotalAmount(),
                order.getEstimatedTime().toString().replace("T", " ")
            );
            
            log.info("✅ [Tool Success] createCoffeeOrder() - 订单创建成功: 订单号={}, 商品={}, 总金额=¥{}", 
                     orderId, item.name, order.getTotalAmount());
            return result;

        } catch (Exception e) {
            log.error("❌ [Tool Error] createCoffeeOrder() - 创建订单失败: {}", e.getMessage(), e);
            return "❌ 创建订单时发生错误：" + e.getMessage();
        }
    }

    /**
     * 查询订单状态
     */
    @Tool(name = "getOrderStatus", description = "根据订单号查询咖啡订单的状态和详情。当用户提供订单号并询问订单情况时调用此工具。")
    public String getOrderStatus(String orderId) {
        log.info("🔧 [Tool Called] getOrderStatus() - AI正在查询订单状态: 订单号={}", orderId);
        // 订单号格式验证
        if (orderId == null || orderId.trim().isEmpty()) {
            return "❌ 请提供订单号。订单号格式：LK开头+数字组合。";
        }
        
        String cleanOrderId = orderId.trim();
        if (!cleanOrderId.startsWith("LK") || cleanOrderId.length() < 5) {
            return "❌ 订单号格式错误。正确格式：LK开头+数字组合，例如：LK1234567890";
        }
        
        CoffeeOrder order = orders.get(cleanOrderId);
        if (order == null) {
            log.warn("⚠️ [Tool Warning] getOrderStatus() - 订单未找到: {}", cleanOrderId);
            return "❌ 未找到订单号为 " + cleanOrderId + " 的订单，请检查订单号是否正确。";
        }

        String statusText = getStatusText(order.getStatus());
        
        String result = String.format(
            "📋 订单状态查询\n\n" +
            "订单号：%s\n" +
            "状态：%s\n" +
            "商品：%s\n" +
            "门店：%s\n" +
            "总金额：¥%.1f\n" +
            "下单时间：%s\n" +
            "预计完成：%s",
            order.getOrderId(), statusText,
            formatOrderItems(order.getItems()),
            order.getStoreName(), order.getTotalAmount(),
            order.getCreateTime().toString().replace("T", " "),
            order.getEstimatedTime().toString().replace("T", " ")
        );
        
        log.info("✅ [Tool Success] getOrderStatus() - 订单查询成功: 订单号={}, 状态={}", cleanOrderId, order.getStatus());
        return result;
    }

    /**
     * 取消订单
     */
    @Tool(name = "cancelOrder", description = "取消指定的咖啡订单。当用户明确表示要取消某个订单时调用此工具。")
    public String cancelOrder(String orderId)
    {
        log.info("🔧 [Tool Called] cancelOrder() - AI正在取消订单: 订单号={}", orderId);
        // 订单号验证
        if (orderId == null || orderId.trim().isEmpty()) {
            return "❌ 请提供要取消的订单号。";
        }
        
        String cleanOrderId = orderId.trim();
        if (!cleanOrderId.startsWith("LK")) {
            return "❌ 订单号格式错误。正确格式：LK开头+数字组合。";
        }
        
        CoffeeOrder order = orders.get(cleanOrderId);
        if (order == null) {
            log.warn("⚠️ [Tool Warning] cancelOrder() - 订单未找到: {}", cleanOrderId);
            return "❌ 未找到订单号为 " + cleanOrderId + " 的订单。";
        }

        if ("COMPLETED".equals(order.getStatus())) {
            log.warn("⚠️ [Tool Warning] cancelOrder() - 订单已完成无法取消: {}", cleanOrderId);
            return "❌ 订单已完成，无法取消。";
        }

        if ("CANCELLED".equals(order.getStatus())) {
            log.info("ℹ️ [Tool Info] cancelOrder() - 订单已是取消状态: {}", cleanOrderId);
            return "ℹ️ 订单已经是取消状态。";
        }

        order.setStatus("CANCELLED");
        log.info("✅ [Tool Success] cancelOrder() - 订单取消成功: 订单号={}", cleanOrderId);
        return "✅ 订单 " + cleanOrderId + " 已成功取消。如有需要，欢迎重新下单！";
    }

    /**
     * 获取门店信息
     */
    @Tool(name = "getStoreInfo", description = "获取瑞幸咖啡门店的位置、营业时间等信息。当用户询问门店位置、营业时间或者需要选择门店时调用此工具。")
    public String getStoreInfo(String location)
    {
        log.info("🔧 [Tool Called] getStoreInfo() - AI正在获取门店信息: 位置筛选={}", location);
        StringBuilder storeInfo = new StringBuilder("🏪 瑞幸咖啡门店信息\n\n");
        
        long matchedStoreCount = stores.values().stream()
              .filter(store -> location == null || store.address.contains(location) || store.name.contains(location))
              .peek(store -> {
                  storeInfo.append(String.format("📍 %s\n", store.name));
                  storeInfo.append(String.format("地址：%s\n", store.address));
                  storeInfo.append(String.format("营业时间：%s\n", store.businessHours));
                  storeInfo.append(String.format("电话：%s\n\n", store.phone));
              })
              .count();

        String result = storeInfo.toString();
        log.info("✅ [Tool Success] getStoreInfo() - 门店信息获取成功: 筛选条件={}, 匹配门店数={}", 
                 location != null ? location : "全部", matchedStoreCount);
        return result;
    }

    /**
     * 查询可用优惠券
     */
    @Tool(name = "getAvailableCoupons", description = "查询可用的优惠券信息。当用户询问优惠券、折扣信息时调用此工具。")
    public String getAvailableCoupons() {
        log.info("🔧 [Tool Called] getAvailableCoupons() - AI正在查询可用优惠券");
        StringBuilder couponInfo = new StringBuilder("🎫 您的可用优惠券\n\n");
        
        long validCouponCount = coupons.stream()
               .filter(coupon -> coupon.isValid())
               .peek(coupon -> {
                   couponInfo.append(String.format("🎟️ %s\n", coupon.name));
                   couponInfo.append(String.format("折扣：%s\n", coupon.discount));
                   couponInfo.append(String.format("使用条件：%s\n", coupon.condition));
                   couponInfo.append(String.format("有效期至：%s\n\n", coupon.expireDate));
               })
               .count();

        if (validCouponCount == 0) {
            couponInfo.append("暂无可用优惠券，关注我们的活动获取更多优惠哦！");
        }

        String result = couponInfo.toString();
        log.info("✅ [Tool Success] getAvailableCoupons() - 优惠券查询成功: 可用优惠券数={}", validCouponCount);
        return result;
    }

    // 辅助方法
    private MenuItem findMenuItemByName(String name) {
        return menuItems.values().stream()
                .filter(item -> item.name.contains(name) || name.contains(item.name))
                .findFirst()
                .orElse(null);
    }

    private String getStatusText(String status) {
        switch (status) {
            case "PENDING": return "⏳ 等待确认";
            case "CONFIRMED": return "✅ 已确认";
            case "PREPARING": return "👨‍🍳 制作中";
            case "READY": return "🔔 制作完成，请取餐";
            case "COMPLETED": return "✅ 已完成";
            case "CANCELLED": return "❌ 已取消";
            default: return "❓ 未知状态";
        }
    }

    private String formatOrderItems(List<CoffeeOrder.OrderItem> items) {
        return items.stream()
                .map(item -> String.format("%s x%d (%s, %s)", 
                    item.getProductName(), item.getQuantity(), 
                    item.getSize(), item.getTemperature()))
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
    }

    // 初始化模拟数据
    private void initializeData() {
        // 初始化菜单
        menuItems.put("americano", new MenuItem("americano", "美式咖啡", 18.0, "经典浓郁美式咖啡", true, true));
        menuItems.put("latte", new MenuItem("latte", "拿铁咖啡", 22.0, "香浓牛奶与咖啡的完美融合", true, true));
        menuItems.put("cappuccino", new MenuItem("cappuccino", "卡布奇诺", 24.0, "丰富奶泡的经典意式咖啡", true, false));
        menuItems.put("mocha", new MenuItem("mocha", "摩卡咖啡", 26.0, "巧克力与咖啡的甜蜜组合", true, true));
        menuItems.put("caramel", new MenuItem("caramel", "焦糖玛奇朵", 28.0, "焦糖香甜的特色咖啡", true, true));

        // 初始化门店
        stores.put("store001", new StoreInfo("store001", "瑞幸咖啡(三里屯店)", "北京市朝阳区三里屯太古里", "07:00-22:00", "010-12345678"));
        stores.put("store002", new StoreInfo("store002", "瑞幸咖啡(国贸店)", "北京市朝阳区国贸中心", "07:00-21:00", "010-87654321"));
        stores.put("store003", new StoreInfo("store003", "瑞幸咖啡(西单店)", "北京市西城区西单大悦城", "08:00-22:00", "010-11223344"));

        // 初始化优惠券
        coupons.add(new Coupon("新人专享", "立减5元", "满20元可用", "2024-12-31"));
        coupons.add(new Coupon("周末特惠", "第二杯半价", "购买任意两杯", "2024-12-31"));
    }

    // 内部数据类
    private static class MenuItem {
        String id, name, description;
        double price;
        boolean availableHot, availableCold;

        MenuItem(String id, String name, double price, String description, boolean availableHot, boolean availableCold) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.description = description;
            this.availableHot = availableHot;
            this.availableCold = availableCold;
        }
    }

    private static class StoreInfo {
        String id, name, address, businessHours, phone;

        StoreInfo(String id, String name, String address, String businessHours, String phone) {
            this.id = id;
            this.name = name;
            this.address = address;
            this.businessHours = businessHours;
            this.phone = phone;
        }
    }

    private static class Coupon {
        String name, discount, condition, expireDate;

        Coupon(String name, String discount, String condition, String expireDate) {
            this.name = name;
            this.discount = discount;
            this.condition = condition;
            this.expireDate = expireDate;
        }

        boolean isValid() {
            return true; // 简化实现，实际应该检查过期时间
        }
    }
} 