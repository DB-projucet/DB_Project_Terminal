package vo;

import java.sql.Timestamp;
import java.util.Date;

public class OrderVO {
    public int orderId;
    public String memId;
    public String memName;
    public String memAddr;
    public String pType;
    public String pName;
    public String pColor;
    public int pSize;
    public int pPrice;
    public Timestamp ordStartTime;  // Changed to java.sql.Timestamp
    public Date ordEndTime;    // Changed to java.sql.Timestamp
    public int productId;
    public int salesVolume;


    // Updated constructor with Timestamp for start and end times
    public OrderVO(int orderId, String memId, String memName, String memAddr, String productType,
                   String productName, String productColor, int productSize, int productPrice,
                   Timestamp orderStartTime, Date orderEndTime, int productId, int salesVolume) {
        this.orderId = orderId;
        this.memId = memId;
        this.memName = memName;
        this.memAddr = memAddr;
        this.pType = productType;
        this.pName = productName;
        this.pColor = productColor;
        this.pSize = productSize;
        this.pPrice = productPrice;
        this.ordStartTime = orderStartTime;
        this.ordEndTime = orderEndTime;
        this.productId = productId;
        this.salesVolume = salesVolume;
    }

    public OrderVO(String productName, int productPrice) {
        this.pName = productName;
        this.pPrice = productPrice;
    }

    public OrderVO(String memId, String memName, String memAddr, String pType, String pName, String pColor, int pSize, int pPrice, Date sqlOrdDStart, Date ordDEnd) {
        this.memId = memId;
        this.memName = memName;
        this.memAddr = memAddr;
        this.pType = pType;
        this.pName = pName;
        this.pColor = pColor;
        this.pSize = pSize;
        this.pPrice = pPrice;
        this.ordStartTime = (Timestamp) sqlOrdDStart;
        this.ordEndTime = ordDEnd;
    }

    public OrderVO(int orderId, String memId, String memName, String memAddr, String pType, String pName, String pColor, int pSize, int pPrice, Timestamp ordDStart, java.sql.Date ordDEnd) {
        this.orderId = orderId;
        this.memId = memId;
        this.memName = memName;
        this.memAddr = memAddr;
        this.pType = pType;
        this.pName = pName;
        this.pColor = pColor;
        this.pSize = pSize;
        this.pPrice = pPrice;
        this.ordStartTime = (Timestamp) ordDStart;
        this.ordEndTime = ordDEnd;
    }

    @Override
    public String toString() {
        return String.format(
                "주문ID: %-10s ID: %-10s 이름: %-10s 주소: %-30s 신발 종류: %-15s 신발 모델: %-5s 신발 색상: %-6s 신발 사이즈: %-5d 신발 가격: %-10d 주문일: %-20s 배송일: %-20s",
                orderId, memId, memName, memAddr, pType, pName, pColor, pSize, pPrice, ordStartTime, ordEndTime
        );
    }

    public int getOrderId() {
        return orderId;
    }

    public String getMemId() {
        return memId;
    }

    public String getMemName() {
        return memName;
    }

    public String getMemAddr() {
        return memAddr;
    }

    public String getpType() {
        return pType;
    }

    public String getpName() {
        return pName;
    }

    public String getpColor() {
        return pColor;
    }

    public int getpSize() {
        return pSize;
    }

    public int getpPrice() {
        return pPrice;
    }

    public Timestamp getOrdStartTime() {
        return ordStartTime;
    }

    public Date getOrdEndTime() {
        return ordEndTime;
    }

    public int getProductId() {
        return productId;
    }

    public int getSalesVolume() {
        return salesVolume;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setMemId(String memId) {
        this.memId = memId;
    }

    public void setMemName(String memName) {
        this.memName = memName;
    }

    public void setMemAddr(String memAddr) {
        this.memAddr = memAddr;
    }

    public void setpType(String pType) {
        this.pType = pType;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public void setpColor(String pColor) {
        this.pColor = pColor;
    }

    public void setpSize(int pSize) {
        this.pSize = pSize;
    }

    public void setpPrice(int pPrice) {
        this.pPrice = pPrice;
    }

    public void setOrdStartTime(Timestamp ordStartTime) {
        this.ordStartTime = ordStartTime;
    }

    public void setOrdEndTime(Date ordEndTime) {
        this.ordEndTime = ordEndTime;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setSalesVolume(int salesVolume) {
        this.salesVolume = salesVolume;
    }
}
