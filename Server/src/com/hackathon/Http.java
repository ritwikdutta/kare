package com.hackathon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Adrian Chmielewski-Anders
 * @since 0.0.1
 */

public class Http {
    public static String read(String where) throws IOException {
        URL url = new URL(where);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();
        while (true) {
            String str = br.readLine();
            if (str == null) {
                break;
            }
            sb.append(str);
        }
        return sb.toString();
    }
}
