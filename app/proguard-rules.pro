# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# 代码混淆压缩比，在0~7之间，默认为5
-optimizationpasses 5
# 混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames
#不做预校验，preverify是proguard的四个步骤之一，Android不需要precerify,去掉这一步能够加快混淆速度。
-dontpreverify
# 混淆时是否记录日志，这句话能够使我们的项目混淆后产生映射文件
# 包含有类名->混淆后类名的映射关系
-verbose
# 保留Annotation不混淆
-keepattributes *Annotation*,InnerClasses
# 避免混淆泛型
-keepattributes Signature
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
# 保留本地native方法不被混淆
-keepclasseswithmembernames class * { native <methods>; }
# 不混淆反射
-keepattributes EnclosingMethod
# Parcelable的子类和Creator静态成员变量不混淆
-keep class * implements Android.os.Parcelable {
    # 保持Parcelable不被混淆
    public static final Android.os.Parcelable$Creator *;
}
# 保持异常不被混淆
-keepattributes Exceptions
# 保留 R 下面的资源
-keep class **.R$* {*;}
-keepclassmembers class **.R$* {
    public static <fields>;
}
# 基础组件不混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference

# 保留support、core、api下的所有类及其内部类
-dontnote android.support.**
-dontwarn android.support.**
-keep class android.support.** {*;}
-keep class cn.rongcloud.rtc.core.** {*;}
-keep class cn.rongcloud.rtc.api.** {*;}
-keep class cn.rongcloud.rtc.base.** {*;}
-keep class cn.rongcloud.rtc.utils.** {*;}
-keep class cn.rongcloud.rtc.media.http.** {*;}
-keep class cn.rongcloud.rtc.engine.view** {*;}
-keep class cn.rongcloud.rtc.proxy.message.** {*;}
-keep class cn.rongcloud.rtc.RongRTCExtensionModule {*;}
-keep class cn.rongcloud.rtc.RongRTCMessageRouter {*;}
# 保留api相关保
-keep class cn.rongcloud.voiceroom.api.** {*;}
-keep class cn.rongcloud.voiceroom.model.** {*;}
-keep class cn.rongcloud.voiceroom.utils.** {*;}


-keep class com.umeng.** {*;}

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class com.zui.**{*;}
-keep class com.miui.**{*;}
-keep class com.heytap.**{*;}
-keep class a.**{*;}
-keep class com.vivo.**{*;}