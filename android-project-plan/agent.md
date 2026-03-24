# 🤖 Guide Agent — Workflow d'implémentation

Ce fichier est ton guide principal. Lis-le **en premier** avant de commencer à coder.

---

## Ta mission

Tu es un agent AI chargé d'implémenter une application Android native (Kotlin + Jetpack Compose) à partir de la spécification contenue dans ce dossier. Tu travailles de manière itérative, une tâche à la fois.

---

## Workflow par tâche

Pour **chaque tâche** (`T-X.Y`), suis ce cycle :

### 1. Comprendre
- Lis la description de la tâche dans le fichier `XX-*.md`
- Si une `> **Ref web**` est mentionnée, **lis le fichier TypeScript source** pour comprendre la logique métier
- Identifie les dépendances (quelles tâches doivent être terminées avant)

### 2. Implémenter
- Crée/modifie les fichiers Kotlin nécessaires
- Respecte les conventions décrites dans `agent-conventions.md`
- Respecte l'architecture décrite dans `agent-architecture.md`
- Les snippets de code dans les `.md` sont des **suggestions** — adapte-les au contexte réel du projet

### 3. Vérifier
- Le projet **doit compiler** après chaque tâche (`./gradlew assembleDebug`)
- Exécute les tests existants (`./gradlew testDebugUnitTest`)
- Vérifie la checklist de `agent-checklist.md`

### 4. Committer
- Un commit par tâche logique (peut regrouper des sous-tâches liées)
- Format du message de commit : `feat(T-X.Y): <description courte>`
- Exemples :
  - `feat(T-1.1): add User domain model and auth types`
  - `feat(T-2.5): add EventApi Retrofit interface`
  - `fix(T-3.1): handle null user_metadata in Supabase mapping`

### 5. Marquer comme fait
- Coche `[x]` la tâche dans le fichier `.md` correspondant
- Si tu rencontres un blocage, ajoute une note `> ⚠️ BLOCAGE : <description>` sous la tâche

---

## Ordre d'exécution strict

```
Phase 0  → Phase 1  → Phase 2  → Phase 3
                                     ↓
Phase 4  → Phase 5  → Phase 6  → Phase 7  → Phase 8
                                                ↓
                                    Phase 9  → Phase 10
```

**Ne saute jamais une phase.** Chaque phase dépend des précédentes.

Au sein d'une phase, respecte l'ordre des tâches (`T-X.1` avant `T-X.2`, etc.), sauf si explicitement indiqué que des tâches sont indépendantes.

---

## Gestion des erreurs et blocages

### Si le code ne compile pas
1. Lis attentivement le message d'erreur
2. Vérifie les imports manquants
3. Vérifie les dépendances Gradle
4. Ne passe **jamais** à la tâche suivante si le projet ne compile pas

### Si un test échoue
1. Vérifie si c'est un test que tu viens de créer ou un test existant
2. Si c'est un test existant qui casse, c'est une **régression** → corrige avant de continuer
3. N'ignore jamais un test qui échoue, ne le supprime pas

### Si tu ne comprends pas le comportement attendu
1. Lis le fichier TypeScript source référencé par `> **Ref web**`
2. Regarde les tests existants de la web app dans `src/__tests__/`
3. En dernier recours, ajoute un `// TODO: clarifier avec l'utilisateur` et passe à la tâche suivante

---

## Fichiers de référence dans ce dossier

| Fichier | Rôle |
|---------|------|
| `agent.md` | **Ce fichier** — workflow global |
| `agent-conventions.md` | Conventions de code Kotlin/Android |
| `agent-architecture.md` | Patterns d'architecture et décisions |
| `agent-checklist.md` | Checklists pré/post implémentation |
| `agent-api-reference.md` | Contrat API backend complet |
| `00-overview.md` | Vue d'ensemble du projet |
| `01-*.md` à `11-*.md` | Tâches par phase |

---

## Principes fondamentaux

1. **Compilation d'abord** — Le projet doit toujours compiler
2. **Pas de code mort** — Ne laisse pas de code commenté ou inutilisé
3. **Consistance** — Suis les mêmes patterns partout (voir `agent-conventions.md`)
4. **Petits pas** — Mieux vaut 10 petits commits qui compilent qu'un gros qui casse tout
5. **Référence web** — En cas de doute sur la logique métier, le code TypeScript fait foi
