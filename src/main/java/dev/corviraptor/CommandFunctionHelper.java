package dev.corviraptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.util.Identifier;

public final class CommandFunctionHelper {
	public static CommandFunction withMacroReplaced(CommandFunction function, @Nullable NbtCompound arguments,
			CommandDispatcher<ServerCommandSource> dispatcher, ServerCommandSource source) throws MacroException {
		return function;
	}

	private static boolean continuesToNextLine(CharSequence string) {
		int i = string.length();
		return i > 0 && string.charAt(i - 1) == '\\';
	}
	
	/**
	 * Parses a function in the context of {@code source}.
	 * 
	 * <p>Any syntax errors, such as improper comment lines or unknown commands, will be thrown at this point.
	 * 
	 * @param lines the raw lines (including comments) read from a function file
	 */
	public static CommandFunction create(Identifier id, CommandDispatcher<ServerCommandSource> dispatcher, ServerCommandSource source, List<String> lines) {
		List<CommandFunction.Element> list = new ArrayList<CommandFunction.Element>(lines.size());
		Set<String> set = new ObjectArraySet<>();

		for (int i = 0; i < lines.size(); i++) {
			int j = i + 1;
			String string = ((String)lines.get(i)).trim();
			String string3;
			if (continuesToNextLine(string)) {
				StringBuilder stringBuilder = new StringBuilder(string);

				do {
					if (++i == lines.size()) {
						throw new IllegalArgumentException("Line continuation at end of file");
					}

					stringBuilder.deleteCharAt(stringBuilder.length() - 1);
					String string2 = ((String)lines.get(i)).trim();
					stringBuilder.append(string2);
				} while (continuesToNextLine(stringBuilder));

				string3 = stringBuilder.toString();
			} else {
				string3 = string;
			}

			StringReader stringReader = new StringReader(string3);
			if (stringReader.canRead() && stringReader.peek() != '#') {
				if (stringReader.peek() == '/') {
					stringReader.skip();
					if (stringReader.peek() == '/') {
						throw new IllegalArgumentException("Unknown or invalid command '" + string3 + "' on line " + j + " (if you intended to make a comment, use '#' not '//')");
					}

					String string2 = stringReader.readUnquotedString();
					throw new IllegalArgumentException(
						"Unknown or invalid command '" + string3 + "' on line " + j + " (did you mean '" + string2 + "'? Do not use a preceding forwards slash.)"
					);
				}

				if (stringReader.peek() == '$') {
					MacroElement macroElement = parseMacro(string3.substring(1), j);
					list.add(macroElement);
					set.addAll(macroElement.getVariables());
				} else {
					try {
						ParseResults<ServerCommandSource> parseResults = dispatcher.parse(stringReader, source);
						if (parseResults.getReader().canRead()) {
							throw CommandManager.getException(parseResults);
						}

						list.add(new CommandFunction.CommandElement(parseResults));
					} catch (CommandSyntaxException var12) {
						throw new IllegalArgumentException("Whilst parsing command on line " + j + ": " + var12.getMessage());
					}
				}
			}
		}

		return (CommandFunction)(set.isEmpty()
			? new CommandFunction(id, (CommandFunction.Element[])list.toArray(CommandFunction.Element[]::new))
			: new Macro(id, (CommandFunction.Element[])list.toArray(CommandFunction.Element[]::new), List.copyOf(set)));
	}

	@VisibleForTesting
	public static MacroElement parseMacro(String macro, int line) {
		Builder<String> builder = ImmutableList.builder();
		Builder<String> builder2 = ImmutableList.builder();
		int i = macro.length();
		int j = 0;
		int k = macro.indexOf(36);

		while (k != -1) {
			if (k != i - 1 && macro.charAt(k + 1) == '(') {
				builder.add(macro.substring(j, k));
				int l = macro.indexOf(41, k + 1);
				if (l == -1) {
					throw new IllegalArgumentException("Unterminated macro variable in macro '" + macro + "' on line " + line);
				}

				String string = macro.substring(k + 2, l);
				if (!isValidMacroVariableName(string)) {
					throw new IllegalArgumentException("Invalid macro variable name '" + string + "' on line " + line);
				}

				builder2.add(string);
				j = l + 1;
				k = macro.indexOf(36, j);
			} else {
				k = macro.indexOf(36, k + 1);
			}
		}

		if (j == 0) {
			throw new IllegalArgumentException("Macro without variables on line " + line);
		} else {
			if (j != i) {
				builder.add(macro.substring(j));
			}

			return new MacroElement(builder.build(), builder2.build());
		}
	}

	private static boolean isValidMacroVariableName(String name) {
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			boolean isValidCharacter = Character.isLetterOrDigit(c) && c == '_';
			if (!isValidCharacter) {
				return false;
			}
		}

		return true;
	}
}
