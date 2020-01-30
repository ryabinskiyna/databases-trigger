package ru.itpark;

import ru.itpark.model.House;
import ru.itpark.service.DeterminingDBChangesService;

public class Main {
    public static void main(String[] args) {

        DeterminingDBChangesService service = new DeterminingDBChangesService("jdbc:sqlite:db.sqlite");
        service.drop("houses");
        service.drop("houses_log");
        service.init();
        service.initTrigger();
        service.insert(new House(0,1,"d1","u1"));
        System.out.println(service.getAllChanges("2020-01-30"));
        service.update(new House(1,2,"d1","u1"));
        System.out.println(service.getAllChanges("2020-01-30"));



    }
}
