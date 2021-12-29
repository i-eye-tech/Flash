package com.ieye.model.core;

import lombok.Data;

@Data
public class Wait {

    // In seconds

    private int timeout;
    private int delay;
    private int interval = 1;

}
