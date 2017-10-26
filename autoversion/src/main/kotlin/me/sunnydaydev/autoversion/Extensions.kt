package me.sunnydaydev.autoversion

import com.android.build.gradle.api.ApplicationVariant
import kotlin.reflect.full.declaredFunctions

/**
 * Created by sunny on 25.10.2017.
 * mail: mail@sunnydaydev.me
 */

fun <T, R> T.transform(transform: (T) -> R) : R = transform(this)

fun String.isOnlyDigits() : Boolean = isNotEmpty() && all { it.isDigit() }

fun ApplicationVariant.setVersionCode(code: Int) {

    mergedFlavor::class.declaredFunctions
            .find { it.name == "setVersionCode" }
            ?.call(mergedFlavor, code) ?: println("Method ProductFlavor.setVersionCode(Int) not found.")

}

fun ApplicationVariant.setVersionName(name: String) {

    println("ProductFlavor class:${mergedFlavor::class.simpleName}")

    mergedFlavor::class.declaredFunctions
            .find { it.name == "setVersionName" }
            ?.call(mergedFlavor, name) ?: println("Method ProductFlavor.setVersionName(String) not found.")

}