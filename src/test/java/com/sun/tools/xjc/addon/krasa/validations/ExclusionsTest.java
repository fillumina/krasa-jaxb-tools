package com.sun.tools.xjc.addon.krasa.validations;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * The {@code exclude} statements: what they match, what they refuse to match, and what they leave
 * unmatched.
 *
 * @author Francesco Illuminati
 */
public class ExclusionsTest {

    @Test
    public void aStatementWithoutHashCoversTheWholeClass() {
        Exclusions exclusions = Exclusions.of(Collections.singletonList("a.RootType"));

        assertNotNull(exclusions.statementFor("a.RootType", "code"));
        assertNotNull(exclusions.statementFor("a.RootType", "label"));
        assertNull(exclusions.statementFor("a.ChildType", "name"));
        assertTrue(exclusions.unmatched().isEmpty());
    }

    @Test
    public void theNamesAreGlobsOverTheQualifiedClassName() {
        Exclusions exclusions = Exclusions.of(Arrays.asList("*#code", "*RootType#*"));

        assertNotNull(exclusions.statementFor("a.RootType", "code"));
        assertNotNull(exclusions.statementFor("a.RootType", "label"));
        assertNull(exclusions.statementFor("a.ChildType", "name"));
        assertTrue(exclusions.unmatched().isEmpty());
    }

    @Test
    public void aQuestionMarkMatchesOneCharacter() {
        Exclusions exclusions = Exclusions.of(Collections.singletonList("a.RootTyp?#code"));

        assertNotNull(exclusions.statementFor("a.RootType", "code"));
        assertNull(exclusions.statementFor("a.RootType", "cod"));
    }

    @Test
    public void everythingButTheGlobsIsLiteral() {
        Exclusions exclusions = Exclusions.of(Collections.singletonList("a.ChildType"));

        assertNull(exclusions.statementFor("aXChildType", "name"));
        assertNull(exclusions.statementFor("a.ChildTypeExtra", "name"));
        assertNotNull(exclusions.statementFor("a.ChildType", "name"));
        assertTrue(Exclusions.validate("a.ChildType.$") == null);
        assertTrue(Exclusions.of(Collections.singletonList("a.ChildType.$")).unmatched().size() == 1);
    }

    @Test
    public void theSimpleNameIsNotTheName() {
        Exclusions exclusions = Exclusions.of(Collections.singletonList("ChildType"));

        assertNull(exclusions.statementFor("a.ChildType", "name"));
        assertEquals(1, exclusions.unmatched().size());
    }

    @Test
    public void aStatementThatMatchedNothingIsReported() {
        Exclusions exclusions = Exclusions.of(Arrays.asList("a.RootType#code", "a.ChildType#nope"));

        assertNotNull(exclusions.statementFor("a.RootType", "code"));
        assertEquals(Arrays.asList("a.ChildType#nope").size(), exclusions.unmatched().size());
        assertTrue(exclusions.unmatched().get(0).toString().contains("a.ChildType#nope"));
    }

    @Test
    public void anEmptyReplacementMeansTheAnnotationIsDropped() {
        Exclusions exclusions = Exclusions.of(Collections.singletonList("a.RootType#code="));

        Exclusions.Statement statement = exclusions.statementFor("a.RootType", "code");
        assertNotNull(statement);
        assertFalse(statement.hasReplacement());
    }

    @Test
    public void aReplacementKeepsItsEqualsSigns() {
        Exclusions exclusions = Exclusions.of(Collections.singletonList("a.RootType#code=@Size(min = 1)"));

        Exclusions.Statement statement = exclusions.statementFor("a.RootType", "code");
        assertNotNull(statement);
        assertTrue(statement.hasReplacement());
        assertEquals("@Size(min = 1)", statement.getReplacement());
    }

    @Test
    public void withoutAClassThereIsNoStatement() {
        assertNotNull(Exclusions.validate("#code"));
        assertNull(Exclusions.validate("a.RootType#code"));
    }

    @Test
    public void withoutAPropertyThereIsNoStatement() {
        assertNotNull(Exclusions.validate("a.RootType#"));
    }

    @Test
    public void anEmptyStatementIsRefused() {
        assertNotNull(Exclusions.validate(""));
        assertNotNull(Exclusions.validate("="));
    }

    @Test
    public void aStatementCanNameTheAnnotation() {
        Exclusions exclusions = Exclusions.of(Collections.singletonList("a.RootType#label@NotNull"));

        Exclusions.Statement statement = exclusions.statementFor("a.RootType", "label");
        assertNotNull(statement);
        assertTrue(statement.coversAnnotation("NotNull"));
        assertFalse(statement.coversAnnotation("Size"));
        assertTrue(exclusions.unmatched().isEmpty());
    }

    @Test
    public void theAnnotationNameIsAGlobToo() {
        Exclusions exclusions = Exclusions.of(Collections.singletonList("a.RootType#amount@Decimal*"));

        Exclusions.Statement statement = exclusions.statementFor("a.RootType", "amount");
        assertTrue(statement.coversAnnotation("DecimalMin"));
        assertTrue(statement.coversAnnotation("DecimalMax"));
        assertFalse(statement.coversAnnotation("NotNull"));
    }

    @Test
    public void withoutAnAnnotationTheStatementCoversThemAll() {
        Exclusions exclusions = Exclusions.of(Collections.singletonList("a.RootType#label"));

        Exclusions.Statement statement = exclusions.statementFor("a.RootType", "label");
        assertTrue(statement.coversAnnotation("NotNull"));
        assertTrue(statement.coversAnnotation("Anything"));
    }

    @Test
    public void aStatementWhoseAnnotationWasNeverThereIsReported() {
        Exclusions exclusions = Exclusions.of(Collections.singletonList("a.RootType#label@Size"));

        Exclusions.Statement statement = exclusions.statementFor("a.RootType", "label");
        assertFalse(statement.coversAnnotation("NotNull"));
        assertEquals(1, exclusions.unmatched().size());
    }

    @Test
    public void aParameterNamesItsAnnotationAndItsValue() {
        Exclusions exclusions = Exclusions.of(Collections.singletonList("a.RootType#label@Size:max = 5"));

        Exclusions.Statement statement = exclusions.statementFor("a.RootType", "label");
        assertTrue(statement.hasParameter());
        assertEquals("max", statement.getParameter());
        assertEquals("5", statement.getParameterValue());
        assertFalse(statement.hasReplacement());
    }

    @Test
    public void aParameterWithoutItsAnnotationIsRefused() {
        assertNotNull(Exclusions.validate("a.RootType#label:max = 5"));
    }

    @Test
    public void aParameterWithoutAValueIsRefused() {
        assertNotNull(Exclusions.validate("a.RootType#label@Size:max"));
    }

    @Test
    public void anEmptyAnnotationNameIsRefused() {
        assertNotNull(Exclusions.validate("a.RootType#label@"));
    }

}
