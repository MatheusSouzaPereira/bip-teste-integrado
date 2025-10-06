package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;

@Configuration
public class EJBClientConfig {

    @Bean
    public Context ejbInitialContext(Environment env) throws NamingException {
        Hashtable<String, Object> props = new Hashtable<>();
        putIfPresent(props, Context.INITIAL_CONTEXT_FACTORY, env.getProperty("ejb.jndi.java.naming.factory.initial"));
        putIfPresent(props, Context.PROVIDER_URL, env.getProperty("ejb.jndi.java.naming.provider.url"));
        putIfPresent(props, Context.SECURITY_PRINCIPAL, env.getProperty("ejb.jndi.java.naming.security.principal"));
        putIfPresent(props, Context.SECURITY_CREDENTIALS, env.getProperty("ejb.jndi.java.naming.security.credentials"));
        // WildFly specific optional flag
        putIfPresent(props, "jboss.naming.client.ejb.context", env.getProperty("ejb.jndi.jboss.naming.client.ejb.context"));

        if (props.isEmpty()) {
            return new InitialContext();
        }
        return new InitialContext(props);
    }

    private static void putIfPresent(Hashtable<String, Object> map, String key, String value) {
        if (value != null && !value.isBlank()) {
            map.put(key, value);
        }
    }
}
