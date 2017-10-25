package me.sunnydaydev.autoversion

/**
 * Created by sunny on 24.10.2017.
 * mail: mail@sunnydaydev.me
 */

class Increment(val name: String) {

    var versionNameIncrement: String = "0.0.0"
        private set

    var buildIncrement: Int = 1
        private set

    var priority = 0
        private set

    var updateReleaseNotes = false
        private set

    var confirmByDialog = false
        private set

    var variants: Array<String> = arrayOf()
        private set

    var buildTypes: Array<String> = arrayOf()
        private set

    var flavours: Array<String> = arrayOf()
        private set

    var tasks: Array<String> = arrayOf()
        private set

    fun increments(increments: String) {
        this.versionNameIncrement = increments
    }

    fun buildIncrement(buildIncrement: Int) {
        this.buildIncrement = buildIncrement
    }

    fun onVariants(variants: Array<String>) {
        this.variants = variants
    }

    fun onBuildTypes(buildTypes: Array<String>) {
        this.buildTypes = buildTypes
    }

    fun onFalvours(flavours: Array<String>) {
        this.flavours = flavours
    }

    fun onTasks(tasks: Array<String>) {
        this.tasks = tasks
    }

    fun priority(priority: Int) {
        this.priority = priority
    }

    fun updateReleaseNotes(updateReleaseNotes: Boolean) {
        this.updateReleaseNotes = updateReleaseNotes
    }

    fun confirmByDialog(confirmByDialog: Boolean) {
        this.confirmByDialog = confirmByDialog
    }

}