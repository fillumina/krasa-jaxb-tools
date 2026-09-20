![Maven Central](https://img.shields.io/maven-central/v/com.fillumina/krasa-jaxb-tools.svg)

# XJC Plugin to generate Bean Validation Annotations 2.0 ([JSR-380](https://jcp.org/en/jsr/detail?id=380))

This project defines 2 XJC and 1 CXF plugins:

- `Jsr308Annotations` a XJC plugin that adds Bean Validation 2.0 or [JSR 380](https://jcp.org/en/jsr/detail?id=380) validations suporting both `javax` or `jakarta` packages

- `ReplacePrimitives`  a XJC plugin that replaces the generated primitives with the corresponding boxed types (i.e. `int` -> `Integer`)

- an [Apache Cxf plugin](https://cxf.apache.org/docs/tools.html) that adds the `javax` or `jakarta` `@Valid` annotation to the SOAP methods and their parameters (both optionally) of the generated Port Type interface. This plugin is configured using the same `JSR308Annotations` name.

The plan for this project, and the one after it
---------------------------------------------------

This repository is the **legacy line**: JDK 8 toolchain, XJC 2.3.x, CXF 3.5 and the `javax`
validation API. It can still be used from newer JDKs (see the examples below), but it will not
move to XJC 4.x or to `jakarta`, and **from now on it receives fixes only** — no new features, no
behaviour changes. Its name, URL and Maven coordinates stay exactly where they are, so nothing has
to be re-published or re-pointed by the projects that depend on it.

The modern line will be a **separate project**, started from a copy of these sources and breaking
compatibility from its first commit, because the two worlds cannot be carried in one codebase:

- **JDK 21**, XJC 4.x, CXF 4.1 and `jakarta.validation` 3.x;
- the collection annotations (`@EachPattern`, `@EachSize`, `@EachDecimalMin`, …) replaced by the
  **standard container element constraints** — `List<@Pattern(regexp = "…") String>` — which Bean
  Validation has supported since 2.0, with `@Valid` moved to the type argument
  ([#33](https://github.com/fillumina/krasa-jaxb-tools/issues/33));
- the three plugins that are conflated here — the `-XJsr303Annotations` generator, the
  `-XReplacePrimitives` replacer and the CXF `krasa` frontend — published separately, so a build
  takes only what it uses;
- a new name and new coordinates. The contract that ports is the fixtures — the schemas with their
  expected annotations — not the code.

**If this plan is a problem for you, say so now** by opening an
[issue](https://github.com/fillumina/krasa-jaxb-tools/issues): in particular if you are in the
middle of a `javax` → `jakarta` migration and would need a bridge release that does both. It is
better to hear it before the split than after.

Nothing above changes what you already depend on: **2.6.0** is the current release of this line,
and fixes keep coming to it.

## Example of usage

There are 2 example projects containing many different plugins and configurations available for reference (each new version of this plugin is tested against these two projects):

- [GitHub - fillumina/krasa-jaxb-tools-jdk21-example: Examples of usage of krasa-jaxb-tools using latest technologies (Java 21)](https://github.com/fillumina/krasa-jaxb-tools-jdk21-example) as the name suggests it's compiled with **JDK 21** and provides working examples of many different plugins using both XJC and CXF configured with both `javax` and `jakarta` packages using the latest versions available.

- [GitHub - fillumina/krasa-jaxb-tools-example: Sample project for https://github.com/fillumina/krasa-jaxb-tools](https://github.com/fillumina/krasa-jaxb-tools-example) uses **JDK 8** and provides examples using the latest versions of plugins and dependencies available for that java version.

JDK 1.8 Support
----------------

The project is bounded to support **Java 8** (**JDK 1.8**) because of some old projects still requiring it. All dependencies are selected from the latest available versions still supporting that.

Versions
----------------

See [CHANGELOG.md](CHANGELOG.md) for the version history.

Release
----------------

```xml
<dependency>
    <groupId>com.fillumina</groupId>
    <artifactId>krasa-jaxb-tools</artifactId>
    <version>2.6.0</version>
</dependency>
```

Options
----------------

- `verbose` (boolean, default=`false`) print verbose messages to output
  example: `-XJsr303Annotations:verbose=true`
- `validationAnnotations` (`javax` | `jakarta`, default=`javax`): selects the library to use for validation annotations
  example: `-XJsr303Annotations:validationAnnotations=javax`
- `targetNamespace` (string): adds @Valid annotation only if the element has the given namespace
  example: `-XJsr303Annotations:targetNamespace=a`
- `generateNotNullAnnotations` (boolean, default=`true`): adds a `@NotNull` annotation if an element has `minOccurs` not 0, is `required` or is not `nillable`.
  examples: `-XJsr303Annotations:generateNotNullAnnotations=true`
- `notNullAnnotationsCustomMessages` (boolean or string, default=`false`): values are `true`, `FieldName`, `ClassName`, or an *actual message* (see further explanation in a note down below)
  example: `-XJsr303Annotations:notNullAnnotationsCustomMessages=ClassName`
- `generateListAnnotations` (boolean, optional, default `false`) generates [validator-collection annotations](https://github.com/jirutka/validator-collection) annotations
  example: `-XJsr303Annotations:generateListAnnotations=true`
- `generateServiceValidationAnnotations` (string, accepts: `in`, `out`, `inout`, works with  `apache-cxf` only) adds `@Valid` annotations to respective message direction (in, out or both).
  example: `-XJsr303Annotations:generateServiceValidationAnnotations=inout`
- `generateAllNumericConstraints` (boolean, defaults to `false`) generates all `@DecimalMin` and `@DecimalMax` even those regarding the natural boundaries of the referred java type.
  example: `-XJsr303Annotations:generateAllNumericConstraints=true`
- `multiPattern` (boolean, default: `false`) uses a multiple javax validation `@Pattern` instead of `@Pattern.List` (see [3.2. Applying multiple constraints of the same type](https://beanvalidation.org/2.0-jsr380/spec/#constraintsdefinitionimplementation-multipleconstraints))

### Notes

- Arguments accepting booleans can either be given the value `true` as with `verbose=true` or simply be left without a value at all and that will be interpreted as being `true`  (you can omit the `=` too).

- All arguments are optional.

#### About `notNullAnnotationsCustomMessages`

**`@NotNull`** default validation message is not always helpful, so it can be customized with **-XJsr303Annotations:notNullAnnotationsCustomMessages=OPTION** where **OPTION** is one of the following:

- `false` default: no custom message
- `true` message is present but equivalent to the default: **"{javax.validation.constraints.NotNull.message}"**
- `FieldName` field name is prefixed to the default message: **"fieldName {javax.validation.constraints.NotNull.message}"**
- `ClassName` class and field name are prefixed to the default message: **"ClassName.fieldName {javax.validation.constraints.NotNull.message}"**
- `other-non-empty-text` arbitrary message, with substitutable, case-sensitive parameters `{ClassName}` and `{FieldName}`, i.e.: **"Class {ClassName} field {FieldName} non-null"**

#### About `generateServiceValidationAnnotations`

Bean validation policy can be customized with `-XJsr303Annotations:generateServiceValidationAnnotations=OPTION` where OPTION is one of the following (the option is case insensitive):

- `InOut` (default: validate requests and responses)
- `In` (validate only requests)
- `Out` (validate only responses)

Using this option requires to specify `krasa` as front end generator in the CXF plugin with the option `-frontend krasa` (See example in [krasa-jaxb-tools-example/krasa-cxf-codegen-plugin-example/pom.xml at master · fillumina/krasa-jaxb-tools-example · GitHub](https://github.com/fillumina/krasa-jaxb-tools-example/blob/master/krasa-cxf-codegen-plugin-example/pom.xml) )

#### About `ReplacePrimitives`

That is a different plugin within this same package that can be enabled with the option `-XReplacePrimitives`.  It replaces primitive types with boxed ones (`int` -> `Integer`). It's enabled in the [krasa-cxf-codegen-plugin-example](https://github.com/fillumina/krasa-jaxb-tools-example/blob/master/krasa-cxf-codegen-plugin-example/pom.xml) project as an example.
**WARNING:** must be defined before XhashCode or Xequals.

Supported Annotations
----------------

The plugin generates sources annotated with the following Java Bean Validation 2.0 (JSR 380) annotations (with either `javax` or `jakarta` packages depending on the configuration, see `-XJsr303Annotations:validationAnnotations=javax`):

- `@Valid` annotation for all complex types, can be further restricted to generate only for types from defined schema: `-XJsr303Annotations:targetNamespace=http://www.foo.com/bar`
- `@NotNull` annotation for objects that has a MinOccur value >= 1 or for required attributes
- `@Size` for lists that have minOccurs > 1
- `@Size` if there is a maxLength or minLength or length restriction
- `@DecimalMax` for maxInclusive restriction
- `@DecimalMin` for minInclusive restriction
- `@DecimalMax` for maxExclusive restriction, with `inclusive = false`
- `@DecimalMin` for minExclusive restriction, with `inclusive = false`
- `@Digits` if there is a totalDigits or fractionDigits restriction.
- `@Pattern` and `@PatternList` if there is a Pattern restriction; strings only — numeric patterns are not supported, see [Numeric patterns are not supported](#numeric-patterns-are-not-supported)

## Numeric patterns are not supported

A `xsd:pattern` on a numeric type is **not supported**: the plugin derives no annotation from it,
neither `@Pattern` (nor `@EachPattern` on collections) nor a translated range. This is a deliberate
refusal, not a missing feature.

Two reasons, and the second is why no translation can be complete:

1. `@Pattern` resolves to the Bean Validation `Pattern` validator, which accepts `CharSequence`
   only: on a `Short`, an `Integer` or a `BigDecimal` it does not check anything, it fails at
   validation time.
2. A regexp is not a range. It can constrain the *shape* of a number (`[0-9]3[0-9]*` requires the
   second digit to be a `3`), which no interval can express, so a pattern-to-range translation
   would be right for a few shapes and wrong for the rest.

### What to write instead

Use the numeric facets, which the plugin does understand:

```xml
<!-- not supported: a pattern on a numeric type -->
<xsd:restriction base="xsd:decimal">
  <xsd:pattern value="-1\.5" />
</xsd:restriction>

<!-- supported: the same constraint, expressed as facets -->
<xsd:restriction base="xsd:decimal">
  <xsd:minInclusive value="-1.5" />
  <xsd:maxInclusive value="-1.5" />
</xsd:restriction>
```

Mind the escaping: a pattern is a regexp, so its point is escaped (`-1\.5`), a facet is a number,
so it is not (`-1.5`). The second form generates exactly the intended constraint:

```java
@DecimalMin(value = "-1.5", inclusive = true)
@DecimalMax(value = "-1.5", inclusive = true)
```

### If the schema cannot be changed

There is a pre-pass: rewrite the patterns into facets before the generator runs, leaving the
original schema in place. It is a workaround for schemas that are not yours to change, **not** a
feature of the plugin, and it is documented on its own — stylesheet, the verified Maven wiring and
the proof of both its halves — in [doc/numeric-patterns-prepass.md](doc/numeric-patterns-prepass.md).

Whatever it produces is for **code generation only**: it drops the pattern, so it is *not*
equivalent as a validation contract — keep validating against the original schema.

Numeric `xsd:enumeration` restrictions are not validated: no annotation is generated for them. A
value pinned by `fixed` is translated, as a fixed range (`minInclusive` + `maxInclusive` with the same
value).

## Note on submitting issues and bugfixes

Any issue or bug fix reported is *extremely* welcome but to help me understand the problem and reduce the time to publish the fix I kindly ask to comply to these roles:

- **Issues** should contain a detailed description **and an example** to show how and when the code is failing. It might be a failing test or a simple github project.

- **Fixes** should contain a test that proves the solution to the problem (and the test should be present in a previous commit to the fix to show that it fails). It is appreciated that the solution contains as *little refactoring* as possible and is focused only on *fixing one issue*. Each fix should focus on one specific issue.

- **Case insensitive filesystem** users (wich is the default on Windows and MacOS) should be careful about file naming especially considering that _signatures_ files (the ones containing the expected annotations) usually are named after the class they refer to so their first letter is often a capital one (ie: `multiplePatternsWithBase-a-annotation.txt` should probably be `MultiplePatternsWithBase-a-annotation.txt`)

## TODO

- change the plugin name to `Jsr380Annotations`, because the plugin implements the Java Specification
  Request 380. The rename is a breaking change for existing builds' configuration, so the current name
  is kept as long as Java 8 is supported: it comes with the next major version, together with lifting
  the Java 8 bound.
