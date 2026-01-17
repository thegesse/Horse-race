package com.geese.horserace.controller;

import com.geese.horserace.model.Bet;
import com.geese.horserace.model.Horse;
import jakarta.annotation.PostConstruct;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
public class PageController {
    private final List<Horse> horseList = new ArrayList<>();
    List<Integer> results = new ArrayList<>();
    private Bet currentBet;
    private int raceLength = 440;

    @PostConstruct
    public void init() {
        horseList.add(new Horse(1, "Honse", 40, 0));
        horseList.add(new Horse(2, "Hon", 42, 0));
        horseList.add(new Horse(3, "Se", 41, 0));
        horseList.add(new Horse(4, "Biscuit", 43, 0));
        horseList.add(new Horse(5, "Horse-1", 40, 0));
        horseList.add(new Horse(6, "NumberOneCrate", 41, 0));
    }

    @GetMapping("/racers")
    public List<Horse> getRacers() {
        return horseList;
    }

    @PostMapping("/gamble/{horseId}/{value}")
    public String collectBet(@PathVariable int horseId, @PathVariable int value) {
        currentBet = new Bet();
        currentBet.setHorseId(horseId);
        currentBet.setAmount(value);
        currentBet.setAmount(value);
        currentBet.setWon(false);
        return "Bet placed on horse " + horseId + " for $" + value;
    }

    public void race() {
        Random r = new Random();
        for(Horse horse : horseList) {
            int runThisTurn = r.nextInt(5);
            horse.setLengthRan(horse.getLengthRan() + runThisTurn + horse.getSpeed());
        }
    }

    @GetMapping("/bet-status")
    public Bet getBetStatus() {
        return currentBet;
    }

    @PostMapping("/race/{length}")
    public List<Integer> startRace(@PathVariable int length) {
        this.raceLength = length;
        results.clear();

        for(Horse horse : horseList) {
            horse.setLengthRan(0);
        }

        while (horseList.stream().anyMatch(h -> h.getLengthRan() < this.raceLength)) {
            race();
        }

        List<Horse> sorted = new ArrayList<>(horseList);
        sorted.sort((h1, h2) -> Integer.compare(h2.getLengthRan(), h1.getLengthRan()));

        for (Horse horse : sorted) {
            results.add(horse.getId());
        }
        if (currentBet != null && !results.isEmpty()) {
            if (currentBet.getHorseId() == results.get(0)) {
                currentBet.setWon(true);
            }
        }
        return results;
    }

    @GetMapping("/results")
    public List<Integer> getResults() {
        return results;
    }

    @PostMapping("/reset")
    public List<Horse> resetTournament() {
        this.horseList.clear();
        this.init();
        return this.horseList;
    }
}
