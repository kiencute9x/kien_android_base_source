package com.kiencute.landmarkremark.extentions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat.startPostponedEnterTransition
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideContext
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.kiencute.landmarkremark.R
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.TimeUnit

fun View?.gone() {
    this?.let {
        visibility = View.GONE
    }
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

@SuppressLint("CheckResult")
fun ImageView.load(
    url: String?,
    @DrawableRes placeholderRes: Int = R.drawable.ic_cloud_download,
) {
    val safePlaceholderDrawable = AppCompatResources.getDrawable(context, placeholderRes)
    val requestOptions = RequestOptions().apply {
        placeholder(safePlaceholderDrawable)
        error(safePlaceholderDrawable)
    }
    val glideRequest = Glide
        .with(context)
        .setDefaultRequestOptions(requestOptions)
        .load(url)
        .dontAnimate()
    glideRequest.into(this)
}

fun AppCompatActivity.setStatusBarColor(@ColorRes color: Int) {
    with(window) {
        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        WindowInsetsControllerCompat(window, decorView).isAppearanceLightStatusBars =
            this@setStatusBarColor.isDarkMode().not()
        ContextCompat.getColor(this@setStatusBarColor, color).let {
            window?.statusBarColor = it
        }
    }
}

fun AppCompatActivity.isDarkMode(): Boolean {
    return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_YES -> true
        Configuration.UI_MODE_NIGHT_NO -> false
        Configuration.UI_MODE_NIGHT_UNDEFINED -> true
        else -> false
    }
}
