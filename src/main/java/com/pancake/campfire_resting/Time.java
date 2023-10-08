package com.pancake.campfire_resting;

public enum Time {
    DAY(1,1000),
    MIDNIGHT(2,18000),
    NIGHT(3,13000),
    NOON(4,6000),
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
