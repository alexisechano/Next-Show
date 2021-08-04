package com.example.next_show;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtil {
    public static void showTopAlignedToast(String m, Context context) {
        Toast msg = Toast.makeText(context, m, Toast.LENGTH_SHORT);
        msg.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
        msg.show();
    }
}