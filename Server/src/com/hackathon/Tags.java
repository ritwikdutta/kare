package com.hackathon;
/**
 * All return Strings
 *
 * @author Adrian Chmielewski-Anders
 * @since 0.0.1
 */

public class Tags {

    public static final String
            header = "<!DOCTYPE html>",
            html   = "<html>",
            head   = "<head>",
            body   = "<body>",
            div    = "<div>",
            h1     = "<h1>",
            h2     = "<h2>",
            h3     = "<h3>",
            h4     = "<h4>",
            h5     = "<h5>",
            h6     = "<h6>",
            a      = "<a>",
            script = "<script>",
            link   = "<link>";

    public static String close(String tag) {
        return "</" + tag.substring(1);
    }


    private static String build(String... args) {
        StringBuilder builder = new StringBuilder();
        for (String s : args) {
            builder.append(s);
        }
        return builder.toString();
    }

    public static String html(String... args) {
        return header + html + build(args) + close(html);
    }

    public static String head(String... args) {
        return head + build(args) + close(head);
    }

    public static String body(String... args) {
        return body + build(args) + close(body);
    }

    public static String div(String... args) {
        return div + build(args) + close(div);
    }

    public static String h1(String... args) {
        return h1 + build(args) + close(h1);
    }

    public static String h2(String... args) {
        return h2 + build(args) + close(h2);
    }

    public static String h3(String... args) {
        return h3 + build(args) + close(h3);
    }

    public static String a(String href, String... args) {
        return a.substring(0, 2) + " href=\"" + href + "\">" + build(args) + close(a);
    }

    public static String script(String... args) {
        StringBuilder scripts = new StringBuilder();
        for (String s : args) {
            scripts.append(script.substring(0, 7) + " src=\"" + s + "\">" + close(script));
        }
        return scripts.toString();
    }

    public static String link(String... args) {
        StringBuilder links = new StringBuilder();
        for (String s : args) {
            links.append(link.substring(0, 5) + " rel=\"stylesheet\" type=\"text/css\" href=\"" + s + "\">");
        }
        return links.toString();
    }
}
