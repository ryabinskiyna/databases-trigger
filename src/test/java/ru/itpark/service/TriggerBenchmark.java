package ru.itpark.service;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import org.junit.Test;
import ru.itpark.model.House;


public class TriggerBenchmark extends AbstractBenchmark {

    @BenchmarkOptions(benchmarkRounds = 20, warmupRounds = 10)
    @Test
    public void getDataChanges() {

        DeterminingDBChangesService service = new DeterminingDBChangesService("jdbc:sqlite:db.sqlite");
        service.drop("houses");
        service.drop("houses_log");
        service.init();
        service.initTrigger();


        int count = 100;

        for (int i = 0; i < count; i++) {
            service.insert(new House(0, i, "D" + i, "U" + i));
        }
        service.getAllChanges("2020-01-31");


    }
}