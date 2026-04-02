# AGENTS.md - Tá Chegando

> Guia para agentes de IA trabalharem neste código Flutter.

## Projeto

**Tá Chegando** é uma plataforma de geolocalização em tempo real que conecta vendedores ambulantes/itinerantes a consumidores próximos.

### Personas
- **Vendedor:** Usa o app para gerenciar rotas, ver heatmap de demanda, notificar clientes
- **Consumidor:** Usa o app gratuitamente para encontrar vendedores próximos
- **Frota:** Dono de múltiplos veículos que gerencia vendedores

---

## Build / Lint / Test Commands

### Setup
```bash
flutter pub get
cp .env.example .env
```

### Run App
```bash
flutter run
flutter run -d chrome
flutter run -d macos
```

### Lint / Analyze
```bash
flutter analyze
dart fix --dry-run
dart fix --apply
```

### Test Commands
```bash
flutter test
flutter test test/features/
flutter test --coverage
```

---

## Architecture

### Clean Architecture - 3 Layers

```
lib/
├── core/
│   ├── theme/
│   ├── routing/
│   ├── utils/
│   └── constants/
│
├── features/
│   ├── auth/
│   ├── vendor/
│   ├── consumer/
│   ├── map/
│   ├── notifications/
│   └── subscription/
│
└── main.dart
```

### Dependency Rule
```
Presentation → Domain ← Data
```

---

## Key Dependencies

| Package | Purpose |
|---------|---------|
| `flutter_riverpod` | State Management |
| `go_router` | Navigation |
| `supabase_flutter` | Backend (Auth, DB, Realtime) |
| `google_maps_flutter` | Maps |
| `geolocator` | GPS location |
| `flutter_local_notifications` | Push notifications |

---

## Code Style

### Imports Order
```dart
// 1. Dart SDK
import 'dart:async';

// 2. Flutter SDK
import 'package:flutter/material.dart';

// 3. External packages
import 'package:flutter_riverpod/flutter_riverpod.dart';

// 4. Internal package (absolute)
import 'package:tachegando_app/core/theme/app_theme.dart';

// 5. Relative imports
import '../models/vendor_model.dart';
```

### Naming Conventions

| Type | Convention | Example |
|------|------------|---------|
| Classes | PascalCase | `VendorController`, `VendorModel` |
| Variables | camelCase | `currentVendor`, `isLoading` |
| Constants | camelCase | `maxRadius`, `defaultTimeout` |
| Files | snake_case | `vendor_controller.dart` |
| Directories | snake_case | `vendor/`, `consumer/` |
| Private members | _prefix | `_isLoading`, `_handleError` |

---

## Cores do App

| Cor | Hex | Uso |
|-----|-----|-----|
| **Primária** | `#FF6B35` | Cor principal |
| **Secundária** | `#2EC4B6` | Ações secundárias |
| **Destaque** | `#FFD166` | Alertas, notificações |
| **Sucesso** | `#06D6A0` | Online, ativo |
| **Erro** | `#EF476F` | Erro, urgente |

---

## Linear - Gestão de Projetos

### Configuração
Ver `docs/LINEAR.md` para detalhes completos.

### Estrutura
- **Time:** Evo (prefixo: EVO-)
- **Label do produto:** `tch` (sempre usar em issues do Tá Chegando)
- **Projetos:** Módulos (Autenticacao, App Vendedor, etc.)

### Prefixo de Issues
**EVO-** (ex: EVO-123)

### Branches
```
feature/EVO-123-descricao
bugfix/EVO-456-descricao
hotfix/EVO-789-descricao
```

### Commits
```
feat(vendor): adiciona heatmap de demanda

Closes EVO-123
```

### Labels Principais
- `tch` - Label do produto Tá Chegando
- `vendor` - Relacionado ao vendedor
- `consumer` - Relacionado ao consumidor
- `fleet` - Gestão de frotas
- `maps` - Mapas e geolocalização
- `P0/P1/P2/P3` - Prioridade

---

## Error Handling

### Exceptions (Data Layer)
```dart
throw ServerException('Error message');
throw NetworkException('No connection');
```

### Failures (Domain/Presentation)
```dart
return Left(ServerFailure('Error message'));
return Right(vendor);
```

---

## Common Gotchas

1. **Sempre use `const` constructors** quando possível
2. **Use `ConsumerWidget` ou `HookConsumerWidget`** para Riverpod
3. **Nunca chame `ref.read` durante build**
4. **Check `mounted` após async gaps**
5. **Dispose controllers** no `dispose()` do widget
6. **Use `Either<Failure, T>`** para operações async que podem falhar

---

## Referências

- `docs/LINEAR.md` - Configuração do Linear
- `analysis_options.yaml` - Lint rules
- `pubspec.yaml` - Dependencies
- `.env.example` - Environment variables template
