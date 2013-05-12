package com.songbook.core.util;

public class DataEntry {
    private final String name;
    private final byte[] data;


    public DataEntry(String name, byte[] data) {
        this.name = name;
        this.data = data;
    }


    public String getName() {
        return name;
    }


    public byte[] getData() {
        return data;
    }
}