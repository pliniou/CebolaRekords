🎵 Cebola Rekords Music Player

[![Android](https://img.shields.io/badge/platform-Android-green.svg)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/language-Kotlin-blue.svg)](https://kotlinlang.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Um player de música moderno para Android, feito com Jetpack Compose, Media3 e uma arquitetura robusta e otimizada. Explore e ouça o catálogo da fictícia gravadora **Cebola Rekords** com uma experiência fluida e visualmente atraente.

---

## ✨ Funcionalidades

- **Navegação Intuitiva:** Bottom Navigation para Início, Artistas, Músicas e Sobre.
- **Player Completo:**
  - Reprodução em segundo plano com `MediaSessionService`.
  - Controles (Play/Pause, Próxima, Anterior) e integração com o sistema.
  - Barra de progresso interativa (seek).
  - Mini player persistente e expansível.
  - Player em tela cheia (Modal Bottom Sheet) com arte de capa dinâmica.
  - Extração de arte de capa dos metadados dos áudios com cache de performance.
- **Telas Dinâmicas:**
  - **Artistas:** Cards expansíveis para exibir biografias.
  - **Músicas:** Grade organizada com destaque visual para a faixa em reprodução.
- **Design Moderno:**
  - Material Design 3 e tema escuro.
  - Animações fluidas e significativas em toda a UI.

---

## 🛠️ Stack Tecnológica

- **Kotlin** & **Coroutines/Flow**
- **Jetpack Compose** (UI Toolkit)
- **MVVM + Clean Architecture** (Padrão de arquitetura)
- **Hilt** (Injeção de dependência)
- **Compose Navigation** (Navegação single-activity)
- **Media3 (ExoPlayer & MediaSession)** (Reprodução de mídia)
- **Coil** (Carregamento de imagens e arte de capa)
- **Gradle Kotlin DSL**

---

## 🏗️ Arquitetura e Otimizações

O projeto foi refatorado para seguir as melhores práticas de desenvolvimento Android, focando em segurança, performance e manutenibilidade.

- **Arquitetura em Camadas:**
  - **UI:** Telas em Compose e ViewModels que gerenciam o estado da UI de forma reativa.
  - **Data:** Repositório que centraliza o acesso aos dados e abstrai as fontes (recursos locais).
  - **Player:** Serviço desacoplado (`PlaybackService`) que gerencia a `MediaSession` e o `ExoPlayer`.

- **Segurança Reforçada:**
  - O `PlaybackService` não é mais exportado (`android:exported="false"`), prevenindo que outros apps o iniciem indevidamente.

- **Performance Otimizada:**
  - **Cache de Metadados:** A extração da arte de capa dos arquivos de áudio é uma operação de I/O custosa. Um cache foi implementado no `CebolaRepository` para realizar essa operação apenas uma vez por faixa.
  - **Cache de `MediaItem`:** A conversão de `Track` para `MediaItem` é otimizada com cache no `MusicViewModel` para evitar recriações desnecessárias.
  - **Atualização de Posição Eficiente:** O polling manual no `PlayerViewModel` foi substituído por uma coroutine mais otimizada, que atualiza a UI em intervalos eficientes e respeita o ciclo de vida.

- **Gerenciamento de Recursos:**
  - O `PlayerViewModel` agora gerencia o `MediaController` de forma segura, liberando-o (`release()`) no método `onCleared()` para evitar vazamentos de memória.
  - Listeners são removidos adequadamente para prevenir memory leaks.

- **Modularidade e SOLID:**
  - Telas complexas como `FullPlayerScreen` foram decompostas em componentes menores e de responsabilidade única (`PlayerTopBar`, `AlbumArtWithAnimation`, `PlaybackControls`, etc.), facilitando a leitura e os testes.

---

## 📲 Como Rodar

1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/cebolarekords.git
   cd cebolarekords

2. Abra no Android Studio (Iguana 2023.2.1+ recomendado).
3. Certifique-se de ter o Java SDK 17 configurado.
4. Conecte um dispositivo ou inicie um emulador (API 26+).
5. Clique em Run (ícone de play verde).

## 📸 Screenshots

Início	Artistas	Músicas	Player
![alt text](screenshots/tela1.jpg)
![alt text](screenshots/tela2.jpg)
![alt text](screenshots/tela3.jpg)
![alt text](screenshots/tela4.jpg)

🤝 Contribua
Contribuições são bem-vindas! Abra uma issue ou envie um pull request.

📄 Licença
Distribuído sob a MIT License.