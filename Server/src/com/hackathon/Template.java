package com.hackathon;

import java.io.*;
import java.util.HashMap;

/**
 * @author Adrian Chmielewski-Anders
 * @since 0.0.1
 */

public class Template implements Defineable {

    @Override
    public String define() {
        return "";
    }

    public String set(HashMap<String, String> vars) {
        return replace(vars, define());
    }

    public String load(HashMap<String, String> vars, File f) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            while (true) {
                String str = br.readLine();
                if (str == null) {
                    break;
                }
                sb.append(str);
            }
        } catch (IOException e) {
            return "";
        }
        return replace(vars, sb.toString());
    }

    private String replace(HashMap<String, String> vars, String template) {
        for (String key : vars.keySet()) {
            template = template.replace("$" + key, vars.get(key));
        }
        return template;
    }

    public String forEach(HashMap<String, String[]> vars, File f) {
        return forEach(vars, readTemplate(f));
    }

    public String forEach(HashMap<String, String[]> vars) {
        return forEach(vars, define());
    }

    private String forEach(HashMap<String, String[]> vars, String template) {
        int length = vars.values().iterator().next().length;
        for (String[] s : vars.values()) {
            if (s.length != length) {
                return "";
            }
        }
        StringBuilder master = new StringBuilder();
        for (int i = 0; i < length; i++) {
            String child = template;
            for (String s : vars.keySet()) {
                child = child.replace("$" + s, vars.get(s)[i]);
            }
            master.append(child);
        }
        return master.toString();
    }

    private String readTemplate(File f) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            while (true) {
                String str = br.readLine();
                if (str == null) {
                    break;
                }
                sb.append(str);
            }
        } catch (IOException e) {
            return "";
        }
        return sb.toString();
    }
}
