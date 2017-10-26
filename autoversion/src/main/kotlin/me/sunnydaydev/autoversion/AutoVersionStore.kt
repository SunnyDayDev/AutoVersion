package me.sunnydaydev.autoversion

import org.gradle.api.Project
import java.io.*
import java.util.*

/**
 * Created by sunny on 24.10.2017.
 * mail: mail@sunnydaydev.me
 */
class AutoVersionStore(private val project: Project) {

    var versionCode: Int
        get() = properties.versionCode ?: 1
        internal set(value) {
            properties.versionCode = value
        }

    var versionName: String
        get() = properties.versionName ?: "0.0.1"
        internal set(value) {
            properties.versionName = value
        }

    val releaseNotesFile: File get() = project.file("autoVersion/releaseNotes.txt")
            .apply {
                if (!exists()) {
                    parentFile.mkdirs()
                    createNewFile()
                }
            }

    private val properties = getProps()

    fun commit() {
        properties.store(getPropsFile().newWriter(), null)
    }

    private fun getProps(): Properties {
        return Properties().apply {
            load(FileInputStream(getPropsFile()))
        }
    }

    private fun getPropsFile(): File {
        return project.file("autoVersion/version.properties").apply {
            if (!exists()) {
                parentFile.mkdirs()
                createNewFile()
            }
        }
    }

    //region// Extensions

    private var Properties.versionCode: Int?
        get() = this["VERSION_CODE"]?.toString()?.toIntOrNull()
        set(value) { this["VERSION_CODE"] = value.toString() }

    private var Properties.versionName: String?
        get() = this["VERSION_NAME"]?.toString()
        set(value) { this["VERSION_NAME"] = value }

    private fun File.newWriter(): Writer = BufferedWriter(FileWriter(this))

    //endregion

}