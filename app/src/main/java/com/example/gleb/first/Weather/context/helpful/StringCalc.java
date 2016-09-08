package com.example.gleb.first.Weather.context.helpful;

/**
 * Created by gleb on 07.09.16.
 */
public class StringCalc {
    public static int countMaxTrim(String[] strings){
        int min_lenght = strings[0].length();
        for (String str : strings){
            min_lenght = min_lenght > str.length() ? str.length() : min_lenght;
        }
        return min_lenght;
    }
}
