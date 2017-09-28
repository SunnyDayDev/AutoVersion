package me.sunnydaydev.autoversion

import org.gradle.api.Plugin
import org.gradle.api.Project

class AutoVersionPlugin implements Plugin<Project> {

    private static final String AUTO_VERSION = 'autoVersion'

    Project project

    private AutoVersionExtension autoVersionExtension

    @Override
    void apply(Project project) {

        this.project = project

        autoVersionExtension = project.extensions.create(
                AUTO_VERSION,
                AutoVersionExtension,
                this,
                project.container(TasksDependedIncrement)
        )

        AutoVersionTask prepeareAutoVersion = project.tasks
                .create(AutoVersionTask.AUTOVERSION_TASK_NAME, AutoVersionTask) {

                    it.propsFile = getVersionFile()
                    it.lastBuildReleaseNotes = new File(getLastBuildReleaseNoteFile())
                    it.extension = autoVersionExtension

                }

        prepeareAutoVersion.group = AUTO_VERSION
        prepeareAutoVersion.description = "Prepeare version."

        project.afterEvaluate {

            def androidExtension = project.extensions.findByName("android")

            if (androidExtension == null) return

            def prepareVariants = { variants ->

                variants.all { variant ->

                    variant.preBuild.dependsOn prepeareAutoVersion

                    prepeareAutoVersion.doLast {

                        variant.mergedFlavor.versionName = incrementalVersionName
                        variant.mergedFlavor.versionCode = incrementalVersionCode

                    }

                }

            }

            if (androidExtension.hasProperty("applicationVariants")) {

                prepareVariants(androidExtension.applicationVariants)

            }

            if (androidExtension.hasProperty("libraryVariants")) {

                prepareVariants(androidExtension.libraryVariants)

            }

        }

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
            versionPropsFile.write("#Initial empty AutoVersion props\nVERSION_CODE=1\nVERSION_NAME=0.0.1")

        }

        return versionPropsFile
    }

}