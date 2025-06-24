package org.xiaoxingbomei.vo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 咖啡订单实体
 */
public class CoffeeOrder {
    
    private String orderId;
    private String userId;
    private String storeId;
    private String storeName;
    private List<OrderItem> items;
    private Double totalAmount;
    private String status; // PENDING, CONFIRMED, PREPARING, READY, COMPLETED, CANCELLED
    private LocalDateTime createTime;
    private LocalDateTime estimatedTime;
    private String paymentMethod;
    private String couponCode;
    private Double discountAmount;
    
    // 构造函数
    public CoffeeOrder() {}
    
    public CoffeeOrder(String orderId, String userId, String storeId) {
        this.orderId = orderId;
        this.userId = userId;
        this.storeId = storeId;
        this.createTime = LocalDateTime.now();
        this.status = "PENDING";
    }
    
    // Getter 和 Setter 方法
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getStoreId() { return storeId; }
    public void setStoreId(String storeId) { this.storeId = storeId; }
    
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
    
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public LocalDateTime getEstimatedTime() { return estimatedTime; }
    public void setEstimatedTime(LocalDateTime estimatedTime) { this.estimatedTime = estimatedTime; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
    
    public Double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(Double discountAmount) { this.discountAmount = discountAmount; }
    
    /**
     * 订单项内部类
     */
    public static class OrderItem {
        private String productId;
        private String productName;
        private Integer quantity;
        private Double price;
        private String size; // S, M, L
        private String temperature; // HOT, COLD
        private List<String> extras; // 额外选项：加糖、加奶等
        
        public OrderItem() {}
        
        public OrderItem(String productId, String productName, Integer quantity, Double price) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
        }
        
        // Getter 和 Setter 方法
        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        
        public String getSize() { return size; }
        public void setSize(String size) { this.size = size; }
        
        public String getTemperature() { return temperature; }
        public void setTemperature(String temperature) { this.temperature = temperature; }
        
        public List<String> getExtras() { return extras; }
        public void setExtras(List<String> extras) { this.extras = extras; }
    }
} 