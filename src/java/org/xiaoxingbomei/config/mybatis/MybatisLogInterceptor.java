package org.xiaoxingbomei.config.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.stereotype.Component;
import org.xiaoxingbomei.aspect.ControllerLogAspectByPath;
import org.xiaoxingbomei.entity.request.GlobalRequestContext;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;


/**
 * mybatis log 拦截器
 */
@Slf4j
@Component
@Intercepts
        ({
        @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
        @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }),
//        @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class,BoundSql.class })
        })
public class MybatisLogInterceptor implements Interceptor
{

    @Override
    public Object intercept(Invocation invocation) throws Throwable
    {
        // 记录开始时间
        long startTime = System.currentTimeMillis();

        log.info("MybatisLogInterceptor start");

        // 获取请求上下文信息
        GlobalRequestContext requestContext = ControllerLogAspectByPath.getGlobalRequestContext();
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = null;
        // 继续执行并记录结束时间
        if (invocation.getArgs().length > 1)
        {
            parameter = invocation.getArgs()[1];
        }
        String sqlId = mappedStatement.getId();
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        Configuration configuration = mappedStatement.getConfiguration();

        String sql = showSql(configuration, boundSql);

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // 美化打印样式
        log.info("\n----------------------------------------------------------\n\t{}{}{}{}{}{}{}{}",
                " << mybatis complete sql log >>",
                "\n\t [request ip]   \t:    \t"  + requestContext.getIp(),
                "\n\t [request url]  \t:    \t"  + requestContext.getUrl(),
                "\n\t [request path] \t:    \t"  + requestContext.getClassName()+requestContext.getMethodName(),
                "\n\t [sql spendTime]\t:    \t"  + executionTime+"ms",
                "\n\t [sql Id]       \t:    \t"  + sqlId,
                "\n\t [complete sql] \t: \n \t"  + sql,
                "\n----------------------------------------------------------\n");


        return invocation.proceed();
    }


    private static String getParameterValue(Object obj)
    {
        String value = null;

        // 判断对象是否为字符串类型
        if (obj instanceof String)
        {
            // 如果是字符串类型，则用单引号包裹字符串值
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date)
        {
            // 如果是日期类型，则格式化日期并用 `to_date` 包裹
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "to_date('" + formatter.format(obj) + "','yyyy-MM-dd hh24:mi:ss')";
        } else
        {
            // 如果对象不为空，则直接将其转换为字符串
            if (obj != null)
            {
                value = obj.toString();
            } else
            {
                // 如果对象为空，则将值设为空字符串
                value = "";
            }
        }
        return value; // 返回格式化后的参数值
    }


    public static String showSql(Configuration configuration, BoundSql boundSql)
    {
        // 获取 SQL 语句的参数对象
        Object parameterObject = boundSql.getParameterObject();
        // 获取 SQL 语句中的参数映射
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        // 获取并格式化 SQL 语句，去除多余的空白字符
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");

        // 检查是否存在参数映射并且参数对象不为空
        if (parameterMappings.size() > 0 && parameterObject != null) {
            // 获取 TypeHandler 注册器
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();

            // 检查参数对象是否有对应的 TypeHandler
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                // 如果有，则直接替换第一个 "?" 为参数值
                sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));
            } else {
                // 否则创建参数对象的 MetaObject
                MetaObject metaObject = configuration.newMetaObject(parameterObject);

                // 遍历参数映射
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();

                    // 如果参数对象中有对应的 getter 方法
                    if (metaObject.hasGetter(propertyName)) {
                        // 获取参数值并替换 SQL 中的 "?"
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        // 如果参数对象中没有对应的 getter 方法，但 BoundSql 中有额外的参数
                        // 获取额外参数并替换 SQL 中的 "?"
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    }
                }
            }
        }
        return sql; // 返回格式化后的 SQL 语句
    }


    @Override
    public Object plugin(Object target)
    {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {}
}
