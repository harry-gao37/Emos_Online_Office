package com.yifu.emos.config;

import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.*;

/**
 * @auther YIFU GAO
 * @date 2022/12/23/22:40
 * TODO using Swagger3 to provide api info page
 */
@EnableWebMvc
@Configuration
public class SwaggerConfig {
    @Bean
    public Docket createRestApi(){
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build()
                .protocols(newHashSet("https","http"))
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }


    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("Api Documentation")
                .build();

    }

    private List<SecurityScheme> securitySchemes(){
        ApiKey apiKey= new ApiKey("token","token", "header");
        return Collections.singletonList(apiKey);
    }

    private List<SecurityContext> securityContexts(){
        return Collections.singletonList(
                SecurityContext.builder().securityReferences(Collections.singletonList(new SecurityReference("token",new AuthorizationScope[]{new AuthorizationScope("global","accessEverything")})))
                        .build()
        );
    }

    @SafeVarargs
    private final <T> Set<T> newHashSet(T... ts){
        if (ts.length >0){
            return new LinkedHashSet<>(Arrays.asList(ts));
        }
        return null;
    }
}
