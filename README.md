# PokeSAL

Simulador de torneios eliminatórios de batalhas por turnos no terminal. Os treinadores escolhem Pokésals e disputam partidas com precisão, críticos, elementos, itens, status e terreno até a definição do campeão.

## Trabalho acadêmico

**Disciplina:** Testes e Qualidade de Software  
**Etapa:** Segunda unidade

**Equipe:**
- Jonathas Santos
- Mariana Sanches
- João Alcântara

## Funcionalidades

- Torneios eliminatórios com 4 ou 8 treinadores.
- Seis Pokésals iniciais, cada um com três golpes.
- Batalhas por turnos com itens, status e terrenos.
- Precisão, acertos críticos e vantagens elementais.
- Registro de eventos e histórico de resultados.

## Tecnologias e ferramentas

- Java 21
- Maven
- JUnit 5
- Mockito
- JaCoCo
- SonarQube
- Docker
- Spotless
- Checkstyle

## Como executar

Requisitos: JDK 21 e Maven instalados.

```bash
mvn compile
java -cp target/classes br.com.pokesal.Main
```

## Testes e cobertura: JaCoCo

O JUnit executa os testes automatizados, enquanto o JaCoCo mede a cobertura do código.

Para executar os testes e gerar os relatórios:

```bash
mvn clean verify
```

Os relatórios são gerados em:

- HTML: `target/site/jacoco/index.html`
- XML: `target/site/jacoco/jacoco.xml`

O relatório HTML permite consultar a cobertura por pacote, classe, método e linha de código. O XML é utilizado pelo SonarQube para importar os dados de cobertura.

**Observação:** o projeto gera relatórios de cobertura, mas ainda não possui uma meta mínima de cobertura configurada no JaCoCo.

## Formatação e análise de código

O projeto utiliza Spotless e Checkstyle para manter um padrão de organização e formatação.

**Spotless: Google Java Format**

Verificar a formatação:

```bash
mvn spotless:check
```

Aplicar a formatação automaticamente:

```bash
mvn spotless:apply
```

**Checkstyle: Google Checks**

Verificar os padrões de código:

```bash
mvn checkstyle:check
```

## Análise de qualidade: SonarQube

O SonarQube permite analisar a qualidade do código, identificando problemas, possíveis bugs, pontos de melhoria e métricas de cobertura.

Neste projeto, sua execução é feita localmente por meio de um container Docker.

### 1. Iniciar o SonarQube

Com o Docker instalado e em execução:

```bash
docker run -d \
  --name sonarqube \
  -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true \
  -p 9000:9000 \
  -v sonarqube_data:/opt/sonarqube/data \
  -v sonarqube_extensions:/opt/sonarqube/extensions \
  -v sonarqube_logs:/opt/sonarqube/logs \
  sonarqube:latest
```

Acompanhar a inicialização:

```bash
docker logs -f sonarqube
```

### 2. Acessar a interface

Abra o navegador em:

http://localhost:9000

Credenciais iniciais:

- Usuário: `admin`
- Senha: `admin`

No primeiro acesso, será necessário alterar a senha.

### 3. Criar o projeto

Na interface do SonarQube:

1. Selecione **Create new project**.
2. Informe `Pokesal` como nome.
3. Utilize `pokesal` como chave do projeto.
4. Gere um token de autenticação e guarde-o.

### 4. Configurar a autenticação

Na raiz do projeto, configure as variáveis de ambiente:

```bash
export SONAR_TOKEN="TOKEN"
```

Não compartilhe o token nem o adicione ao repositório.

### 5. Executar a análise

Execute:

```bash
mvn clean verify \
  org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
  -Dsonar.projectKey=pokesal \
  -Dsonar.host.url=http://localhost:9000
```

Esse comando executa os testes, gera o relatório do JaCoCo e depois envia a análise para o SonarQube.

O relatório XML do JaCoCo é reconhecido automaticamente pelo scanner em seu caminho padrão.

Após a execução, acesse novamente `http://localhost:9000` e consulte os resultados do PokeSAL.

### 6. Gerenciar o container

Parar:

```bash
docker stop sonarqube
```

Iniciar novamente:

```bash
docker start sonarqube
```