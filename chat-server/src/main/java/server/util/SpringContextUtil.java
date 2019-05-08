package server.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Author: Dunfu Peng
 * @Date: 2019/4/5 13:45
 */
@Slf4j
public class SpringContextUtil {

    private SpringContextUtil() {
    }

    //Spring应用上下文环境
    private static ApplicationContext applicationContext;

    static {
        applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
    }

    public static <T> T getBean(String beanId) {
        T bean = null;
        try {
            if (StringUtils.isNotEmpty(StringUtils.trim(beanId))) {
                bean = (T) applicationContext.getBean(beanId);
            }
        } catch (NoSuchBeanDefinitionException e) {
            log.error("获取Bean失败");
            return null;
        }
        return bean;
    }

    public static <T> T getBean(String... partName) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < partName.length; i++) {
            sb.append(partName[i]);
            if (i != partName.length - 1) {
                sb.append(".");
            }
        }
        return getBean(sb.toString());
    }
}
