package labs.lab5;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        return "" + row + "\n";
    }
}
