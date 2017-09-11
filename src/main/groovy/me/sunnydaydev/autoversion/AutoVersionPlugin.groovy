package me.sunnydaydev.autoversion

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class AutoVersionPlugin implements Plugin<Project> {

    private static final String PREPEARE_AUTO_VERSION = 'prepeareAutoVersion'
    private static final String AUTO_VERSION = 'autoVersion'

    Project project

    @Override
    void apply(Project project) {

        this.project = project

        AutoVersionExtension extension = project.extensions.create(AUTO_VERSION, AutoVersionExtension)//, project)
        //AutoVersionExtension extension = project.extensions.add('autoVersion', AutoVersionExtension)

        extension.plugin = this

        AutoVersionTask prepeareAutoVersion = project.tasks.create(PREPEARE_AUTO_VERSION, AutoVersionTask) {

            it.propsFile = getVersionFile()
            it.lastBuildReleaseNotes = new File(getLastBuildReleaseNoteFile())

        }

        prepeareAutoVersion.group = AUTO_VERSION
        prepeareAutoVersion.description = "Prepeare version."

        project.gradle.taskGraph.whenReady { graph ->

            List<Task> tasks = graph.allTasks

            if (tasks.size() > 1) {

                prepeareAutoVersion.autoVersion = tasks.any {
                    task -> task.name in extension.autoVersionForTasks
                }

            } else {

                prepeareAutoVersion.autoVersion = tasks.size() == 1 && tasks.get(0).name == PREPEARE_AUTO_VERSION

            }

        }

        project.tasks.findByName("preBuild").dependsOn prepeareAutoVersion
        
    }

    int getIncrementalVersionCode() {

        Properties versionProperties = new Properties()
        File propsFile = getVersionFile()

        if(propsFile.exists()) {
            versionProperties.load(new FileInputStream(propsFile))
        } else {
            throw new IllegalStateException("Signing properties file not exist! File: " + propsFile.absolutePath)
        }

        Integer code = versionProperties["VERSION_CODE"].toString().toInteger()

        return code > 0 ? code : 1
    }

    String getIncrementalVersionName() {

        Properties versionProperties = new Properties()
        File propsFile = getVersionFile()

        if(propsFile.exists()) {
            versionProperties.load(new FileInputStream(propsFile))
        } else {
            throw new IllegalStateException("Signing properties file not exist! File: " + propsFile.absolutePath)
        }

        return versionProperties["VERSION_NAME"]
    }

    String getLastBuildReleaseNoteFile() {

        String path = project.projectDir.absolutePath + "/autoVersion/lastBetaBuildNotes.txt"

        File lastbuildNote = new File(path)

        if (!lastbuildNote.exists()) {

            lastbuildNote.parentFile.mkdirs()
            lastbuildNote.createNewFile()
            lastbuildNote.write("Initial release note.")

        }

        return lastbuildNote.toString()
    }

    private File getVersionFile() {

        String path = project.projectDir.absolutePath + '/autoVersion/version.properties'

        File versionPropsFile = new File(path)

        if (!versionPropsFile.exists()){

            versionPropsFile.parentFile.mkdirs()
            versionPropsFile.createNewFile()
            versionPropsFile.write("#Initial empty AutoVersion props\nVERSION_CODE=0\nVERSION_NAME=0.0.1")

        }

        return versionPropsFile
    }

}