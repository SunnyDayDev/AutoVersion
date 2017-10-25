package me.sunnydaydev.autoversion

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by sunny on 24.10.2017.
 * mail: mail@sunnydaydev.me
 */
class AutoVersionPlugin: Plugin<Project> {

    companion object {
        val GROUP = "Auto version"
    }

    override fun apply(project: Project) {

        if (project.plugins.hasPlugin(AppPlugin::class.java)) {

            val app = project.extensions.getByType(AppExtension::class.java) ?: return

            configure(project, app)

        }

    }

    private fun configure(project: Project, app: AppExtension) {

        val store = AutoVersionStore(project)
        val incrementer = Incrementer(store, app.applicationVariants.toList())

        val extension = project.extensions.create(
                "autoVersion",
                AutoVersionExtension::class.java,
                store,
                project.container(Increment::class.java)
        )

        project.afterEvaluate {

            checkTaskDependedIncrements(extension.increments.toList())
            checkIncrements(extension.increments.toList())

            extension.increments.forEach { increment ->

                project.tasks.create(
                        "increment${increment.name.capitalize()}",
                        SimpleIncrementTask::class.java
                ) {

                    it.group = GROUP

                    it.increment = increment
                    it.incrementer = incrementer

                }

            }

        }

        app.applicationVariants.all { variant ->

            val variantAvailableIncrements = extension.increments
                    .filter { canIncrement(it, variant) }

            val incrementTask = if (variantAvailableIncrements.isNotEmpty()) {

                val maxPriority = variantAvailableIncrements.map { it.priority }.max()

                val increment = variantAvailableIncrements.single { it.priority == maxPriority }

                project.tasks.create(
                        "increment${increment.name.capitalize()}On${variant.name.capitalize()}",
                        IncrementTask::class.java
                ) {

                    it.group = AutoVersionPlugin.GROUP

                    it.attachedIncrement = increment
                    it.incrementer = incrementer
                    it.variant = variant

                    variant.preBuild.dependsOn(it)

                }

            } else { null }

            val prepareTask = project.tasks.create(
                    "prepareAutoVersionFor${variant.name.capitalize()}",
                    PrepareTask::class.java
            ) {

                it.group = AutoVersionPlugin.GROUP
                it.incrementer = incrementer
                it.tasksDependedIncrements = extension.increments
                        .filter { it.tasks.isNotEmpty() }
                it.variant = variant
                it.variantIncrementTask = incrementTask

            }

            if (incrementTask == null) {
                variant.preBuild.dependsOn(prepareTask)
            } else {
                incrementTask.dependsOn(prepareTask)
                variant.preBuild.dependsOn(incrementTask)
            }

        }

    }

    private fun canIncrement(increment: Increment, variant: ApplicationVariant): Boolean {
        return increment.variants.contains(variant.name) ||
                increment.buildTypes.contains(variant.buildType.name) ||
                (variant.flavorName?.let { increment.flavors.contains(it) } ?: false)
    }

    private fun checkTaskDependedIncrements(increments: List<Increment>) {

        val tasks = increments
                .map { it.tasks.distinct() }
                .transform {
                    arrayListOf<String>()
                            .apply { it.forEach { this.addAll(it) } }
                            .toList()
                }

        val uniqueNames = mutableSetOf<String>()

        tasks.forEach { taskName ->
            if (uniqueNames.contains(taskName)) {

                val sameNames = increments
                        .filter { it.tasks.contains(taskName) }.joinToString { it.name }

                throw IllegalStateException(
                        "More than one AutoVersion increment depend to task $taskName: $sameNames"
                )

            } else {
                uniqueNames.add(taskName)
            }
        }

    }

    private fun checkIncrements(increments: List<Increment>) {

        increments.forEach {

            if (it.buildIncrement < 0) {
                throw IllegalStateException(
                        "Incorrect increment for ${it.name}: Build increment must be more than 0."
                )
            }

            val versionNameIncrement = it.versionNameIncrement
            val parts = versionNameIncrement.split(".")

            if (parts.size != 3 ||
                    !parts.all { it == "x" || it.isOnlyDigits() } ||
                    parts.all { it == "x" }) {

                throw IllegalStateException(
                        "Incorrect increment for ${it.name}: Version name increment must have " +
                                "this format \"X.X.X\" where X can be " +
                                "any integer or \"x\"(set to 0), but not all is \"x\"."
                )

            }

        }

    }

}