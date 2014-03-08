package com.hackathon;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by ritwik on 3/8/14.
 */
public class Parser {

    private static ArrayList<String> tags = null;

    public static void main(String... args) throws IOException {
        ArrayList<String> keys = getKeywords("JavaScript jQuery BootStrap CDNto");
        for (int i = 0; i < keys.size(); i++) {
            System.out.println(keys.get(i));
        }

    }

    public static ArrayList<String> getKeywords(String readme) throws IOException {
        if (tags == null) {
            init();
        }

        readme = readme.replaceAll("[\\W]|_", " ");
        String[] words = readme.split(" ");
        ArrayList<String> wordList= new ArrayList<>();

        for (int i = 0; i < 101; i++) {
            wordList.add(words[i]);
        }
        ArrayList<String> keyWords = new ArrayList<>();
        for (String word: wordList) {
            if (tags.indexOf(word) != -1 && keyWords.indexOf(word) == -1) {
                keyWords.add(word);
            }
        }
        return keyWords;
    }

    private static void init() throws IOException {
        System.out.println(new File("MultiPlexer/Data/tags.csv").getAbsolutePath());

        BufferedReader fileReader = new BufferedReader(new FileReader("MultiPlexer/Data/tags.csv"));

        while (true) {
            String str = fileReader.readLine();
            if (str == null) { break; }
            tags.add(str.replaceAll("[\\W]|_", " "));
        }

    }
}
