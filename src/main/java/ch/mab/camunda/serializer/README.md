# Camunda custom serializers example with local date

A common use case is declaring Jackson annotations in custom classes to finetune JSON handling. 
With relocated dependencies, annotations in the com.fasterxml.jackson namespace will not be recognized by Spin.
In that case, consider using camunda-spin-core. 

Keep in mind the implications this may have as described in the Integration Use Cases section. // checkout the camunda doc.
**Note that the package relocation means that you cannot develop against the original namespaces.**
Example: camunda-spin-dataformat-json-jackson uses jackson-databind for object (de-)serialization.

- https://github.com/camunda/camunda-bpm-examples/tree/master/spin/dataformat-configuration-global
- https://docs.camunda.org/manual/7.14/user-guide/data-formats/configuring-spin-integration/#camunda-engine-plugin-spin

When a data format is accessed for the very first time, Spin performs a look up of all data formats on the classpath. 
After having instantiated the data formats, Spin detects so-called data format configurators and calls these with the detected format instances. 
Users can provide custom configurators to influence the way a data format serializes and deserializes objects, which is what this example shows.

## Important

- Check the spin configuration for your camunda project

My spring boot project uses following dependencies:

        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.8.8</version>
        </dependency>

        <dependency>
            <groupId>com.fasterxml.jackson.datatype</groupId>
            <artifactId>jackson-datatype-jsr310</artifactId>
        </dependency>

        <dependency>
            <groupId>org.camunda.spin</groupId>
            <artifactId>camunda-spin-core</artifactId>
        </dependency>

        <dependency>
            <groupId>org.camunda.bpm</groupId>
            <artifactId>camunda-engine-plugin-spin</artifactId>
        </dependency>

## Occurring errors

### Error 1

    Cannot find serializer for value 
    'ObjectValue [value=ch.mab.camunda.serializer.Event@3b9206a7, isDeserialized=true, 
    serializationDataFormat=application/json, objectTypeName=null, serializedValue=null, isTransient=false]'.
    org.camunda.bpm.engine.ProcessEngineException: Cannot find serializer for value
    'ObjectValue [value=ch.mab.camunda.serializer.Event@3b9206a7, isDeserialized=true, s
    erializationDataFormat=application/json, objectTypeName=null, serializedValue=null, isTransient=false]'.


### Error 2

    Caused by: spinjar.com.fasterxml.jackson.databind.exc.InvalidDefinitionException: 
    Cannot construct instance of `java.time.LocalDate` (no Creators, like default construct, exist): 
    cannot deserialize from Object value (no delegate- or property-based Creator)
    at [Source: UNKNOWN; line: -1, column: -1] (through reference chain: ch.mab.camunda.serializer.Event["startDate"])





