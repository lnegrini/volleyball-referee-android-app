package com.tonkar.volleyballreferee.interfaces;

public enum PositionType {

    POSITION_1, POSITION_2, POSITION_3, POSITION_4, POSITION_5, POSITION_6, BENCH;

    public PositionType previousPosition() {
        final PositionType next;

        switch (this) {
            case POSITION_1:
                next = POSITION_2;
                break;
            case POSITION_2:
                next = POSITION_3;
                break;
            case POSITION_3:
                next = POSITION_4;
                break;
            case POSITION_4:
                next = POSITION_5;
                break;
            case POSITION_5:
                next = POSITION_6;
                break;
            case POSITION_6:
                next = POSITION_1;
                break;
            default:
            case BENCH:
                next = BENCH;
                break;
        }

        return next;
    }

    public PositionType nextPosition() {
        final PositionType previous;

        switch (this) {
            case POSITION_1:
                previous = POSITION_6;
                break;
            case POSITION_2:
                previous = POSITION_1;
                break;
            case POSITION_3:
                previous = POSITION_2;
                break;
            case POSITION_4:
                previous = POSITION_3;
                break;
            case POSITION_5:
                previous = POSITION_4;
                break;
            case POSITION_6:
                previous = POSITION_5;
                break;
            default:
            case BENCH:
                previous = BENCH;
                break;
        }

        return previous;
    }

    public boolean isAtTheBack() {
        final boolean back;

        switch (this) {
            case POSITION_1:
                back = true;
                break;
            case POSITION_2:
                back = false;
                break;
            case POSITION_3:
                back = false;
                break;
            case POSITION_4:
                back = false;
                break;
            case POSITION_5:
                back = true;
                break;
            case POSITION_6:
                back = true;
                break;
            default:
            case BENCH:
                back = false;
                break;
        }

        return back;
    }

    public PositionType oppositePosition() {
        final PositionType opposite;

        switch (this) {
            case POSITION_1:
                opposite = POSITION_4;
                break;
            case POSITION_2:
                opposite = POSITION_5;
                break;
            case POSITION_3:
                opposite = POSITION_6;
                break;
            case POSITION_4:
                opposite = POSITION_1;
                break;
            case POSITION_5:
                opposite = POSITION_2;
                break;
            case POSITION_6:
                opposite = POSITION_3;
                break;
            default:
            case BENCH:
                opposite = BENCH;
                break;
        }

        return opposite;
    }
}
