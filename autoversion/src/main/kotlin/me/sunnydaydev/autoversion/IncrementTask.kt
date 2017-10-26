package me.sunnydaydev.autoversion

import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Created by sunny on 25.10.2017.
 * mail: mail@sunnydaydev.me
 */
open class IncrementTask : DefaultTask() {

    internal lateinit var attachedIncrement: Increment
    internal lateinit var incrementer: Incrementer
    internal lateinit var variant: ApplicationVariant

    internal var incrementForExecution: Increment? = null

    @TaskAction
    fun run() {

        if (project.gradle.taskGraph.allTasks.last() === this) {

            incrementer.executeIncrement(attachedIncrement)

        } else {

            val increment = incrementForExecution ?: return
            incrementer.executeIncrement(increment)

        }

    }

}