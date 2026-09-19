<?xml version="1.0" encoding="UTF-8"?>
<!--
  numeric-patterns-to-facets.xsl

  Rewrites a *literal* numeric xsd:pattern into a fixed range, e.g.

      <xsd:pattern value="-1\.5"/>   ->   <xsd:minInclusive value="-1.5"/>
                                             <xsd:maxInclusive value="-1.5"/>

  so that an annotation generator sees a numeric facet instead of a pattern.

  Use the output for CODE GENERATION ONLY, and keep the original schema as the one you
  validate against: this stylesheet drops the pattern, so the two are not equivalent as
  validation contracts.

  Anything else on a numeric type stops the build (xsl:message terminate), so a
  constraint can never be silently weakened. Patterns on non-numeric types are copied
  untouched.

  Limits: only direct built-in base names are recognised (base="xsd:int" and friends).
  A restriction of a *named* type is left alone - safe, but not converted; add a type
  map if your schema needs that.

  XSLT 1.0, so it runs with xsltproc, Saxon, or any Maven XSLT step.
-->
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema">

  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

  <!-- built-in XSD numeric types; single spaces, because they are matched as ' name ' -->
  <xsl:variable name="numericTypes"
      select="' byte short int long integer decimal float double unsignedByte unsignedShort unsignedInt unsignedLong nonNegativeInteger positiveInteger negativeInteger nonPositiveInteger '"/>

  <!-- identity: copy everything that no other template matches -->
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="xsd:pattern">
    <!-- local part of the base QName: 'xsd:int' -> 'int', 'int' -> 'int' -->
    <xsl:variable name="type">
      <xsl:choose>
        <xsl:when test="contains(../@base, ':')">
          <xsl:value-of select="substring-after(../@base, ':')"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="../@base"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:choose>
      <!-- not a numeric restriction: copy the pattern unchanged -->
      <xsl:when test="not(contains($numericTypes, concat(' ', $type, ' ')))">
        <xsl:copy>
          <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
      </xsl:when>

      <xsl:otherwise>
        <!-- a pattern is a regexp, so the decimal point is escaped: unescape it -->
        <xsl:variable name="value" select="translate(normalize-space(@value), '\', '')"/>
        <xsl:choose>
          <!-- exactly one value: a fixed range says the same thing -->
          <xsl:when test="string(number($value)) = $value">
            <xsd:minInclusive value="{$value}"/>
            <xsd:maxInclusive value="{$value}"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:message terminate="yes">numeric pattern cannot be expressed as a range: "<xsl:value-of select="@value"/>" on <xsl:value-of select="../@base"/></xsl:message>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
