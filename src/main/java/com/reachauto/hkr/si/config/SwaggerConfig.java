package com.reachauto.hkr.si.config;

import com.google.common.base.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

/**
 * Created with IntelliJ IDEA.
 * User: chenxiangning
 * Date: 2016/4/28
 * Time: 0:02
 * ...............................
 */
@Configuration
@EnableSwagger2
@Slf4j
public class SwaggerConfig {

    private static final int LENGTH = 2;

    @Value("${server.port}")
    private String port;
    private String wildcard = "*";

    public SwaggerConfig() {
        //无参构造方法
    }

    private static ApiInfo initApiInfo() {
        return new ApiInfo("支付服务 HKR-SI API",//大标题
                "支付服务。",//简单的描述
                "2.0",//版本
                "服务条款",
                new Contact("氢氪出行开发团队", "", ""),//作者
                "氢氪出行",//链接显示文字
                "http://hkr.evershare.cn/"//网站链接
        );
    }

    /**
     * 设置过滤规则
     * 这里的过滤规则支持正则匹配
     *
     * @return
     */
    private static Predicate<String> doFilteringRules() {
        return or(
                regex("/api/v.*")
        );
    }

    @Bean
    public Docket restfulApi() {

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("RestfulApi")
                .genericModelSubstitutes(ResponseEntity.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(false)
                .select()
                .paths(doFilteringRules())
                .build()
                .apiInfo(initApiInfo());
    }


}
