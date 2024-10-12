package dev.corviraptor.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.text.Text;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.brigadier.CommandDispatcher;

@Mixin(CommandFunctionManager.class)
public interface InvokerCommandFunctionManager {
	@Accessor
    public CommandFunctionManager.Execution getExecution();

    @Accessor("execution")
    public void setExecution(CommandFunctionManager.Execution execution);

    @Accessor
    public MinecraftServer getServer();

    @Accessor("NO_TRACE_IN_FUNCTION_TEXT")
    public static Text getNoTraceInFunctionText() {
        throw new AssertionError();
    }

    @Invoker("getDispatcher")
    public CommandDispatcher<ServerCommandSource> getDispatcher();
}
