package dev.corviraptor.mixin;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import dev.corviraptor.ExecutionUtility;

@Mixin(CommandFunctionManager.Execution.class)
public abstract class MixinExecution {
	@Dynamic
	@Final
    @Shadow(aliases = "field_33544")
	private CommandFunctionManager field_33544;
	
	@Unique
	public CommandFunctionManager getManager() {
		return field_33544;
	}

    /**
	 * @author corviraptor
	 * @reason This is being used to test if this mixin will work. In addition,
	 *         this may be the best way to do this since this is a `public static`
	 *         method, however I need to learn more about mixins before I can be
	 *         sure.
	 *         TODO: come back to this and make sure its okay
	 */
	@Overwrite
	int run(CommandFunction function, ServerCommandSource source) {
		return ExecutionUtility.run(this, function, source);
	}

}
