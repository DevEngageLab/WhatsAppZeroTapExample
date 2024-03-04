package com.example.whatsapp;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class OtpCodeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PendingIntent pendingIntent = intent.getParcelableExtra("_ci_");
        // verify source of the pendingIntent
        String pendingIntentCreatorPackage = pendingIntent.getCreatorPackage();
        String creatorPackage = pendingIntent.getCreatorPackage();
        if ("com.whatsapp".equals(creatorPackage) ||
                "com.whatsapp.w4b".equals(creatorPackage)) {
            // use OTP code
            String otpCode = intent.getStringExtra("code");
            Log.d("ReceiveCode",otpCode);
            Toast.makeText(context.getApplicationContext(), otpCode, Toast.LENGTH_SHORT).show();

            // 发送广播，携带获取到的code值
            Intent broadcastIntent = new Intent("get_whatsapp_code_action");
            broadcastIntent.putExtra("whatsapp_code", otpCode);
            context.sendBroadcast(broadcastIntent);

        }
    }
}
