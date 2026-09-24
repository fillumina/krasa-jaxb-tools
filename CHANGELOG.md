# Changelog

The version history of krasa-jaxb-tools, newest first. The README describes how to use the
plugin; this file records what changed in each release.

## Versions

- `2.9.0-SNAPSHOT` the version under development:

  - an element whose type is an `xs:list` holds its items in the generated field, so the number of
    times the element occurs is no longer written there. An optional one used to get
    `@Size(min = 0, max = 1)`, which counts the items instead of the occurrences and rejects a value
    the schema allows, and a list type stating its own length had that length displaced by it. The
    length facets of the list type are written as before

  - an element that repeats and is a list is generated as a list of `JAXBElement`, one list of items
    each: its cardinality is now written on the outer list, where it counts the occurrences. The
    items inside the `JAXBElement` stay unannotated, because a constraint written there is refused
    by a provider at run time

- `2.8.0` a maintenance release: **nothing the plugin does changes.** The generated annotations, the
  options and their defaults are those of 2.7.0 exactly, and the one change in the plugin's own code is
  the removal of a method nothing called. What changed is the project around it:

  - the test suite runs as JUnit 4, with the annotation library as the test parameter, so a single
    fixture and a single library can be run on their own from an IDE. The expectations are untouched:
    the same eighty `-annotation.txt` files and the same schemas, compared as before

  - the tests the JUnit 3 harness needed for itself went with it, and the extraction of the generated
    classes into those expectation files is now covered by tests that were shown to fail when the
    extraction is deliberately broken

  - the README says which XJC the CXF frontends put on the build classpath, why a binding file can then
    be read by an XJC that is not the one you asked for, and the two ways out of it

- `2.7.0` a feature release: two new options, a new frontend, an option alias, and the dependencies
  and build plugins moved to the newest release of each line that still runs on JDK 8. The new options
  change nothing unless they are asked for.

  - dependencies updated: CXF 3.5.10 → 3.5.11, JAXB/XJC 2.3.5 → 2.3.9 (pinned, so a consumer keeps
    resolving its own), `jakarta.validation-api` 3.0.2 → 3.1.1 and the Maven plugins (surefire,
    compiler, javadoc, source, enforcer, gpg, central-publishing), so a consumer of the plugin
    resolves the newer CXF patch. `velocity-engine-core` stays at 2.3, because 2.4.1 is not
    compatible with the `commons-lang3` the CXF stack brings

  - new `exclude` option ([issue #34](https://github.com/fillumina/krasa-jaxb-tools/issues/34)): a
    repeatable statement that leaves chosen classes, properties or annotations out of the generated
    annotations, or sets a single parameter of one of them — a different message, for example —
    without touching the schema

  - new `generateValidOnCollections` option ([issue #33](https://github.com/fillumina/krasa-jaxb-tools/issues/33)):
    set to `false`, `@Valid` is no longer written on a collection, which Bean Validation deprecated
    because it belongs on the type argument; it is `true` by default, so nothing changes unless it
    is asked for

  - new frontend `-frontend krasa-jaxws` ([issue #29](https://github.com/fillumina/krasa-jaxb-tools/issues/29)):
    CXF's own generators next to the validated interface, for the builds that need both. The `krasa`
    frontend is unchanged

  - the plugin answers to `-XBeanValidationAnnotations` as well, the name the specification gives it;
    `-XJsr303Annotations` keeps working

  - documentation: the README is reorganised with one chapter per tool and a use example, the
    maintenance notice says what this line does and what the announced new projects will do, and the
    `generateListAnnotations` option now warns that the `validator-collection` annotations it emits
    are `javax`-only — a jakarta provider does not enforce them, and a recent `javax` one refuses
    to initialise them

- `2.6.0` bug fix release, with one small enhancement and a few corrections of what a schema means:

  - a `fixed` value on an element or an attribute was lost: the field kept a primitive type with
    `@NotNull` only, so the value the schema pins was never checked. It is now translated into a
    fixed range (`minInclusive` + `maxInclusive`), on numeric fields only

  - a numeric `xsd:pattern` is **not supported**: `@Pattern` accepts `CharSequence` only, and a
    pattern is not a range (it can pin a digit at a position). Support was added during this line
    and withdrawn before the release (see [issue #38](https://github.com/fillumina/krasa-jaxb-tools/issues/38)
    and the [pull request #39](https://github.com/fillumina/krasa-jaxb-tools/pull/39) reverted by
    [#40](https://github.com/fillumina/krasa-jaxb-tools/pull/40)). For a schema that cannot be
    changed there is a pre-pass, see `doc/numeric-patterns-prepass.md`

  - a numeric `enumeration` is not validated either: no annotation is generated for it

  - `@EachPattern` is no longer generated on a collection of numbers, where it could not validate
    anything

  - numeric bounds are no longer copied onto a collection field: they are filtered by the element
    type of the list

  - an unknown option name is reported as a command line error with the usage, instead of as an
    `IllegalArgumentException` from the enum lookup

  - the `xjcArgs:` line is no longer printed to stderr on every CXF build, and an element property
    without a particle is skipped instead of failing the build

  - documentation: the version history moved here from the README, the references to options that
    no longer exist were dropped, and the README gained the section *Numeric patterns are not
    supported*

  - `mvn test` now runs on JDK 8 in CI, on push and pull request


- `2.5.1` that's a **bugfix** to version `2.4.0` fixing [issue #31](https://github.com/fillumina/krasa-jaxb-tools/issues/31), it was just wrongly named 2.5.1 instead of 2.4.1. Sorry for that.

- `2.4.0` the algorithm to search for inherited restrictions has been completely rewritten and it is now much more reliable (especially with @EachXXX item annotations)

- `2.3.8` add `multiPattern` option (false by default) to enable writing multiple `@Pattern` annotations instead of using `@Pattern.List` (thanks to [CrEaK (Niklas Neesen)](https://github.com/CrEaK))

- `2.3.7` fix duplicate pattern and critical vulnerability on dependency

  - duplicated regex pattern and enumeration fix (thanks to Niklas Neesen)

  - CVE-2025-23184 on dependency org.apache.cxf:cxf-tools-wsdlto-frontend-jaxws:3.5.9

- `2.3.6` various fixes and improvements:

  - allow numeric annotations to String generated values

  - fix invalid multiple pattern generation with inherited restrictions

  - all tests are now performed automatically against both `javax` and `jakarta` packages

  - fix for `javax` packages being generated by the CXF plugin even when `jakarta` was required (ValidSEIGenerator plugin)

  - reorganization of test packages to improve readability

- `2.3.5` fix critical vulnerabilities found in dependencies, see [Sonatype report](https://sbom.sonatype.com/report/T1-a4e79c5353879ed9b588-23af948b811c4e-1726254616-6f0f87d1e3be445d8022a8d5689bf3c5).

- `2.3.4` bug fix release:

  - fix [Issue #17](https://github.com/fillumina/krasa-jaxb-tools/issues/17) where `@DecimalMin` and `@DecimalMax` superfluous annotations were added to numeric java types. A new argument has been created `generateAllNumericConstraints` in case all constraints would be needed (even superfluous ones).

- `2.3.3` bug fix release:

  - fix [Issue #13](https://github.com/fillumina/krasa-jaxb-tools/issues/13) where it's been wrongly assumed that:
    `SimpleTypeImpl particle = (SimpleTypeImpl) definition` was always true.
    A check has been added to prevent the `ClassCastException`.

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
