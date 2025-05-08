package mil.llm.tools;

import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SidcAssembler {

    @Tool("Generates 30-character SIDC from extracted components")
    public String assembleSidc(String standardIdentity, String echelon, String entityCode, String symbolSet) {
        StringBuilder sidc = new StringBuilder();
        sidc.append("13")       // Version (2 digits)
        .append(standardIdentity)   // Standard Identity(2 digits)
        .append(symbolSet)          // Symbol Set (2 digits)
        .append("0")            // Status (1 digits)
        .append("0")            // HQ/TF/Dummy 1 digits)   
        .append(echelon)            // Echelon (2 digits)
        .append(entityCode)         // Entity Code (6 digits)
        .append("00")           // Modifier 1 (2 digits)
        .append("00")           // Modifier 2 (2 digits)
        .append("0000000000");  // Reserve (10 digits)
        return sidc.toString();
    }

}
