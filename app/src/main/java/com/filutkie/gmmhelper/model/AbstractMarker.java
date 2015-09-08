package com.filutkie.gmmhelper.model;

public abstract class AbstractMarker {

    protected static final int TYPE_MARKER_DEFAULT = 1;
    protected static final int TYPE_MARKER_PATH = 2;
    protected static final int TYPE_MARKER_TEMP = 3;

    public abstract int getType();
}
