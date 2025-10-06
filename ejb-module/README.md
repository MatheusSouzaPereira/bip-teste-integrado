# ejb-module

Este módulo foi configurado como um projeto EJB com Jakarta EE 10.

O que foi adicionado:
- pom.xml com dependências do Jakarta EE (escopo provided) e Hibernate + H2 para testes locais
- persistence.xml (RESOURCE_LOCAL) em META-INF com H2 em memória
- Entidade: com.example.ejb.entity.Beneficio (já existia)
- EJB Stateless: com.example.ejb.service.BeneficioServiceBean + interface local
- DevRunner (main) para validar a configuração JPA localmente sem um servidor

Como compilar
- É necessário ter o Maven instalado. Execute:
  mvn -f ejb-module/pom.xml clean package

Como rodar localmente (apenas JPA, sem container EJB)
- Depois de compilar, rode a classe main com sua IDE ou via Maven Exec Plugin (se desejar adicionar):
  Classe: com.example.ejb.DevRunner
  Ela irá criar a tabela em H2 em memória e inserir um registro de exemplo.

Como implantar como EJB em servidor
- Gere o artefato .ejb (na prática é um .jar) com o comando de build
- Faça o deploy em um servidor compatível com Jakarta EE 10 (ex.: WildFly 28+, Payara 6+)
- O persistence.xml pode ser ajustado para usar JTA/DataSource do servidor; atualmente está configurado como RESOURCE_LOCAL para execução local.

Integração remota (backend Spring Boot)
- Este módulo expõe a interface remota `com.example.ejb.service.remote.BeneficioServiceRemote` no bean `BeneficioServiceBean`.
- Exemplos de JNDI name por servidor:
  - WildFly: `ejb:/ejb-module/BeneficioServiceBean!com.example.ejb.service.remote.BeneficioServiceRemote`
  - Payara/GlassFish: `java:global/ejb-module/BeneficioServiceBean!com.example.ejb.service.remote.BeneficioServiceRemote`

Ajustes de produção
- Altere o persistence.xml para transaction-type="JTA" e configure um datasource JNDI do seu servidor
- Remova dependências de provider em tempo de execução (hibernate/h2) se o servidor já prover, mantendo apenas jakarta.jakartaee-api como provided
