package com.sodo.xmarketing.constants;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import com.sodo.xmarketing.model.customer.Customer;

/**
 * Created by tahi1990 on 23/06/2017.
 */
public class Constants {

  public static final String OTHER = "OTHER";
  public static final String VIETCOMBANK = "Vietcombank";
  public static final String TPBANK = "TPBank";

  public static final List<String> DEFAULT_TRANSSHIPMENT_CODE = Arrays.asList(new String[] {"QA1"});
  public static final List<String> DEFAULT_WAREHOUSE_ZONE_CODE =
      Arrays.asList(new String[] {"WZHN", "WZHCM", "WZSTC", "WZU", "WZSTCTW"});
  public static final List<String> DEFAULT_TRANSPORT_PARTNER_CODE =
      Arrays.asList(new String[] {"TP1"});



  public interface BagKind {

    String NORMAL = "normal"; // Bao thuong
    String WOODEN = "wooden"; // Bao Kien Go
    String PACKAGE = "package"; // Bao Kien Thuong
  }

  public interface CommodityType {

    String SUPER_HEAVY = "superHeavy"; // Siêu nặng
    String BULKY = "bulky"; // Cồng kềnh
    String LIGHTWEIGHT = "lightweight"; // Nhẹ
  }
  public interface CommodityMovingStatus {
    String APPROVED = "approved";
    String CANCELED = "canceled";

  }

  public interface ProblemCode {
    String SAME_CODE = "sameCode"; // Trùng mã
    String UNDEFINED = "undefined"; // Vô chủ
    String WRONG_WAREHOUSE = "wrongWarehouse"; // Nhầm kho
    String WRONG_WEIGHT_HEIGHT = "wrongWeightHeight"; // Sai cân nặng kích thước
    String NEED_TRANSFER = "needTransfer"; // Cần điều chuyển

    String CONTAIN_PACK_SAME_CODE = "containPackageSameCode"; // Chứa kiện trùng mã
    String CONTAIN_PACK_DIFF_TARGET_WH = "containPkgDiffTargetWH"; // chứa kiện khác kho đích
    String WRONG_WEIGHT = "wrongWeight"; // Sai cân nặng
  }

  public interface TransportMethod {
    String ROADWAYS = "roadways"; // Đường bộ
    String WATERWAYS = "waterways"; // Đường thủy
    String RAILWAYS = "railways"; // Đường sắt
    String AIRWAYS = "airways"; // Đường hàng không
  }

  public interface DeliverySlipStatus {
    String NEW = "new"; // mới tạo (nhân viên kho đích tạo)
    String CREATED_BY_CUSTOMER = "createdByCustomer"; // phiếu giao tạo bởi
    // khách hàng
    String WAITING_FOR_APPROVE = "waitingForApprove"; // Phiếu giao đợi kế toán
    // approve
    String APPROVING = "approving"; // Đang duyệt
    String SHIPPING = "shipping"; // Đang giao
    String COMPLETING = "completing"; // Đang hoàn thành
    String COMPLETED = "completed"; // Đã hoàn thành
    String CANCELED = "canceled"; // Đã hủy
    String COMPLETED_WITH_DEBIT = "completeWithDebit"; // Đã hoàn thành nhưng
                                                       // vẫn chưa thu đủ tiền
                                                       // trên phiếu giao
  }

  public interface DeliveryMethod {

    String DELIVERY_BY_WAREHOUSE = "deliveryByWarehouse"; // Kho giao
    String DELIVERY_BY_THIRD_PARTY = "deliveryByThirdParty"; // Giao bởi bên thứ
    String DELIVERY_BY_CUSTOMER = "deliveryByCustomer"; // Khách tự đến lấy
  }

  public interface Format {
    DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
  }

  public interface MessageQueueChannel {
    String UPDATE_ID_PATH = "employee.update.idpath";
    String MESSAGE_TO_EMPLOYEE = "notification.employee.message";
    String UPDATE_HISTORY_DISCOUNT = "update.history.discount.ticket";
    // String ACCOUNTING_CUSTOMER_WALLET = "accounting.deposit";
    String ACCOUNTING_CUSTOMER_WALLET = "accounting.customer.wallet";
    String ACTION_TICKET = "action.ticket";
    String AUTO_SMS = "auto.deposit.sms";
    /* Message hủy đơn hàng */
    String CANCEL_ORDER = "order.update.cancel";
    /* Message đổi kho Trung Quốc nhận */
    String CHANGE_WAREHOUSE_PACKAGE = "package.update.warehouse";
    String WAREHOUSE_CLEAR_ORDER_DATA = "warehouse.clear.order-data";
  }

  public interface ApplicationEnvironment {
    String DEV = "dev";
    String PRODUCT = "prod";
    String CI = "ci";
    String TEST = "test";
  }

  public interface BagDateType {
    byte CREATEDDATE = 0;
    byte DELIVERYDATE = 1;
    byte RECEIVINGDATE = 2;
  }

  public interface Tenant {
    String SOD = "sod";
    String LIKE_ORDER = "likeorder";
    String X_ORDER = "xorder";
    String NAMDINH_ORDER = "namdinh";
  }
  public interface Role {
    String ROLE_ADMIN = "ROLE_ADMIN";
  }

  public interface ExchangeRateGroup {
    String GROUP_NAME = "EXCHANGE_RATE";
    String TIMER = "TIMER_RATE";
  }

  public interface FundCategories {
    // Quỹ
    String ROOT = "FUND";

    // Nhóm quỹ
    String FUND_GROUP = "FUND_GROUP";
    String FUND_DEBT = "FUND_DEBT"; // Quỹ công nợ
    String FUND_OFFICE = "FUND_OFFICE"; // Quỹ văn phòng
    String FUND_PAY = "FUND_PAY"; // Quỹ tiền tệ thanh toán

    // Chi nhánh quỹ
    String FUND_BRANCH_SYSTEM = "FUND_BRANCH_SYSTEM";
  }

  public interface CompleteDeliverySlipMethod {
    String CASH = "cash"; // khách trả tiền mặt
    String WALLET = "wallet"; // trừ ví
    String NONE = "none"; // Không cần trả thêm
  }

  // Các kiểu chậm xử lý của kiện
  public interface PackageLateType {
    // Chậm xử lý kiện vô chủ
    String EXECUTE_UNDEFINED = "EXECUTE_UNDEFINED";
    // Chậm xuất kho trung quốc
    String DELIVERY_WAREHOUSE = "DELIVERY_WAREHOUSE";
    // Chậm chuyển trạng thái sau khi nhập kho
    String DELIVERY_CUSTOMER = "DELIVERY_CUSTOMER";
    // Chậm hoàn thành kiện sau khi hoàn thành phiếu giao
    String COMPLETE_PACKAGE = "COMPLETE_PACKAGE";
  }

  public interface TransportAction {
    String DELIVERY = "delivery";
    String IMPORT = "import";
    String CHECKING = "checking";
  }

}
