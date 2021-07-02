package controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CsvToJsonTransformator {

    String[] headers;
    public CsvToJsonTransformator(String[] headers) {
        this.headers = headers;
    }
    public void makeJsonFileOutOfCsvLine(String[] information , String pathToWriteJsons) {
        StringBuilder jsonString = new StringBuilder("{");
        for (int keyNumber = 0; keyNumber < headers.length; keyNumber++) {
            String key = headers[keyNumber], value = information[keyNumber];
            if (!value.equals("e"))
            {
            jsonString.append("\"").append(key).append("\"").append(":");
            int integerValue;
            try {
                integerValue = Integer.parseInt(value);
                jsonString.append(integerValue);
            } catch (NumberFormatException numberFormatException) {
                jsonString.append("\"").append(value).append("\"");
            }
                if (keyNumber != headers.length - 1)
                    jsonString.append(",");
            }

            if (keyNumber == headers.length - 1) {
                if (jsonString.charAt(jsonString.length()-1) == ',')
                    jsonString.replace(jsonString.length()-1 , jsonString.length() , "");
                jsonString.append('}');
            }
        }
        try {
            File jsonFile = new File(pathToWriteJsons + information[0].replace("\\s+" , "") + ".json");
            jsonFile.createNewFile();
            FileWriter fileWriter = new FileWriter(pathToWriteJsons + information[0].replace("\\s+" , "") + ".json");
            fileWriter.write(String.valueOf(jsonString).replace("  ", ", "));
            fileWriter.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
