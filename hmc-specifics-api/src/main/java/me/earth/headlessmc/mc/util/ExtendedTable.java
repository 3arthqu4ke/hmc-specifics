package me.earth.headlessmc.mc.util;

import me.earth.headlessmc.api.util.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    // would be nice if we could just override String.getLength from Table instead of having to copy the build methods
    public String buildCalculatingAnsiWidth() {
        List<Integer> columnWidths = new ArrayList<>(this.columns.size());
        List<List<String>> columns = this.columns.stream().map(e -> {
            List<String> entries = this.elements.isEmpty()
                    ? new ArrayList<>(Collections.singletonList("-"))
                    : this.elements.stream()
                        .map(e.function)
                        .map(str -> str == null ? "null" : str)
                        .collect(Collectors.toList());
            entries.add(0, String.valueOf(e.name));
            // let's hope the Terminal uses a fixed-width font
            columnWidths.add(entries.stream()
                    .map(ExtendedTable::getLengthWithoutAnsi)
                    .max(Integer::compareTo)
                    .get());

            return entries;
        }).collect(Collectors.toList());
        return buildWithoutAnsiLength(columns, columnWidths);
    }

    private String buildWithoutAnsiLength(List<List<String>> columns, List<Integer> columnWidths) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; !columns.isEmpty() && i < columns.get(0).size(); i++) {
            for (int j = 0; j < columns.size(); j++) {
                String entry = columns.get(j).get(i);
                int width = columnWidths.get(j);
                builder.append(entry);
                // last column doesn't need to be filled up
                if (j == columns.size() - 1) {
                    continue;
                }

                for (int k = 0; k < width - getLengthWithoutAnsi(entry) + 3; k++) {
                    builder.append(' ');
                }
            }

            // no need to append a linebreak on the last row
            if (i < columns.get(0).size() - 1) {
                builder.append('\n');
            }
        }

        return builder.toString();
    }

    public static int getLengthWithoutAnsi(String string) {
        return stripAnsi(string).length();
    }

    public static String stripAnsi(String string) {
        return string.replaceAll("\u001B\\[[;\\d]*m", "");
    }

}
