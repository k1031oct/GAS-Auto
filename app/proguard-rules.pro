-keep public class com.google.firebase.firestore.** { *; }
-keep public class io.grpc.** { *; }
-keep class com.google.protobuf.** { *; }
-keep class com.google.android.gms.common.** { *; }
-keep class com.google.android.gms.auth.** { *; }
-keep class com.google.android.libraries.identity.googleid.** { *; }

# Keep the annotations
-keepattributes Signature, InnerClasses, *Annotation*

# Keepclasses with annotation
-keep,allowobfuscation,allowshrinking class * {
    @com.google.firebase.firestore.PropertyName <fields>;
}

# gRPC rules
-keepclassmembers class * {
    @io.grpc.stub.annotations.RpcMethod <methods>;
}
-keep class io.grpc.internal.AbstractManagedChannelImplBuilder
-keep class io.grpc.internal.AbstractManagedChannelImplBuilder$ChannelBuilderDefaultPortProvider
