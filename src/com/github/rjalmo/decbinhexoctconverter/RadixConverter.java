package com.github.rjalmo.decbinhexoctconverter;

public final class RadixConverter {
    
    public static String decToBin(String numstr) {
        return Long.toBinaryString(Long.parseLong(numstr));
    }
    
    public static String decToHex(String numstr) {
        return Long.toHexString(Long.parseLong(numstr));
    }
    
    public static String decToOct(String numstr) {
        return Long.toOctalString(Long.parseLong(numstr));
    }
    
    public static String toDec(String numstr, ConvertType from) {
        int radix = from.getRadix();
        if (numstr.isEmpty() || radix < 1) return "0";
        if (radix == 10) return numstr; // already base 10
        
        long sum = 0;
        int len = numstr.length();
        for (int i=len; i>0; --i) {
            sum += Character.getNumericValue(numstr.charAt(len-i)) * Math.pow(radix, i-1);
        }
        return Long.valueOf(sum).toString();
    }
    
    public static String toBin(String numstr, ConvertType from) {
        switch (from) {
        case BIN: return numstr;
        default:  return decToBin(toDec(numstr, from));
        }
    }
    
    public static String toHex(String numstr, ConvertType from) {
        switch (from) {
        case HEX: return numstr;
        default:  return decToHex(toDec(numstr, from));
        }
    }
    
    public static String toOct(String numstr, ConvertType from) {
        switch (from) {
        case OCT: return numstr;
        default:  return decToOct(toDec(numstr, from));
        }
    }
    
}
