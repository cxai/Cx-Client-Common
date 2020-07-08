package com.cx.utility;

import com.cx.restclient.ast.dto.sca.AstScaConfig;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public final class TestingUtils {

    public static Properties getProps(String propsName, Class<?> clazz) throws IOException {
        Properties properties = new Properties();
        ClassLoader classLoader = clazz.getClassLoader();
        URL resource = classLoader.getResource(propsName);
        if (resource == null) {
            throw new IOException(String.format("Resource '%s' is not found.", propsName));
        }
        properties.load(new FileReader(resource.getFile()));

        return properties;
    }

    public static AstScaConfig getScaConfig(Properties props, boolean useOnPremiseAuthentication) {
        String accessControlProp, usernameProp, passwordProp;
        if (useOnPremiseAuthentication) {
            accessControlProp = "sca.onPremise.accessControlUrl";
            usernameProp = "sca.onPremise.username";
            passwordProp = "sca.onPremise.password";
        } else {
            accessControlProp = "sca.cloud.accessControlUrl";
            usernameProp = "sca.cloud.username";
            passwordProp = "sca.cloud.password";
        }

        AstScaConfig result = new AstScaConfig();
        result.setApiUrl(props.getProperty("sca.apiUrl"));
        result.setWebAppUrl(props.getProperty("sca.webAppUrl"));
        result.setTenant(props.getProperty("sca.tenant"));
        result.setAccessControlUrl(props.getProperty(accessControlProp));
        result.setUsername(props.getProperty(usernameProp));
        result.setPassword(props.getProperty(passwordProp));
        return result;
    }
}
