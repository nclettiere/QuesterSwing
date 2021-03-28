package com.valhalla.application.gui;

import java.awt.*;
import java.io.InputStream;

public class Utils {
    public static class Vector2D
    {
        Vector2D(int x, int y) { this.x = x; this.y = y; }
        public int x;
        public int y;
    };

    // get a file from the resources folder
    // works everywhere, IDEA, unit test and JAR file.
    public InputStream getFileFromResourceAsStream(String fileName) {

        // The class loader that loaded the class
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        // the stream holding the file content
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        }
    }

    public Font getFontFromResource (String fileName) {
        InputStream is;
        try {
            is = getFileFromResourceAsStream(fileName);
            return Font.createFont(Font.TRUETYPE_FONT, is);
        }catch(IllegalArgumentException e) {
            System.out.println("Font file not found!\n" + e.getMessage());
            return null;
        }catch (Exception e) {
            return null;
        }
    }
}
