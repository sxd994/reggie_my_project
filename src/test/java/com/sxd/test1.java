package com.sxd;

import org.junit.jupiter.api.Test;

public class test1 {
    @Test
    public void test(){
        String fileName = "sxdfasdf.jpg";
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        System.out.println(suffix);
        String[] split = fileName.split(".");
        for (int i = 0; i < split.length; i++) {
            System.out.println(split[i]);
        }

    }
}
