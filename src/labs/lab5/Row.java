package labs.lab5;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Row {

    public Map<String, String> row;

    public Row(List<String> Vt) {
        row = new HashMap<>();
        Vt.forEach(t -> {
            row.put(t, "");
        });
        row.put("$", "");
    }

    @Override
    public String toString() {
        return row.keySet().stream()
                .map(k -> String.format("%-5s", row.get(k))).collect(Collectors.joining(" | "));
    }

    public void printHeader() {
        System.out.println("     " + row.keySet().stream()
                .map(k -> String.format("%-5s", k)).collect(Collectors.joining(" | ")));
    }
}
