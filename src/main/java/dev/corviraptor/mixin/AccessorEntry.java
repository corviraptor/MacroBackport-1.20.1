package dev.corviraptor.mixin;

import net.minecraft.server.function.CommandFunctionManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CommandFunctionManager.Entry.class)
public interface AccessorEntry {
    @Accessor
    public int getDepth();
}
