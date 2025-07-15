# 🎵 Cebola Rekords Music Player
Uma solução da Cebola Studios para reproduzir músicas fictícias – com um catálogo tão bom que você vai lamentar não ser real.

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.24-blue)
![Compose](https://img.shields.io/badge/Jetpack_Compose-1.6-4285F4)
![License](https://img.shields.io/badge/license-MIT-yellow)
![API Level](https://img.shields.io/badge/API-26%2B-green)
![Status](https://img.shields.io/badge/Status-Estável-brightgreen) ## ✨ Funcionalidades Principais
- 🎶 Player com reprodução em segundo plano e controles avançados.
- 🎨 Interface com tema escuro e animações fluidas.
- 🎵 Biblioteca organizada por artistas, álbuns e playlists.
- 🔍 Busca avançada e filtros inteligentes.
- 🔧 Otimizações para cache e performance offline.

## 🛠️ Stack Tecnológica
- **Media**: Media3 (ExoPlayer) `1.3.1`
- **UI**: Jetpack Compose `1.6.7`
- **Splash Screen**: Core Splashscreen `1.0.1` - **Banco de Dados**: Room `2.6.1`
- **Injeção de Dependências**: Hilt `2.48`
- **Arquitetura**: Clean Architecture com MVVM

## 📸 Screenshots
<div align="center">
| Tela Inicial | Artistas | Biblioteca | Player |
|-------------|----------|------------|--------|
| ![Início](screenshots/tela1.png) | ![Artistas](screenshots/tela2.png) | ![Músicas](screenshots/tela3.png) | ![Player](screenshots/tela4.png) |
</div>

## 🚀 Instalação
1. Clone o repositório: `git clone https://github.com/pliniou/cebola-rekords.git`
2. Abra no Android Studio e sincronize o Gradle.
3. Construa o projeto: `./gradlew clean build` ## 🏗️ Estrutura do Projeto
- `app/src/main/`: Código principal (data, domain, presentation).
- `app/src/test/`: Testes unitários.
- `app/src/androidTest/`: Testes instrumentados.
- `screenshots/`: Imagens da documentação.

## 🧪 Testes
Para executar todos os testes unitários, use: `./gradlew test`

## 📄 Licença
Este projeto está licenciado sob a Licença MIT. Veja o arquivo `LICENSE` para mais detalhes. Desenvolvido pela Cebola Studios.