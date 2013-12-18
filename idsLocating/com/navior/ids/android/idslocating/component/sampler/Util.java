/**
 * ==============================BEGIN_COPYRIGHT===============================
 * ===================NAVIOR CO.,LTD. PROPRIETARY INFORMATION==================
 * This software is supplied under the terms of a license agreement or
 * nondisclosure agreement with NAVIOR CO.,LTD. and may not be copied or
 * disclosed except in accordance with the terms of that agreement.
 * ==========Copyright (c) 2010 NAVIOR CO.,LTD. All Rights Reserved.===========
 * ===============================END_COPYRIGHT================================
 *
 * @author cs1
 * @date 13-11-6
 */
package com.navior.ids.android.idslocating.component.sampler;

public class Util {
  private final static char[] SOURCE = {'A', 'B', 'C', 'D', 'E', 'F', 'G',
      'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
      'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
      'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
      'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '_',
      '-',};

  private final static char[] DESTIN = {'J', 'R', 'Z', 'o', 'm', 'j', 'w',
      'I', 'Y', 'G', 'W', 'O', 'D', 'P', 't', 'e', 'X', 'q', '-', 'K', 'H',
      '9', 'r', 'c', '6', 'y', 'h', 'd', 'Q', '0', 'l', 'a', 'B', 'F', 'N',
      '1', '3', 'L', '7', 'C', 'U', 'A', 'v', 'z', '8', 'i', '4', 'u', '5',
      '_', 'E', 'p', 'M', 's', 'x', 'f', 'S', 'g', 'b', 'n', 'k', 'V', '2',
      'T',};


  public final static String encode(String source) {
    return op(source, SOURCE, DESTIN);
  }

  public final static String decode(String source) {
    return op(source, DESTIN, SOURCE);
  }


  private final static String op(String source, char[] sourceCode,
                                 char[] destinCode) {
    if (source != null) {
      char[] src = source.toCharArray();

      for (int i = 0; i < src.length / 2; i++) {
        char temp = src[i];
        src[i] = src[src.length - 1 - i];
        src[src.length - 1 - i] = temp;
      }

      char[] dst = new char[src.length];
      for (int i = 0; i < dst.length; i++) {
        for (int j = 0; j < sourceCode.length; j++) {
          if (sourceCode[j] == src[i]) {
            dst[i] = destinCode[j];
            break;
          }
        }
      }
      return new String(dst);
    } else {
      return null;
    }
  }

}
