# MacroBackport-1.20.1

Backports Function Macros and multi-line commands to 1.20.1 from 1.20.2.

The code is directly ripped from 1.20.2 with very little modifications to the actual function implementations in that code. `@Overwrite` is used quite a bit, and there's some evil reflection along the way as well. It's not pretty, but this should never have to be updated to a future version of Minecraft for reasons that should be obvious and I don't think many other mods have a reason to directly edit stuff in the `net.minecraft.server.function` namespace.

I'm pretty certain this shouldn't actually change any data in your Minecraft instance aside from the stuff functions are supposed to be able to change in your world's save (NBT data, scoreboard values, etc). However, some messed up stuff happens in this code because I'm replacing a lot of stuff so you are still installing this mod at your own risk. Test it in a backup instance with your datapacks first to make sure nothing blows up.
