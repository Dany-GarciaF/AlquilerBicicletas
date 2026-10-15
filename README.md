# Team Bici

Plataforma de alquiler de bicicletas.

| Carpeta | Contenido |
|---|---|
| `backend/` | API REST en Kotlin + Spring Boot, PostgreSQL, JWT y Google OAuth. Ver `backend/.env.example`. |
| `frontend/` | App Kotlin Multiplatform (Compose) para Android y Web. Ver `frontend/README.md`. |
| `docs/` | Documentación técnica y manual de usuario. |

## Arranque rápido

```
cd backend;  .\gradlew.bat bootRun                              # API en :8080
cd frontend; .\gradlew.bat :webApp:wasmJsBrowserDevelopmentRun  # Web en :3000
```

Copia `backend/.env.example` a `backend/.env` y completa los valores. Nunca subas `.env`.