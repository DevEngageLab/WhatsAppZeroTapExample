# 零接触身份验证 - 客户端集成文档

以下步骤是从whatsApp官网中获取。更详细的使用方式请参照 [Zero-Tap Authentication Templates](https://developers.facebook.com/docs/whatsapp/business-management-api/authentication-templates/zero-tap-authentication-templates#handshake)

WhatsApp 开通链接请参照 https://www.engagelab.com/zh_CN/docs/whatsapp/quick-access/open-service

# 1. 初始化握手 （不使用SDK的方式）

```

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

```

# 2. 零接触 广播接收者

创建一个Receiver 继承自 BroadcastReceiver，下面以 OtpCodeReceiver 为例。
创建OtpCodeReceiver 类 继承自 BroadcastReceiver，并在 AndroidManifest.xml 文件中进行以下配置。

```
<receiver
   android:name=".app.receiver.OtpCodeReceiver"
   android:enabled="true"
   android:exported="true">
   <intent-filter>
       <action android:name="com.whatsapp.otp.OTP_RETRIEVED" />
   </intent-filter>
</receiver>
```

在 OtpCodeReceiver 类中监听广播, 获取到的 otpCode 即为 收到的whatsApp code码。

```
public class OtpCodeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
       PendingIntent pendingIntent =     intent.getParcelableExtra("_ci_");
       // verify source of the pendingIntent
       String pendingIntentCreatorPackage = pendingIntent.getCreatorPackage();

       String creatorPackage = pendingIntent.getCreatorPackage();
       if ("com.whatsapp".equals(creatorPackage) ||
           "com.whatsapp.w4b".equals(creatorPackage)) {
          // use OTP code
          String otpCode = intent.getStringExtra("code");
          // ...
       }
    }
}

```
