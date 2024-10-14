package dev.corviraptor;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.server.function.CommandFunctionManager.Execution;

import org.spongepowered.asm.mixin.Mixin;

import dev.corviraptor.mixin.InvokerCommandFunctionManager;
import dev.corviraptor.mixin.InvokerExecution;

@Mixin(CommandFunctionManager.class)
public final class CommandFunctionManagerHelper {
    /**
     * Executes a function. This may have two cases: new or recursive.
     * 
     * <p>
     * In a new execution, the {@link #execution execution == null}, and a custom
     * {@code tracer} can be specified. The return value indicates the number of
     * commands and nested functions ran.
     * 
     * <p>
     * In a recursive execution, {@link #execution execution != null}. It is
     * required that {@code tracer == null}, or the execution reports an error and
     * is
     * skipped. The return value is {@code 0}.
     * 
     * @return a non-zero value for a new execution, or {@code 0} for a recursive
     *         execution
     * @see #execute(CommandFunction, ServerCommandSource)
     * 
     * @param manager  the CommandFunctionManager
     * @param function  the function
     * @param source    the command source to execute with
     * @param tracer    a tracer for a non-recursive function execution
     * @param arguments arguments for macro substitution, if any
     */
    public static int execute(
        CommandFunctionManager manager,
        CommandFunction function,
        ServerCommandSource source,
        @Nullable CommandFunctionManager.Tracer tracer,
        @Nullable NbtCompound arguments)
    throws MacroException {

        CommandFunction commandFunction = CommandFunctionHelper.withMacroReplaced(
            function, arguments, manager.getDispatcher(), source
        );
        
        InvokerCommandFunctionManager accessor = (InvokerCommandFunctionManager) manager;
        Execution execution = accessor.getExecution();
        InvokerExecution executionInvoker = (InvokerExecution) execution;
        
        if (execution != null) {
            if (tracer != null) {
                execution.reportError(InvokerCommandFunctionManager.getNoTraceInFunctionText().getString());
                return 0;
            } else {
                executionInvoker.invokeRecursiveRun(commandFunction, source);
                return 0;
            }
        } else {
            int var6;
            try {
                execution = manager.new Execution(tracer);
                var6 = executionInvoker.run(commandFunction, source);
            } finally {
                execution = null;
            }

            return var6;
        }
    }
}
