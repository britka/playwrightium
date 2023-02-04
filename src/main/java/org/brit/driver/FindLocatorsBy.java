package org.brit.driver;

public class FindLocatorsBy {
    public static String byId(String id){
        return "#" + id;
    }

    public static String byName(String name){
        return "[name='%s']".formatted(name);
    }
}
