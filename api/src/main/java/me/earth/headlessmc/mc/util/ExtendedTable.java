package me.earth.headlessmc.mc.util;

import me.earth.headlessmc.util.Table;

import java.util.function.Function;

public class ExtendedTable<T> extends Table<T> {
    public ExtendedTable<T> withColumn(String name,
                                       Function<T, String> column) {
        super.withColumn(name, column);
        return this;
    }

    public ExtendedTable<T> addAll(Iterable<T> elements) {
        super.addAll(elements);
        return this;
    }

    public ExtendedTable<T> withInt(String name, Function<T, Integer> column) {
        columns.add(new Column<>(name, t -> column.apply(t).toString()));
        return this;
    }

    public ExtendedTable<T> withBool(String name, Function<T, Boolean> column) {
        columns.add(new Column<>(name, t -> column.apply(t).toString()));
        return this;
    }

    public ExtendedTable<T> insert(String name,
                                   String at,
                                   Function<T, String> column) {
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).name.equalsIgnoreCase(at)) {
                columns.add(i, new Column<>(name, column));
                return this;
            }
        }

        return this;
    }

}
