package com.cts.reny.interview.scenario.three;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IdBasedFolder extends Folder {
    private String id;
    private List<String> parentIds = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getParentIds() {
        return Collections.unmodifiableList(parentIds);
    }

    public void setParentIds(List<String> parentIds) {
        this.parentIds = parentIds;
    }
}
