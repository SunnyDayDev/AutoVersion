package me.sunnydaydev.autoversion

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer

class AutoVersionExtension {

    private String[] autoVersionForTasks = []

    AutoVersionPlugin plugin

    NamedDomainObjectContainer<TasksDependedIncrement> autoIncrements

    Increment defaultIncrement = Increment.defaultIncrement()

    AutoVersionExtension(AutoVersionPlugin plugin,
                         NamedDomainObjectContainer<TasksDependedIncrement> autoIncrements) {
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

    void defaultIncrement(Action<? super Increment> action) {
        action.execute(defaultIncrement)
    }

    Increment getDefaultIncrement() {
        return defaultIncrement
    }

    void autoIncrements(Action<? super NamedDomainObjectContainer<TasksDependedIncrement>> action) {
        action.execute(autoIncrements)
    }

    NamedDomainObjectContainer<TasksDependedIncrement> getAutoIncrements() {
        return autoIncrements
    }
}