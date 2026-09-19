package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import java.math.BigDecimal;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * What {@link FieldHelper} answers for a field, per field type.
 *
 * <p>
 * The fixtures exercise these checks through XJC, so they only cover the shapes XJC produces for the
 * schemas in {@code src/test/resources}. This test builds the codemodel directly and covers the
 * shapes that matter to the checks but that no fixture reaches: a raw {@code List}, a nested
 * generic, arrays, a directly referenced class, a numeric element whose bounds are a different
 * range. It is a characterisation test - it fixes the behaviour a refactor of these checks must
 * preserve, because the difference between emitting an annotation and not emitting it is invisible
 * from the fixtures alone.
 *
 * Method names follow JUnit 4: this class does not extend the XJC harness, so surefire runs it.
 *
 * @author Francesco Illuminati
 */
public class FieldHelperTest {

    private static final BigDecimal VALUE = new BigDecimal("2");
    private static final BigDecimal INT_LIMIT = BigDecimal.valueOf(Integer.MIN_VALUE);

    private final JCodeModel codeModel = new JCodeModel();
    private JDefinedClass clazz;
    private int counter;

    /** The codemodel is built lazily: {@code _class} and {@code parseType} are checked exceptions. */
    private FieldHelper helperOf(String typeName) throws Exception {
        return new FieldHelper(fieldOf(codeModel.parseType(typeName)));
    }

    private JFieldVar fieldOf(JType type) throws Exception {
        if (clazz == null) {
            clazz = codeModel._class("probe.Fields");
        }
        return clazz.field(JMod.PRIVATE, type, "field" + counter++);
    }

    @Test
    public void stringIsAString() throws Exception {
        FieldHelper helper = helperOf("java.lang.String");
        assertTrue(helper.isString());
        assertFalse(helper.isStringList());
        assertFalse(helper.isList());
        assertFalse(helper.isArray());
    }

    @Test
    public void listOfStringIsAStringList() throws Exception {
        FieldHelper helper = helperOf("java.util.List<java.lang.String>");
        assertTrue(helper.isList());
        assertTrue(helper.isStringList());
        assertFalse(helper.isString());
    }

    @Test
    public void listOfNumbersIsAListButNotAStringList() throws Exception {
        FieldHelper helper = helperOf("java.util.List<java.lang.Integer>");
        assertTrue(helper.isList());
        assertFalse(helper.isStringList());
    }

    @Test
    public void rawListHasNoElementTypeSoItIsNoList() throws Exception {
        FieldHelper helper = helperOf("java.util.List");
        assertFalse(helper.isList());
        assertFalse(helper.isStringList());
    }

    @Test
    public void nestedGenericIsAListWhoseElementTypeIsUnknown() throws Exception {
        FieldHelper helper = helperOf("java.util.List<java.util.List<java.lang.Integer>>");
        assertTrue(helper.isList());
        assertFalse(helper.isStringList());
        assertSame(VALUE, helper.validItemValue(VALUE));
    }

    @Test
    public void arrayIsAnArrayAndNotAList() throws Exception {
        assertTrue(helperOf("java.lang.String[]").isArray());
        assertTrue(helperOf("int[]").isArray());
        assertFalse(helperOf("java.lang.String[]").isList());
    }

    @Test
    public void primitivesAndTheirWrappersAreNumbers() throws Exception {
        assertTrue(helperOf("int").isNumber());
        assertTrue(helperOf("java.lang.Integer").isNumber());
        assertTrue(helperOf("java.math.BigDecimal").isNumber());
        assertFalse(helperOf("java.lang.String").isNumber());
        assertFalse(helperOf("java.util.Date").isNumber());
    }

    @Test
    public void aDirectlyReferencedClassIsACustomType() throws Exception {
        FieldHelper helper = new FieldHelper(fieldOf(codeModel.directClass("com.example.Custom")));
        assertTrue(helper.isCustomType());
        assertFalse(helper.isNumber());
    }

    @Test
    public void theNaturalBoundsOfTheFieldTypeAreDropped() throws Exception {
        assertNull(helperOf("int").validValue(INT_LIMIT));
        assertNull(helperOf("java.lang.Integer").validValue(INT_LIMIT));
        assertSame(VALUE, helperOf("int").validValue(VALUE));
        // the limit of one type is a valid value of another
        assertSame(INT_LIMIT, helperOf("long").validValue(INT_LIMIT));
        assertSame(INT_LIMIT, helperOf("java.math.BigDecimal").validValue(INT_LIMIT));
    }

    @Test
    public void theNaturalBoundsOfTheElementTypeAreDropped() throws Exception {
        assertNull(helperOf("java.util.List<java.lang.Integer>").validItemValue(INT_LIMIT));
        assertSame(VALUE, helperOf("java.util.List<java.lang.Integer>").validItemValue(VALUE));
        assertSame(INT_LIMIT, helperOf("java.util.List<java.lang.Long>").validItemValue(INT_LIMIT));
        assertSame(INT_LIMIT,
                helperOf("java.util.List<java.math.BigDecimal>").validItemValue(INT_LIMIT));
        assertSame(INT_LIMIT, helperOf("java.lang.String").validItemValue(INT_LIMIT));
    }
}
