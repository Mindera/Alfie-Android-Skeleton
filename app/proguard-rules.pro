# Add project specific ProGuard rules here.
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keepnames class androidx.navigation.fragment.NavHostFragment
