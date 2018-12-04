package com.sodo.xmarketing.utils;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;


/**
 * @author HenryDo
 * @created 09/08/2017 9:54 AM
 */
public class StringUtils {

  /**
   * Chuyển tiếng việt có dấu thành tiếng việt không dấu.
   *
   * @param str Chuỗi string tiếng việt có dấu
   * @return Chuỗi string tiếng việt không dấu
   */
  public static String unAccent(String str) {
    String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    return pattern.matcher(temp).replaceAll("").replaceAll("Đ", "D").replace("đ", "d");
  }

  public static String getContent(String str, String culture, List<Object> value) {
    NumberFormat nf_us1 = null;
    List<Object> valueFormated = new ArrayList();
    if (culture.equalsIgnoreCase("vi")) {
      nf_us1 = NumberFormat.getInstance(new Locale("vi", "VN"));
    }
    if (culture.equalsIgnoreCase("th")) {
      nf_us1 = NumberFormat.getInstance(new Locale("th", "TH"));
    }
    if (value != null) {
      for (Object obj : value) {
        if (obj instanceof Integer || obj instanceof Double || obj instanceof Float
            || obj instanceof BigDecimal) {
          valueFormated.add(nf_us1.format(obj));
          continue;
        }
        valueFormated.add(obj);
      }
    }

    Object[] args = valueFormated.toArray();
    MessageFormat fmt = new MessageFormat(str);

    return fmt.format(args);
  }

  /**
   * @param arg
   */
  public static void main(String[] arg) {

  }

}
