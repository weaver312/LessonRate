package com.weaverhong.lesson.lessonrate;

import java.util.ArrayList;
import java.util.List;

public class RateLab {
    public static List<Rate> list;
    static {
        list = new ArrayList<>();
        list.add(new Rate("nmsl", "123"));
        list.add(new Rate("nbsl", "456"));
    }
    public static void delete(String name) {
        for (Rate r : list) {
            if (r.name.equals(name)) {
                list.remove(r);
                return;
            }
        }
    }
}
