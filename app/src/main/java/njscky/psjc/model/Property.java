package njscky.psjc.model;

public class Property {
    public String name;
    public String value;
    public boolean enable;

    public Property() {
        this("", "");
    }

    public Property(String name, String value) {
        this(name, value, false);
    }

    public Property(String name, String value, boolean enable) {
        this.name = name;
        this.value = value;
        this.enable = enable;
    }
}
