package ru.itpark.service;

import ru.itpark.model.House;
import ru.itpark.util.JdbcTemplate;

import java.util.List;

/**
 * Мониторинг изменений в базах данных с помощью использования механизма триггеров
 *
 * @author Рябинский Никита
 */
public class DeterminingDBChangesService {
    private final String dataSource;

    /**
     * Constructor
     *
     * @param dataSource ссылка на базу данных
     */

    public DeterminingDBChangesService(String dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * метод для работы с таблицей для примера
     */

    public void init() {
        JdbcTemplate.executeInit(
                dataSource,
                "CREATE TABLE IF NOT EXISTS houses(id INTEGER PRIMARY KEY, price INTEGER NOT NULL, district TEXT NOT NULL, underground TEXT NOT NULL);"
        );
    }

    /**
     * метод для работы с таблицей для примера
     *
     * @param table название удаляемой таблицы
     */

    public void drop(String table) {
        JdbcTemplate.executeInit(
                dataSource,
                "DROP TABLE IF EXISTS " + table + ";"
        );
    }

    /**
     * метод для работы с таблицей для примера
     *
     * @param house объект класса House, который хотим добавить в таблицу houses
     */

    public void insert(House house) {
        String sql = "INSERT INTO houses (price, district, underground) VALUES(?, ?, ?);";

        JdbcTemplate.executeCreateQuery(dataSource, sql, pstmt -> {
            pstmt.setInt(1, house.getPrice());
            pstmt.setString(2, house.getDistrict());
            pstmt.setString(3, house.getUnderground());
        });
    }

    /**
     * метод для работы с таблицей для примера
     *
     * @param house объект класса House, который хотим обновить в таблице houses
     */

    public void update(House house) {
        String sql = "UPDATE houses SET price = ?, district = ?, underground = ? WHERE id = ?;";

        JdbcTemplate.executeCreateQuery(dataSource, sql, pstmt -> {
            pstmt.setInt(1, house.getPrice());
            pstmt.setString(2, house.getDistrict());
            pstmt.setString(3, house.getUnderground());
            pstmt.setInt(4, house.getId());
        });
    }

    /**
     * триггеры вставки и обновления. при срабатывании триггера данные попадают в таблицу houses_log
     */

    public void initTrigger() {
        JdbcTemplate.executeInit(
                dataSource,
                "CREATE TABLE IF NOT EXISTS houses_log(new_id INTEGER NOT NULL, new_price INTEGER NOT NULL, new_district TEXT NOT NULL, new_underground TEXT NOT NULL, date TEXT NOT NULL, operation TEXT NOT NULL);"
        );
        JdbcTemplate.executeInit(
                dataSource,
                "CREATE TRIGGER IF NOT EXISTS after_insert AFTER INSERT ON houses BEGIN INSERT INTO houses_log(new_id, new_price, new_district, new_underground, date, operation) VALUES (NEW.id, NEW.price, NEW.district, NEW.underground, datetime('now'), 'ins');END;"
        );
        JdbcTemplate.executeInit(
                dataSource,
                "CREATE TRIGGER IF NOT EXISTS after_update AFTER UPDATE ON houses BEGIN INSERT INTO houses_log(new_id, new_price, new_district, new_underground, date, operation) VALUES (OLD.id, OLD.price, OLD.district, OLD.underground, datetime('now'), 'upd');END;"
        );
    }

    /**
     * @param date день, за который интересуют изменения
     * @return собираем изменения в коллекцию LinkedList
     */

    public List<House> getAllChanges(String date) {
        return JdbcTemplate.executeQuery(
                dataSource,
                "SELECT new_id, new_price, new_district, new_underground, date FROM houses_log WHERE date LIKE '" + date + "%';",
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
