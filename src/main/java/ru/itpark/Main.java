package ru.itpark;

import ru.itpark.model.House;
import ru.itpark.service.TriggerService;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        TriggerService triggerService = new TriggerService("jdbc:sqlite:db.sqlite");
        triggerService.init();
        triggerService.insert(new House(0, 1, "D", "U"));
        triggerService.initTrigger();
        triggerService.insert(new House(0, 2, "D2", "U2"));
        triggerService.update(new House(2, 3, "d2", "u2"));
        System.out.println(triggerService.getAllChanges("2020-01-29"));
        List<House> houses = triggerService.getAllChanges("2020-01-29");
        System.out.println(houses.size());


    }
}
