package com.netizenkorea.android
import expo.modules.splashscreen.SplashScreenManager

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.ImageView
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import android.os.Build

import com.facebook.react.ReactActivity
import com.facebook.react.ReactRootView
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled
import com.facebook.react.defaults.DefaultReactActivityDelegate

import expo.modules.ReactActivityDelegateWrapper

class MainActivity : ReactActivity() {

  private var splash2ImageView: ImageView? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    // Set the theme to AppTheme BEFORE onCreate to support
    // coloring the background, status bar, and navigation bar.
    // This is required for expo-splash-screen.
    setTheme(R.style.AppTheme);

    // @generated begin expo-splashscreen - expo prebuild (DO NOT MODIFY) sync-f3ff59a738c56c9a6119210cb55f0b613eb8b6af
    //SplashScreenManager.registerOnActivity(this)
    // @generated end expo-splashscreen

    super.onCreate(null)

    window.setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN
    )

    val rootView: ReactRootView = reactRootView

    rootView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            rootView.viewTreeObserver.removeOnPreDrawListener(this)
            showSplash2()
            return true
        }
    })
  }

  private fun showSplash2() {
    splash2ImageView = ImageView(this).apply {
        setImageResource(R.drawable.splash2) // app/src/main/res/drawable 에 위치해야 함
        scaleType = ImageView.ScaleType.CENTER_CROP // contain 은 CENTER_INSIDE
        layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
    }

    addContentView(
        splash2ImageView,
        FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
    )

    Handler(Looper.getMainLooper()).postDelayed({
        hideSplash2()
    }, 2000)
  }

  private fun hideSplash2() {
    (splash2ImageView?.parent as? FrameLayout)?.removeView(splash2ImageView)
    splash2ImageView = null
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
