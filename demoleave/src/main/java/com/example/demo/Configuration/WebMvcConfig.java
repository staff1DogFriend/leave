package com.example.demo.Configuration;
import com.example.demo.Controller.EmployeeController;
import com.example.demo.Interceptor.AdminInterceptor;
import com.example.demo.Interceptor.EmployeeInterceptor;
import com.example.demo.Interceptor.ManagerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan(basePackages = "com.example.demo")
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private AdminInterceptor adminInterceptor;
    @Autowired
    private ManagerInterceptor managerInterceptor;
    @Autowired
    private EmployeeInterceptor employeeInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminInterceptor).addPathPatterns("/Admin/**");
        registry.addInterceptor(managerInterceptor).addPathPatterns("/Manager/**");
        registry.addInterceptor(employeeInterceptor).addPathPatterns("/Employee/**");
    }
}
