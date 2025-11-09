-keepattributes Signature, InnerClasses
-keep class com.google.protobuf.** { *; }
-keep class io.grpc.** { *; }
-keep,allowobfuscation,allowshrinking class * {
    @com.google.firebase.firestore.PropertyName <fields>;
}
