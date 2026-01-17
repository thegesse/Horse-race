package com.geese.horserace.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Bet {
    private int amount;
    private int horseId;
    private boolean won;
}
