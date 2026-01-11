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

# 基本保留规则
-keepattributes Signature, Exceptions

-keep class com.johnny.tinyalsa.TinyAlsaManager {
    public <init>();
    native <methods>;
    public void setAudioTestCallback(com.johnny.tinyalsa.TinyAlsaManager$AudioTestCallback);
}

-keep interface com.johnny.tinyalsa.TinyAlsaManager$AudioTestCallback {
    public void onTestProgress(int);
    public void onTestComplete(boolean, java.lang.String);
}

-keep class com.johnny.tinyalsa.TinyAlsaManager$AudioParams {
    public static <fields>;
}

# 通用配置
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontpreverify