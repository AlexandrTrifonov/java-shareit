package ru.practicum.shareit.booking;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static State toState(String state) {
        return State.valueOf(state);
    }
}
