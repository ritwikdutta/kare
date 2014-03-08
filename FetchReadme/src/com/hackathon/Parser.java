package com.hackathon;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by ritwik on 3/8/14.
 */
public class Parser {

    public static void main(String... args) throws IOException {
        ArrayList<String> keys = getKeywords("JavaScript jQuery BootStrap CDNto");
        for (int i = 0; i < keys.size(); i++) {
            System.out.println(keys.get(i));
        }

    }

    public static ArrayList<String> getKeywords(String readme) throws IOException {

        System.out.println(new File("MultiPlexer/Data/tags.csv").getAbsolutePath());

        BufferedReader fileReader = new BufferedReader(new FileReader("MultiPlexer/Data/tags.csv"));

        ArrayList<String> tagList = new ArrayList<String>();


        while (true) {
            String str = fileReader.readLine();
            if (str == null) { break; }
            tagList.add(str.replaceAll("[\\W]|_", " "));
        }

        System.out.println(readme);

        readme = readme.replaceAll("[\\W]|_", " ");
        String[] words = readme.split(" ");
        ArrayList<String> wordList= new ArrayList<String>();
        for (int i = 0; i < 101; i++) {
            wordList.add(words[i]);
        }
        ArrayList<String> keyWords = new ArrayList<String>();
        for (String word: wordList) {
            if (tagList.indexOf(word) != -1 && keyWords.indexOf(word) == -1) {
                keyWords.add(word);
            }
        }
        return keyWords;
    }
}
