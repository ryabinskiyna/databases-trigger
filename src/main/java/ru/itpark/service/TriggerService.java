package ru.itpark.service;

import ru.itpark.model.House;
import ru.itpark.util.JdbcTemplate;

import java.util.List;

public class TriggerService {
    private final String dataSource;

    public TriggerService(String dataSource) {
        this.dataSource = dataSource;
    }

    public void init() {
        JdbcTemplate.executeInit(
                dataSource,
                "CREATE TABLE IF NOT EXISTS houses(id INTEGER PRIMARY KEY, price INTEGER NOT NULL, district TEXT NOT NULL, underground TEXT NOT NULL);"
        );
    }

    public House insert(House house) {
        JdbcTemplate.executeCreateQuery(
                dataSource,
                "INSERT INTO houses (price, district, underground) VALUES(?, ?, ?);",
                pstmt -> {
                    pstmt.setInt(1, house.getPrice());
                    pstmt.setString(2, house.getDistrict());
                    pstmt.setString(3, house.getUnderground());
                }
        );
        return house;
    }

    public House update(House house) {
        JdbcTemplate.executeCreateQuery(
                dataSource,
                "UPDATE houses SET price = ?, district = ?, underground = ? WHERE id = ?;",
                pstmt -> {
                    pstmt.setInt(1, house.getPrice());
                    pstmt.setString(2, house.getDistrict());
                    pstmt.setString(3, house.getUnderground());
                    pstmt.setInt(4, house.getId());
                }
        );
        return house;
    }

    public void initTrigger() {
        JdbcTemplate.executeInit(
                dataSource,
                "CREATE TABLE IF NOT EXISTS houses_trigger(new_id INTEGER PRIMARY KEY, new_price INTEGER NOT NULL, new_district TEXT NOT NULL, new_underground TEXT NOT NULL, date TEXT NOT NULL, operation TEXT NOT NULL);"
        );
        JdbcTemplate.executeInit(
                dataSource,
                "CREATE TRIGGER IF NOT EXISTS after_insert AFTER INSERT ON houses BEGIN INSERT INTO houses_trigger(new_id, new_price, new_district, new_underground, date, operation) VALUES (NEW.id, NEW.price, NEW.district, NEW.underground, datetime('now'), 'ins');END;"
        );
        JdbcTemplate.executeInit(
                dataSource,
                "CREATE TRIGGER IF NOT EXISTS after_update AFTER UPDATE ON houses BEGIN INSERT INTO houses_trigger(new_id, new_price, new_district, new_underground, date, operation) VALUES (OLD.id, OLD.price, OLD.district, OLD.underground, datetime('now'), 'upd');END;"
        );
    }

    public List<House> getAllChanges(String date) {
        return JdbcTemplate.executeQuery(
                dataSource,
                "SELECT new_id, new_price, new_district, new_underground, date FROM houses_trigger WHERE date LIKE '" + date + "%'",
                rs -> new House(
                        rs.getInt("new_id"),
                        rs.getInt("new_price"),
                        rs.getString("new_district"),
                        rs.getString("new_underground"),
                        rs.getString("date")

                )
        );
    }
}
