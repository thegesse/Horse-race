package com.geese.horserace.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Horse {

    private int id;
    private String name;
    private int speed;
    private int lengthRan = 0;
}
