package reflective.visitor;

public interface ReflectiveElement {
    void accept(ReflectiveVisitor visitor);
}