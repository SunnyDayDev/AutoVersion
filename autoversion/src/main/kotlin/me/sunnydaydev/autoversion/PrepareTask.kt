package me.sunnydaydev.autoversion

import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Created by sunny on 25.10.2017.
 * mail: mail@sunnydaydev.me
 */
open class PrepareTask : DefaultTask() {

    internal lateinit var tasksDependedIncrements: List<Increment>
    internal lateinit var incrementer: Incrementer
    internal lateinit var variant: ApplicationVariant
    internal var variantIncrementTask: IncrementTask? = null

    private val willAssemble get() = project.gradle.taskGraph.allTasks
            .any { it.name == "assemble${variant.name.capitalize()}" }

    @TaskAction
    fun prepare() {

        val prepareTasks = project.gradle.taskGraph.allTasks
                .mapNotNull { it as? PrepareTask }
                .filter { it.willAssemble }

        if (this !== prepareTasks.firstOrNull()) return

        val availableIncrementTasks = prepareTasks.map { it.variantIncrementTask }

        val allTasks = project.gradle.taskGraph.allTasks
        val allTasksNames = allTasks.map { it.name }
        val incrementTasks = allTasks
                .mapNotNull { it as? IncrementTask }
                .filter { availableIncrementTasks.contains(it) }

        val incrementTaskIncrements = incrementTasks.map { it.attachedIncrement }

        val incrementsByTaskName = tasksDependedIncrements
                .filter { it.tasks.any { allTasksNames.contains(it) } }

        val increments = (incrementTaskIncrements + incrementsByTaskName).distinct()

        val increment = increments.maxBy { it.priority } ?: return

        checkSingle(increment, increments)

        if (incrementTasks.isEmpty()) {

            incrementer.executeIncrement(increment)

        } else {

            incrementTasks.first().incrementForExecution = increment
            incrementTasks.drop(1).forEach { it.incrementForExecution = null }

        }

    }

    private fun checkSingle(increment: Increment, increments: List<Increment>) {

        val sameIncrements = increments.filter { it.priority == increment.priority }

        if (sameIncrements.size > 1) {

            throw IllegalStateException(
                    "More than one increment with same priority available for current task graph:" +
                            " ${sameIncrements.joinToString { it.name }}"
            )

        }

    }

}