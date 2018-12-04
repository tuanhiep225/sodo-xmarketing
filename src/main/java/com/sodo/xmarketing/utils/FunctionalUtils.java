/**
 * 
 */
package com.sodo.xmarketing.utils;

import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sodo.xmarketing.dto.FundInfo;
import com.sodo.xmarketing.dto.StaffDTO;
import com.sodo.xmarketing.model.ServicePrice;
import com.sodo.xmarketing.model.account.Account;
import com.sodo.xmarketing.model.config.Format;
import com.sodo.xmarketing.model.wallet.Determinant;
import com.sodo.xmarketing.model.wallet.TransactionStatus;
import com.sodo.xmarketing.model.wallet.TransactionType;
import com.sodo.xmarketing.model.wallet.Wallet;

import net.logstash.logback.encoder.org.apache.commons.lang.WordUtils;

/**
 * @author tuanhiep225
 *
 */
public class FunctionalUtils {

	public static boolean isExpried(LocalDateTime time, LocalDateTime timeOne) {
		if (time.getDayOfMonth() == timeOne.getDayOfMonth() && time.getMonthValue() == timeOne.getMonthValue()
				&& time.getYear() == timeOne.getYear() && time.getHour() == timeOne.getHour()
				&& time.getMinute() == timeOne.getMinute() && time.getSecond() == timeOne.getSecond()) {
			return true;
		} else {
			return false;
		}
	}

	public static <T> void exportExcel(List<T> data, Map<String, String> mapFieldNames, String filePath,
			String timezone) throws Exception {

		List<String> fieldNames = new ArrayList<>();
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(translateCode(data.get(0).getClass().getName()));
		setupFieldsForClass(data.get(0).getClass(), fieldNames);
		// Create a row and put some cells in it. Rows are 0 based.
		int rowCount = 0;
		int columnCount = 0;

		Row row = sheet.createRow(rowCount++);
		row.createCell(columnCount++).setCellValue("STT");
		for (Entry<String, String> pair : mapFieldNames.entrySet()) {
			String realName = pair.getValue();
			Cell cel = row.createCell(columnCount++);
			cel.setCellValue(realName);
		}
		Class<? extends Object> classz = data.get(0).getClass();
		int index = 1;
		for (T t : data) {
			row = sheet.createRow(rowCount++);
			columnCount = 0;
			row.createCell(columnCount++).setCellValue(index++);
			for (String fieldName : mapFieldNames.keySet()) {

				Cell cel = row.createCell(columnCount);
				Method method = classz.getMethod("get" + WordUtils.capitalize(fieldName));
				Object value = method.invoke(t, (Object[]) null);
				if (value != null) {
					if (value instanceof String) {
						cel.setCellValue(translateCode((String) value));
					} else if (value instanceof Long) {
						cel.setCellValue((Long) value);
					} else if (value instanceof Integer) {
						cel.setCellValue((Integer) value);
					} else if (value instanceof Double) {
						cel.setCellValue((Double) value);
					} else if (value instanceof Boolean) {
						cel.setCellValue((Boolean) value);
					} else if (value instanceof LocalDateTime) {
						cel.setCellValue(formatDateTime((LocalDateTime) value, timezone));
					} else if (value instanceof LocalDate) {
						cel.setCellValue(((LocalDate) value).toString());
					} else if (value instanceof BigDecimal) {
						cel.setCellValue(((BigDecimal) value).toString());
					} else if (value instanceof Account) {
						cel.setCellValue(((Account) value).getUsername());
						
					} else if (value instanceof Determinant) {
						cel.setCellValue(((Determinant) value).getName());
						
					}else if (value instanceof Format) {
						cel.setCellValue(((Format) value).getCurrencyName());
						
					}else if (value instanceof Wallet) {
						cel.setCellValue(((Wallet) value).getCustomerCode()+ " - "+ ((Wallet) value).getCustomerName());
						
					} else if (value instanceof FundInfo) {
						cel.setCellValue(((FundInfo) value).getName());
						
					}else if (value instanceof TransactionType) {
						cel.setCellValue(translateCode(((TransactionType) value).name()));
					}
					else if (value instanceof ServicePrice) {
						cel.setCellValue(translateCode(((ServicePrice) value).getCode()) + " - "+ ((ServicePrice) value).getCulture().get("vi").getName());
					}
					else if (value instanceof StaffDTO) {
						cel.setCellValue(translateCode(((StaffDTO) value).getCode()) + " - "+ ((StaffDTO) value).getName());
					}
					else if (value instanceof TransactionStatus) {
						cel.setCellValue(translateCode(((TransactionStatus) value).name()));
					}
					else if (value instanceof Set<?>) {
						String strValue = "";
						for (Object obj : (Set<Object>) value) {
							strValue += obj.toString() + "  ";
						}
						cel.setCellValue(strValue);
					} else if (value instanceof List<?>) {
						cel.setCellValue(((List<?>) value).size());
					} else if (value instanceof Object) {
						cel.setCellValue(value.toString());
					}
				}
				columnCount++;
			}
		}
		try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
			workbook.write(outputStream);
		} finally {
			workbook.close();
		}
	}

	private static boolean setupFieldsForClass(Class<?> clazz, List<String> fieldNames) throws Exception {
		Field[] fields = clazz.getDeclaredFields();
		Class<?> superClazz = clazz.getSuperclass();
		Field[] superClazzFields = superClazz.getDeclaredFields();

		for (int i = 0; i < fields.length; i++) {
			fieldNames.add(fields[i].getName());
		}

		for (int i = 0; i < superClazzFields.length; i++) {
			fieldNames.add(superClazzFields[i].getName());
		}
		return true;
	}

	public static String formatDateTime(LocalDateTime localDateTime, String timezone) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss a");
		localDateTime = localDateTime.plusHours(Long.parseLong(timezone));
		return localDateTime.format(formatter);
	}

	public static String translateCode(String code, Map<String, String> retMap) {
		return retMap.containsKey(code) ? retMap.get(code) : code;
	}

	public static String translateCode(String code) {
		String jsonString = "{ \"DEBIT\": \"Thu\"" + ", \"CREDIT\": \"Chi\""
				+ ", \"COMPLETED\": \"Đã xác nhận\"" + ", \"CANCEL\": \"Hủy\""
				+ ", \"WAITTING\": \"Chờ xác nhận\"" + ", \"railways\": \"Đường sắt\""
				+ ", \"airways\": \"Đường hàng không\"" + ", \"normal\": \"Bao thường\""
				+ ", \"wooden\": \"Bao kiện gỗ\"" + ", \"package\": \"Bao kiện thường\""
				+ ", \"superHeavy\": \"Siêu nặng\"" + ", \"bulky\": \"Cồngkềnh\"" + ", \"lightweight\": \"Nhẹ\""
				+ ", \"sameCode\": \"Trùng mã\"" + ", \"undefined\": \"Vô chủ\"" + ", \"wrongWarehouse\": \"Nhầm kho\""
				+ ", \"wrongWeightHeight\": \"Sai cân nặng\"" + ", \"new\": \"Mới tạo\""
				+ ", \"createdByCustomer\": \"Tạo bởi khách\"" + ", \"waitingForApprove\": \"Chờduyệt\""
				+ ", \"shipping\": \"Đang vận chuyển\"" + ", \"completed\": \"Đã hoàn thành\""
				+ ", \"deliveryByWarehouse\": \"Kho giao\"" + ", \"deliveryByThirdParty\": \"Bên thứ ba\""
				+ ", \"deliveryByCustomer\": \"Khách tự đến lấy\""
				+ ", \"com.sodo.sod.warehouse.model.DeliverySlip\": \"Danh sách phiếu giao\""
				+ ", \"com.sodo.sod.warehouse.model.DeliveryNote\": \"Danh sách phiếu xuất kho\" "
				+ ", \"com.sodo.sod.warehouse.model.ImportWarehouseNote\": \"Danh sách phiếu nhập kho\" "
				+ ", \"com.sodo.xmarketing.model.fund.FundTransaction\": \"Giao dịch quỹ\""
				+ "}";
		Map<String, String> retMap = new Gson().fromJson(jsonString, new TypeToken<HashMap<String, String>>() {
		}.getType());
		return retMap.containsKey(code) ? retMap.get(code) : code;
	}
}
