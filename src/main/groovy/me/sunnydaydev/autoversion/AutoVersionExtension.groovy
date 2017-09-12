package me.sunnydaydev.autoversion

class AutoVersionExtension {

    private String[] autoVersionForTasks

    AutoVersionPlugin plugin

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

}