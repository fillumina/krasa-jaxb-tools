![Maven Central](https://img.shields.io/maven-central/v/com.fillumina/krasa-jaxb-tools.svg)

# XJC and CXF plugins that generate Bean Validation annotations

## Content of the project

Three independent tools ship in this one artifact, and a build can use any combination of them:

- **[`BeanValidationAnnotations`](#the-xjc-plugin-beanvalidationannotations)** — a XJC plugin that adds Bean Validation 2.0 or [JSR 380](https://jcp.org/en/jsr/detail?id=380) annotations to the generated JAXB types, supporting both `javax` and `jakarta` packages, configured with the `-XBeanValidationAnnotations` options — the same ones under the older `-XJsr303Annotations` name, which keeps working.

- **[`ReplacePrimitives`](#the-xjc-plugin-replaceprimitives)** — a XJC plugin that replaces the generated primitives with the corresponding boxed types (i.e. `int` -> `Integer`), enabled with `-XReplacePrimitives`.

- **[The CXF frontends](#the-cxf-frontends-krasa-and-krasa-jaxws)** — an [Apache Cxf plugin](https://cxf.apache.org/docs/tools.html) that adds the `javax` or `jakarta` `@Valid` annotation to the SOAP methods and their parameters (both optionally) of the generated Port Type interface. It ships two frontends, `krasa` and `krasa-jaxws`, selected with `-frontend`.

The two XJC plugins work on the schema and the frontends on the WSDL handed to CXF: nothing else is shared between them, and none of them needs the others to be configured.

Release
----------------

```xml
<dependency>
    <groupId>com.fillumina</groupId>
    <artifactId>krasa-jaxb-tools</artifactId>
    <version>2.7.0</version>
</dependency>
```

Versions
----------------

The latest release is **2.7.0**: two new options, a new frontend, an option alias, and the
dependencies moved forward.

- `exclude` leaves chosen classes, properties or annotations out of the generated annotations, or
  changes one parameter of one of them — a different message, for example — without touching the
  schema.
- `generateValidOnCollections` can stop the plugin from writing `@Valid` on a collection, which Bean
  Validation deprecated because the annotation belongs on the type argument.
- The new `krasa-jaxws` frontend runs CXF's own generators beside the validated interface, so a
  single invocation can produce both.
- The plugin also answers to `-XBeanValidationAnnotations`, the name the specification gives it, while
  `-XJsr303Annotations` keeps working.

See [CHANGELOG.md](CHANGELOG.md) for the full history of every release.

## This project is in maintenance — new projects will follow

**This project** (`com.fillumina:krasa-jaxb-tools`)

- **JDK 8** toolchain, **XJC 2.3.x** and **CXF 3.5.x** — the newest line of each that still
  supports JDK 8 — with the `javax` validation API. Usable from newer JDKs (see the examples
  below), but it will not move to XJC 4.x or to `jakarta`.
- It takes **only changes that cannot break a build that already depends on it**: fixes and new
  options from the issues, with every default left exactly as it is.
- Its name, URL and Maven coordinates stay where they are, so nothing has to be re-pointed.

**New projects, one per tool** (their own names and coordinates, announced later)

- Built from a copy of these sources, and breaking compatibility from the first commit: the two
  worlds cannot be carried in one codebase.
- JDK 21, XJC 4.x, CXF 4.1, `jakarta.validation` 3.x.
- The collection annotations (`@EachPattern`, `@EachSize`, `@EachDecimalMin`, …) replaced by the
  standard **container element constraints** — `List<@Pattern(regexp = "…") String>` — with
  `@Valid` moved to the type argument
  ([#33](https://github.com/fillumina/krasa-jaxb-tools/issues/33)).
- The three tools conflated here — the `-XBeanValidationAnnotations` generator, the `-XReplacePrimitives`
  replacer and the CXF `krasa` frontend — separated, each into its own project, so a build takes
  only what it uses.
- **New features land here**: these projects are where further development happens.

**Both populations are covered:**

- A project that moves to JDK 21, XJC 4.x and `jakarta.validation` finds the same features in the new
  projects, in a modern package.
- A project that has to stay on a JDK 8 toolchain stays here, and this project stays maintained for
  it — fixes and new options, with every default left exactly as it is.

The one difficult case is staying on JDK 8 *and* needing the new standards — `jakarta.validation`,
or the annotations the specification moved from containers to type arguments. That combination is
the only unstable ground in this arrangement, and it gets a **best-effort, optional-only** answer:
a new option where one can be written, never a change to what the current defaults generate. If it
is your case, an [issue](https://github.com/fillumina/krasa-jaxb-tools/issues) describing it is what
tells us how far that effort should go.

## Example projects

There are 2 example projects containing many different plugins and configurations available for reference (each new version of this plugin is tested against these two projects):

- [GitHub - fillumina/krasa-jaxb-tools-jdk21-example: Examples of usage of krasa-jaxb-tools using latest technologies (Java 21)](https://github.com/fillumina/krasa-jaxb-tools-jdk21-example) as the name suggests it's compiled with **JDK 21** and provides working examples of many different plugins using both XJC and CXF configured with both `javax` and `jakarta` packages using the latest versions available.

- [GitHub - fillumina/krasa-jaxb-tools-example: Sample project for https://github.com/fillumina/krasa-jaxb-tools](https://github.com/fillumina/krasa-jaxb-tools-example) uses **JDK 8** and provides examples using the latest versions of plugins and dependencies available for that java version.

## The XJC plugin `BeanValidationAnnotations`

A **XJC plugin** that writes Bean Validation 2.0 or [JSR 380](https://jcp.org/en/jsr/detail?id=380)
(not fully supported) annotations into the classes XJC generates, in the `javax` or the `jakarta`
package. It writes annotations only and never changes the generated types; it is configured with the
options below.

It answers to two names: **`-XBeanValidationAnnotations`**, the one to use in a new build, and
`-XJsr303Annotations`, which is where this plugin started — its option name says JSR 303, while the
annotations it writes are those of Bean Validation 2.0. **The old name is not retired**: it keeps
working exactly as it does today, so no existing build has to be re-pointed, and either name takes the
same options.

```
-XBeanValidationAnnotations
-XBeanValidationAnnotations:validationAnnotations=jakarta
-XJsr303Annotations:targetNamespace=http://www.foo.com/bar
```

Both are accepted everywhere the plugin is: with the `cxf-codegen-plugin` that means either
`-xjc-XBeanValidationAnnotations` or `-xjc-XJsr303Annotations`.

Example with the [maven-jaxb2-plugin](https://github.com/highsource/maven-jaxb2-plugin), the wiring of [krasa-maven-jaxb2-plugin-example](https://github.com/fillumina/krasa-jaxb-tools-example/tree/master/krasa-maven-jaxb2-plugin-example):

```xml
<plugin>
  <groupId>org.jvnet.jaxb2.maven2</groupId>
  <artifactId>maven-jaxb2-plugin</artifactId>
  <executions>
    <execution>
      <goals>
        <goal>generate</goal>
      </goals>
      <configuration>
        <extension>true</extension>
        <args>
          <arg>-XBeanValidationAnnotations</arg>
          <arg>-XBeanValidationAnnotations:targetNamespace=a</arg>
        </args>
        <plugins>
          <plugin>
            <groupId>com.fillumina</groupId>
            <artifactId>krasa-jaxb-tools</artifactId>
            <version>${krasa-jaxb-tools.version}</version>
          </plugin>
        </plugins>
      </configuration>
    </execution>
  </executions>
</plugin>
```

The plugin goes among the XJC run's `plugins`, and its options are passed as `-XBeanValidationAnnotations:…` arguments. With the `cxf-codegen-plugin` the same arguments are prefixed with `-xjc-`, see [The CXF frontends](#the-cxf-frontends-krasa-and-krasa-jaxws).

### Options

- `verbose` (boolean, default=`false`) print verbose messages to output
  example: `-XBeanValidationAnnotations:verbose=true`
- `validationAnnotations` (`javax` | `jakarta`, default=`javax`): selects the library to use for validation annotations
  example: `-XBeanValidationAnnotations:validationAnnotations=javax`
- `targetNamespace` (string): adds @Valid annotation only if the element has the given namespace
  example: `-XBeanValidationAnnotations:targetNamespace=a`
- `generateNotNullAnnotations` (boolean, default=`true`): adds a `@NotNull` annotation if an element has `minOccurs` not 0, is `required` or is not `nillable`.
  examples: `-XBeanValidationAnnotations:generateNotNullAnnotations=true`
- `notNullAnnotationsCustomMessages` (boolean or string, default=`false`): values are `true`, `FieldName`, `ClassName`, or an *actual message* (see further explanation in a note down below)
  example: `-XBeanValidationAnnotations:notNullAnnotationsCustomMessages=ClassName`
- `generateListAnnotations` (boolean, optional, default `false`) generates [validator-collection annotations](https://github.com/jirutka/validator-collection) annotations
  example: `-XBeanValidationAnnotations:generateListAnnotations=true`
  **Warning**: the [validator-collection](https://github.com/jirutka/validator-collection) library is
  `javax`-only and unmaintained, so these `@Each*` constraints work with an old `javax` provider
  only. With a recent one (Hibernate Validator 6 and later) validation fails with a
  `ConstraintDefinitionException`; under `validationAnnotations=jakarta` no provider recognises them
  and the constraints are **silently not enforced**, while the build stays green. To get the same checks
  in a form every provider understands, use the container element constraints the new projects announced
  above generate — `List<@Size(max = 5) String>` — or set this option to `false`.
- `generateValidOnCollections` (boolean, default=`true`): adds a `@Valid` annotation to a collection. Bean Validation deprecated `@Valid` on a container ([HV000271](https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/)) and asks for it on the type argument - `List<@Valid Foo>` - which this generator cannot write, so turning the option off drops it from the container instead. **It is a workaround, not a fix**: with it off, the elements of a collection are no longer validated through the parent object. It is meant for the day a provider stops honouring the old form, when the annotation would be dead weight; the form the specification asks for is generated by the new projects announced above.
  example: `-XBeanValidationAnnotations:generateValidOnCollections=false`
- `generateServiceValidationAnnotations` (string, accepts: `in`, `out`, `inout`, works with  `apache-cxf` only) adds `@Valid` annotations to respective message direction (in, out or both).
  example: `-XBeanValidationAnnotations:generateServiceValidationAnnotations=inout`
- `generateAllNumericConstraints` (boolean, defaults to `false`) generates all `@DecimalMin` and `@DecimalMax` even those regarding the natural boundaries of the referred java type.
  example: `-XBeanValidationAnnotations:generateAllNumericConstraints=true`
- `multiPattern` (boolean, default: `false`) uses a multiple javax validation `@Pattern` instead of `@Pattern.List` (see [3.2. Applying multiple constraints of the same type](https://beanvalidation.org/2.0-jsr380/spec/#constraintsdefinitionimplementation-multipleconstraints))

- `exclude` (string, repeatable, optional): leaves chosen classes, properties or annotations out of the
  generated code, sets a parameter of one of them, or writes another annotation in its place. Its shape is
  `Class[#property][@Annotation][:parameter = value][=@Annotation(...)]`, it is described in the section
  below, and it is the only option which changes the annotations the schema produced.

### `exclude`: leaving annotations out, or setting their parameters

The annotations are derived from the schema, and sometimes that is not what the generated code needs:
one constraint has to go, or its message has to say something else. `exclude` changes what is written for
a chosen class, property or annotation, without touching the schema and without touching the rest of the
generated code.

A statement has these parts, and every name in it is a glob (`*`, `?`; every other character literal):

| part | what it means |
|---|---|
| `Class` | the **qualified** name of the generated class |
| `#property` | the property name; without it, every property of the class is covered |
| `@Annotation` | the simple name of an annotation the plugin computed for it — `NotNull`, `Size`, `Pattern`, `Valid`, `EachSize`, …; without it, all of them are covered |
| `:parameter = value` | sets that parameter and keeps the rest of the annotation as it was |
| `= @Annotation(...)` | writes this annotation instead of the covered ones |

The option can be repeated, several statements may cover one property, and they apply in the order given:

```
-XBeanValidationAnnotations:exclude=com.example.RootType#code
    every annotation of that property is left out
-XBeanValidationAnnotations:exclude=*RootType#*
    the same for the whole class, '*RootType' matching com.example.RootType
-XBeanValidationAnnotations:exclude=*#label@NotNull
    only @NotNull is left out, so @Size and the rest of that property stay
-XBeanValidationAnnotations:exclude=*#amount@Decimal*
    both decimal constraints go, @NotNull stays
-XBeanValidationAnnotations:exclude=*#label@Size:message = at most {max} characters
    @Size keeps the min and max the plugin computed and takes that message
-XBeanValidationAnnotations:exclude=*#label=@Size(max = {max})
    only a @Size of yours is written, out of the values the plugin had computed
-XBeanValidationAnnotations:exclude=*#label@NotNull=@NotNull(message = "required")
    the computed @NotNull becomes that one, and @Size stays
```

**Placeholders.** In a replacement and in a parameter value, `{…}` is substituted once, so a value that
came from a placeholder is not scanned again — a regexp containing `{2}` is safe:

- `{className}` and `{fieldName}` — the generated class, qualified, and the property;
- any parameter of the annotation being written, with the value the plugin computed for it: `{max}`,
  `{min}`, `{regexp}`, `{value}`, `{inclusive}`, `{fraction}`, `{integer}`, …;
- the annotation's own default for a parameter the plugin does not write: `{message}` is
  `{javax.validation.constraints.NotNull.message}` in a `javax` build, the `jakarta` one in a `jakarta`
  build.

An unknown name stops the generation with the list of the names that exist, rather than writing braces
into your code.

**What it will not do.** A replacement may name only the annotations this plugin manages: the bean
validation constraints, `@Valid` and the `@Each*` annotations of validator-collection. Nothing else about
it is checked — if it does not compile, the compiler of the generated code is what says so. And a
statement that matched no class, no property, or none of the annotations it names is reported as a
warning, because a typo would otherwise leave the annotations in place, silently.

**When not to reach for it.** The annotations come from the schema, and a statement that changes them
makes the generated code diverge from it, with nothing keeping the two in step afterwards. It is meant
for a message, for a pattern the XSD dialect and Java do not agree on, and for the one constraint a field
must not carry — not for maintaining constraints by hand.

### Notes

- Arguments accepting booleans can either be given the value `true` as with `verbose=true` or simply be left without a value at all and that will be interpreted as being `true`  (you can omit the `=` too).

- All arguments are optional.

#### About `notNullAnnotationsCustomMessages`

**`@NotNull`** default validation message is not always helpful, so it can be customized with **-XBeanValidationAnnotations:notNullAnnotationsCustomMessages=OPTION** where **OPTION** is one of the following:

- `false` default: no custom message
- `true` message is present but equivalent to the default: **"{javax.validation.constraints.NotNull.message}"**
- `FieldName` field name is prefixed to the default message: **"fieldName {javax.validation.constraints.NotNull.message}"**
- `ClassName` class and field name are prefixed to the default message: **"ClassName.fieldName {javax.validation.constraints.NotNull.message}"**
- `other-non-empty-text` arbitrary message, with substitutable, case-sensitive parameters `{ClassName}` and `{FieldName}`, i.e.: **"Class {ClassName} field {FieldName} non-null"**

#### About `generateServiceValidationAnnotations`

Bean validation policy can be customized with `-XBeanValidationAnnotations:generateServiceValidationAnnotations=OPTION` where OPTION is one of the following (the option is case insensitive):

- `InOut` (default: validate requests and responses)
- `In` (validate only requests)
- `Out` (validate only responses)

Using this option requires one of the frontends of this project as the CXF plugin's front end — `-frontend krasa` or `-frontend krasa-jaxws` — see [The CXF frontends](#the-cxf-frontends-krasa-and-krasa-jaxws).

### Supported annotations

The plugin generates sources annotated with the following Java Bean Validation 2.0 (JSR 380) annotations (with either `javax` or `jakarta` packages depending on the configuration, see `-XBeanValidationAnnotations:validationAnnotations=javax`):

- `@Valid` annotation for all complex types, can be further restricted to generate only for types from defined schema: `-XBeanValidationAnnotations:targetNamespace=http://www.foo.com/bar`
- `@NotNull` annotation for objects that has a MinOccur value >= 1 or for required attributes
- `@Size` for lists that have minOccurs > 1
- `@Size` if there is a maxLength or minLength or length restriction
- `@DecimalMax` for maxInclusive restriction
- `@DecimalMin` for minInclusive restriction
- `@DecimalMax` for maxExclusive restriction, with `inclusive = false`
- `@DecimalMin` for minExclusive restriction, with `inclusive = false`
- `@Digits` if there is a totalDigits or fractionDigits restriction.
- `@Pattern` and `@PatternList` if there is a Pattern restriction; strings only — numeric patterns are not supported, see [Numeric patterns are not supported](#numeric-patterns-are-not-supported)

**That is only part of JSR 380.** The generator writes the constraints a schema can express, where a
JAXB field or parameter allows them — the annotations above, on a field, a getter or a parameter —
and not:

- the constraints that belong to a **type argument** — `List<@Pattern(regexp = "…") String>`, and
  `@Valid` on the type argument rather than on the container. A collection carries either `@Valid` on
  the container (deprecated, see `generateValidOnCollections`) or the `@Each*` annotations of
  [validator-collection](https://github.com/jirutka/validator-collection), a third-party stand-in for
  the same constraints; the form the specification asks for is what the new projects announced above
  are for.
- the JSR 380 constraints a schema has no source for — `@Email`, `@NotEmpty`, `@NotBlank`, the sign
  constraints (`@Positive`, `@Negative`, …) and the date constraints (`@PastOrPresent`,
  `@FutureOrPresent`) — which are not derived from anything.

### Numeric patterns are not supported

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

#### What to write instead

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

#### If the schema cannot be changed

There is a pre-pass: rewrite the patterns into facets before the generator runs, leaving the
original schema in place. It is a workaround for schemas that are not yours to change, **not** a
feature of the plugin, and it is documented on its own — stylesheet, the verified Maven wiring and
the proof of both its halves — in [doc/numeric-patterns-prepass.md](doc/numeric-patterns-prepass.md).

Whatever it produces is for **code generation only**: it drops the pattern, so it is *not*
equivalent as a validation contract — keep validating against the original schema.

Numeric `xsd:enumeration` restrictions are not validated: no annotation is generated for them. A
value pinned by `fixed` is translated, as a fixed range (`minInclusive` + `maxInclusive` with the same
value).

## The XJC plugin `ReplacePrimitives`

A **XJC plugin**, in the same artifact but independent of `BeanValidationAnnotations`, that replaces the primitive types of the generated classes with the corresponding boxed ones (`int` -> `Integer`). It is enabled with `-XReplacePrimitives` and takes no option; it is used in the [krasa-cxf-codegen-plugin-example](https://github.com/fillumina/krasa-jaxb-tools-example/tree/master/krasa-cxf-codegen-plugin-example) project as an example.

In a `maven-jaxb2-plugin` run it is one more XJC argument:

```xml
<args>
  <arg>-XReplacePrimitives</arg>
  <arg>-XhashCode</arg>
  <arg>-Xequals</arg>
</args>
```

**WARNING:** must be defined before `XhashCode` or `Xequals`.

## The CXF frontends `krasa` and `krasa-jaxws`

An [Apache Cxf plugin](https://cxf.apache.org/docs/tools.html) that runs as a CXF *frontend*, selected with `-frontend`. It adds the `javax` or `jakarta` `@Valid` annotation to the SOAP methods and their parameters of the generated Port Type interface (`generateServiceValidationAnnotations`), and it ships two frontends, which differ only in the generators they run:

- **`krasa`** — the frontend this project has always shipped. It declares the `ValidSEIGenerator` only, so next to the JAXB types it writes the port type interface with the `@Valid` annotations and nothing else. No CXF generator is registered under this name, so **CXF's own switches have no effect**: with `-all`, `-client`, `-server` or `-impl` you still get that one interface.
- **`krasa-jaxws`** — new in 2.7.0. The same `ValidSEIGenerator` plus the seven generators CXF's own `jaxws` frontend declares — `AntGenerator`, `ClientGenerator`, `FaultGenerator`, `ImplGenerator`, `SEIGenerator`, `ServerGenerator`, `ServiceGenerator` — so a single invocation writes the validated interface **and** the classes those generators produce.

### The frontends bring CXF's XJC onto your build classpath

A frontend needs CXF's WSDL-to-Java tooling, and CXF's `cxf-tools-common` declares a JAXB XJC of its
own — `jaxb-xjc` 2.3.5 with CXF 3.5.11. Adding this plugin therefore puts a 2.3.x XJC on the build
classpath, and if your build has a newer one, Maven chooses between them by its nearest-wins rule
rather than by which one you asked for. **The plugin does not read binding files itself**, so when XJC
complains about yours it is complaining about the XJC that won:

- the **2.3 line** knows only `http://java.sun.com/xml/ns/jaxb`, the Java EE namespace;
- the **3.x and 4.x lines** know only `https://jakarta.ee/xml/ns/jaxb`, the jakarta one.

A binding file written for one of them cannot be read by the other. To see which XJC your build
resolves:

```
mvn dependency:tree -Dincludes=org.glassfish.jaxb:jaxb-xjc
```

Two ways out, depending on whether you need the jakarta namespace in that file:

- **stay on the 2.3 line** and write the binding file in the namespace it knows — no dependency change
  at all, and the simplest thing while you use this library;
- **exclude CXF's tooling from the plugin dependency**, so your own XJC is the one that runs:

```xml
<exclusion>
  <groupId>org.apache.cxf</groupId>
  <artifactId>cxf-tools-wsdlto-frontend-jaxws</artifactId>
</exclusion>
```

The annotations plugin itself runs on XJC 4.x once that is out of the way; what it cannot do is emit
the `jakarta` annotations from an XJC 4 model. That combination — and the separation of the three
tools, so that a validation-only build never pulls CXF at all — is what the new projects announced
above are for.

`krasa` was deliberately left alone: adding those generators to it would change what every current user generates, with extra `Client`, `Server`, `Fault` and `Impl` classes appearing in their builds. The new name is additive, and `krasa` keeps generating exactly what it generated before.

Example with the `cxf-codegen-plugin` — the wiring of [krasa-cxf-codegen-plugin-example](https://github.com/fillumina/krasa-jaxb-tools-example/tree/master/krasa-cxf-codegen-plugin-example), with the frontend and the XJC options this project adds:

```xml
<plugin>
  <groupId>org.apache.cxf</groupId>
  <artifactId>cxf-codegen-plugin</artifactId>
  <executions>
    <execution>
      <phase>generate-sources</phase>
      <goals>
        <goal>wsdl2java</goal>
      </goals>
      <configuration>
        <wsdlOptions>
          <wsdlOption>
            <wsdl>${project.basedir}/wsdl/Hello.wsdl</wsdl>
            <extraargs>
              <!-- the frontend -->
              <extraarg>-frontend</extraarg>
              <extraarg>krasa-jaxws</extraarg>
              <!-- XJC options, prefixed with -xjc- -->
              <extraarg>-xjc-XBeanValidationAnnotations</extraarg>
              <extraarg>-xjc-XBeanValidationAnnotations:generateServiceValidationAnnotations=InOut</extraarg>
              <extraarg>-xjc-XReplacePrimitives</extraarg>
            </extraargs>
          </wsdlOption>
        </wsdlOptions>
      </configuration>
    </execution>
  </executions>
  <dependencies>
    <dependency>
      <groupId>com.fillumina</groupId>
      <artifactId>krasa-jaxb-tools</artifactId>
      <version>${krasa-jaxb-tools.version}</version>
    </dependency>
  </dependencies>
</plugin>
```

On the command line the frontend is the same argument: `wsdl2java -frontend krasa-jaxws wsdl/service.wsdl`.

With the default switches `krasa-jaxws` writes the JAXB types, the interface with its `@Valid` annotations, the service class and the fault exception classes; `-all` (or `-client`, `-server`, `-impl`, `-ant`) adds the corresponding client, server, implementation and Ant artifacts — the switches that do nothing under `krasa`.

If the annotated interface is all you want, keep `krasa` — `krasa-jaxws` adds nothing to it.

Both names are guarded by tests: one asserts that `krasa-jaxws` writes the CXF classes next to the validated interface while `krasa` writes the interface alone, another reads CXF's own `META-INF/tools-plugin.xml`, so that a CXF upgrade cannot silently change what the new frontend generates.

JDK 1.8 Support
----------------

The project is bounded to support **Java 8** (**JDK 1.8**) because of some old projects still requiring it. All dependencies are selected from the latest available versions still supporting that.

## Note on submitting issues and bugfixes

Any issue or bug fix reported is *extremely* welcome but to help me understand the problem and reduce the time to publish the fix I kindly ask to comply to these roles:

- **Issues** should contain a detailed description **and an example** to show how and when the code is failing. It might be a failing test or a simple github project.

- **Fixes** should contain a test that proves the solution to the problem (and the test should be present in a previous commit to the fix to show that it fails). It is appreciated that the solution contains as *little refactoring* as possible and is focused only on *fixing one issue*. Each fix should focus on one specific issue.

- **Case insensitive filesystem** users (wich is the default on Windows and MacOS) should be careful about file naming especially considering that _signatures_ files (the ones containing the expected annotations) usually are named after the class they refer to so their first letter is often a capital one (ie: `multiplePatternsWithBase-a-annotation.txt` should probably be `MultiplePatternsWithBase-a-annotation.txt`)

