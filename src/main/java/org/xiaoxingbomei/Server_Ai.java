package org.xiaoxingbomei;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@EnableAspectJAutoProxy
@Slf4j
public class Server_Ai
{
    public static void main(String[] args) throws UnknownHostException
    {
        long start = System.currentTimeMillis();

        // 创建SpringApplication实例
        SpringApplication app = new SpringApplication(Server_Ai.class);

        // 启动springboot应用并获取上下文
        ConfigurableApplicationContext application  = app.run(args);

        //
        Environment env                             = application.getEnvironment();
        String ip                                   = InetAddress.getLocalHost().getHostAddress();
        String applicationName                      = env.getProperty("spring.application.name");
        String port                                 = env.getProperty("server.port");
        String path                                 = env.getProperty("server.servlet.context-path");

        //
        if (StringUtils.isEmpty(path) || "/".equals(path))
        {
            path = "";
        }

        // 打印系统信息
        log.info("\n----------------------------------------------------------\n\t{}{}{}{}{}",
                applicationName + " is running, Access URLs:",
                "\n\t Local    访问网址: \t http://localhost:"  + port + path,
                "\n\t External 访问网址: \t http://" + ip + ":" + port + path,
                "\n\t Swagger  访问网址: \t http://" + ip + ":" + port + path + "/swagger-ui/index.html",
                "\n----------------------------------------------------------\n");
        log.info("服务启动成功!>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 耗时：{} s", (System.currentTimeMillis() - start) / 1000);

    }

}
