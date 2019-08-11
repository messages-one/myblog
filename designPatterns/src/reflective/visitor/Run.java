package reflective.visitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Run {

    public static void main(String[] args) {

        List heterogenList = new ArrayList();

        for (int i = 0; i < 10; i++) {
            heterogenList.add(new WrappedString("wString", "A"));//default visit method
            heterogenList.add(new WrappedDouble("wDouble", 1.0));
        }


        ReflectiveVisitor visitor = new AnotherConcreteVisitor();
        for (Iterator iterator = heterogenList.iterator(); iterator.hasNext(); ) {
            ReflectiveElement reflectiveElement = (ReflectiveElement) iterator.next();
            reflectiveElement.accept(visitor);
        }

    }

}
