package me.sunnydaydev.autoversion

import org.gradle.api.Project
import org.gradle.api.provider.PropertyState

class AutoVersionExtension {

    private String[] autoVersionForTasks

    AutoVersionPlugin plugin

    String[] getAutoVersionForTasks() {
        autoVersionForTasks
    }

    void setAutoVersionForTasks(String[] tasks) {
        autoVersionForTasks = tasks
    }

    Integer getIncrementalVersionCode() {
        plugin.incrementalVersionCode
    }

    String getIncrementalVersionName() {
        plugin.incrementalVersionName
    }

    String getLastBuildReleaseNoteFile() {
        plugin.lastBuildReleaseNoteFile
    }

}