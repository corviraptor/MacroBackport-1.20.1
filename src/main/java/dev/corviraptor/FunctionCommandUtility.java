package dev.corviraptor;

import org.jetbrains.annotations.Nullable;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import dev.corviraptor.mixin.InvokerDataCommand;

import java.util.Collection;
import net.minecraft.command.DataCommandObject;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.FunctionCommand;
import net.minecraft.text.Text;
import org.apache.commons.lang3.mutable.MutableObject;

/*
 * FunctionCommand was essentially entirely changed in 1.20.2.
 */
public final class FunctionCommandUtility {
    private static final DynamicCommandExceptionType ARGUMENT_NOT_COMPOUND_EXCEPTION = new DynamicCommandExceptionType(
        argument -> Text.translatable("commands.function.error.argument_not_compound", argument)
	);
    
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("with");

		for (DataCommand.ObjectType objectType : DataCommand.SOURCE_OBJECT_TYPES) {
			objectType.addArgumentsToBuilder(
					literalArgumentBuilder,
					builder -> builder.executes(
							context -> execute(
									(ServerCommandSource) context.getSource(),
									CommandFunctionArgumentType.getFunctions(context, "name"),
									objectType.getObject(context).getNbt()))
							.then(
									CommandManager.argument("path", NbtPathArgumentType.nbtPath())
											.executes(
													context -> execute(
															context.getSource(),
															CommandFunctionArgumentType.getFunctions(context, "name"),
															getArgument(NbtPathArgumentType.getNbtPath(context, "path"),
																	objectType.getObject(context))))));
		}

		dispatcher.register(
				CommandManager.literal("function")
						.requires(source -> source.hasPermissionLevel(2))
						.then(
								CommandManager.argument("name", CommandFunctionArgumentType.commandFunction())
										.suggests(FunctionCommand.SUGGESTION_PROVIDER)
										.executes(context -> execute(context.getSource(),
												CommandFunctionArgumentType.getFunctions(context, "name"), null))
										.then(
												CommandManager
														.argument("arguments", NbtCompoundArgumentType.nbtCompound())
														.executes(
																context -> execute(
																		context.getSource(),
																		CommandFunctionArgumentType
																				.getFunctions(context, "name"),
																		NbtCompoundArgumentType.getNbtCompound(context,
																				"arguments"))))
										.then(literalArgumentBuilder)));
	}

	private static NbtCompound getArgument(NbtPathArgumentType.NbtPath path, DataCommandObject object)
			throws CommandSyntaxException {
		NbtElement nbtElement = InvokerDataCommand.invokeGetNbt(path, object);
		if (nbtElement instanceof NbtCompound) {
			return (NbtCompound) nbtElement;
		} else {
			throw ARGUMENT_NOT_COMPOUND_EXCEPTION.create(nbtElement.getNbtType().getCrashReportName());
		}
	}

	private static int execute(ServerCommandSource source, Collection<CommandFunction> functions,
			@Nullable NbtCompound arguments) {
		int i = 0;
		boolean bl = false;
		boolean bl2 = false;

		for (CommandFunction commandFunction : functions) {
			try {
				FunctionCommandUtility.FunctionResult functionResult = execute(source, commandFunction, arguments);
				i += functionResult.value();
				bl |= functionResult.isReturn();
				bl2 = true;
			} catch (MacroException var9) {
				source.sendError(Text.of(var9.getMessage()));
			}
		}

		if (bl2) {
			int j = i;
			if (functions.size() == 1) {
				if (bl) {
					source.sendFeedback(() -> Text.translatable("commands.function.success.single.result", j,
							((CommandFunction) functions.iterator().next()).getId()), true);
				} else {
					source.sendFeedback(() -> Text.translatable("commands.function.success.single", j,
							((CommandFunction) functions.iterator().next()).getId()), true);
				}
			} else if (bl) {
				source.sendFeedback(
						() -> Text.translatable("commands.function.success.multiple.result", functions.size()), true);
			} else {
				source.sendFeedback(() -> Text.translatable("commands.function.success.multiple", j, functions.size()),
						true);
			}
		}

		return i;
	}

	public static FunctionCommandUtility.FunctionResult execute(ServerCommandSource source, CommandFunction function,
			@Nullable NbtCompound arguments) throws MacroException {
		MutableObject<FunctionCommandUtility.FunctionResult> mutableObject = new MutableObject<>();
		
		CommandFunctionManager manager = source.getServer().getCommandFunctionManager();

		int i = CommandFunctionManagerUtility.execute(
						manager,
						function,
						source.withSilent().withMaxLevel(2).withReturnValueConsumer(
								value -> mutableObject.setValue(new FunctionCommandUtility.FunctionResult(value, true))),
						null,
						arguments);
		FunctionCommandUtility.FunctionResult functionResult = mutableObject.getValue();
		return functionResult != null ? functionResult : new FunctionCommandUtility.FunctionResult(i, false);
	}

	public static record FunctionResult(int value, boolean isReturn) {
	}
}
