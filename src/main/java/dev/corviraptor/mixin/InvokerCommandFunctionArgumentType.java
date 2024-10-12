package dev.corviraptor.mixin;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;

import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CommandFunctionArgumentType.class)
public interface InvokerCommandFunctionArgumentType {
    @Invoker("getFunctionTag")
    static Collection<CommandFunction> invokeGetFunctionTag(CommandContext<ServerCommandSource> context, Identifier id)
            throws CommandSyntaxException {
        throw new AssertionError();
    }

    @Invoker("getFunction")
    static CommandFunction invokeGetFunction(CommandContext<ServerCommandSource> context, Identifier id) throws CommandSyntaxException {
        throw new AssertionError();
    }
}
