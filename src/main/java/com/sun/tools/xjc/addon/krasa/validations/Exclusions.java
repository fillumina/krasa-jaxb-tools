package com.sun.tools.xjc.addon.krasa.validations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * The statements of the {@code exclude} option: a glob for the generated class name, optionally a
 * {@code #} and a glob for the property, optionally a {@code =} and the annotation to write instead.
 * A statement without a replacement leaves out every annotation this plugin would write for what it
 * matches.
 *
 * @author Francesco Illuminati
 */
class Exclusions {

    private static final Exclusions NONE = new Exclusions(Collections.<Statement>emptyList());

    /** @return {@code null} when the statement is well formed, the reason when it is not. */
    static String validate(String statement) {
        try {
            Statement.parse(statement);
            return null;
        } catch (IllegalArgumentException ex) {
            return ex.getMessage();
        }
    }

    static Exclusions of(List<String> statements) {
        if (statements == null || statements.isEmpty()) {
            return NONE;
        }
        List<Statement> parsed = new ArrayList<>();
        for (String statement : statements) {
            parsed.add(Statement.parse(statement));
        }
        return new Exclusions(parsed);
    }

    private final List<Statement> statements;
    private final Set<Statement> matched = new LinkedHashSet<>();

    private Exclusions(List<Statement> statements) {
        this.statements = statements;
    }

    /** @return the statement covering this class and property, remembering that it matched. */
    Statement statementFor(String className, String propertyName) {
        for (Statement statement : statements) {
            if (statement.matches(className, propertyName)) {
                matched.add(statement);
                return statement;
            }
        }
        return null;
    }

    /** @return the statements that matched no class and no property, which are likely typos. */
    List<Statement> unmatched() {
        List<Statement> unmatched = new ArrayList<>(statements);
        unmatched.removeAll(matched);
        return unmatched;
    }

    static class Statement {

        private static final String REGEX_SPECIAL = "\\.[]{}()<>*+-=!?^$|";

        private final String text;
        private final Pattern classPattern;
        private final Pattern propertyPattern;
        private final String replacement;

        static Statement parse(String text) {
            if (text == null || text.trim().isEmpty()) {
                throw new IllegalArgumentException("no class name");
            }
            final String value = text.trim();
            String head = value;
            String replacement = null;
            final int equals = value.indexOf('=');
            if (equals != -1) {
                head = value.substring(0, equals).trim();
                // an empty value means the annotation is removed, as if it were not there
                replacement = value.substring(equals + 1).trim().isEmpty()
                        ? null : value.substring(equals + 1).trim();
            }
            String classGlob = head;
            String propertyGlob = null;
            final int hash = head.indexOf('#');
            if (hash != -1) {
                classGlob = head.substring(0, hash).trim();
                propertyGlob = head.substring(hash + 1).trim();
                if (classGlob.isEmpty()) {
                    throw new IllegalArgumentException("no class name before the #");
                }
                if (propertyGlob.isEmpty()) {
                    throw new IllegalArgumentException("no property name after the #");
                }
            }
            if (classGlob.isEmpty()) {
                throw new IllegalArgumentException("no class name");
            }
            return new Statement(value, toPattern(classGlob),
                    propertyGlob == null ? null : toPattern(propertyGlob), replacement);
        }

        /** @return the glob as a pattern: {@code *} and {@code ?} only, everything else literal. */
        private static Pattern toPattern(String glob) {
            StringBuilder regex = new StringBuilder("^");
            for (int i = 0; i < glob.length(); i++) {
                char c = glob.charAt(i);
                if (c == '*') {
                    regex.append(".*");
                } else if (c == '?') {
                    regex.append('.');
                } else {
                    if (REGEX_SPECIAL.indexOf(c) >= 0) {
                        regex.append('\\');
                    }
                    regex.append(c);
                }
            }
            return Pattern.compile(regex.append('$').toString());
        }

        private Statement(String text, Pattern classPattern, Pattern propertyPattern,
                String replacement) {
            this.text = text;
            this.classPattern = classPattern;
            this.propertyPattern = propertyPattern;
            this.replacement = replacement;
        }

        boolean matches(String className, String propertyName) {
            if (!classPattern.matcher(className).matches()) {
                return false;
            }
            return propertyPattern == null || propertyPattern.matcher(propertyName).matches();
        }

        boolean hasReplacement() {
            return replacement != null;
        }

        String getReplacement() {
            return replacement;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}
