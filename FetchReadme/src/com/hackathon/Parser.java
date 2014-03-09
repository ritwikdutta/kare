package com.hackathon;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ritwik on 3/8/14.
 */

public class Parser {

    private static ArrayList<String> tags = null;

    public static void main(String... args) throws IOException {
        ArrayList<String> keys = getKeywords("JavaScript jQuery BootStrap CDNto afsd farts");
        System.out.println(Arrays.toString(keys.toArray()));

    }

    public static ArrayList<String> getKeywords(String readme) throws IOException {
        if (tags == null) {
            init();
        }
        readme = readme.replaceAll("[\\W]|_", " ").toLowerCase();
        String[] words = readme.split(" ");
        ArrayList<String> wordList= new ArrayList<>();

        int len = words.length;
        if (len > 100) {
            len = 100;
        }

        for (int i = 0; i < len; i++) {
            wordList.add(words[i]);
        }
        ArrayList<String> keyWords = new ArrayList<>();
        for (String word: wordList) {
            if (tags.contains(word) && !keyWords.contains(word)) {
                keyWords.add(word);
            }
        }
        System.out.println(Arrays.toString(wordList.toArray()));
        return keyWords;
    }

    private static void init() throws IOException {
        System.out.println(new File("FetchReadme/Data/tags.csv").getAbsolutePath());

        BufferedReader fileReader = new BufferedReader(new FileReader("FetchReadme/Data/tags.csv"));
        tags = new ArrayList<>();
        while (true) {
            String str = fileReader.readLine();
            if (str == null) { break; }
            tags.add(str.replaceAll("[\\W]|_", " ").toLowerCase());
        }

    }
}
