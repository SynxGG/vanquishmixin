# VanquishMixin - Feuille de passation technique

**Projet :** Vanquish Modpack  
**Mod :** VanquishMixin (`vanquishmixin`)  
**Plateforme :** Minecraft Forge 1.20.1  
**État de référence :** craft Workstation et affichage JEI validés le 30 juin 2026  
**Dépôt GitHub :** https://github.com/SynxGG/vanquishmixin

---

## 1. Rôle du mod

VanquishMixin est le **mod de liaison technique** du modpack Vanquish. Son objectif n'est pas d'ajouter un contenu autonome, mais de relier plusieurs mods, mécaniques et paliers de progression afin que Vanquish fonctionne comme un ensemble cohérent plutôt que comme une simple juxtaposition de mods.

Il doit servir lorsqu'une intégration devient trop fragile, trop limitée ou trop complexe pour être réalisée proprement avec KubeJS, In Control, des datapacks ou de simples fichiers de configuration.

Exemples de responsabilités adaptées à VanquishMixin :

- GUI et menus personnalisés ;
- logique serveur/client et synchronisation réseau ;
- recettes propres à une station custom ;
- intégration JEI personnalisée ;
- hooks Forge absents de KubeJS ;
- interactions entre plusieurs mods ;
- événements de boss et changements mondiaux ;
- mixins ou accès à des comportements internes ;
- systèmes persistants nécessitant un contrôle Java plus fin.

VanquishMixin **ne remplace pas KubeJS**. KubeJS reste préférable pour les recettes simples, les loots, les tags, les petits événements et les réglages rapides. VanquishMixin devient le recours recommandé lorsqu'il faut une vraie logique Java, une interface custom, une synchronisation fiable ou un point d'injection absent des scripts.

> Règle de décision : si une tâche demandée pour Vanquish paraît trop complexe, trop fragile ou impossible à maintenir en KubeJS, proposer explicitement une implémentation dans VanquishMixin via MCreator et Java.

---

## 2. Contexte Vanquish

Vanquish est un modpack Forge 1.20.1 orienté RPG, exploration, progression par boss et power fantasy contrôlée. La progression est pensée par **paliers globaux**, à la manière de Terraria : certains boss ouvrent de nouvelles ressources, recettes, ennemis, difficultés mondiales et spécialisations d'équipement.

VanquishMixin doit donc rester aligné sur les principes suivants :

- les boss sont des clés de progression ;
- les anciennes ressources doivent garder une utilité ;
- les récompenses de boss doivent offrir des choix de craft ;
- les branches de progression peuvent être parallèles ;
- les recettes puissantes doivent être placées au bon palier ;
- les systèmes de plusieurs mods doivent se répondre.

La Workstation développée ici est une première brique de cette philosophie : elle fournit une station de craft avancée pouvant accepter les recettes 3x3 normales et des recettes exclusives Vanquish.

---

## 3. État fonctionnel validé

### 3.1 Workstation

La Workstation dispose de :

- 9 slots d'entrée en grille 3x3 ;
- 1 slot de sortie ;
- l'inventaire du joueur ;
- une sortie calculée côté serveur ;
- la prise en charge des recettes Crafting standards ;
- des recettes exclusives Vanquish prioritaires ;
- une catégorie JEI dédiée ;
- la Workstation enregistrée comme catalyst JEI.

### 3.2 Recettes actuellement implémentées

Les premières recettes exclusives concernent l'armure Warrior d'Immersive Armors, fabriquée à partir de `cataclysm:ancient_metal_ingot` :

| Pièce | Coût | Résultat |
|---|---:|---|
| Warrior Helmet | 5 | `immersive_armors:warrior_helmet` |
| Warrior Chestplate | 8 | `immersive_armors:warrior_chestplate` |
| Warrior Leggings | 7 | `immersive_armors:warrior_leggings` |
| Warrior Boots | 4 | `immersive_armors:warrior_boots` |
| **Total du set** | **24** | Armure Warrior complète |

Ces recettes ne sont qu'un prototype fonctionnel. La Workstation doit ensuite accueillir plusieurs crafts post-Ancient Remnant et d'autres recettes de progression Vanquish.

### 3.3 JEI

L'intégration JEI manuelle fonctionne :

- le plugin Vanquish est détecté ;
- une catégorie Workstation existe ;
- les recettes Warrior sont affichées ;
- la Workstation est utilisée comme icône/catalyst ;
- la Workstation est aussi déclarée comme station compatible avec les recettes Crafting standards.

**État à confirmer séparément :** le transfert automatique via le bouton `+` de JEI n'a pas été explicitement validé lors de cette passation. Ne pas le considérer comme acquis sans test.

---

## 4. Environnement technique

### 4.1 Outils et versions

- MCreator : `2026.1.14619`
- Générateur : Forge 1.20.1 communautaire
- Forge de développement : `47.4.20`
- Forge observé dans l'instance Vanquish : `47.4.10`
- Java runtime Minecraft : Java 17
- Mod ID : `vanquishmixin`
- Version actuelle : `1.0.0`

Le mod a été validé dans l'instance Vanquish malgré la différence mineure entre Forge 47.4.20 et 47.4.10. Il reste préférable de retester après toute mise à jour de Forge ou d'aligner les versions si un problème de compatibilité apparaît.

### 4.2 Dépendances locales utilisées au build

Le workspace charge notamment les JAR locaux suivants :

- Curios `5.14.1`
- Lionfish API `3.0`
- Cataclysm `3.31`
- Immersive Armors `1.7.2`
- JEI `15.20.0.132`

Les dépendances sont déclarées dans `build.gradle` via un dépôt `flatDir` pointant vers `libs`.

---

## 5. Architecture de la Workstation

### 5.1 Slots custom MCreator

IDs logiques de la grille :

```text
0 1 2
3 4 5
6 7 8

Sortie : 9
```

Point critique : MCreator n'insère pas forcément les slots dans l'ordre visuel dans `AbstractContainerMenu.slots`. L'ordre physique observé est :

```text
0, 1, 6, 3, 4, 7, 2, 5, 8, 9
```

Il ne faut donc pas utiliser aveuglément :

```java
menu.getSlot(customSlotId)
```

La méthode fiable utilisée pour cibler un slot custom MCreator est :

```java
menu.getSlots().get(customSlotId)
```

`getSlots()` expose ici la map des slots custom du menu MCreator.

### 5.2 `WorkstationMenu.java`

Le fichier essentiel est :

```text
src/main/java/vanquishmixin/world/inventory/WorkstationMenu.java
```

La logique custom doit rester appelée directement côté serveur :

```java
private void slotChanged(int slotid, int ctype, int meta) {
    if (this.world == null || this.world.isClientSide()) {
        return;
    }

    if (ctype == 0 && slotid >= 0 && slotid <= 8) {
        VQAdvancedCrafting.updateOutput(this);
        return;
    }

    if (ctype == 1 && slotid == 9) {
        VQAdvancedCrafting.takeOutput(this);
    }
}
```

À la fermeture, le résultat doit être supprimé avant que MCreator rende les objets au joueur :

```java
if (this.world != null && !this.world.isClientSide()) {
    VQAdvancedCrafting.clearOutput(this);
}
```

### 5.3 Slot de sortie virtuel

Le slot 9 est un **résultat virtuel**. Son contenu est recalculé, mais ne doit pas être traité comme un objet stocké appartenant au joueur.

Sans `clearOutput()` dans `removed(Player)`, MCreator peut rendre gratuitement le résultat au joueur lors de la fermeture du GUI.

### 5.4 Taille visuelle du slot de sortie

Un `Slot` Minecraft n'a pas de largeur/hauteur configurable. Le slot logique et l'icône de l'objet restent à la taille vanilla.

Pour obtenir un rendu proche de la table de craft vanilla :

- conserver le slot logique standard ;
- dessiner un cadre de sortie plus grand dans la texture ou dans `WorkstationScreen.java` ;
- utiliser par exemple un cadre 26x26 ou 32x32 centré sur le slot.

Ne pas tenter d'agrandir le slot logique uniquement pour un effet esthétique.

---

## 6. Moteur de craft `VQAdvancedCrafting`

Fichier :

```text
src/main/java/vanquishmixin/VQAdvancedCrafting.java
```

### 6.1 Ordre de résolution

Le moteur suit cet ordre :

1. chercher une recette exclusive Vanquish ;
2. si aucune recette spéciale ne correspond, interroger le `RecipeManager` avec `RecipeType.CRAFTING` ;
3. afficher le résultat sans consommer ;
4. à la prise du résultat, consommer les ingrédients ;
5. recalculer immédiatement la sortie.

La priorité aux recettes exclusives permet à la Workstation d'avoir des crafts propres même si une recette standard identique existe ailleurs.

### 6.2 Compatibilité recettes standards

Le fallback via `RecipeType.CRAFTING` rend la Workstation compatible avec :

- les recettes vanilla ;
- les recettes de mods ;
- les recettes KubeJS ;
- les recettes de datapacks ;
- les sorties dynamiques utilisant `assemble(...)`.

### 6.3 Contenants et objets restants

Le moteur utilise `CraftingRecipe#getRemainingItems(...)` avant de consommer les ingrédients. Cela permet de restituer correctement :

- seaux ;
- bouteilles ;
- outils ou contenants conservés ;
- restes spécifiques à d'autres mods.

Le reste retourne d'abord dans le slot, puis dans l'inventaire du joueur, puis est déposé au sol si nécessaire.

### 6.4 Protection contre les appels récursifs

Le retrait d'un ingrédient déclenche `setChanged()`, qui peut provoquer un nouveau calcul. Un ensemble de menus occupés, basé sur un `WeakHashMap`, empêche la récursion et les doubles consommations.

### 6.5 Dette technique actuelle

Les recettes spéciales et leur affichage JEI sont actuellement définis séparément. Cela crée un risque de désynchronisation entre :

- la recette réellement craftable ;
- la recette affichée par JEI.

À moyen terme, les deux systèmes doivent lire une **source de vérité commune** : classe de registre partagée, recette custom sérialisée ou format data-driven JSON.

---

## 7. Intégration JEI

Fichier principal :

```text
src/main/java/vanquishmixin/VQJeiPlugin.java
```

### 7.1 Choix retenu

L'intégration est réalisée manuellement avec l'API JEI :

- `@JeiPlugin` ;
- implémentation de `IModPlugin` ;
- `RecipeType` custom Workstation ;
- catégorie graphique ;
- enregistrement des recettes ;
- catalyst Workstation ;
- association avec `RecipeTypes.CRAFTING`.

Une implémentation autonome dans un seul Custom Element MCreator a été retenue pour limiter les problèmes de fichiers absents ou de packages non compilés.

### 7.2 Pourquoi le plugin MCreator « Just Enough Recipes » n'a pas été utilisé

Le plugin MCreator installé exposait bien des blocs Scratch JEI, mais son support de générateur ciblait NeoForge 1.21.x et non le générateur Forge 1.20.1 communautaire utilisé par VanquishMixin. L'onglet External APIs restait vide et l'intégration n'était pas fiable pour ce workspace.

### 7.3 Étapes de diagnostic utilisées

1. création d'un plugin JEI minimal ;
2. ajout d'un message console dans le constructeur ;
3. validation de la ligne :

```text
[VQ JEI] Plugin Vanquish détecté
```

4. ajout progressif de la catégorie, des recettes et du catalyst.

Cette approche est à réutiliser si JEI cesse de détecter le plugin.

---

## 8. MCreator : règles de modification

### 8.1 GUI verrouillé

Le GUI Workstation est verrouillé car MCreator génère à la fois :

- `WorkstationScreen.java` ;
- `WorkstationMenu.java`.

Déverrouiller puis enregistrer le GUI peut régénérer `WorkstationMenu.java` et supprimer :

- les appels directs à `VQAdvancedCrafting` ;
- le nettoyage du slot virtuel ;
- les imports custom.

### 8.2 Modifications esthétiques sûres

Pour une modification purement visuelle, conserver le GUI verrouillé et modifier directement :

```text
src/main/resources/assets/vanquishmixin/textures/screens/workstation.png
src/main/resources/assets/vanquishmixin/textures/screens/gui_arrow_right.png
src/main/resources/assets/vanquishmixin/textures/screens/crafting_table_output.png
```

Il est aussi possible de modifier manuellement `WorkstationScreen.java` pour déplacer des images ou des textes, tant que le GUI reste verrouillé.

### 8.3 Utiliser exceptionnellement l'éditeur visuel

Procédure sûre :

1. faire un commit Git avant toute modification ;
2. déverrouiller le GUI ;
3. faire uniquement les changements visuels ;
4. enregistrer ;
5. restaurer le menu fonctionnel :

```bat
git restore --source=HEAD -- src/main/java/vanquishmixin/world/inventory/WorkstationMenu.java
```

6. vérifier que les changements de `WorkstationScreen.java` et des textures sont conservés ;
7. reverrouiller immédiatement le GUI ;
8. compiler et exécuter la checklist de tests.

---

## 9. Build et déploiement dans Vanquish

### 9.1 Point critique : utiliser le JAR reobfusqué

Le JAR brut produit directement par la tâche `jar` est un JAR de développement en mappings Mojmap. Le copier dans une vraie instance Forge provoque notamment :

```text
java.lang.NoSuchMethodError: 'int net.minecraft.server.TickTask.getTick()'
```

La tâche de déploiement doit dépendre de :

```gradle
dependsOn tasks.named("reobfJar")
```

et copier le JAR final présent dans :

```text
build/libs
```

Ne pas copier directement `tasks.named("jar").archiveFile` dans le modpack.

### 9.2 Exemple de tâche de déploiement

Adapter le chemin à l'instance réelle :

```gradle
def vanquishModsDirectory = file(
    "C:/Users/elist/curseforge/minecraft/Instances/Vanquish (dev)/mods"
)

def vanquishReleaseJarName =
    "${base.archivesName.get()}-${project.version}.jar"

tasks.register("deployToVanquish", Copy) {
    dependsOn tasks.named("reobfJar")

    from(
        layout.buildDirectory.file(
            "libs/${vanquishReleaseJarName}"
        )
    )

    into vanquishModsDirectory

    rename {
        "vanquishmixin-dev.jar"
    }
}
```

### 9.3 Commande de déploiement

```bat
.\gradlew.bat deployToVanquish
```

En cas de doute ou après une modification du build :

```bat
.\gradlew.bat clean deployToVanquish --no-configuration-cache
```

### 9.4 Configuration cache Gradle

Éviter dans les blocs d'exécution :

- `delete` appelé directement depuis le projet ;
- `Project.copy` ;
- un `doLast` laissé à l'extérieur de la tâche ;
- des closures capturant inutilement des objets Gradle non sérialisables.

La tâche `Copy` doit faire elle-même la copie et l'écrasement du fichier de destination.

---

## 10. Pistes essayées et abandonnées

### 10.1 Procédures MCreator et paquet réseau pour chaque changement de slot

Ancienne piste : envoyer un `WorkstationSlotMessage` côté client, puis exécuter une procédure MCreator.

Problèmes :

- appels côté client et serveur ;
- spam massif de logs `INPUT CHANGED` ;
- risque de calcul avant synchronisation de la grille serveur ;
- comportement instable du résultat ;
- complexité réseau inutile.

Solution retenue : logique directe dans `WorkstationMenu.slotChanged(...)`, exclusivement côté serveur.

### 10.2 Classes JEI dans `integration/jei`

Des classes auxiliaires avaient été proposées dans un sous-package, mais elles n'existaient pas réellement dans `src/main/java` au moment du build. Le plugin principal ne compilait donc pas.

Solution retenue : Custom Elements réellement créés par MCreator, puis simplification vers une intégration JEI autonome et contrôlable.

### 10.3 Plugin JEI minimal

Le plugin minimal affichant uniquement le message de détection a été utile pour le diagnostic, mais il n'enregistrait aucune catégorie ni recette. Il était donc normal que rien ne soit visible dans JEI.

### 10.4 Copie directe du JAR de développement

La copie directe de la sortie de `jar` donnait un build réussi mais un crash dans l'instance Vanquish à cause des mappings. La correction est l'utilisation obligatoire de `reobfJar`.

### 10.5 Suppression manuelle du JAR dans `doFirst`

L'appel `delete` dans une closure d'exécution provoquait une erreur avec le configuration cache de Gradle 9. La tâche `Copy` peut écraser le fichier et n'a pas besoin de suppression manuelle.

---

## 11. Checklist de non-régression

À exécuter après toute modification du menu, du moteur de recettes, de JEI ou du build.

### Craft

- [ ] Une bûche donne bien 4 planches.
- [ ] Une recette vanilla 3x3 fonctionne.
- [ ] Une recette de mod fonctionne.
- [ ] Une recette KubeJS fonctionne.
- [ ] Le Warrior Helmet fonctionne.
- [ ] Les quatre pièces Warrior fonctionnent.
- [ ] Les ingrédients sont consommés une seule fois.
- [ ] Les seaux/bouteilles/restes sont correctement rendus.
- [ ] La sortie se recalcule après chaque craft.
- [ ] Fermer le GUI avec un résultat affiché ne donne aucun objet gratuit.

### JEI

- [ ] Le plugin Vanquish est détecté dans les logs.
- [ ] La catégorie Workstation apparaît.
- [ ] Les quatre recettes Warrior apparaissent.
- [ ] Les formes JEI correspondent aux formes réellement craftables.
- [ ] La Workstation apparaît comme catalyst.
- [ ] Les recettes Crafting standards reconnaissent la Workstation comme station compatible.
- [ ] Le bouton de transfert `+` est testé séparément avant d'être déclaré fonctionnel.

### Déploiement

- [ ] Le build MCreator fonctionne.
- [ ] `reobfJar` fonctionne.
- [ ] Le JAR final vient de `build/libs`.
- [ ] Un seul JAR `vanquishmixin` est présent dans le dossier `mods`.
- [ ] L'instance Vanquish atteint le menu principal.
- [ ] Un monde existant se charge sans erreur de registre.

---

## 12. Feuille de route

### Priorité haute

- [ ] Ajouter plusieurs crafts post-Ancient Remnant.
- [ ] Définir précisément les crafts exclusifs de chaque palier de boss.
- [ ] Centraliser les recettes dans une source unique partagée entre le moteur et JEI.
- [ ] Valider ou implémenter le transfert JEI par bouton `+`.
- [ ] Ajouter les noms localisés de la catégorie Workstation.
- [ ] Ajouter une recette d'obtention de la Workstation au palier prévu.
- [ ] Vérifier les locks Reskillable ou les prérequis de progression associés.

### Priorité moyenne

- [ ] Remplacer les IDs hardcodés par des tags Vanquish lorsque pertinent.
- [ ] Ajouter des IDs de recettes stables et documentés.
- [ ] Prévoir des recettes shaped/shapeless et des quantités supérieures à 1.
- [ ] Étudier un système data-driven JSON pour les recettes exclusives.
- [ ] Ajouter des messages de debug activables par configuration plutôt que des `System.out` permanents.
- [ ] Tester le comportement en serveur dédié.
- [ ] Ajouter des tests de non-régression reproductibles.

### Nettoyage du dépôt

Le dépôt doit être audité afin de ne pas versionner inutilement :

- `.gradle/` ;
- `build/` ;
- `run/` ;
- fichiers générés temporaires ;
- doublons de sources à la racine.

Créer ou compléter `.gitignore`, puis conserver uniquement les fichiers nécessaires au workspace MCreator et aux sources du mod.

---

## 13. Choisir entre KubeJS et VanquishMixin

| Besoin | Outil recommandé |
|---|---|
| Recette vanilla simple | KubeJS |
| Modification de loot table | LootJS / KubeJS |
| Tags, blacklist, petits événements | KubeJS |
| Spawn conditionnel simple | In Control |
| GUI custom | VanquishMixin |
| Menu avec logique serveur/client | VanquishMixin |
| Nouvelle station de craft | VanquishMixin |
| Intégration JEI custom | VanquishMixin |
| Hook de boss absent des scripts | VanquishMixin |
| État mondial persistant complexe | VanquishMixin |
| Interaction profonde entre plusieurs mods | VanquishMixin |
| Mixin ou modification d'une classe interne | VanquishMixin |

Quand une demande est à la limite des capacités de KubeJS, ne pas forcer un script fragile. Proposer VanquishMixin comme couche d'intégration durable.

---

## 14. Fichiers clés

```text
src/main/java/vanquishmixin/VQAdvancedCrafting.java
src/main/java/vanquishmixin/VQJeiPlugin.java
src/main/java/vanquishmixin/world/inventory/WorkstationMenu.java
src/main/java/vanquishmixin/client/gui/WorkstationScreen.java
src/main/java/vanquishmixin/init/VanquishmixinModBlocks.java
src/main/java/vanquishmixin/init/VanquishmixinModMenus.java
src/main/resources/assets/vanquishmixin/textures/screens/workstation.png
src/main/resources/assets/vanquishmixin/textures/screens/gui_arrow_right.png
src/main/resources/assets/vanquishmixin/textures/screens/crafting_table_output.png
build.gradle
```

Ne pas modifier manuellement les fichiers d'initialisation générés par MCreator sans nécessité absolue.

---

## 15. Git et méthode de travail

Dépôt principal :

https://github.com/SynxGG/vanquishmixin

Références projet complémentaires : `Vanquish Modpack Documentation.pdf`, `Boss Progression Vanquish.pdf`, `mod list.txt` et `skill_locks.json`.

Pratiques recommandées :

1. commit avant toute modification d'un élément MCreator verrouillé ;
2. une fonctionnalité par commit ;
3. message de commit explicite ;
4. test en environnement MCreator ;
5. test dans la vraie instance Vanquish ;
6. push seulement après validation des deux environnements ;
7. documenter tout fichier généré qui doit rester verrouillé.

Exemples de commits :

```text
feat(workstation): add post-remnant exclusive recipes
feat(jei): add workstation recipe transfer
fix(menu): preserve virtual output on close
build: deploy reobfuscated jar to Vanquish dev instance
```

---

## 16. Résumé opérationnel pour le prochain intervenant

- Ne pas déverrouiller le GUI Workstation sans commit préalable.
- Ne pas laisser MCreator écraser `WorkstationMenu.java`.
- Garder toute la logique de craft côté serveur.
- Utiliser les IDs custom via `menu.getSlots().get(id)`.
- Considérer le slot 9 comme virtuel et le vider à la fermeture.
- Tester les recettes spéciales avant les recettes standards.
- Garder l'affichage JEI synchronisé avec les recettes réelles.
- Déployer uniquement le JAR reobfusqué produit après `reobfJar`.
- Pour toute intégration Vanquish trop ambitieuse pour KubeJS, envisager VanquishMixin en priorité.

---
