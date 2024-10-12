package dev.corviraptor.mixin;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;

import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.command.argument.CommandFunctionArgumentType.FunctionArgument;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.server.function.CommandFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(CommandFunctionArgumentType.class)
public abstract class MixinCommandFunctionArgumentType implements ArgumentType<FunctionArgument> {
	/**
	 * @author corviraptor
	 * @reason This is being used to test if this mixin will work. In addition,
	 *         this may be the best way to do this since this is a `public static`
	 *         method, however I need to learn more about mixins before I can be
	 *         sure.
	 *         TODO: come back to this and make sure its okay
	 */
	@Overwrite
	public CommandFunctionArgumentType.FunctionArgument parse(StringReader stringReader) throws CommandSyntaxException {
		if (stringReader.canRead() && stringReader.peek() == '#') {
			stringReader.skip();
			final Identifier identifier = Identifier.fromCommandInput(stringReader);
			return new CommandFunctionArgumentType.FunctionArgument() {
				@Override
				public Collection<CommandFunction> getFunctions(CommandContext<ServerCommandSource> context)
						throws CommandSyntaxException {
					return InvokerCommandFunctionArgumentType.invokeGetFunctionTag(context, identifier);
				}

				@Override
				public Pair<Identifier, Either<CommandFunction, Collection<CommandFunction>>> getFunctionOrTag(
						CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
					return Pair.of(identifier,
							Either.right(InvokerCommandFunctionArgumentType.invokeGetFunctionTag(context, identifier)));
				}
			};
		} else {
			final Identifier identifier = Identifier.fromCommandInput(stringReader);
			return new CommandFunctionArgumentType.FunctionArgument() {
				@Override
				public Collection<CommandFunction> getFunctions(CommandContext<ServerCommandSource> context)
						throws CommandSyntaxException {
					return Collections.singleton(InvokerCommandFunctionArgumentType.invokeGetFunction(context, identifier));
				}

				@Override
				public Pair<Identifier, Either<CommandFunction, Collection<CommandFunction>>> getFunctionOrTag(
						CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
					return Pair.of(identifier,
							Either.left(InvokerCommandFunctionArgumentType.invokeGetFunction(context, identifier)));
				}
			};
		}
	}
}
