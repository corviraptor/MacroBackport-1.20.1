package dev.corviraptor.mixin;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;

import java.util.Deque;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CommandFunctionManager.Execution.class)
public interface InvokerExecution {
    @Invoker("recursiveRun")
    public void invokeRecursiveRun(CommandFunction function, ServerCommandSource source);

    @Invoker("run")
    public int run(CommandFunction function, ServerCommandSource source);

    @Invoker("addReturnConsumer")
    public ServerCommandSource invokeAddReturnConsumer(ServerCommandSource source);

    @Accessor
    public Deque<CommandFunctionManager.Entry> getQueue();

    @Accessor
    public CommandFunctionManager.Tracer getTracer();

    @Accessor
    public int getDepth();

    @Accessor("depth")
    public void setDepth(int depth);

    @Accessor
    public boolean getReturned();

    @Accessor("returned")
    public void setReturned(boolean returned);

    @Accessor
    public List<CommandFunctionManager.Entry> getWaitlist();
}
