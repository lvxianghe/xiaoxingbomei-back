package org.xiaoxingbomei.config.datasource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * 本地数据库连接配置
 */
// 标注该类是一个配置类
@Configuration
// 指定 MyBatis 扫描的 Mapper 接口包路径，并指定使用的 SqlSessionTemplate
@MapperScan(basePackages = "org.xiaoxingbomei.dao.localhost", sqlSessionTemplateRef = "localhostSqlSessionTemplate")
public class MysqlConfigOfLocal
{
    // 第二数据源
    @Bean
    @ConfigurationProperties(prefix = "datasource.localhost")
    public DataSource localhostDataSource() {
        return DataSourceBuilder.create().build();
    }

    // 第二数据源的 SqlSessionFactory
    @Bean
    public SqlSessionFactory localhostSqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(localhostDataSource());
        
        // 设置Mapper XML文件的位置（从Java目录下查找）
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:org/xiaoxingbomei/dao/localhost/*.xml");
        factoryBean.setMapperLocations(resources);
        
        return factoryBean.getObject();
    }

    // 第二数据源的 SqlSessionTemplate
    @Bean
    public SqlSessionTemplate localhostSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
