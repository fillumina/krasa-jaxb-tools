package com.sun.tools.xjc.addon.krasa;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * Substitute primitive types with their boxed types (for example, int becomes Integer).
 * A boxed boolean keeps its is-prefixed accessor and gains a get-prefixed accessor for JavaBeans readers.
 * <br>
 * The plugin is configured in {@code /resources/META-INF/services/com.sun.tools.xjc.Plugin} .
 * 
 * @author Vojtěch Krása
 */
public class PrimitiveFixerPlugin extends Plugin {

    public static final String PLUGIN_NAME = "XReplacePrimitives";

    private static final HashMap<String, Class<?>> NUMERIC_TYPE_MAP = new HashMap<String, Class<?>>();
    static {
        NUMERIC_TYPE_MAP.put("int", Integer.class);
        NUMERIC_TYPE_MAP.put("long", Long.class);
        NUMERIC_TYPE_MAP.put("boolean", Boolean.class);
        NUMERIC_TYPE_MAP.put("double", Double.class);
        NUMERIC_TYPE_MAP.put("float", Float.class);
        NUMERIC_TYPE_MAP.put("byte", Byte.class);
        NUMERIC_TYPE_MAP.put("short", Short.class);
    }

    @Override
    public String getOptionName() {
        return PLUGIN_NAME;
    }

    @Override
    public String getUsage() {
        return "-" + PLUGIN_NAME
                + "    :   Boxes primitive fields and accessors; boxed booleans keep isX and gain getX. " +
                "Define it before XhashCode or Xequals.  \n";
    }

    @Override
    public boolean run(Outline outline, Options opt, ErrorHandler errorHandler) throws SAXException {
        for (ClassOutline co : outline.getClasses()) {

            Map<String, JFieldVar> fields = co.implClass.fields();

            for (Map.Entry<String, JFieldVar> stringJFieldVarEntry : fields.entrySet()) {
                JFieldVar fieldVar = stringJFieldVarEntry.getValue();
                JType type = fieldVar.type();

                /*
                 * Exclude "serialVersionUID" from processing XReplacePrimitives as this will
                 * have no getter or setter defined.
                 */
                if ("serialVersionUID".equals(fieldVar.name())) {
                	continue;
                }

                if (type.isPrimitive()) {
                    Class<?> o = NUMERIC_TYPE_MAP.get(type.name());
                    if (o != null) {
                        JCodeModel jCodeModel = new JCodeModel();
                        JClass newType = jCodeModel.ref(o);
                        fieldVar.type(newType);
                        JMethod getter = getMethodsMap(MethodType.GETTER, fieldVar, co);
                        setReturnType(newType, getter);
                        if (o == Boolean.class && getter.name().startsWith("is")) {
                            String name = "get" + getter.name().substring(2);
                            boolean exists = co.implClass.methods().stream()
                                    .anyMatch(method -> method.name().equals(name)
                                            && method.listParams().length == 0);
                            if (!exists) {
                                co.implClass.method(JMod.PUBLIC, newType, name).body()._return(fieldVar);
                            }
                        }
                        setParameter(newType, getMethodsMap(MethodType.SETTER, fieldVar, co));
                    }
                }
            }
        }
        return true;
    }

    enum MethodType {
        GETTER, SETTER
    }

    private void setParameter(JClass newType, JMethod jMethod) {
        if (jMethod != null) {
            JVar jVar = jMethod.listParams()[0];
            jVar.type(newType);
        }
    }

    private void setReturnType(JType type, JMethod jMethod) {
        if (jMethod != null) {
            jMethod.type(type);
        }
    }

    /** Finds the generated accessor by the field reference in its body. */
    private JMethod getMethodsMap(MethodType type, JFieldVar field, ClassOutline co) {
        String getterBody = "return " + field.name() + ";";
        for (JMethod method : co.implClass.methods()) {
            String name = method.name();
            if (method.type().isPrimitive()) {
                if (MethodType.GETTER == type && (name.startsWith("is") || name.startsWith("get"))) {
                    JStatement o = (JStatement) method.body().getContents().get(0);
                    String s = getterBody(o);
                    if (s.trim().equals(getterBody)) {
                        return method;
                    }
                } else if (MethodType.SETTER == type && name.startsWith("set")) {
                    JStatement o = (JStatement) method.body().getContents().get(0);
                    String s = setterBody(o);
                    if (s.startsWith("this." + field.name() + " =")) {
                        return method;
                    }
                }
            }

        }
        throw new RuntimeException("Failed to find " + type + " for " + field.name() +
                ", disable XReplacePrimitives and report a bug");
    }

    public static String getterBody(JStatement jStatement) {
        StringWriter w = new StringWriter();
        jStatement.state(new JFormatter(w));
        return w.toString();
    }

    public static String setterBody(JStatement jStatement) {
        StringWriter w = new StringWriter();
        jStatement.state(new JFormatter(w));
        return w.toString();
    }
}
