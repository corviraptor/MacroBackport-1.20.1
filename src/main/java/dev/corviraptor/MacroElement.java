package dev.corviraptor;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Deque;
import java.util.List;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.server.function.CommandFunction;

import org.jetbrains.annotations.Nullable;

public class MacroElement implements CommandFunction.Element {
		private final List<String> parts;
		private final List<String> variables;

		public MacroElement(List<String> parts, List<String> variables) {
			this.parts = parts;
			this.variables = variables;
		}

		public List<String> getVariables() {
			return this.variables;
		}

		public String getCommand(List<String> arguments) {
			StringBuilder stringBuilder = new StringBuilder();

			for (int i = 0; i < this.variables.size(); i++) {
				stringBuilder.append((String)this.parts.get(i)).append((String)arguments.get(i));
			}

			if (this.parts.size() > this.variables.size()) {
				stringBuilder.append((String)this.parts.get(this.parts.size() - 1));
			}

			return stringBuilder.toString();
		}

		@Override
		public void execute(
			CommandFunctionManager commandFunctionManager,
			ServerCommandSource serverCommandSource,
			Deque<CommandFunctionManager.Entry> deque,
			int i,
			int j,
			@Nullable CommandFunctionManager.Tracer tracer
		) throws CommandSyntaxException {
			throw new IllegalStateException("Tried to execute an uninstantiated macro");
		}
}
