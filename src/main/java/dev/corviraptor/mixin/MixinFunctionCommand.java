package dev.corviraptor.mixin;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.server.command.FunctionCommand;
import net.minecraft.server.command.ServerCommandSource;
import dev.corviraptor.FunctionCommandUtility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(FunctionCommand.class)
public class MixinFunctionCommand {
	/**
	 * @author corviraptor
	 * @reason This is being used to test if this mixin will work. In addition,
	 * this may be the best way to do this since this is a `public static`
	 * method, however I need to learn more about mixins before I can be
	 * sure.
	 */
	@Overwrite
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		FunctionCommandUtility.register(dispatcher);
	}
}
