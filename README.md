# Tá Chegando

Aplicativo Kotlin Multiplatform/Compose para conectar consumidores a vendedores ambulantes/itinerantes próximos em tempo real.

## Estrutura

- `KotlinProject/composeApp`: app compartilhado em Compose Multiplatform
- `KotlinProject/composeApp/src/commonMain`: UI, navegação, domínio e integrações compartilhadas
- `KotlinProject/composeApp/src/androidMain`: implementações Android, incluindo mapa e localização
- `KotlinProject/composeApp/src/iosMain`: entrada/implementações iOS
- `KotlinProject/composeApp/src/wasmJsMain`: entrada/implementações web Wasm
- `supabase/migrations`: schema e funções do backend

## Comandos Principais

Execute dentro de `KotlinProject/`.

```bash
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:compileDebugKotlinAndroid
./gradlew :composeApp:check
```

Para web/Wasm:

```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

## Stack

- Kotlin Multiplatform
- Compose Multiplatform + Material 3
- Navigation Compose
- Supabase Kotlin
- Google Maps/Maps Compose no Android
- Ktor por plataforma
