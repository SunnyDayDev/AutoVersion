package me.sunnydaydev.autoversion

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer

/**
 * Created by sunny on 24.10.2017.
 * mail: mail@sunnydaydev.me
 */
open class AutoVersionExtension(private val store: AutoVersionStore,
                           val increments: NamedDomainObjectContainer<Increment> ) {

    val versionCode: Int get() = store.versionCode

    val versionName: String get() = store.versionName

    val releaseNotesFilePath: String get() = store.releaseNotesFile.path

    fun increments(action: Action<NamedDomainObjectContainer<Increment>>) {
        action.execute(increments)
    }

}