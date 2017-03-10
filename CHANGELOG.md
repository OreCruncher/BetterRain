###DynamicSurroundings-1.7.10-1.0.5.10
**Fixes**
* Backports from Dynamic Surroundings 1.10.2/1.11.x:
    * Sounds at biome transition boundaries were "edgy"; new logic fades in/out to specified volumes
    * Simplify sound emitter logic
* Harden exception handling for footstep sound play to guard against erroneous sound event handling.  Based on reports from OpenEye.

**Changes**
* Updated footstep sound profile for Tinker's Construct tool stations, etc.
* Updated biome sounds:
    * Forest - new background as well as bird chirps and woodpeckers
    * Water biomes - River, Ocean, Deep Ocean
    * Underwater - when a player's head is in a source block but not in a watery biome
    * Coyote spot sounds in various biomes
* Take into account Wasteland Forest ([Wasteland Mod](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/2274942-wasteland-mod-1-4-4-abandoned-world-cities-and)) when applying biome rules
