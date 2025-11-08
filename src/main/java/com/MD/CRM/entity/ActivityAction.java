package com.MD.CRM.entity;

public enum ActivityAction {
    CREATE,
    EDIT,
    UPDATE,
    LOGIN,
    CALL,
    EMAIL,
    MEETING,
    OTHER;

    public ActivityType getType() {
        switch (this) {
            case CREATE:
            case EDIT:
            case UPDATE:
            case LOGIN:
                return ActivityType.LOG;
            case CALL:
            case EMAIL:
            case MEETING:
            case OTHER:
            default:
                return ActivityType.INTERACTION;
        }
    }
}

