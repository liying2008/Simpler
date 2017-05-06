# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in F:\AndroidStudio\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}


# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class com.bumptech.glide.integration.okhttp3.OkHttpGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# okio
-dontwarn okio.**

# okhttp3
-dontwarn okhttp3.**

# for DexGuard only
-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

# 不混淆JSoup
-keep class org.jsoup.** { *; }
-keep interface org.jsoup.** { *; }

# 友盟统计
-keepclassmembers class * {
    public <init> (org.json.JSONObject);
}
-keep public class cc.duduhuo.simpler.R$*{
    public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}