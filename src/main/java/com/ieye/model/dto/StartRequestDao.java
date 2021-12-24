package com.ieye.model.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class StartRequestDao {

    @NonNull
    private String projectId;

    @NonNull
    private String testId;

    @Override
    public String toString() {
        return String.format("Project Id: %s, Test Id: %s", projectId, testId);
    }

}
