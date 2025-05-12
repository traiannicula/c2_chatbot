# c2_chatbot


This project is a proof-of-concept C2 chatbot designed to extract land units and their associated locations from natural language input. Once a unit is identified, the system derives its 30-character Symbol Identification Code (SIDC), extracts the unit’s unique designator, and generates a brief summary of its activities. If a location is mentioned, it is geocoded to obtain its latitude and longitude. All extracted data is visualized on a map using MIL-STD-2525D symbology and returned to the user in structured JSON format.
 

The application is built using the Quarkus Framework, leveraging its powerful extensions to support backend development. Integration with large language models (LLMs) is achieved through the Quarkus LangChain4j extension.

  

For mapping functionality, the project uses the excellent OpenLayers JavaScript library <https://openlayers.org/> The map is rendered using OpenStreetMap (OSM) as the base tile layer, offering a clean and open-source-friendly solution.

  

## Configutation

  

This project relies entirely on an Azure OpenAI-hosted language model’ In order to run this application in dev mode the proper configuration keys must be added:

 - LLM Configuration: 
    ```shell script
    quarkus.langchain4j.azure-openai.api-key=<your-api-key>
    quarkus.langchain4j.azure-openai.api-version=2024-08-01-preview
    quarkus.langchain4j.azure-openai.resource-name=<resource-name>
    quarkus.langchain4j.azure-openai.deployment-name=gpt-4o
    ```

	For debugging
    ```shell script
    quarkus.langchain4j.log-requests=true
    quarkus.langchain4j.log-responses=true
    ```

 - Geocoding URL
    ```shell script
    quarkus.rest-client.geocoding.url=http://api.geonames.org
    ```

 - Postgresql

Quarkus supports the automatic provisioning of unconfigured services in development and test model, using Testcontainers. In order to use most Dev Services with zero configuration you will need a working Docker container environment.


## Running the application in dev mode
You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```


