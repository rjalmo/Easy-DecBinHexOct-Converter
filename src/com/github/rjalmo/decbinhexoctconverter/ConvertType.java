package com.github.rjalmo.decbinhexoctconverter;

public enum ConvertType {
    
    DEC("Decimal", 10), BIN("Binary", 2), HEX("Hexadecimal", 16), OCT("Octal", 8);
    
    private String mText;
    private int mRadix;
    
    private ConvertType(String text, int radix) {
        mText = text;
        mRadix = radix;
    }
    
    public int getRadix() {
        return mRadix;
    }
    
    public static ConvertType findByString(String s) {
        for (ConvertType t : values()) {
            if (t.toString().equalsIgnoreCase(s)) {
                return t;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return mText;
    }
}
