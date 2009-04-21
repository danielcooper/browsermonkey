package browsermonkey.utility;

import java.util.*;
import java.lang.reflect.*;

/**
 *
 * @author Paul Calcraft
 */
public class CachedVisitor {
    private Class superClass;
    private static Dictionary<Class, Dictionary<Class, Method>> superClassLookup = new Hashtable<Class, Dictionary<Class, Method>>();
    public CachedVisitor() {
        superClass = getClass();
        Dictionary<Class, Method> methodLookup = superClassLookup.get(superClass);
        if (methodLookup == null) { // First object of this super type being constructed in the life of the program.
            methodLookup = new Hashtable<Class, Method>();
            superClassLookup.put(superClass, methodLookup);
        }
    }

    public void visit(Object visitee) {
        Class visiteeClass = visitee.getClass();
        Dictionary<Class, Method> methodLookup = superClassLookup.get(superClass);
        Method method = methodLookup.get(visiteeClass);
        if (method == null) {
            try {
                method = superClass.getMethod("visitSub", visiteeClass);
            } catch (NoSuchMethodException ex) {
                throw new AssertionError("There is no method implemented in "+superClass+" for visiting "+visiteeClass+". Exception: "+ex);
            }
            methodLookup.put(visiteeClass, method);
        }
        try {
            method.invoke(this, visitee);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new AssertionError("Failed to invoke "+superClass+".visitSub("+visiteeClass+"); Exception: "+ex);
        }
    }
}