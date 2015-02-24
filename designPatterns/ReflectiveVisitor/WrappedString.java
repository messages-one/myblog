package sil;

public class WrappedString implements ReflectiveElement {
    private String name;
    private String wString;
    public WrappedString(String name, String wString) {
        this.name = name;
        this.wString = wString;
    }
    public String getName() {
        return name;
    }
    public String getwString() {
        return wString;
    }
    @Override
    public void accept(ReflectiveVisitor visitor) {
       visitor.visit(this);
    }
    @Override
    public String toString() {
        return name + "-" + wString;
    }
}
