package org.rhok.pdx.web;

enum LEVEL {
    NONE, LOW, GOOD;

    private static final int MEDIUM_THRESHOLD = 8;


    public static LEVEL from(double signal) {
        if (signal < 1) {
            return LEVEL.NONE;
        }
        if (signal < MEDIUM_THRESHOLD) {
            return LEVEL.LOW;
        }
        return LEVEL.GOOD;
    }
}




