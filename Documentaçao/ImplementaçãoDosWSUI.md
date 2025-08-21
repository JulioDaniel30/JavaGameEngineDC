# 🚀 Roadmap de Implementação dos WSUI (JDStudio Engine)

Este roadmap organiza os **40 WSUI** em fases de prioridade, para facilitar a implementação progressiva.

---

## 📌 Fase 1 — Essenciais (Fundação da Engine)
> Prioridade máxima: básicos para qualquer jogo.

- [x] [UIHealthBar](#uihealthbarsegmented)  
- [x] [UIHealthBarSegmented](#uihealthbarsegmented)  
- [x] [UIManaBar](#uimanabar)  
- [ ] [UIStaminaRing](#uistaminaring)  
- [x] [UINameplate](#uilevelindicator)  
- [x] [UIPopup (dano/cura)](#uibuffdebufficon)  
- [x] [UIInteractionPrompt](#uiinteractionprompt)  

---

## 📌 Fase 2 — Feedback de Combate
> Reforçam a experiência em batalhas.

- [ ] [UICriticalHitPopup](#uicriticalhitpopup)  
- [ ] [UIDodgePopup](#uidodgepopup)  
- [ ] [UIHealingAura](#uihealingaura)  
- [ ] [UIDamageDirectionIndicator](#uidamagedirectionindicator)  
- [ ] [UIComboCounter](#uicombocounter)  
- [ ] [UIWeaponCooldown](#uiweaponcooldown)  
- [ ] [UIChargeIndicator](#uichargeindicator)  

---

## 📌 Fase 3 — RPG e Progressão
> Elementos que ampliam a sensação de evolução do personagem.

- [ ] [UILevelIndicator](#uilevelindicator)  
- [ ] [UIXPBarFloating](#uixpbarfloating)  
- [x] [UIQuestMarker](#uiquestmarker)  
- [ ] [UIObjectiveMarker](#uiobjectivemarker)  
- [ ] [UIMissionProgress](#uimissionprogress)  
- [ ] [UIBattleTurnOrder](#uibattleturnorder)  
- [ ] [UIRankBadge](#uirankbadge)  
- [ ] [UIPetNameplate](#uipetnameplate)  

---

## 📌 Fase 4 — Mundo e Exploração
> Facilitam interação com o cenário e exploração.

- [x] [UIDirectionArrow](#uidirectionarrow)  
- [ ] [UITargetMarker](#uitargetmarker)  
- [ ] [UILootIndicator](#uilootindicator)  
- [ ] [UITimerBubble](#uitimerbubble)  
- [ ] [UIZoneIndicator](#uizoneindicator)  
- [ ] [UIAnchorMarker](#uianchormarker)  
- [ ] [UICollectibleCounter](#uicollectiblecounter)  

---

## 📌 Fase 5 — Status, Emoção e Personalidade
> Tornam NPCs e o mundo mais vivos.

- [ ] [UIBuffDebuffIcon](#uibuffdebufficon)  
- [x] [UIEmotionBubble](#uiemotionbubble)  
- [ ] [UIAffinityIndicator](#uiaffinityindicator)  
- [ ] [UIOverheadIcon](#uioverheadicon)  
- [ ] [UIWeatherEffectIcon](#uiweathereffecticon)  
- [ ] [UIFloatingEmoji](#uifloatingemoji)  
- [ ] [UIFloatingWarning](#uifloatingwarning)  
- [ ] [UISoundWave](#uisoundwave)  

---

## 📌 Fase 6 — Avançados e Cosméticos
> Usados para polimento, imersão e estilo.

- [ ] [UIResourceBar](#uiresourcebar)  
- [ ] [UIRespawnTimer](#uirespawntimer)  
- [ ] [UIFloatingIcon](#uifloatingicon)  
- [ ] [UITrackingLine](#uitrackingline)  

# 🎯 Estratégia de Implementação
1. **Comece pela Fase 1** (essenciais).  
2. **Implemente em blocos** (barras → popups → indicadores).  
3. **Avance para fases seguintes** apenas quando a base estiver estável.  
4. **Use Fase 5 e 6** para dar polimento final ao jogo.  

---


# Descrições

## BarElements (Barras e Indicadores Visuais)

<a name="uihealthbarsegmented"></a>1. **`UIHealthBarSegmented`**  
   * Versão avançada da barra de vida, dividida em segmentos (como corações ou quadrados).
   * Ideal para jogos estilo *Zelda* ou *RPG clássico*.

<a name="uimanabar"></a>
2. **`UIManaBar`**  
   * Barra de mana/energia mágica.
   * Pode ser horizontal ou circular em volta do personagem.

<a name="uistaminaring"></a>
3. **`UIStaminaRing`**  
   * Um círculo em torno do personagem que diminui quando ele corre ou ataca.
   * Muito usado em jogos de ação e sobrevivência.

<a name="uishieldbar"></a>
4. **`UIShieldBar`**  
   * Barra de escudo que absorve dano antes da vida.
   * Pode ser mostrada acima ou abaixo da barra de HP.

<a name="uixpbarfloating"></a>
5. **`UIXPBarFloating`**  
   * Indicador de experiência que flutua acima do personagem.
   * Útil em RPGs para feedback imediato ao ganhar XP.

<a name="uiresourcebar"></a>
6. **`UIResourceBar`**  
   * Barra genérica para recursos (fome, sede, calor, oxigênio).

---

## InformationElements (Informações e Dados)
<a name="uilevelindicator"></a>
7. **`UILevelIndicator`**  
   * Exibe o nível ou ranking do personagem/NPC acima da cabeça.
   * Pode mostrar estrelas, números ou ícones.

<a name="uiquestmarker"></a>
8. **`UIQuestMarker`**  
   * Um ícone flutuante (ex: "!" ou "?") indicando NPCs com missões.
   * Clássico de MMORPGs.

<a name="uiobjectivemarker"></a>
9. **`UIObjectiveMarker`**  
   * Marca objetivos de missões dentro do mundo, com ícone diferenciado.
   * Pode piscar ou ter seta animada.

<a name="uitargetmarker"></a>
10. **`UITargetMarker`**  
    * Um marcador especial (círculo, seta, brilho) para indicar o alvo selecionado.
    * Comum em jogos de ação e estratégia.

<a name="uibattleturnorder"></a>
11. **`UIBattleTurnOrder`**  
    * Mostra ordem de turnos (em RPGs de turno) sobre cada personagem.

<a name="uicollectiblecounter"></a>
12. **`UICollectibleCounter`**  
    * Exibe número de colecionáveis próximos ("3/5 moedas").

<a name="uimissionprogress"></a>
13. **`UIMissionProgress`**  
    * Barra/contador de progresso da missão em cima do NPC/objeto.

<a name="uirankbadge"></a>
14. **`UIRankBadge`**  
    * Medalha/insígnia que mostra ranking (Bronze, Prata, Ouro).

<a name="uicombocounter"></a>
15. **`UIComboCounter`**  
    * Exibe número de golpes consecutivos.

<a name="uirespawntimer"></a>
16. **`UIRespawnTimer`**  
    * Mostra tempo restante até personagem/NPC renascer.

<a name="uizoneindicator"></a>
17. **`UIZoneIndicator`**  
    * Mostra o nome da zona/área quando o personagem entra.

<a name="uilootindicator"></a>
18. **`UILootIndicator`**  
    * Ícone/etiqueta flutuante mostrando itens que caíram no chão (ex: "Espada de Ferro").
    * Some após alguns segundos ou quando o item é recolhido.

<a name="uianchormarker"></a>
19. **`UIAnchorMarker`**  
    * Marcador fixo para localização (ex: bandeira, checkpoint, base).

---

## InteractionElements (Interação e Feedback)
<a name="uiprogresscircle"></a>
20. **`UIProgressCircle`**  
    * Indicador de carregamento (ex: para abrir baús, carregar feitiços, hackear portas).
    * Pode ser uma animação circular.

<a name="uiinteractionprompt"></a>
21. **`UIInteractionPrompt`**  
    * Texto/ícone que aparece quando o jogador está próximo (ex: "[E] Falar", "[F] Abrir").
    * Facilita a interação sem poluir a tela.

<a name="uidirectionarrow"></a>
22. **`UIDirectionArrow`**  
    * Setinha sobre um NPC/objeto indicando para onde o jogador deve ir.
    * Pode piscar ou girar.

<a name="uibuffdebufficon"></a>
23. **`UIBuffDebuffIcon`**  
    * Ícones que aparecem acima do personagem mostrando status temporários: veneno, stun, buff de ataque etc.

<a name="uifloatingicon"></a>
24. **`UIFloatingIcon`**  
    * Ícones genéricos (ex: moedas, chaves, itens) que aparecem flutuando ao pegar algo.

<a name="uitimerbubble"></a>
25. **`UITimerBubble`**  
    * Pequeno cronômetro que aparece sobre objetos temporários (ex: bomba, baú que fecha).
    * Mostra tempo restante em segundos.

<a name="uichargeindicator"></a>
26. **`UIChargeIndicator`**  
    * Barra/círculo que carrega ao segurar um botão de ataque.

<a name="uiweaponcooldown"></a>
27. **`UIWeaponCooldown`**  
    * Mostra o tempo de recarga da arma sobre o personagem.

<a name="uicriticalhitpopup"></a>
28. **`UICriticalHitPopup`**  
    * Texto especial para acertos críticos.

<a name="uidodgepopup"></a>
29. **`UIDodgePopup`**  
    * Mostra "Esquivou!" ou ícone quando um ataque é evitado.

<a name="uihealingaura"></a>
30. **`UIHealingAura`**  
    * Anel verde pulsante em volta do personagem quando recebe cura.

<a name="uidamagedirectionindicator"></a>
31. **`UIDamageDirectionIndicator`**  
    * Seta/flash que indica de onde veio o dano.

<a name="uitrackingline"></a>
32. **`UITrackingLine`**  
    * Uma linha sutil que conecta o personagem a um objetivo ou alvo.
    * Muito útil em jogos stealth ou de estratégia.

---

## PersonalityElements (Personalidade e Expressão)
<a name="uiemotionbubble"></a>
33. **`UIEmotionBubble`**  
    * Balões simples com emojis/ícones (💡 ideia, ❗ alerta, 😡 raiva, ❤️ amizade).
    * Dá vida aos NPCs sem precisar de fala.

<a name="uiaffinityindicator"></a>
34. **`UIAffinityIndicator`**  
    * Mostra a afinidade ou relação com NPCs (ex: carinha feliz/triste, corações).
    * Útil em simuladores sociais ou RPGs com sistema de amizade.

<a name="uioverheadicon"></a>
35. **`UIOverheadIcon`**  
    * Ícone simples que fica fixo sobre o personagem.
    * Exemplo: coroa sobre chefes, caveira sobre inimigos fortes, ícone de jogador.

<a name="uisoundwave"></a>
36. **`UISoundWave`**  
    * Efeito visual de "ondas sonoras" saindo do personagem quando ele fala ou emite som.
    * Pode ser usado para inimigos que detectam pelo som.

<a name="uifloatingemoji"></a>
37. **`UIFloatingEmoji`**  
    * Pequenos emojis animados (risada, raiva, sono).

<a name="uiweatherEffecticon"></a>
38. **`UIWeatherEffectIcon`**  
    * Ícone sobre personagens afetados pelo clima (chuva, neve, sol escaldante).

<a name="uifloatingwarning"></a>
39. **`UIFloatingWarning`**  
    * Mensagens curtas de alerta ("Área proibida!", "Muito frio!").

<a name="uipetnameplate"></a>
40. **`UIPetNameplate`**  
    * Nome/nível de pets/companheiros.

---
