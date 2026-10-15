package method;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OrderCounter {
    public Map<String, Integer> countPriority(List<String> list) {
        Map<String, Integer> cnt = new HashMap<>();
        for (var S : list) {
            if (Objects.equals(S, "LOW")) {
                cnt.merge("LOW", 1, Integer::sum);
            } else if (Objects.equals(S, "NORMAL")) {
                cnt.merge("NORMAL", 1, Integer::sum);
            } else if (Objects.equals(S, "HIGH")) {
                cnt.merge("HIGH", 1, Integer::sum);
            } else {
                cnt.merge("OTHER", 1, Integer::sum);
            }
        }
        return cnt;
    }

}
