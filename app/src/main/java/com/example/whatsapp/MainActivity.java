package com.example.whatsapp;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("get_whatsapp_code_action".equals(intent.getAction())) {
                // 从Intent中获取值
                String code = intent.getStringExtra("whatsapp_code");
                TextView codeView = findViewById(R.id.code_textview);
                codeView.setText(code);
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter("get_whatsapp_code_action");
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化握手
        sendOtpIntentToWhatsApp();
    }

    public void sendOtpIntentToWhatsApp() {
        // Send OTP_REQUESTED intent to both WA and WA Business App
        sendOtpIntentToWhatsApp("com.whatsapp");
        sendOtpIntentToWhatsApp("com.whatsapp.w4b");
    }

    private void sendOtpIntentToWhatsApp(String packageName) {

        /**
         * Starting with Build.VERSION_CODES.S, it will be required to explicitly
         * specify the mutability of  PendingIntents on creation with either
         * {@link #FLAG_IMMUTABLE} or FLAG_MUTABLE
         */
        int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? FLAG_IMMUTABLE : 0;
        PendingIntent pi = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                new Intent(),
                flags);


        // Send OTP_REQUESTED intent to WhatsApp
        Intent intentToWhatsApp = new Intent();
        intentToWhatsApp.setPackage(packageName);
        intentToWhatsApp.setAction("com.whatsapp.otp.OTP_REQUESTED");
        // WA will use this to verify the identity of the caller app.
        Bundle extras = intentToWhatsApp.getExtras();
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putParcelable("_ci_", pi);
        intentToWhatsApp.putExtras(extras);
        getApplicationContext().sendBroadcast(intentToWhatsApp);
    }


}