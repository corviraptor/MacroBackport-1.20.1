package dev.corviraptor;

import java.util.Collection;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class CommandFunctionArgumentTypeHelper {
    
    public static final DynamicCommandExceptionType UNKNOWN_FUNCTION_TAG_EXCEPTION = new DynamicCommandExceptionType((id) -> {
        return Text.translatable("arguments.function.tag.unknown", new Object[]{id});
    });
    public static final DynamicCommandExceptionType UNKNOWN_FUNCTION_EXCEPTION = new DynamicCommandExceptionType((id) -> {
        return Text.translatable("arguments.function.unknown", new Object[]{id});
    });

    public static CommandFunction getFunction(CommandContext<ServerCommandSource> context, Identifier id)
            throws CommandSyntaxException {
        return (CommandFunction) ((ServerCommandSource) context.getSource()).getServer().getCommandFunctionManager()
                .getFunction(id).orElseThrow(() -> {
                    return UNKNOWN_FUNCTION_EXCEPTION.create(id.toString());
                });
    }

    public static Collection<CommandFunction> getFunctionTag(CommandContext<ServerCommandSource> context, Identifier id)
            throws CommandSyntaxException {
        Collection<CommandFunction> collection = ((ServerCommandSource) context.getSource()).getServer()
                .getCommandFunctionManager().getTag(id);
        if (collection == null) {
            throw UNKNOWN_FUNCTION_TAG_EXCEPTION.create(id.toString());
        } else {
            return collection;
        }
    }
}
