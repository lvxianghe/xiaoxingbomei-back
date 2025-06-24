package org.xiaoxingbomei;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class Server_Common
{
    public static void main(String[] args)
    {
        SpringApplication.run(Server_Common.class, args);
    }
}