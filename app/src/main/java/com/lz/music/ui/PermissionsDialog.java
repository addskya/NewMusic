package com.lz.music.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.lz.music.kuyuehui.R;

/**
 * Created by Orange on 17-9-7.
 * Email:chenghe.zhang@ck-telecom.com
 */
@Deprecated
public class PermissionsDialog extends Dialog {

    public PermissionsDialog(Context context) {
        super(context);
    }

    public PermissionsDialog(Context context,
                             int themeResId) {
        super(context, themeResId);
    }

    public PermissionsDialog(Context context,
                             boolean cancelable,
                             OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_permissions);
    }
}
