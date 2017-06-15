###DynamicSurroundings-1.7.10-1.0.5.12
**Fixes**
* Setting footstep sound scale factor to 0 reverts to vanilla footstep sounds (backport)
* [OpenEye](https://openeye.openmods.info/crashes/4a99da03285429c87ec8d9347210268e): Fix NPE in storm render when calculating color
* [OpenEye](https://openeye.openmods.info/crashes/35c791f2c48ae15e0a8c42b832c38aac): Defensive code for bad potion IDs
* [OpenEye](https://openeye.openmods.info/crashes/ef9cee918b144eaa128da318420d6dbf): Hard requirement of [Forge 10.13.4.1614](https://files.minecraftforge.net/maven/net/minecraftforge/forge/index_1.7.10.html) or later for Minecraft 1.7.10

**Changes**
* Footstep sound processing performance changes (backport)
* Use ASM to hook Minecraft sound stream loading to improve responsiveness and reduce stream errors; can be turned off in config if needed (backport)
* Pumpkin/Melon footstep sounds no longer squishy (backport)
* Display diagnostic info only when debug screen is active and debug logging is enabled in config

###DynamicSurroundings-1.7.10-1.0.5.11
**Fixes**
* No more sound clicking when moving fast; was noticeable when flying over a beach (backport)

**Changes**
* No sound attenuation for player centered sounds (backport)
* Increase total number of sound channels (backport)
* Attempt automatic restart of crashed sound system (backport)
* Improve RNG used by scanning routines (backport)

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
