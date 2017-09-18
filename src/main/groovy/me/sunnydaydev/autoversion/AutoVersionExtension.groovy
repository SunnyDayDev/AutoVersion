package me.sunnydaydev.autoversion

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer

class AutoVersionExtension {

    private String[] autoVersionForTasks = []

    AutoVersionPlugin plugin

    NamedDomainObjectContainer<AutoIncrement> autoIncrements

    AutoVersionExtension(AutoVersionPlugin plugin,
                         NamedDomainObjectContainer<AutoIncrement> autoIncrements) {
        this.plugin = plugin
        this.autoIncrements = autoIncrements
    }

    String[] getAutoVersionForTasks() {
        autoVersionForTasks
    }

    void setPrepareVersionOnTasks(String[] tasks) {
        autoVersionForTasks = tasks
    }

    Integer getVersionCode() {
        plugin.incrementalVersionCode
    }

    String getVersionName() {
        plugin.incrementalVersionName
    }

    String getReleaseNoteFilePath() {
        plugin.lastBuildReleaseNoteFile
    }

    void autoIncrements(Action<? super NamedDomainObjectContainer<AutoIncrement>> action) {

        println "Set autoincrements."

        action.execute(autoIncrements)
    }

    NamedDomainObjectContainer<AutoIncrement> getAutoIncrements() {
        return autoIncrements
    }
}