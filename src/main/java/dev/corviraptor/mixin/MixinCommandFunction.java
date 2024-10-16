package dev.corviraptor.mixin;

import com.mojang.brigadier.CommandDispatcher;
import java.util.List;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.server.function.CommandFunction;
import dev.corviraptor.CommandFunctionHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(CommandFunction.class)
public class MixinCommandFunction {
	/**
	 * @author corviraptor
	 * @reason This is being used to test if this mixin will work. In addition,
	 *         this may be the best way to do this since this is a `public static`
	 *         method, however I need to learn more about mixins before I can be
	 *         sure. This is my first one!
	 *         TODO: come back to this and make sure its okay
	 */
	@Overwrite
	public static CommandFunction create(Identifier id, CommandDispatcher<ServerCommandSource> dispatcher, ServerCommandSource source, List<String> lines) {
		return CommandFunctionHelper.create(id, dispatcher, source, lines);
	}
}
