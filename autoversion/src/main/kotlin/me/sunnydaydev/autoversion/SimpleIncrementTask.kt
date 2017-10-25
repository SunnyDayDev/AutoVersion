package me.sunnydaydev.autoversion

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Created by sunny on 25.10.2017.
 * mail: mail@sunnydaydev.me
 */
open class SimpleIncrementTask : DefaultTask() {

    internal lateinit var increment: Increment
    internal lateinit var incrementer: Incrementer

    @TaskAction
    fun run() {

        incrementer.executeIncrement(increment)

    }

}