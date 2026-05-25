# Calcetinder

Aplicación Android desarrollada en Kotlin que simula una plataforma de *swipe* al estilo Tinder pero para calcetines. Los usuarios pueden registrarse, subir sus calcetines y hacer like o dislike a los de otros usuarios.

## Tecnologías

- Kotlin
- Room (base de datos local)
- Navigation Component
- ViewModel + StateFlow (MVVM)
- View Binding
- Material Components

## Estructura del proyecto

```
com.example.calcetinder
├── datos/
│   ├── CalcetinDAO.kt
│   ├── CalcetinderDB.kt
│   ├── MatchDAO.kt
│   ├── Repositorio.kt
│   └── UsuarioDAO.kt
├── modelo/
│   ├── Calcetin.kt
│   ├── Match.kt
│   └── Usuario.kt
└── ui/
    ├── login/
    │   ├── LoginFragment.kt
    │   ├── LoginViewModel.kt
    │   └── RegistroFragment.kt
    ├── miscalcetines/
    │   ├── CalcetinAdapter.kt
    │   ├── MisCalcetinesFragment.kt
    │   └── MisCalcetinesViewModel.kt
    └── swipe/
        ├── SwipeFragment.kt
        └── SwipeViewModel.kt
```

## Funcionalidades

- Registro e inicio de sesión de usuario
- Swipe con like / dislike a calcetines
- Gestión de calcetines propios (crear, editar, eliminar)
- Historial de matches

## Autor

David Merchán — DAM 2025/2026
