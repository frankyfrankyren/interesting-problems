package com.cts.reny.interview.scenario.three;

/**
 * Generic folder applicable to both Id based folder structure or path based ones
 */
public abstract class Folder {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
