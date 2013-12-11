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
 * @date 13-11-26
 */
package com.navior.ids.android.view.popup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.navior.ids.android.R;

public class LoadingDialog {
  public static ProgressDialog show(Context context, String msg, DialogInterface.OnCancelListener listener) {
    return ProgressDialog.show(context, context.getString(R.string.loading), msg, false, true, listener);
  }
}
