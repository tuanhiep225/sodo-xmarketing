package com.sodo.xmarketing.utils;

import com.sodo.xmarketing.exception.SodException;

/**
 * Created by Henry Do User: henrydo Date: 13/08/2018 Time: 18/36
 */
public class GenerateValueIdentifier {

  public static String generate(int value) throws SodException {
    if (value < 0) {
      throw new SodException("Value can not is vegative numbers",
          "Value can not is vegative numbers");
    }
    if (value > 25999) {
      throw new SodException("Can not generate value. Cause by input value > 25999",
          "Can not generate value.");
    }
    int valueBase = 65;
    int prefix = (value / 1000) % 26;
    int suffix = value % 1000;
    String prefixStr = Character.toString((char) (valueBase + prefix));
    String suffixStr = null;
    if (suffix < 100) {
      suffix += 1000;
      suffixStr = String.valueOf(suffix).substring(1, 4);
    } else {
      suffixStr = String.valueOf(suffix);
    }
    return prefixStr + suffixStr;
  }

  public static String generateCode(String prefixStr, long value) throws SodException {
    if (value < 0) {
      throw new SodException("Value can not is vegative numbers",
          "Value can not is vegative numbers");
    }

    if (value > 999) {
      return prefixStr + value;
    }

    long suffix = value % 1000;
    String suffixStr = null;
    if (suffix < 100) {
      suffix += 1000;
      suffixStr = String.valueOf(suffix).substring(1, 4);
    } else {
      suffixStr = String.valueOf(suffix);
    }
    return prefixStr + suffixStr;
  }
}
