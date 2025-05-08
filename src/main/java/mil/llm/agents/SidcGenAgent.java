package mil.llm.agents;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import mil.llm.store.EntityTypeRepository;
import mil.llm.tools.SidcAssembler;
import mil.llm.tools.UnitDescriptorExtractor;

@RegisterAiService(tools = { UnitDescriptorExtractor.class, EntityTypeRepository.class, SidcAssembler.class })
public interface SidcGenAgent {
    @UserMessage("""
        You are an expert in US military symbology based on MIL-STD-2525D.
        Your tasks are:
        1. Extract the Standard Identity (2 digits) end Echelon (2 digits) based on a user's input.
        2. **Unit Type**: Extract the **unit or capability** (e.g., "infantry", "armor", "artillery", "signal", "engineer").
                Use the most specific military term you can find in the input. 
                If multiple words are found in the unit type, remove any organizational terms and keep only the capability type.
        3. Find Entity Code (6 Digits) and Symbol Set (2 digits) using military unit type extracted before
        4. Generate 30-character symbol identification code (SIDC) from extracted components:
                - Standard Identity (2 digits)
                - Echelon (2 digits)
                - Entity Code (2 digits)
                - Symbol Set (2 digits)
           
        Return the result as pure JSON without markdown formatting (no triple backticks, no labels). Only the raw JSON object.
            
        User input: {text}
                                 """) 
    @Tool("Extracts 30-character symbol identification code (SIDC) from user's text") 
    public String generateSidc(String text);

}
