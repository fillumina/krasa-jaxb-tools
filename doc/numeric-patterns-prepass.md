# Numeric patterns: the pre-pass

This document describes a **workaround**, not a feature of the plugin. The plugin does not
support `xsd:pattern` on a numeric type, for the reasons given in the README section *Numeric
patterns are not supported*: `@Pattern` resolves to a validator that accepts `CharSequence` only,
and a regexp is not a range.

The pre-pass does not change that. It rewrites the schema *before* code generation, so that the
generator sees numeric facets instead of a pattern. Use it only when the schema is not yours to
change; when it is, write the facets (see the README) and forget this document.

## What it does

`numeric-patterns-to-facets.xsl` (in this directory) rewrites a **literal** numeric pattern into a
fixed range:

```xml
<!-- in -->
<xsd:restriction base="xsd:decimal">
  <xsd:pattern value="-1\.5"/>
</xsd:restriction>

<!-- out -->
<xsd:restriction base="xsd:decimal">
  <xsd:minInclusive value="-1.5"/>
  <xsd:maxInclusive value="-1.5"/>
</xsd:restriction>
```

Everything else is copied untouched: patterns on non-numeric types stay patterns, and so does any
other element or attribute of the schema. A numeric pattern that no range can express does not get
a best effort: the stylesheet **stops the build**, so a constraint can never be silently weakened.

## Wiring it into a Maven build

Two steps, both in `generate-sources`: transform the schema into the build directory, then point
the generator at the transformed copy and leave the original in place. With `xml-maven-plugin`
(1.2.2 verified) and `maven-jaxb2-plugin` (0.15.3 verified):

```xml
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>xml-maven-plugin</artifactId>
  <version>1.2.2</version>
  <executions>
    <execution>
      <id>numeric-patterns-to-facets</id>
      <phase>generate-sources</phase>
      <goals><goal>transform</goal></goals>
      <configuration>
        <transformationSets>
          <transformationSet>
            <dir>src/main/resources</dir>
            <stylesheet>doc/numeric-patterns-to-facets.xsl</stylesheet>
            <outputDir>${project.build.directory}/generated-schemas</outputDir>
          </transformationSet>
        </transformationSets>
      </configuration>
    </execution>
  </executions>
</plugin>
```

This plugin must be declared **before** the generator, since both bind to `generate-sources` and
executions of the same phase run in declaration order:

```xml
<plugin>
  <groupId>org.jvnet.jaxb2.maven2</groupId>
  <artifactId>maven-jaxb2-plugin</artifactId>
  <version>0.15.3</version>
  <executions>
    <execution>
      <goals><goal>generate</goal></goals>
    </execution>
  </executions>
  <configuration>
    <schemaDirectory>${project.build.directory}/generated-schemas</schemaDirectory>
    <generateDirectory>${project.build.directory}/generated-sources/xjc</generateDirectory>
    <args>
      <arg>-XJsr303Annotations</arg>
      <arg>-XJsr303Annotations:targetNamespace=a</arg>
      <arg>-XJsr303Annotations:validationAnnotations=jakarta</arg>
    </args>
    <plugins>
      <plugin>
        <groupId>com.fillumina</groupId>
        <artifactId>krasa-jaxb-tools</artifactId>
        <version>2.6.0</version>
      </plugin>
    </plugins>
  </configuration>
</plugin>
```

Note on the generator options: pass the plugin option itself, `-XJsr303Annotations`, next to the
`:option=value` forms. A run that passed only `-XJsr303Annotations:validationAnnotations=jakarta`
produced no annotations at all and no error: the option was silently ignored.

## Proof

Both halves were run on 2026-09-19, offline, with JDK `1.8.0_504`, `xml-maven-plugin` 1.2.2,
`maven-jaxb2-plugin` 0.15.3 and `krasa-jaxb-tools` 2.6.0.

A schema with `<xsd:pattern value="-1\.5"/>` on `xsd:decimal` and `<xsd:pattern value="[A-Z]{3}"/>`
on `xsd:string` produced:

```java
    @NotNull
    @DecimalMin(value = "-1.5", inclusive = true)
    @DecimalMax(value = "-1.5", inclusive = true)
    protected BigDecimal amount;

    @NotNull
    @Pattern(regexp = "[A-Z]{3}")
    protected String code;
```

The numeric pattern became the intended fixed range; the string pattern stayed a pattern.

A schema with `<xsd:pattern value="[0-9]{3}"/>` on `xsd:int` stopped the same build:

```
[ERROR] Failed to execute goal org.codehaus.mojo:xml-maven-plugin:1.2.2:transform
  (numeric-patterns-to-facets) on project …: Failed to transform input file …
  java.lang.RuntimeException: Termination forced by an xsl:message instruction
```

with `numeric pattern cannot be expressed as a range: "[0-9]{3}" on xsd:int` on the console and no
sources generated. The stylesheet itself was also checked with `xsltproc`: a restriction of a
*named* type is copied untouched, and a string pattern is copied untouched.

## Limits

- A restriction of a **named** type (`base="a:MyInt"`) is left alone: deciding whether it is
  numeric needs the type graph, not the local name. Convert those by hand, or add a type map.
- Patterns a range cannot express (`[0-9]{3}`, `[0-9]3[0-9]*`) are not recoverable this way, and
  the build stops on them rather than guessing.
- The transformed schema is for **code generation only**. It drops the pattern, so it is not
  equivalent as a validation contract: keep validating against the original schema.
