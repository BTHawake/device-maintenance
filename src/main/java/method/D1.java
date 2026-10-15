package method;

import java.util.*;


public class D1 {
    public Map<String, Integer> handleList(List<Object> L) {

        Map<String,Integer>res = new HashMap<>();
        for (Object s : L) {
            if (Objects.equals(s, "ACTIVE")) {
                res.merge("ACTIVE", 1, Integer::sum);
            } else if (Objects.equals(s, "ARCHIVED")) {
                res.merge("ARCHIVED", 1, Integer::sum);
            } else {
                res.merge("OTHER", 1, Integer::sum);
            }
        }
        return res;
    }
}
