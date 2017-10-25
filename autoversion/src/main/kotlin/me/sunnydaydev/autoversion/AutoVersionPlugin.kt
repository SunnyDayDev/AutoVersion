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
            checkTaskDependedIncrements(extension)
            checkIncrements(extension.increments.toList())
        }

        app.applicationVariants.all { variant ->

            val variantAvailableIncrements = extension.increments
                    .filter { canIncrement(it, variant) }

            val rootTask = if (variantAvailableIncrements.isNotEmpty()) {

                val maxPriority = variantAvailableIncrements.map { it.priority }.max()

                val increment = variantAvailableIncrements.single { it.priority == maxPriority }

                project.tasks.create(
                        "increment${increment.name.capitalize()}On${variant.name.capitalize()}",
                        IncrementTask::class.java
                ) {

                    it.group = AutoVersionPlugin.GROUP

                    it.attachedIncrement = increment
                    it.incrementer = incrementer

                    variant.preBuild.dependsOn(it)

                }

            } else {
                variant.preBuild
            }

            val prepareTask = project.tasks.create(
                    "prepareAutoVersionFor${variant.name.capitalize()}",
                    PrepareTask::class.java
            ) {

                it.group = AutoVersionPlugin.GROUP
                it.incrementer = incrementer
                it.tasksDependedIncrements = extension.increments
                        .filter { it.tasks.isNotEmpty() }
                it.variant = variant

            }

            rootTask.dependsOn(prepareTask)

        }

    }

    private fun canIncrement(increment: Increment, variant: ApplicationVariant): Boolean {
        return increment.variants.contains(variant.name) ||
                increment.buildTypes.contains(variant.buildType.name) ||
                increment.flavours.any { variant.productFlavors.map { it.name }.contains(it) }
    }

    private fun checkTaskDependedIncrements(extension: AutoVersionExtension) {

        val tasks = extension.increments
                .map { it.tasks.distinct() }
                .transform {
                    arrayListOf<String>()
                            .apply { it.forEach { this.addAll(it) } }
                            .toList()
                }

        val uniqueNames = mutableSetOf<String>()

        tasks.forEach {
            if (uniqueNames.contains(it)) {
                throw IllegalStateException(
                        "More than one AutoVersion increment depend to task $it"
                )
            } else {
                uniqueNames.add(it)
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
                                "this format \"X.X.X\" where X can be integer or \"x\", " +
                                "but not all is \"x\"."
                )

            }

        }

    }

}