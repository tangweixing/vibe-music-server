package cn.edu.seig.vibemusic.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Vibe Music 音乐平台API文档")
                        .description("Vibe Music 后端接口的详细说明与调试")
                        .version("1.0.0"));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("音乐平台API")
                .packagesToScan("cn.edu.seig.vibemusic.controller") // 替换为你的Controller包路径
                .build();
    }
}