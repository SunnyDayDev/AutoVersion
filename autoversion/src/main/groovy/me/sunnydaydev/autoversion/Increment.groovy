package me.sunnydaydev.autoversion

class Increment {

    public static String DEFAULT_NAME = "defaultIncrement"

    private String name

    private int[] increments = [0, 0, 0]

    private int buildIncrement = 1

    static Increment defaultIncrement() {
        new Increment(DEFAULT_NAME)
    }

    Increment(String name) {
        this.name = name
    }

    Increment increments(int[] increments) {
        setIncrements(increments)
        return this
    }

    Increment buildIncrement(int buildIncrement) {
        setBuildIncrement(buildIncrement)
        return this
    }

    void setIncrements(int[] increments) {
        this.increments = increments
    }

    void setBuildIncrement(int buildIncrement) {
        this.buildIncrement = buildIncrement
    }

    int[] getIncrements() {
        return increments
    }

    int getBuildIncrement() {
        return buildIncrement
    }

    String getName() {
        return name
    }

}