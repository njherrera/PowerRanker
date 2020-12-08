package com.mucholabs;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.simmetrics.Distance;

public class StringDistanceFinder {

    public double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2; shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) { return 1.0; /* both strings are zero length */ }
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
    }

    public int editDistance(String s1, String s2){
        LevenshteinDistance newCalculation = new LevenshteinDistance();
        return newCalculation.apply(s1, s2);
    }
}
