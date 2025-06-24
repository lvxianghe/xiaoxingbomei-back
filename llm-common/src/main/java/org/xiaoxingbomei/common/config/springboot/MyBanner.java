package org.xiaoxingbomei.common.config.springboot;


import org.springframework.boot.Banner;
import org.springframework.core.env.Environment;

import java.io.PrintStream;

public class MyBanner implements Banner
{

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out)
    {
        System.out.println("██     ██    ██     ██   ██ ██  █████  ███    ██  ██████      ██   ██ ███████     \n" +
                "██     ██    ██      ██ ██  ██ ██   ██ ████   ██ ██           ██   ██ ██          \n" +
                "██     ██    ██       ███   ██ ███████ ██ ██  ██ ██   ███     ███████ █████       \n" +
                "██      ██  ██       ██ ██  ██ ██   ██ ██  ██ ██ ██    ██     ██   ██ ██          \n" +
                "███████  ████       ██   ██ ██ ██   ██ ██   ████  ██████      ██   ██ ███████     \n" +
                "                                                                                  \n" +
                "                                                                                  ");
    }
}
