package security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("security")
public record SecurityProps(List<PublicPath> publicPaths) {
    public record PublicPath(String method, String path) {}

    public SecurityProps {
        publicPaths = publicPaths == null ? List.of() : publicPaths;
        System.out.println(publicPaths.toString());

    }
}