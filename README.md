# PotatoRender
A mod that removes Ambient Occlusion and adds resolution adjustment feature.
this can achieve higher FPS with visual tradeoffs.
Supports both OpenGL and Vulkan renderer.

## Requirements
- Minecraft 26.2 or later (see mod's release notes)
- Fabric Loader
- Fabric API

## Mod configuration example
{
  // sets rendering resolution scaling. setting this as 0.5 in 1080p window should set rendering to 540p.
  "scale": 0.25,
  
  // disables Ambient Occlusion if set true
  "disableAO": true, 
  
  // Use linear filtering when upscaling (true) or nearest-neighbor (false).false is faster and looks ugly.
  "linearFilter": true 
}

## Recommended Graphics Settings
### VSync: OFF (IMPORTANT!)
VSync typically increases latency too much.
### Preset: Fast
'Fast' graphics preset should set most of the features to potato-friendly value.
### Mipmap: OFF
Mipmap increases frame time with distant object visual improvements. While the 'Fast' graphics preset will lower Mipmap level to 2x, it is better to turn it off entirely.
### Texture Filtering: OFF
'Fast' graphics preset should shut this off, but double check it is turned off.
### FPS Limit: depends
Double your display refresh rate on <=120Hz monitor, Unlimited on >120Hz monitor. Setting it so will keep lowest 1% FPS above your monitor's refresh rate.

## Recommended mods
[C2ME](https://modrinth.com/mod/c2me-fabric): Concurrently builds up the chunks.
[Ksyxis](https://modrinth.com/mod/ksyxis): Removes 'Spawn Chunks' feature.
[fast noise](https://modrinth.com/mod/zfastnoise): Speeds up perlin noise function used by world generation

# Porting
i will not port this onto neoforge, forge, older versions, whatever.

# Source
some sources are from [RenderScale](https://github.com/Zolo101/RenderScale) mod. affected files are marked with comments in source code.
