# Jopiter release ProGuard/R8 rules.
# The app (de)serializes several models with Jackson via reflection, so those classes and the
# Jackson runtime must be kept from being stripped or renamed.

# Keep metadata needed by Kotlin reflection and Jackson.
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations, AnnotationDefault
-keep class kotlin.Metadata { *; }

# Jackson (databind + kotlin + jsr310 modules) uses reflection heavily.
-keep class com.fasterxml.jackson.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-dontwarn com.fasterxml.jackson.**
-keep class kotlin.reflect.** { *; }
-dontwarn kotlin.reflect.**

# Jopiter models and DTOs that Jackson binds by field/property name — keep names and members intact.
-keep class app.jopiter.restaurant.model.** { *; }
-keep class app.jopiter.subject.external.** { *; }

# Koin (reflection-free, but keep to be safe against R8 stripping module definitions).
-keep class org.koin.** { *; }
-dontwarn org.koin.**

# OkHttp / Okio / Fuel — standard library-provided consumer rules plus these suppressions.
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# SLF4J (used by Fuel/MockServer transitive deps) — optional at runtime.
-dontwarn org.slf4j.**

# Room, pulled in transitively by WorkManager. room-runtime 2.5.0 ships
# "-keep class * extends androidx.room.RoomDatabase" with no member specification. Under R8 full
# mode (the default since AGP 8) a member-less -keep does NOT implicitly retain the default
# constructor, so the generated WorkDatabase_Impl survives with no <init>() and Room's reflective
# instantiation blows up while androidx.startup initializes WorkManager, crashing the app on launch.
-keep class * extends androidx.room.RoomDatabase { <init>(); }
-keep class * extends androidx.room.RoomDatabase$Callback { <init>(); }
