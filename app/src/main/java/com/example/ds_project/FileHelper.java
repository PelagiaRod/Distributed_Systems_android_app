package com.example.ds_project;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public final class FileHelper {
    public static ArrayList<String> readFile(String path) {
        ArrayList<String> txtLines = new ArrayList<>();
        try {
            FileReader fReader = new FileReader(path);
            BufferedReader buffReader = new BufferedReader(fReader);

            String line = buffReader.readLine();
            while (line != null) {
                txtLines.add(line.trim()); //strip
                line = buffReader.readLine();
            }
            buffReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return txtLines;
    }

    public static void writeFile(String path, String content) {
        try {
            FileWriter fWriter = new FileWriter(path, true);
            fWriter.write("\n" + content);
            fWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
