package mil.llm.store;

import java.io.*;
import java.nio.file.*;

public class Importer {

    public static void csvToSql(String csvFilePath, String outputFilePath) {
        String tableName = Paths.get(csvFilePath).getFileName().toString().replace(".csv", "");
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath, true))) {

            String headerLine = br.readLine();
            if (headerLine == null) {
                throw new IllegalArgumentException("CSV file is empty.");
            }

            String[] columns = headerLine.split(",");
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append(" (");

                // Append column names
                sql.append(String.join(", ", columns)).append(") VALUES (");

                // Append values
                for (int i = 0; i < values.length; i++) {
                    sql.append("'").append(values[i].replace("'", "''")).append("'");
                    if (i < values.length - 1) {
                        sql.append(", ");
                    }
                }
                sql.append(");");

                // Write SQL statement to the output file
                bw.write(sql.toString());
                bw.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String csvFilePath = "src/main/resources/data/entity_type.csv";
        String outputFilePath = "src/main/resources/import.sql";
        csvToSql(csvFilePath, outputFilePath);
        System.out.println("SQL statements generated and appended to " + outputFilePath);
    }
}
