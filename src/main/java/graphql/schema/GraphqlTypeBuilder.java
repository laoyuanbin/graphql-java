package graphql.schema;

import com.google.common.collect.ImmutableList;
import graphql.Internal;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static graphql.util.FpKit.valuesToList;

@Internal
public abstract class GraphqlTypeBuilder {

    protected String name;
    protected String description;
    protected GraphqlTypeComparatorRegistry comparatorRegistry = GraphqlTypeComparatorRegistry.AS_IS_REGISTRY;

    GraphqlTypeBuilder name(String name) {
        this.name = name;
        return this;
    }

    GraphqlTypeBuilder description(String description) {
        this.description = description;
        return this;
    }

    GraphqlTypeBuilder comparatorRegistry(GraphqlTypeComparatorRegistry comparatorRegistry) {
        this.comparatorRegistry = comparatorRegistry;
        return this;
    }


    <T extends GraphQLSchemaElement> List<T> sort(Map<String, T> types, Class<? extends GraphQLSchemaElement> parentType, Class<? extends GraphQLSchemaElement> elementType) {
        return sort(valuesToList(types), parentType, elementType);
    }

    <T extends GraphQLSchemaElement> List<T> sort(List<T> types, Class<? extends GraphQLSchemaElement> parentType, Class<? extends GraphQLSchemaElement> elementType) {
        Comparator<? super GraphQLSchemaElement> comparator = getComparatorImpl(comparatorRegistry, parentType, elementType);
        return ImmutableList.copyOf(GraphqlTypeComparators.sortTypes(comparator, types));
    }

    Comparator<? super GraphQLSchemaElement> getComparator(Class<? extends GraphQLSchemaElement> parentType, Class<? extends GraphQLNamedSchemaElement> elementType) {
        return getComparatorImpl(comparatorRegistry, parentType, elementType);
    }

    private static Comparator<? super GraphQLSchemaElement> getComparatorImpl(GraphqlTypeComparatorRegistry comparatorRegistry, Class<? extends GraphQLSchemaElement> parentType, Class<? extends GraphQLSchemaElement> elementType) {
        GraphqlTypeComparatorEnvironment environment = GraphqlTypeComparatorEnvironment.newEnvironment()
                .parentType(parentType)
                .elementType(elementType)
                .build();
        return comparatorRegistry.getComparator(environment);
    }
}