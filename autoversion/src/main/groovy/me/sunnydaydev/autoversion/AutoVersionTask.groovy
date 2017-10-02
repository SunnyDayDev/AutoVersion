package me.sunnydaydev.autoversion

import groovy.swing.SwingBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class AutoVersionTask extends DefaultTask {

    static String AUTOVERSION_TASK_NAME = "prepareAutoVersion"

    private Properties versionProperties
    private File propsFile
    private File lastBuildReleaseNotes

    private AutoVersionExtension extension

    @TaskAction
    void update() {

        List<String> currentTasks = project.gradle.startParameter.taskNames.collect {

            String[] taskNamePaths = it.split(":")
            taskNamePaths[taskNamePaths.length -1]

        }

        boolean needPrepareVersion = currentTasks.any {

            extension.autoVersionForTasks.contains(it) || it == AUTOVERSION_TASK_NAME

        }


        println "Current autoIncrementOnTasks $currentTasks"

        TasksDependedIncrement autoIncrement = null

        if (extension.autoIncrements != null) {

            autoIncrement = extension.autoIncrements.find {

                return it.useOnTasks.any { currentTasks.contains(it) }

            }

        }

        if (autoIncrement != null) {

            println "Autoincrement enabled: ${autoIncrement.increments}"

        } else {

            println "Autoincrement disabled."

        }

        if (needPrepareVersion) {

            System.setProperty("java.awt.headless", "false")

            prepareVersion(autoIncrement != null ? autoIncrement : extension.defaultIncrement)

        } else if (autoIncrement != null) {

            Integer[] versions = versionProperties["VERSION_NAME"].toString()
                    .split("\\.")
                    .collect { it.toInteger() }
            Integer currentVersionCode = versionProperties["VERSION_CODE"].toString().toInteger()

            for(int i = 0; i < 3; i++) {
                versions[i] += autoIncrement.increments[i]
            }
            currentVersionCode += autoIncrement.buildIncrement

            versionProperties["VERSION_NAME"] = versions.join(".")
            versionProperties["VERSION_CODE"] = String.valueOf(currentVersionCode)
            versionProperties.store(propsFile.newWriter(), null)

        }

    }

    void setExtension(AutoVersionExtension extension) {
        this.extension = extension
    }

    AutoVersionExtension getExtension() {
        return this.extension
    }

    void setLastBuildReleaseNotes(File file) {
        this.lastBuildReleaseNotes = file
    }

    void setPropsFile(File propsFile) {

        Properties versionProperties = new Properties()
        if(propsFile.exists()) {
            versionProperties.load(new FileInputStream(propsFile))
        } else {
            throw new IllegalStateException("Signing properties file not exist! File: " + propsFile.absolutePath)
        }

        this.propsFile = propsFile
        this.versionProperties = versionProperties
    }

    private void prepareVersion(Increment increment) {

        Integer[] versions = versionProperties["VERSION_NAME"].toString()
                .split("\\.")
                .collect { it.toInteger() }
        Integer currentVersionCode = versionProperties["VERSION_CODE"].toString().toInteger()

        boolean cancelled = true

        String lastBuildNotes = lastBuildReleaseNotes.readLines().join("\n")

        boolean lastBuildNotesChanged = false
        boolean versionChanged = true

        new SwingBuilder().edt {

            dialog(modal: true, // Otherwise the build will continue running before you closed the dialog
                    title: 'Build version.', // Dialog title
                    alwaysOnTop: true, // pretty much what the name says
                    resizable: false, // Don't allow the user to resize the dialog
                    locationRelativeTo: null, // Place dialog in center of the screen
                    pack: true, // We need to pack the dialog (so it will take the size of it's children)
                    show: true // Let's show it
            ) {

                def releaseNotesInput
                def newVersionLabel

                int[] increments = new int[4]

                for (int i = 0; i < 3; i ++) {
                    increments[i] = increment.increments[i]
                }
                increments[3] = increment.buildIncrement

                def updateVersion = {

                    String global = versions[0].toInteger() + increments[0]
                    String major = versions[1].toInteger() + increments[1]
                    String minor = versions[2].toInteger() + increments[2]
                    String build = currentVersionCode + increments[3]

                    newVersionLabel.text = "New version: $global.$major.$minor ($build)"
                }

                vbox {

                    hbox {
                        label text: "Current version: ${versions.join(".")} ($currentVersionCode)"
                    }

                    hbox {
                        label text: "What version changes?"
                    }


                    hbox {

                        button text: '1.x.x', actionPerformed: {
                            increments[0] = increments[0] + 1
                            updateVersion() // Close dialog
                        }

                        button text: 'x.1.x', actionPerformed: {
                            increments[1] = increments[1] + 1
                            updateVersion() // Close dialog
                        }

                        button text: 'x.x.1', actionPerformed: {
                            increments[2] = increments[2] + 1
                            updateVersion() // Close dialog
                        }

                    }

                    hbox {

                        button text: '0.x.x', actionPerformed: {
                            increments[0] = -versions[0]
                            updateVersion()
                        }

                        button text: 'x.0.x', actionPerformed: {
                            increments[1] = -versions[1]
                            updateVersion()
                        }

                        button text: 'x.x.0', actionPerformed: {
                            increments[2] = -versions[2]
                            updateVersion()
                        }

                    }

                    hbox {

                        button text: 'Build number', actionPerformed: {
                            increments[3] = increments[3] + 1
                            updateVersion()
                        }

                        button text: 'Nothing Changed', actionPerformed: {
                            increments[0] = 0
                            increments[1] = 0
                            increments[2] = 0
                            increments[3] = 0

                            updateVersion()
                        }

                        button text: 'Reset', actionPerformed: {

                            for (int i = 0; i < 4; i ++) {
                                increments[i] = startIncrements[i]
                            }

                            updateVersion()
                        }
                    }

                    hbox {
                        newVersionLabel = label text: ""
                        updateVersion()
                    }

                    hbox {
                        releaseTitle = label text: 'Release notes:'
                    }

                    releaseNotesInput = textArea text: lastBuildNotes, preferredSize: [400, 300]

                    hbox {

                        button text: 'Ok', actionPerformed: {

                            versionChanged = increments.any { it != 0 }

                            if (versionChanged) {

                                versions[0] = versions[0] + increments[0]
                                versions[1] = versions[1].toInteger() + increments[1]
                                versions[2] = versions[2].toInteger() + increments[2]

                                currentVersionCode = currentVersionCode + increments[3]

                            }

                            if (lastBuildNotes != releaseNotesInput.text) {

                                lastBuildNotes = releaseNotesInput.text
                                lastBuildNotesChanged = true

                            }

                            cancelled = false

                            dispose()
                        }

                        button text: 'Cancel', actionPerformed: {
                            cancelled = true
                            dispose() // Close dialog
                        }

                    }

                } // vbox end

            } // dialog end

        } // edt end


        if (cancelled){
            throw new Exception("Cancelled.")
        }

        if (versionChanged) {

            versionProperties["VERSION_NAME"] = versions.join(".")
            versionProperties["VERSION_CODE"] = String.valueOf(currentVersionCode)
            versionProperties.store(propsFile.newWriter(), null)

        }

        if (lastBuildNotesChanged) {

            lastBuildReleaseNotes.write(lastBuildNotes)

        }

    }

}