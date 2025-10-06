# backend-module

Este backend Spring Boot foi ajustado para se conectar ao EJB do módulo `ejb-module` via JNDI quando o profile `ejb` estiver ativo.

## Como usar o EJB remoto

1. Faça o deploy do `ejb-module` em um servidor Jakarta EE (ex.: WildFly 28+/Payara 6+).
2. Compile e instale localmente o `ejb-module` (para que a interface remota esteja disponível):
   - mvn -f ejb-module/pom.xml clean install
3. Inicie o backend com o profile `ejb` e configure as propriedades de JNDI conforme o seu servidor.

### Propriedades (application.properties ou variáveis de ambiente)

- ejb.beneficio.jndi: JNDI name do EJB remoto
- ejb.jndi.java.naming.factory.initial: Factory inicial do JNDI
- ejb.jndi.java.naming.provider.url: URL do provedor JNDI
- ejb.jndi.java.naming.security.principal: Usuário (se necessário)
- ejb.jndi.java.naming.security.credentials: Senha (se necessário)
- ejb.jndi.jboss.naming.client.ejb.context: (opcional para WildFly) true

#### Exemplo WildFly (EJB Client remoto)
```
ejb.beneficio.jndi=ejb:/ejb-module/BeneficioServiceBean!com.example.ejb.BeneficioServiceRemote
# Para remote+http-remoting
ejb.jndi.java.naming.factory.initial=org.wildfly.naming.client.WildFlyInitialContextFactory
ejb.jndi.java.naming.provider.url=http-remoting://localhost:8080
# Se usar autenticação de management
# ejb.jndi.java.naming.security.principal=admin
# ejb.jndi.java.naming.security.credentials=adminPassword
# ejb.jndi.jboss.naming.client.ejb.context=true
```

#### Exemplo Payara (GlassFish)
```
ejb.beneficio.jndi=java:global/ejb-module/BeneficioServiceBean!com.example.ejb.BeneficioServiceRemote
ejb.jndi.java.naming.factory.initial=com.sun.enterprise.naming.SerialInitContextFactory
ejb.jndi.java.naming.provider.url=iiop://localhost:3700
```

## Seleção de implementação
- Profile default (sem `-Dspring.profiles.active=ejb`): usa JPA local e o repositório interno (`BeneficioServiceImpl`).
- Profile `ejb`: usa cliente EJB remoto (`BeneficioServiceEjbImpl`).

