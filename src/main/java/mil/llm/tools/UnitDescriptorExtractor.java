package mil.llm.tools;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RegisterAiService(chatMemoryProviderSupplier = RegisterAiService.NoChatMemoryProviderSupplier.class)
public interface UnitDescriptorExtractor {

    @Tool("Extracts the Standard Identity and Echelon code from a text")
    @UserMessage("""
            You are an expert in US military symbology based on MIL-STD-2525D.

            Your task is to extract:
            1. **Standard Identity Code** (2-digit):
                - Pending → 00
                - Unknown → 01
                - Assumed Friend → 02
                - Friend → 03
                - Neutral → 04
                - Suspect / Joker → 05
                - Hostile / Faker → 06
                - Default: 03 (Friend) if unclear.

                Synonyms:
                - "enemy", "hostile", "opponent", "aggressor" → Hostile
                - "own", "our", "friendly", "allied", "us forces" → Friend
                - "unknown", "unidentified" → Unknown
                - "possible friend", "likely ally" → Assumed Friend
                - "civilian", "non-combatant" → Neutral

            2. **Echelon Code** (2-digit):
                - Team/Crew → 11
                - Squad → 12
                - Section → 13
                - Platoon/Detachment → 14
                - Company/Battery/Troop → 15
                - Battalion/Squadron → 16
                - Regiment/Group → 17
                - Brigade → 18
                - Division → 21
                - Corps/MEF → 22
                - Army → 23
                - Army Group/Front → 24
                - Region/Theater → 25
                - Command → 26
                - Default: 00 if not found.

                Abbreviations:
                - bn → Battalion
                - bde → Brigade
                - div → Division
                - sqdn → Squadron

            Return only a compact JSON like:

            {
              "standard_identity": "03",
              "echelon": "16"
            }

                        User input: {text}
                    """)
    String extractDescriptors(String text);
}
