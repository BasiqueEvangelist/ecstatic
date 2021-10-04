# Ecstatic

## Getting started
1. Add Ecstatic to the gradle project
```gradle
plugins {
    id "fabric-loom" version "0.9-SNAPSHOT"
    id "me.basiqueevangelist.ecstatic" version "0.1.0"
}
```
2. Configure classes to be made static
```gradle
ecstatic {
    targetedClasses.add 'net/minecraft/entity/mob/SpellcastingIllagerEntity$CastSpellGoal'
}
```
3. Profit!
![Example](./media/example.png)