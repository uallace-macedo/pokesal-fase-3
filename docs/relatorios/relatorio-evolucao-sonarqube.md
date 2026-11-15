# Relatório de Evolução da Qualidade - Pokesal

## 1. Identificação

- **Projeto:** Pokesal
- **Disciplina:** Testes e Qualidade de Software
- **Etapa:** Segunda unidade
- **Equipe:** Jonathas Santos, Mariana Sanches e João Alcântara
- **Ferramenta:** SonarQube Community Build
- **Comparação:** Fase 02 e Fase 03

## 2. Objetivo

Este relatório compara os indicadores de qualidade do código do PokeSAL entre as Fases 02 e 03. O objetivo é observar a evolução da cobertura dos testes, a ocorrência de problemas identificados pelo SonarQube e a presença de código duplicado.

A comparação utiliza as análises registradas no histórico (*Activity*) do mesmo projeto no SonarQube, identificadas pelas versões `Fase-02` e `Fase-03`.

## 3. Resultados

| Indicador | Fase 02 | Fase 03 | Variação |
|---|---:|---:|---:|
| Issues | 0 | 0 | Sem alteração |
| Reliability Issues | 0 | 0 | Sem alteração |
| Coverage | 89,0% | 95,8% | **+6,8 pontos percentuais** |
| Line Coverage | 93,2% | 96,6% | **+3,4 pontos percentuais** |
| Unit Test Success | 100% | 100% | Sem alteração |
| Duplicated Lines | 0 | 0 | Sem alteração |

### 3.1 Interpretação dos indicadores

- **Issues:** indicador geral de problemas registrados pelo SonarQube. O histórico mostra zero nas duas fases.
- **Reliability Issues:** problemas classificados na dimensão de confiabilidade. O histórico mostra zero nas duas fases.
- **Coverage:** cobertura geral apresentada pelo SonarQube. A métrica passou de 89,0% para 95,8%.
- **Line Coverage:** percentual de linhas cobertas pelos testes. A métrica passou de 93,2% para 96,6%.
- **Unit Test Success:** percentual de sucesso dos testes reportado na análise. O resultado permaneceu em 100% nas duas fases.
- **Duplicated Lines:** quantidade de linhas duplicadas apresentada no gráfico. O resultado permaneceu em zero nas duas fases.

## 4. Análise da evolução

A principal evolução ocorreu na cobertura dos testes. A cobertura geral aumentou de 89,0% na Fase 02 para 95,8% na Fase 03, uma diferença positiva de **6,8 pontos percentuais**. A cobertura de linhas também aumentou, passando de 93,2% para 96,6%, uma diferença de **3,4 pontos percentuais**.

O indicador de sucesso dos testes permaneceu em 100% nas duas versões. Isso mostra que, nas análises registradas, todos os testes considerados pelo relatório foram concluídos com sucesso.

Os indicadores `Issues`, `Reliability Issues` e `Duplicated Lines` também permaneceram em zero. Portanto, não houve aumento nesses indicadores entre as duas fases.

Em conjunto, os resultados apontam uma evolução positiva da cobertura de testes na Fase 03, mantendo os demais indicadores exibidos no histórico sem alterações.

## 5. Conclusão

A comparação entre as Fases 02 e 03 demonstra uma melhora nos indicadores de cobertura do PokeSAL. A cobertura geral cresceu **6,8 pontos percentuais**, enquanto a cobertura de linhas aumentou **3,4 pontos percentuais**. O sucesso dos testes permaneceu em 100%, e os indicadores gerais de problemas, problemas de confiabilidade e linhas duplicadas permaneceram em zero.

Assim, os dados registrados pelo SonarQube indicam uma evolução positiva na cobertura dos testes após as alterações da Fase 03, sem piora nos demais indicadores apresentados nesta comparação.
