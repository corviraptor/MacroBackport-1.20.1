package dev.corviraptor.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.DataCommandObject;
import net.minecraft.command.argument.NbtPathArgumentType.NbtPath;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.command.DataCommand;

@Mixin(DataCommand.class)
public interface InvokerDataCommand {
    @Invoker("getNbt")
    public static NbtElement invokeGetNbt(NbtPath Path, DataCommandObject Object) throws CommandSyntaxException {
        throw new AssertionError();
    }
    
}
