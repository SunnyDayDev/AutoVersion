package me.sunnydaydev.autoversion

class AutoIncrement {

    private String name

    private String[] autoIncrementOnTasks = []

    private int[] increments = [0, 0, 0, 1]

    AutoIncrement(String name) {
        this.name = name
    }

    AutoIncrement setAutoIncrementOnTasks(String[] autoIncrementOnTasks) {
        println "Set autoIncrementOnTasks: $autoIncrementOnTasks"
        this.autoIncrementOnTasks = autoIncrementOnTasks
        return this
    }

    AutoIncrement setIncrements(int[] increments) {
        println "Set increments: $increments"
        this.increments = increments
        return this
    }

    String[] getAutoIncrementOnTasks() {
        return autoIncrementOnTasks
    }

    int[] getIncrements() {
        return increments
    }

}