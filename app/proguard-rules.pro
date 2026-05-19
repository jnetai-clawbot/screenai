# Add project specific ProGuard rules here.
-keepattributes Signature
-keepattributes *Annotation*

-keep class com.jnetaol.screenai.data.model.** { *; }
-keep class com.jnetaol.screenai.engine.** { *; }

-dontwarn okhttp3.**
-dontwarn okio.**
