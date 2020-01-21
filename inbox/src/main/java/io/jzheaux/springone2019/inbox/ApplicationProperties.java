package io.jzheaux.springone2019.inbox;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ApplicationProperties
 * 
 * @author Julius Krah
 */
@Component
@ConfigurationProperties("app")
public class ApplicationProperties {
    private String defaultHost = "example.com";
    private String subdomainTemplate = "{tenant}.example.com";

    public String getDefaultHost() {
        return defaultHost;
    }

    public void setDefaultHost(String defaultHost) {
        this.defaultHost = defaultHost;
    }

    public String getSubdomainTemplate() {
        return subdomainTemplate;
    }

    public void setSubdomainTemplate(String subdomainTemplate) {
        this.subdomainTemplate = subdomainTemplate;
    }
}