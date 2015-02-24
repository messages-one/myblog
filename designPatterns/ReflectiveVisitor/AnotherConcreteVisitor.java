package sil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AnotherConcreteVisitor implements ReflectiveVisitor {
    @Override
    public void visit(Object o) {
        try {
            Method visitMethod = this.getClass().getMethod("visit", new Class[] { o.getClass() });
            if (visitMethod == null) {
                defaultVisit(o);
            } else {
                visitMethod.invoke(this, new Object[] { o });
            }
        } catch (NoSuchMethodException e) {
            this.defaultVisit(o);
        } catch (InvocationTargetException e) {
            this.defaultVisit(o);
        } catch (IllegalAccessException e) {
            this.defaultVisit(o);
        }
    }
    public void defaultVisit(Object o) {
        //System.out.println(o.toString());
    }
    public void visit(WrappedDouble wDouble){
        //System.out.println(wDouble.getName() + "-" + wDouble.getWDouble());
        // do sth
    } 
}