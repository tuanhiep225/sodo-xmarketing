/**
 *
 */
package com.sodo.xmarketing.model.config;

/**
 * @author ANH MINH - PC
 */
public class WalletDeter {

  // Hoàn tiền khách hàng sau khi hoàn thành đơn
  private String afterComplete;

  // Tất toán đơn hàng
  private String deliveryApproved;

  // Thu tiền ship trên phiếu giao hàng
  private String deliveryShip;

  // Đặt cọc đơn
  private String orderDeposit;

  // Đặt cọc đơn hàng thêm sản phẩm
  private String orderDepositAdd;

  // Hoàn tiền đơn hàng
  private String orderRefund;

  // Phí lưu kho
  private String orderStorage;

  // Khác
  private String other;

  // Hoàn tiền hủy/ giảm sản phẩm
  private String productCancelRefund;

  // Hoàn tiền khiếu nại
  private String ticketRefund;

  // Nạp ví điện tử khách hàng
  private String walletAddCustomer;

  // Nạp ví
  private String walletDeposit;

  // Trừ ví điện tủ
  private String walletSubtract;

  // Trừ ví
  private String walletWithdrawal;

  // Nạp ví điện tử theo mã phiếu giao
  private String walletAddDelivery;

  public String getAfterComplete() {
    return afterComplete;
  }

  public void setAfterComplete(String afterComplete) {
    this.afterComplete = afterComplete;
  }

  public String getDeliveryApproved() {
    return deliveryApproved;
  }

  public void setDeliveryApproved(String deliveryApproved) {
    this.deliveryApproved = deliveryApproved;
  }

  public String getDeliveryShip() {
    return deliveryShip;
  }

  public void setDeliveryShip(String deliveryShip) {
    this.deliveryShip = deliveryShip;
  }

  public String getOrderDeposit() {
    return orderDeposit;
  }

  public void setOrderDeposit(String orderDeposit) {
    this.orderDeposit = orderDeposit;
  }

  public String getOrderDepositAdd() {
    return orderDepositAdd;
  }

  public void setOrderDepositAdd(String orderDepositAdd) {
    this.orderDepositAdd = orderDepositAdd;
  }

  public String getOrderRefund() {
    return orderRefund;
  }

  public void setOrderRefund(String orderRefund) {
    this.orderRefund = orderRefund;
  }

  public String getOrderStorage() {
    return orderStorage;
  }

  public void setOrderStorage(String orderStorage) {
    this.orderStorage = orderStorage;
  }

  public String getOther() {
    return other;
  }

  public void setOther(String other) {
    this.other = other;
  }

  public String getProductCancelRefund() {
    return productCancelRefund;
  }

  public void setProductCancelRefund(String productCancelRefund) {
    this.productCancelRefund = productCancelRefund;
  }

  public String getTicketRefund() {
    return ticketRefund;
  }

  public void setTicketRefund(String ticketRefund) {
    this.ticketRefund = ticketRefund;
  }

  public String getWalletAddCustomer() {
    return walletAddCustomer;
  }

  public void setWalletAddCustomer(String walletAddCustomer) {
    this.walletAddCustomer = walletAddCustomer;
  }

  public String getWalletDeposit() {
    return walletDeposit;
  }

  public void setWalletDeposit(String walletDeposit) {
    this.walletDeposit = walletDeposit;
  }

  public String getWalletSubtract() {
    return walletSubtract;
  }

  public void setWalletSubtract(String walletSubtract) {
    this.walletSubtract = walletSubtract;
  }

  public String getWalletWithdrawal() {
    return walletWithdrawal;
  }

  public void setWalletWithdrawal(String walletWithdrawal) {
    this.walletWithdrawal = walletWithdrawal;
  }

  public String getWalletAddDelivery() {
    return walletAddDelivery;
  }

  public void setWalletAddDelivery(String walletAddDelivery) {
    this.walletAddDelivery = walletAddDelivery;
  }


}
