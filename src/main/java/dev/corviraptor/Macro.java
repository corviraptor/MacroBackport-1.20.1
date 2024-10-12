package dev.corviraptor;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jetbrains.annotations.Nullable;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtShort;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class Macro extends CommandFunction {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#");
    private final List<String> variables;
    /* i have no idea what CACHE_SIZE is used for in the original code, there are no
    references to it anywhere in the original CommandFunction$Macro file in 1.20.2 */
    // private static final int CACHE_SIZE = 8;
    private final Object2ObjectLinkedOpenHashMap<List<String>, CommandFunction> cache = new Object2ObjectLinkedOpenHashMap<>(
            8, 0.25F);

    public Macro(Identifier id, CommandFunction.Element[] elements, List<String> variables) {
        super(id, elements);
        this.variables = variables;
    }

    public CommandFunction withMacroReplaced(@Nullable NbtCompound arguments,
            CommandDispatcher<ServerCommandSource> dispatcher, ServerCommandSource source) throws MacroException {
        if (arguments == null) {
            throw new MacroException(Text.translatable("commands.function.error.missing_arguments", this.getId()));
        } else {
            List<String> list = new ArrayList<String>(this.variables.size());

            for (String string : this.variables) {
                if (!arguments.contains(string)) {
                    throw new MacroException(
                            Text.translatable("commands.function.error.missing_argument", this.getId(), string));
                }

                list.add(toString(arguments.get(string)));
            }

            CommandFunction commandFunction = this.cache.getAndMoveToLast(list);
            if (commandFunction != null) {
                return commandFunction;
            } else {
                if (this.cache.size() >= 8) {
                    this.cache.removeFirst();
                }

                CommandFunction commandFunction2 = this.withMacroReplaced(list, dispatcher, source);
                if (commandFunction2 != null) {
                    this.cache.put(list, commandFunction2);
                }

                return commandFunction2;
            }
        }
    }

    private static String toString(NbtElement nbt) {
        if (nbt instanceof NbtFloat nbtFloat) {
            return DECIMAL_FORMAT.format((double) nbtFloat.floatValue());
        } else if (nbt instanceof NbtDouble nbtDouble) {
            return DECIMAL_FORMAT.format(nbtDouble.doubleValue());
        } else if (nbt instanceof NbtByte nbtByte) {
            return String.valueOf(nbtByte.byteValue());
        } else if (nbt instanceof NbtShort nbtShort) {
            return String.valueOf(nbtShort.shortValue());
        } else {
            return nbt instanceof NbtLong nbtLong ? String.valueOf(nbtLong.longValue()) : nbt.asString();
        }
    }

    private CommandFunction withMacroReplaced(List<String> arguments, CommandDispatcher<ServerCommandSource> dispatcher,
            ServerCommandSource source) throws MacroException {
        CommandFunction.Element[] elements = this.getElements();
        CommandFunction.Element[] elements2 = new CommandFunction.Element[elements.length];

        for (int i = 0; i < elements.length; i++) {
            CommandFunction.Element element = elements[i];
            if (!(element instanceof MacroElement macroElement)) {
                elements2[i] = element;
            } else {
                List<String> list = macroElement.getVariables();
                List<String> list2 = new ArrayList<String>(list.size());

                for (String string : list) {
                    list2.add((String) arguments.get(this.variables.indexOf(string)));
                }

                String string2 = macroElement.getCommand(list2);

                try {
                    ParseResults<ServerCommandSource> parseResults = dispatcher.parse(string2, source);
                    if (parseResults.getReader().canRead()) {
                        throw CommandManager.getException(parseResults);
                    }

                    elements2[i] = new CommandFunction.CommandElement(parseResults);
                } catch (CommandSyntaxException var13) {
                    throw new MacroException(Text.translatable("commands.function.error.parse", this.getId(), string2,
                            var13.getMessage()));
                }
            }
        }

        Identifier identifier = this.getId();
        return new CommandFunction(
                new Identifier(identifier.getNamespace(), identifier.getPath() + "/" + arguments.hashCode()),
                elements2);
    }

    static {
        DECIMAL_FORMAT.setMaximumFractionDigits(15);
        DECIMAL_FORMAT.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
    }
}