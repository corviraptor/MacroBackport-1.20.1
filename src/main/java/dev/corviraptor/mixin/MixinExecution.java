package dev.corviraptor.mixin;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import dev.corviraptor.ExecutionHelper;

@Mixin(CommandFunctionManager.Execution.class)
public abstract class MixinExecution {
	/*
	 * "field_33544" is a synthetic field for the outer class instance of
	 * Execution, its CommandFunctionManager, in the bytecode
	 * this doesnt seem to work though. it's weird because 
	 * [this guide](https://gist.github.com/TelepathicGrunt/59f5ae53cf2b336ddfa0a37032e5e5a3) 
	 * claims to work doing the same thing. "this$0" also doesn't work
	 */ 
	//@Dynamic
	//@Final
	@Shadow(aliases = { "this$0", "field_33544" })
	private CommandFunctionManager this$0;
	
	@Unique
	public CommandFunctionManager getEnclosingManager() {
		return this$0;
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
		return ExecutionHelper.run(this, function, source);
	}

}
