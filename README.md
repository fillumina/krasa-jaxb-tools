![Maven Central](https://img.shields.io/maven-central/v/com.fillumina/krasa-jaxb-tools.svg)

# XJC Plugin to generate Bean Validation Annotations 2.0 ([JSR-380](https://jcp.org/en/jsr/detail?id=380))

This project defines 2 XJC and 1 CXF plugins:

- `Jsr308Annotations` a XJC plugin that adds Bean Validation 2.0 or [JSR 380](https://jcp.org/en/jsr/detail?id=380[The Java Community Process(SM) Program - JSRs: Java Specification Requests - detail JSR# 380](https://jcp.org/en/jsr/detail?id=380)) validations suporting both `javax` or `jakarta` packages

- `ReplacePrimitives`  a XJC plugin that replaces the generated primitives with the corresponding boxed types (i.e. `int` -> `Integer`)

- an [Apache Cxf plugin](https://cxf.apache.org/docs/tools.html) that adds the `javax` or `jakarta` `@Valid` annotation to the methods and the parameters (both optionally) of the generated Port Type interface. This plugin is configured using the same `JSR308Annotations` name.

## Example of usage

There are 2 example projects containing many different plugins and configurations available for reference:

- [GitHub - fillumina/krasa-jaxb-tools-jdk21-example: Examples of usage of krasa-jaxb-tools using latest technologies (Java 21)](https://github.com/fillumina/krasa-jaxb-tools-jdk21-example) as the name suggests it's compiled with **JDK 21** and provides working examples of many different plugins using both XJC and CXF configured with both `javax` and `jakarta` packages using the latest versions available.

- [GitHub - fillumina/krasa-jaxb-tools-example: Sample project for https://github.com/fillumina/krasa-jaxb-tools](https://github.com/fillumina/krasa-jaxb-tools-example) uses **JDK 8** and provides examples using the latest versions of plugins and dependencies available for that java version.

JDK 1.8 Support
----------------

The project is bounded to support **Java 8** (**JDK 1.8**) because of some old projects still requiring it. All dependencies are selected from the latest available versions still supporting that.

Version
----------------

- `2.3.5` fix critical vulnerabilities found in dependencies, see [Sonatype report](https://sbom.sonatype.com/report/T1-a4e79c5353879ed9b588-23af948b811c4e-1726254616-6f0f87d1e3be445d8022a8d5689bf3c5).

- `2.3.4` bug fix release:
  
  - fix [Issue #17](https://github.com/fillumina/krasa-jaxb-tools/issues/17) where `@DecimalMin` and `@DecimalMax` superfluous annotations were added to numeric java types. A new argument has been created `generateAllNumericConstraints` in case all constraints would be needed (even superflous ones).

- `2.3.3` bug fix release:
  
  - fix [Issue #13](https://github.com/fillumina/krasa-jaxb-tools/issues/13) where it's been wrongly assumed that:
    `SimpleTypeImpl particle = (SimpleTypeImpl) definition` was always true.
    A check has beed added to prevent the `ClassCastException`.

- `2.3.2` another bug fix release:
  
  - fix `@Pattern` added to wrong fields (regression from 2.2)
  - add `@EachPattern` when needed for `List<String>` fields
  - rename `generateStringListAnnotations` option to `generateListAnnotations` because it is not limited to list of strings
  - disable `generateListAnnotations` by default (was enabled)

- `2.3.1` bug fix release:
  
  - `@Valid` annotation was not added by default
  - remove `singlePattern` option because `@Pattern.List` is not semantically correct
  - disable `jpa` option because not really useful
  - disable `JSR_349` option it was referring to Validation API 1.1 while now we use 2.0
  - add a lot of tests to establish a solid baseline (defaults was backported and tested on 2.2)

- `2.3` A huge refactoring and bug fixing:
  
  - added `singlePattern` option
  - fixed `generateServiceValidationAnnotations` used by `ValidSEIGenerator` to accept string parameter
  - dependencies updated to the latest version still supporting JDK 1.8
  - a maven rule has been set to force compilation with JDK 1.8

- `2.2` Some new features added because of PR requests
  
  - Added `@Valid` annotation to `sequence`s to force items validation
  - Added support for `Jakarta EE 9` with parameter `validationAnnotations`

- `2.1` Revert back to Java 1.8 (sorry folks!).

- `2.0` A refactorized version of the original [krasa-jaxb-toos](https://github.com/krasa/krasa-jaxb-tools) last synced on August 2022, with some enhancements (support for `EachDigits`, `EachDecimalMin` and `EachDecimalMax` in primitive lists), improved tests and bug fixed. It is compiled using JDK 11. The `pom.xml` `groupId` has been changed to `com.fillumina`.

Release
----------------

```xml
<dependency>
    <groupId>com.fillumina</groupId>
    <artifactId>krasa-jaxb-tools</artifactId>
    <version>2.3.5</version>
</dependency>
```

Options
----------------

- `verbose` (boolean, default=`false`) print verbose messages to output
  example: `-XJsr303Annotations:verbose=true`
- `validationAnnotations` (`javax` | `jakarta`, default=`javax`): selects the library to use for annotations
  example: `-XJsr303Annotations:validationAnnotations=javax`
- `targetNamespace` (string): adds @Valid annotation to all elements with given namespace
  example: `-XJsr303Annotations:targetNamespace=a`
- `generateNotNullAnnotations` (boolean, default=`true`): adds a `@NotNull` annotation if an element has `minOccours` not 0, is `required` or is not `nillable`.
  examples: `-XJsr303Annotations:generateNotNullAnnotations=true`
- `notNullAnnotationsCustomMessages` (boolean or string, default=`false`): values are `true`, `FieldName`, `ClassName`, or an *actual message* (see further explanation down below).
- `generateListAnnotations` (boolean, optional, default `false`) generates [validator-collection annotations](https://github.com/jirutka/validator-collection) annotations
- `generateServiceValidationAnnotations` (string, accepts: `in`, `out`, `inout`, works with  `apache-cxf` only) adds `@Valid` annotations to respective message direction (in, out or both).
- `generateAllNumericConstraints` (boolean, defaults to `false`) generates all `@DecimalMin` and `@DecimalMax` even those regarding the natural boudaries of the referred java type.

### Notes

- Arguments accepting booleans can either be given the value `true` as with `verbose=true` or simply be left without a value at all and that will be interpteted as being `true`  (you can omit the `=` too).

- All arguments are optional.

#### About `notNullAnnotationsCustomMessages`

**`@NotNull`**'s default validation message is not always helpful, so it can be customized with **-XJsr303Annotations:notNullAnnotationsCustomMessages=OPTION** where **OPTION** is one of the following:

- `false` default: no custom message
- `true` message is present but equivalent to the default: **"{javax.validation.constraints.NotNull.message}"**
- `FieldName` field name is prefixed to the default message: **"fieldName {javax.validation.constraints.NotNull.message}"**
- `ClassName` class and field name are prefixed to the default message: **"ClassName.fieldName {javax.validation.constraints.NotNull.message}"**
- `other-non-empty-text` arbitrary message, with substitutable, case-sensitive parameters `{ClassName}` and `{FieldName}`: **"Class {ClassName} field {FieldName} non-null"**

#### About `generateServiceValidationAnnotations`

Bean validation policy can be customized with `-XJsr303Annotations:generateServiceValidationAnnotations=OPTION` where OPTION is one of the following:

- `InOut` (default: validate requests and responses)
- `In` (validate only requests)
- `Out` (validate only responses)

Using this option requires to specify krasa as front end generator (See example in https://github.com/fillumina/krasa-jaxb-tools-example )

#### About `ReplacePrimitives`

That is a different plugin within this same packakge and replaces primitive types with boxed ones. It's enabled in the [krasa-cxf-codegen-plugin-example](https://github.com/fillumina/krasa-jaxb-tools-example/blob/master/krasa-cxf-codegen-plugin-example/pom.xml) project as an example.
**WARNING:** must be defined before XhashCode or Xequals.

Supported Annotations
----------------

Generates:

- `@Valid` annotation for all complex types, can be further restricted to generate only for types from defined schema: `-XJsr303Annotations:targetNamespace=http://www.foo.com/bar`
- `@NotNull` annotation for objects that has a MinOccur value >= 1 or for required attributes
- `@Size` for lists that have minOccurs > 1
- `@Size` if there is a maxLength or minLength or length restriction
- `@DecimalMax` for maxInclusive restriction
- `@DecimalMin` for minInclusive restriction
- `@DecimalMax` for maxExclusive restriction, enable new parameter (inclusive=false) with: -XJsr303Annotations:JSR_349=true
- `@DecimalMin` for minExclusive restriction, enable new parameter (inclusive=false) with: -XJsr303Annotations:JSR_349=true
- `@Digits` if there is a totalDigits or fractionDigits restriction.
- `@Pattern` and `@PatternList` if there is a Pattern restriction (see `singlePattern` option)

## TODO

- change the plugin name to `Jsr380Annotations` because it's now about the Java Specification Request 308. Being a breaking change it should cause the version to jump to 2.5
