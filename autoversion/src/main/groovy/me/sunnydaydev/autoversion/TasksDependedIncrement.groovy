package me.sunnydaydev.autoversion

class TasksDependedIncrement extends Increment {

    private String[] useOnTasks = []

    TasksDependedIncrement(String name) {
        super(name)
    }

    TasksDependedIncrement useOnTasks(String[] useOn) {
        this.useOnTasks = useOn
        return this
    }

    String[] getUseOnTasks() {
        return useOnTasks
    }

}