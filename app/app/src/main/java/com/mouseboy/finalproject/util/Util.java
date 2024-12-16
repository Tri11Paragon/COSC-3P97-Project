package com.mouseboy.finalproject.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Util {
    public static void logThrowable(Throwable e){
        Logger.getGlobal().log(Level.SEVERE, "", e);
    }
}
