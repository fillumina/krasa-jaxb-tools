package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.xml.xsom.XSListSimpleType;
import com.sun.xml.xsom.XSRestrictionSimpleType;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.impl.ListSimpleTypeImpl;
import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Starting from a base type it goes up the hierarchy and consolidates all inherited facets.
 *
 * @author Francesco Illuminati
 */
public class HierarchyFacetGatherer {

    public static AccumulatorFacet gatherRestrictions(XSSimpleType type) {
        if (type == null) {
            return AccumulatorFacet.EMPTY;
        }
        AccumulatorFacet facet = new AccumulatorFacet();
        navigateUpTheHierarchy(facet, type);
        consolidatePatterns(facet);
        return facet;
    }

    /**
     * @return true when the schema type is an {@code xsd:list}, whatever restriction hides it, in
     *     which case the field holds the items of the value rather than the occurrences of the
     *     element it comes from.
     */
    public static boolean isListType(XSSimpleType type) {
        XSSimpleType current = type;
        while (current != null) {
            if (current instanceof XSListSimpleType || current instanceof ListSimpleTypeImpl) {
                return true;
            }
            final XSSimpleType base = current.getSimpleBaseType();
            current = base == current ? null : base;
        }
        return false;
    }

    private static void consolidatePatterns(AccumulatorFacet facet) {
        final LinkedHashSet<LinkedHashSet<String>> multiPatterns = facet.getMultiPatterns();
        final LinkedHashSet<String> multiEnumerations = facet.getMultiEnumerations();

        if (! (multiPatterns.isEmpty() && multiEnumerations.isEmpty()) ) {
            if (multiPatterns.size() > 1) {
                Utils.addIfNotNullOrEmpty(multiPatterns, multiEnumerations, Collection::isEmpty);
            } else if (!multiEnumerations.isEmpty()) {
                if (multiPatterns.isEmpty()) {
                    multiPatterns.add(new LinkedHashSet<>());
                }
                multiPatterns.iterator().next().addAll(multiEnumerations);
            }
        }

    }

    private static void navigateUpTheHierarchy(AccumulatorFacet facet, XSSimpleType type) {
        XSSimpleType baseType = null;
        if (type instanceof XSListSimpleType) {
            baseType = type.getBaseListType();
        } else if (type instanceof XSRestrictionSimpleType) {
            baseType = type.getSimpleBaseType();
        }

        if ((baseType == null || baseType == type) && type instanceof ListSimpleTypeImpl) {
            baseType = ((ListSimpleTypeImpl)type).getItemType();
            facet = facet.createItemFacet();
        }
        if (baseType != null && baseType != type) {
            navigateUpTheHierarchy(facet, baseType);
        }

        final XSSimpleTypeFacet typeFacet = new XSSimpleTypeFacet(type);
        facet.apply(typeFacet);
    }

}
