package com.netizenkorea.android
import expo.modules.splashscreen.SplashScreenManager

import android.os.Build
import android.os.Bundle

import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled
import com.facebook.react.defaults.DefaultReactActivityDelegate

import expo.modules.ReactActivityDelegateWrapper

class MainActivity : ReactActivity() {

  private var splashView: View? = null
  private var splashRemoved = false

  override fun onCreate(savedInstanceState: Bundle?) {
    // Set the theme to AppTheme BEFORE onCreate to support
    // coloring the background, status bar, and navigation bar.
    // This is required for expo-splash-screen.
    setTheme(R.style.AppTheme);

    //    we add it to the window decor view so it sits above the React root until removed.
    val inflater = LayoutInflater.from(this)
    splashView = inflater.inflate(R.layout.splash_layout, null)
    // add to decorView
    val decor = window.decorView as FrameLayout
    decor.addView(splashView)

    // Disable window animation completely
    window.setWindowAnimations(0)

    // @generated begin expo-splashscreen - expo prebuild (DO NOT MODIFY) sync-f3ff59a738c56c9a6119210cb55f0b613eb8b6af
    //SplashScreenManager.registerOnActivity(this)
    // @generated end expo-splashscreen

    super.onCreate(null)

    // Override Activity transition fade controlled by the system
    overridePendingTransition(0, 0)

    // Use global layout or pre-draw to detect first frame.
    val content = findViewById<View>(android.R.id.content)
    content.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
      override fun onPreDraw(): Boolean {
        // remove listener at once
        if (content.viewTreeObserver.isAlive) {
          content.viewTreeObserver.removeOnPreDrawListener(this)
        }
        // remove the splash view on UI thread
        if (!splashRemoved) {
          splashRemoved = true
          runOnUiThread {
            try {
              val decorView = window.decorView as FrameLayout
              splashView?.let { decorView.removeView(it) }
              splashView = null
            } catch (e: Exception) {
              // ignore
              splashView = null
            }
          }
        }
        // allow drawing to continue
        return true
      }
    })    
  }

  /**
   * Returns the name of the main component registered from JavaScript. This is used to schedule
   * rendering of the component.
   */
  override fun getMainComponentName(): String = "main"

  /**
   * Returns the instance of the [ReactActivityDelegate]. We use [DefaultReactActivityDelegate]
   * which allows you to enable New Architecture with a single boolean flags [fabricEnabled]
   */
  override fun createReactActivityDelegate(): ReactActivityDelegate {
    return ReactActivityDelegateWrapper(
          this,
          BuildConfig.IS_NEW_ARCHITECTURE_ENABLED,
          object : DefaultReactActivityDelegate(
              this,
              mainComponentName,
              fabricEnabled
          ){})
  }

  /**
    * Align the back button behavior with Android S
    * where moving root activities to background instead of finishing activities.
    * @see <a href="https://developer.android.com/reference/android/app/Activity#onBackPressed()">onBackPressed</a>
    */
  override fun invokeDefaultOnBackPressed() {
      if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
          if (!moveTaskToBack(false)) {
              // For non-root activities, use the default implementation to finish them.
              super.invokeDefaultOnBackPressed()
          }
          return
      }

      // Use the default back button implementation on Android S
      // because it's doing more than [Activity.moveTaskToBack] in fact.
      super.invokeDefaultOnBackPressed()
  }

  override fun onPostResume() {
      super.onPostResume()
      overridePendingTransition(0, 0)
  }
}
