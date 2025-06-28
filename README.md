# 🎵 Cebola Rekords Music Player

[![Android](https://img.shields.io/badge/platform-Android-green.svg)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/language-Kotlin-blue.svg)](https://kotlinlang.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Um player de música moderno para Android, feito com Jetpack Compose, Media3 e arquitetura robusta. Explore e ouça o catálogo da fictícia gravadora **Cebola Rekords** com uma experiência fluida e visualmente atraente.

---

## ✨ Funcionalidades

- **Navegação Intuitiva:** Bottom Navigation para Início, Artistas, Músicas e Sobre.
- **Player Completo:**
  - Reprodução em segundo plano.
  - Controles (Play/Pause, Próxima, Anterior).
  - Barra de progresso interativa (seek).
  - Mini player persistente.
  - Player em tela cheia (Modal Bottom Sheet) com arte de capa dinâmica.
  - Extração de arte de capa dos metadados dos áudios.
- **Artistas:** Lista com cards expansíveis e biografias.
- **Músicas:** Grade organizada alfabeticamente, com destaque para a faixa em reprodução.
- **Sobre:** Cards expansíveis com informações e animações.
- **Design Moderno:** Material Design 3, tema escuro e animações fluidas.
- **"Feito com ❤️ pela Cebola Studios"** no card de versão.

---

## 🛠️ Stack Tecnológica

- **Kotlin**
- **Jetpack Compose** (UI)
- **MVVM + Clean Architecture**
- **Hilt** (Injeção de dependência)
- **Compose Navigation**
- **Media3 (ExoPlayer, MediaSession)**
- **Coil** (imagens e arte de capa)
- **Coroutines & Flow**
- **Material Design 3**
- **Gradle Kotlin DSL**

---

## 🏗️ Arquitetura

```
app/
 ├── data/        # Repositório e fontes de dados
 ├── player/      # Lógica do player (PlaybackService, PlayerViewModel)
 ├── ui/          # Composables e telas
 ├── navigation/  # Rotas e navegação
 └── theme/       # Cores, tipografia, temas
```

- **MVVM**: Separação clara entre UI, lógica e dados.
- **Clean Architecture**: Camadas desacopladas para fácil manutenção.

---

## 🚀 Otimizações & Melhorias

- **Gerenciamento eficiente de estado** no Compose (`remember`, `derivedStateOf`, `LaunchedEffect`, `collectAsState`).
- **Animações suaves**: `tween`, `spring`, `AnimatedListItem`, cards expansíveis.
- **Player robusto**: Media3 para reprodução eficiente e integração com controles do sistema.
- **UX aprimorada**: Botões desabilitados quando necessário, organização alfabética, layouts otimizados.
- **Padronização visual**: Material Design 3, espaçamentos e tipografia consistentes.

---

## 📲 Como rodar

1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/cebolarekords.git
   cd cebolarekords
   ```
2. Abra no **Android Studio** (2023.x+ recomendado).
3. Certifique-se de ter o **Java SDK 17** configurado.
4. Conecte um dispositivo ou inicie um emulador (API 26+).
5. Clique em **Run** (ícone de play verde).

---

## 📸 Screenshots

| Início | Artistas | Músicas | Player |
|:------:|:--------:|:-------:|:------:|
| ![Tela Inicial](screenshots/tela1.jpg) | ![Tela de Artistas](screenshots/tela2.jpg) | ![Tela de Músicas](screenshots/tela3.jpg) | ![Player Fullscreen](screenshots/tela4.jpg) |

---

## 🤝 Contribua

Contribuições são bem-vindas! Abra uma [issue](https://github.com/seu-usuario/cebolarekords/issues) ou envie um pull request.

---

## 📄 Licença

Distribuído sob a [MIT License](LICENSE).

---