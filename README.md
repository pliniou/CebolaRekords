# 🎵 Cebola Rekords Music Player

Uma solução da Cebola Studios para reproduzir músicas fictícias – com um catálogo tão bom que você vai lamentar não ser real.

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.24-blue)
![Compose](https://img.shields.io/badge/Jetpack_Compose-1.6-4285F4)
![License](https://img.shields.io/badge/license-MIT-yellow)
![API Level](https://img.shields.io/badge/API-26%2B-green)
![Status](https://img.shields.io/badge/Status-Estável-brightgreen)

## ✨ Funcionalidades Principais

- 🎶 Player com reprodução em segundo plano e controles avançados
- 🎨 Interface com tema escuro e animações fluidas
- 🎵 Biblioteca organizada por artistas, álbuns e playlists
- 🔍 Busca avançada e filtros inteligentes
- 🔧 Otimizações para cache e performance offline

## 🛠️ Stack Tecnológica

- **Media**: Media3 (ExoPlayer) `1.3.1`
- **UI**: Jetpack Compose `1.6.7`
- **Splash Screen**: Core Splashscreen `1.0.1`
- **Banco de Dados**: Room `2.6.1`
- **Injeção de Dependências**: Hilt `2.48`
- **Arquitetura**: Clean Architecture com MVVM

## 📸 Screenshots

<div align="center">
  <img src="screenshots/tela1.png" alt="Tela Inicial" width="200"/>
  <img src="screenshots/tela2.png" alt="Biblioteca" width="200"/>
  <img src="screenshots/tela3.png" alt="Stream" width="200"/>
  <img src="screenshots/tela4.png" alt="Sobre" width="200"/>
</div>

*Tela Inicial • Artistas • Biblioteca • Player*

## 🚀 Instalação

1. Clone o repositório:
   ```bash
   git clone https://github.com/pliniou/cebola-rekords.git
   ```

2. Abra no Android Studio e sincronize o Gradle

3. Construa o projeto:
   ```bash
   ./gradlew clean build
   ```

## 🏗️ Estrutura do Projeto

```
app/
├── src/
│   ├── main/           # Código principal (data, domain, presentation)
│   ├── test/           # Testes unitários
│   └── androidTest/    # Testes instrumentados
├── screenshots/        # Imagens da documentação
└── ...
```

## 🧪 Testes

Para executar todos os testes unitários:

```bash
./gradlew test
```

## 📄 Licença

Este projeto está licenciado sob a Licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

**Desenvolvido pela Cebola Studios** 🧅  
*Fazendo você chorar de emoção desde sempre*