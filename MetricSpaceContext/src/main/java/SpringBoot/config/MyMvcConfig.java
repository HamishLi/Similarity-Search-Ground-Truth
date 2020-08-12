/*
package com.tosit.fourteen.init.config;

import com.tosit.fourteen.init.component.LoginHandlerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//使用WebMvcConfigurerAdapter可以来扩展SpringMVC的功能
//@EnableWebMvc   不要接管SpringMVC
//
@Configuration
//public class MyMvcConfig extends WebMvcConfigurationSupport {
public class MyMvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // super.addViewControllers(registry);
        //浏览器发送 /atguigu 请求来到 success
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/index.html").setViewName("index");
        registry.addViewController("/login.html").setViewName("login");
    }

    //注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //静态资源；  *.css , *.js
        //SpringBoot已经做好了静态资源映射
        registry.addInterceptor(new LoginHandlerInterceptor()).addPathPatterns("/**")
                .excludePathPatterns("/assets/**", "/index.html", "/", "/login", "/login.html","change_password.html");
    }


        //所有的WebMvcConfigurerAdapter组件都会一起起作用
    //@Bean //将组件注册在容器
    public WebMvcConfigurationSupport webMvcConfigurerAdapter() {
        WebMvcConfigurationSupport adapter = new WebMvcConfigurationSupport() {
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addViewController("/").setViewName("login");
                registry.addViewController("/login.html").setViewName("login");
                registry.addViewController("/index.html").setViewName("index");
            }

            //注册拦截器
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                //super.addInterceptors(registry);
                //静态资源；  *.css , *.js
                //SpringBoot已经做好了静态资源映射
               registry.addInterceptor(new LoginHandlerInterceptor()).addPathPatterns("/**")
                        .excludePathPatterns("/index.html", "/", "/login", "/login.html","change_password.html");
            }
        };
        return adapter;
    }

}
*/
