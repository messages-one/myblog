package reflective.visitor;

public class WrappedDouble implements ReflectiveElement {
    private String name;
    private double wDouble;
    public WrappedDouble(String name, double wDouble) {
        this.name = name;
        this.wDouble = wDouble;
    }
    public String getName() {
        return name;
    }
    public double getWDouble() {
        return wDouble;
    }
    @Override
    public void accept(ReflectiveVisitor visitor) {
        visitor.visit(this);
    } 
}