package me.sunnydaydev.autoversion

import com.android.build.gradle.api.ApplicationVariant

/**
 * Created by sunny on 25.10.2017.
 * mail: mail@sunnydaydev.me
 */
internal class Incrementer(
        private val store: AutoVersionStore
) {

    internal var variants: List<ApplicationVariant>? = null

    fun executeIncrement(increment: Increment) {

        if (increment.confirmByDialog || increment.updateReleaseNotes) {

            executeWithDialog(increment)

        } else {

            if (!increment.willIncrement()) return

            store.versionCode += increment.versionCodeIncrement
            store.versionName = incrementVersionName(store.versionName, increment.versionNameIncrement)

            store.commit()

        }

        updateVariants()

    }

    private fun executeWithDialog(increment: Increment) {

        System.setProperty("java.awt.headless", "false")

        val versionNameIncrements = increment.versionNameIncrement
                .split(".")
                .map { it.toInt() }
                .toIntArray()
        val currentReleaseNotes = store.releaseNotesFile.readText()

        val result = IncrementVersionGroovyDialog.prepareVersion(
                store.versionCode,
                store.versionName.split(".").map { it.toInt() }.toIntArray(),
                increment.versionCodeIncrement,
                versionNameIncrements,
                currentReleaseNotes
        )

        if (result.releaseNotes != currentReleaseNotes) {
            store.releaseNotesFile.writeText(text = result.releaseNotes)
        }

        if (result.versionChanged()) {

            store.versionCode += result.versionCodeIncrement

            val versionNameIncrement = result.increments.joinToString(".")
            store.versionName = incrementVersionName(store.versionName, versionNameIncrement)

            store.commit()

        }

    }

    private fun updateVariants() {

        variants?.forEach {
            it.setVersionCode(store.versionCode)
            it.setVersionName(store.versionName)
        }

    }

    private fun Increment.willIncrement() : Boolean =
            versionCodeIncrement != 0 || versionNameIncrement != "0.0.0"

    private fun IncrementVersionGroovyDialog.Result.versionChanged() : Boolean =
            versionCodeIncrement != 0 || increments.any { it != 0 }

    private fun incrementVersionName(base: String, appendix: String) : String {

        val parts = base.split(".").map { it.toInt() }.toMutableList()

        appendix.split(".").forEachIndexed { i, it ->
            if (it == "x") {
                parts[i] = 0
            } else {
                parts[i] += it.toInt()
            }
        }

        return parts.joinToString(".")

    }

}