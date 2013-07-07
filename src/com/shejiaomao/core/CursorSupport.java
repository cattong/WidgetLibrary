package com.shejiaomao.core;

public interface CursorSupport {

    boolean hasPrevious();

    long getPreviousCursor();

    boolean hasNext();

    long getNextCursor();
}
