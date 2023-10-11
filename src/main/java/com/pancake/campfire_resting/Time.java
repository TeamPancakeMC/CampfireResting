package com.pancake.campfire_resting;

public enum Time {
    EARLY_MORNING(1,0),
    NOON(2,6000),
    DUSK(3,12000),
    MIDNIGHT(4,18000),
    ;

    private final int select;
    private final int time;
    Time(int select, int time) {
        this.select = select;
        this.time = time;
    }

    public int getSelect() {
        return select;
    }

    public int getTime() {
        return time;
    }
}
