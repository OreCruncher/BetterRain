##Additional Configuration Content
This folder contains Json configuration files that modify sound and visual effects from defaults.  To use these configuration files in your pack:

1. Download the configuration file and place it into your `./minecraft/config/dsurround` directory.
2. Edit the `dsurround.cfg` file to let Dynamic Surroundings know to process the configuration file during startup.

The following configuration files are available for use.  The name in brackets after the file name tells what section in the dsurround.cfg file needs to be modified to add the file to the configuration.

Keep in mind that configuration files are processed in the order they are listed in `dsurround.cfg`.  If you are adding a configuration that removes effects (like those in safeunderground.json) they should be listed last.

**nodust.json**  
[blocks.Config Files]    
Turns off the dust drop particle effect for Vanilla blocks.

**safeunderground.json**  
[biome.Config Files]    
Turns off the spot sounds for the Underground biome.  No more monster growls or rock fall sound effects.

**noplayereffects.json**  
[biome.Config Files]  
Turns off the heartbeat and tummy grumble player effects.
