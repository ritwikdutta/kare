package com.hackathon;

/**
 * @author arshsab
 * @since 03 2014
 */

public interface Callback {
    public void onComplete(String data);
    public void onComplete(String data, String opt);
}
