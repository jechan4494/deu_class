package model;

public class Professor {
    private String name;
    private int class_id;

    public Professor(String name, int class_id) {
        this.name = name;
        this.class_id = class_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getClass_id() {
        return class_id;
    }

    public void setClass_id(int class_id) {
        this.class_id = class_id;
    }
}
