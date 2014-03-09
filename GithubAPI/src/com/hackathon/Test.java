package com.hackathon;

/**
 * @author arshsab
 * @since 03 2014
 */

public class Test {
    public static void main(String... args) {
        API api = new API();

        api.get("rate_limit", new Callback() {
            @Override
            public void onComplete(String data) {
                System.out.println(data);
            }
        });
    }
}
