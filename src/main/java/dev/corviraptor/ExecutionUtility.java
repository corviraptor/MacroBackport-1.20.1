package dev.corviraptor;

import java.util.Deque;

import com.google.common.collect.Lists;

import dev.corviraptor.mixin.AccessorEntry;
import dev.corviraptor.mixin.InvokerCommandFunctionManager;
import dev.corviraptor.mixin.InvokerExecution;
import dev.corviraptor.mixin.MixinExecution;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;

import java.lang.reflect.Field;

public final class ExecutionUtility {
    /**
     * Handles a new case in {@link CommandFunctionManager#execute(CommandFunction,
     * ServerCommandSource, CommandFunctionManager.Tracer, NbtCompound)}.
     * 
     * @return a value for the command result
     * 
     * @param source the command source
     * @param function the function
     */
    public static int run(
            MixinExecution executionMixin, CommandFunction function,
            ServerCommandSource source) 
    {
        CommandFunctionManager.Execution execution = (CommandFunctionManager.Execution) (Object) executionMixin;
        CommandFunctionManager manager = executionMixin.getManager();
        InvokerExecution executionInvoker = (InvokerExecution) execution;
        Deque<CommandFunctionManager.Entry> queue = executionInvoker.getQueue();
        InvokerCommandFunctionManager managerInvoker = (InvokerCommandFunctionManager)manager;

        int i = manager.getMaxCommandChainLength();
        ServerCommandSource serverCommandSource = executionInvoker.invokeAddReturnConsumer(source);
        int j = 0;
        CommandFunction.Element[] elements = function.getElements();

        for (int k = elements.length - 1; k >= 0; k--) {
            executionInvoker.getQueue().push(new CommandFunctionManager.Entry(serverCommandSource, 0, elements[k]));
        }

        while (!queue.isEmpty()) {
            try {
                AccessorEntry entry = (AccessorEntry)queue.removeFirst();
                managerInvoker.getServer().getProfiler().push(entry::toString);
                executionInvoker.setDepth(entry.getDepth());
                ((CommandFunctionManager.Entry)entry).execute(manager, queue, i, executionInvoker.getTracer());
                if (!executionInvoker.getReturned()) {
                    if (!executionInvoker.getWaitlist().isEmpty()) {
                        Lists.reverse(executionInvoker.getWaitlist()).forEach(queue::addFirst);
                    }
                } else {
                    while (!queue.isEmpty() && ((AccessorEntry)queue.peek()).getDepth() >= executionInvoker.getDepth()) {
                        queue.removeFirst();
                    }

                    executionInvoker.setReturned(false);
                }

                executionInvoker.getWaitlist().clear();
            } finally {
                managerInvoker.getServer().getProfiler().pop();
            }

            if (++j >= i) {
                return j;
            }
        }

        return j;
    }
	
    /**
     * HERE BE REFLECTION! I will not make this generic because it is Evil!
     * 
     * @return The instance of the class that surrounds {@link execution}
     * 
     * @param execution The instance of the inner {@link CommandFunctionManager.Execution} class that we
     * want to find the outer {@link CommandFunctionManager} instance of
     * 
     * TODO: if there's somehow a type-safe way of doing this i'll cry
     */
	public static CommandFunctionManager GetManager(CommandFunctionManager.Execution execution) {
        try {
            // Get the implicit reference from the inner to the outer instance
            // ... make it accessible, as it has default visibility
            Field field = CommandFunctionManager.Execution.class.getDeclaredField("this$0");
            field.setAccessible(true);

            // Dereference and cast it
            CommandFunctionManager manager = (CommandFunctionManager) field.get(execution);
            return manager;
        } catch (Exception e) {
            throw new AssertionError(new ReflectiveOperationException(e));
        }
	}
}
