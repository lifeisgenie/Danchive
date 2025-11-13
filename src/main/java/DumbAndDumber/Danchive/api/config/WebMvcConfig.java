package DumbAndDumber.Danchive.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Path;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Value("${file.storage.root:./uploads}")
    private String rootDir;
    @Value("${file.storage.public-base-url:/files/}")
    private String publicBaseUrl;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String handler = publicBaseUrl.endsWith("/") ? publicBaseUrl + "**" : publicBaseUrl + "/**";
        String location = "file:" + Path.of(rootDir).toAbsolutePath().normalize().toString() + "/";
        registry.addResourceHandler(handler).addResourceLocations(location);
    }
}