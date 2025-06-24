package org.xiaoxingbomei;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.xiaoxingbomei.common.config.springboot.MyBanner;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

@SpringBootApplication(exclude = { 
    DataSourceAutoConfiguration.class
})
@ServletComponentScan
@EnableAspectJAutoProxy
@EnableScheduling
@EnableAsync
@Slf4j
public class Server_Ai
{
    public static void main(String[] args) throws UnknownHostException
    {

        //
        long start = System.currentTimeMillis();

        // 创建SpringApplication实例
        SpringApplication app = new SpringApplication(Server_Ai.class);

        // 设置自定义的banner
        app.setBanner(new MyBanner());

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

        // 打印自定义注册的Bean信息
        printCustomBeans(application);

    }

    /**
     * 打印自定义注册的Bean信息
     */
    private static void printCustomBeans(ConfigurableApplicationContext application)
    {
        log.info("\n--------------------以下是自定义注册的 Bean 信息----------------------\n");

        // 获取所有Bean名称
        String[] beanNames = application.getBeanDefinitionNames();
        Arrays.sort(beanNames); // 按名称排序，方便查看


        for (String beanName : beanNames)
        {
            if (application.containsBean(beanName))
            { // 判断是否存在
                Object bean = application.getBean(beanName);
                if (bean.getClass().getPackage() != null && bean.getClass().getPackage().getName().startsWith("org.xiaoxingbomei"))
                {
                    log.info("Bean 名称: {}, Bean 类型: {}", beanName, bean.getClass().getName());
                }
            } else
            {
                log.warn("Bean 名称: {} 未在容器中找到！", beanName);
            }
        }

        log.info("\n----------------------------------------------------------\n");
    }

}

