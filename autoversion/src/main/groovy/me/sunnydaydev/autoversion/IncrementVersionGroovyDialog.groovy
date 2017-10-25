package me.sunnydaydev.autoversion

import groovy.swing.SwingBuilder

import javax.annotation.Nullable

class IncrementVersionGroovyDialog  {

    static Result prepareVersion(int initalVersionCode,
                                 int[] initialVersion,
                                 int initialVersionCodeIncrement,
                                 int[] initialVersionIncrements,
                                 String initialReleaseNotes) {

        int[] versionIncrements
        int versionCodeIncrement
        String releaseNotes = null

        boolean cancelled = true

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

                def updateVersion = {

                    String global = initialVersion[0] + versionIncrements[0]
                    String major = initialVersion[1] + versionIncrements[1]
                    String minor = initialVersion[2] + versionIncrements[2]
                    String build = initalVersionCode + versionCodeIncrement

                    newVersionLabel.text = "New version: $global.$major.$minor ($build)"
                }

                def resetIncrements = {
                    versionIncrements = initialVersionIncrements.collect { it }
                    versionCodeIncrement = initialVersionCodeIncrement
                }

                resetIncrements()

                vbox {

                    hbox {
                        label text: "Current version: ${initialVersion.join(".")} ($initalVersionCode)"
                    }

                    hbox {
                        label text: "What version changes?"
                    }


                    hbox {

                        button text: '1.0.0', actionPerformed: {
                            versionIncrements[0] = versionIncrements[0] + 1
                            updateVersion() // Close dialog
                        }

                        button text: '0.1.0', actionPerformed: {
                            versionIncrements[1] = versionIncrements[1] + 1
                            updateVersion() // Close dialog
                        }

                        button text: '0.0.1', actionPerformed: {
                            versionIncrements[2] = versionIncrements[2] + 1
                            updateVersion() // Close dialog
                        }

                    }

                    hbox {

                        button text: 'x.0.0', actionPerformed: {
                            versionIncrements[0] = -initialVersion[0]
                            updateVersion()
                        }

                        button text: '0.x.0', actionPerformed: {
                            versionIncrements[1] = -initialVersion[1]
                            updateVersion()
                        }

                        button text: '0.0.x', actionPerformed: {
                            versionIncrements[2] = -initialVersion[2]
                            updateVersion()
                        }

                    }

                    hbox {

                        button text: 'Build number', actionPerformed: {
                            versionCodeIncrement++
                            updateVersion()
                        }

                        button text: 'Nothing Changed', actionPerformed: {
                            versionIncrements[0] = 0
                            versionIncrements[1] = 0
                            versionIncrements[2] = 0
                            versionCodeIncrement = 0

                            updateVersion()
                        }

                        button text: 'Reset', actionPerformed: {

                            resetIncrements()
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

                    releaseNotesInput = textArea text: initialReleaseNotes, preferredSize: [400, 300]

                    hbox {

                        button text: 'Ok', actionPerformed: {

                            releaseNotes = releaseNotesInput.text

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

        return new Result(versionIncrements, versionCodeIncrement, releaseNotes)

    }

    static class Result {

        private int[] increments
        private int versionCodeIncrement
        private String releaseNotes

        Result(int[] increments, int versionCodeIncrement, @Nullable String releaseNotes) {
            this.increments = increments
            this.versionCodeIncrement = versionCodeIncrement
            this.releaseNotes = releaseNotes
        }

        int[] getIncrements() {
            return increments
        }

        int getVersionCodeIncrement() {
            return versionCodeIncrement
        }

        String getReleaseNotes() {
            return releaseNotes
        }

    }

}