-verbose
-dontpreverify
-optimizationpasses 5
-dontskipnonpubliclibraryclasses

-dontwarn org.conscrypt.**

-repackageclasses com.sanmer.mrepo
-keep class com.sanmer.mrepo.data.json.**{*;}
-keep class com.sanmer.mrepo.data.module.**{*;}