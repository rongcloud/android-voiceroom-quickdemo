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

# rongcloud im
-keepattributes Exceptions,InnerClasses

-keepattributes Signature

-keep class io.rong.** {*;}
# im的这个混淆配置，已经涵盖了语聊房的SDK的混淆配置，
# 这里 voiceroom的混淆配置其实可以省略
-keep class cn.rongcloud.** {*;}
-keep class * implements io.rong.imlib.model.MessageContent {*;}
-dontwarn io.rong.push.**
-dontnote com.xiaomi.**
-dontnote com.google.android.gms.gcm.**
-dontnote io.rong.**

# 下方混淆使用了Location包时才需要配置, 可参考高德官网的混淆方式:https://lbs.amap.com/api/android-sdk/guide/create-project/dev-attention
-keep class com.amap.api.**{*;}
-keep class com.amap.api.services.**{*;}
-keep class com.autonavi.**{*;}

# voiceroom sdk 的混淆配置
-keep class cn.rongcloud.voiceroom.api.** {*;}
-keep class cn.rongcloud.voiceroom.model.** {*;}
-keep class cn.rongcloud.voiceroom.utils.** {*;}
-keep class cn.rongcloud.messager.** {*;}

-ignorewarnings
