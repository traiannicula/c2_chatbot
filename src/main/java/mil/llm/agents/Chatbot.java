package mil.llm.agents;

import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.smallrye.mutiny.Multi;
import mil.llm.geocoding.GeocodingService;

@RegisterAiService(tools = { SidcGenAgent.class, GeocodingService.class, MapUpdater.class })
public interface Chatbot {

    @UserMessage("""
        You are an expert in US military symbology based on MIL-STD-2525D.
        IF the user input is not related to military units, respond with "I am not able to help you with that".
        Your tasks are:
        1. Identify all military units mentioned in the user input. A unit can be a division, brigade, battalion, company, etc.
        2. For each unit, extract the following information:
            a. Extract the symbol identification code (SIDC) based on a user's input.
            b. Extract unit name (e.g. 2nd Infantry Division - name is 2, 1st Armored Brigade - name is 1 ).
            c. Generate a summary of the unit description or actions based on input text.
            d. Extracts the associated location (city or named place) mentioned in relation to the militay unit identified in the input text.
            e. Find latitude, longitude and the correct name of the location, if the city name exists.
            f. Create a JSON object with the following fields:
                - sidc: 30-character symbol identification code (SIDC) 
                - unit_name: name of the unit
                - summary: summary of the unit description or actions based on input text
                - location: the correct location name if it exists or 'unknown_location' if there is no location name in text,
                - admin1: the admin1 name of found location, null otherwise
                - country: the country name of found location, null otherwise
                - coordinates: an array containing longitute and latitude (eg. [26.035409, 44.404558]) of found location, null otherwise
                - type: 'military'.
            g. Update map with the geocoding information in JSON format previously generated
        3. Use this information to generate a user-friendly summary describing each unit's current status, location, and activity.
            - Mention the unit name, and action.
            - Include the location and geocoordinates where available.
            - Format the result as a readable summary of ongoing military operations.
        4. End with a sentence confirming that the map was updated with the extracted information.

        User input: {text}
                                                """)
    public Multi<String> chat(String text);
}
