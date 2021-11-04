package com.skizzium.projectlib.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Supplier;

import static net.minecraftforge.fml.util.ObfuscationReflectionHelper.findField;

/* |-------------------------------------------------------|
 * | Credits to sciwhiz12 for the command replacement code |
 * |-------------------------------------------------------|
 */
public class CommandReplacement {
    private static final Field CHILDREN_FIELD = findField(CommandNode.class, "children");
    private static final Field LITERALS_FIELD = findField(CommandNode.class, "literals");
    private static final Field ARGUMENTS_FIELD = findField(CommandNode.class, "arguments");

    public static <T> T uncheck(final ThrowingSupplier<T> supplier) {
        return supplier.get();
    }

    public static <T> T get(Field field, Object instance) {
        return (T) uncheck(() -> field.get(instance));
    }

    public static <T> void replaceAndRegister(CommandDispatcher<T> dispatcher, LiteralArgumentBuilder<T> node) {
        final RootCommandNode<T> root = dispatcher.getRoot();
        final String literal = node.getLiteral();
        final CommandNode<T> existingNode = root.getChild(literal);
        if (existingNode != null) {
            removeChild(root, existingNode);
        }
        else {
            return;
        }
        dispatcher.register(node);
    }

    private static void removeChild(CommandNode<?> parentNode, CommandNode<?> childNode) {
        final Map<String, CommandNode<?>> children = get(CHILDREN_FIELD, parentNode);
        children.remove(childNode.getName());

        Map<String, CommandNode<?>> typedMap = null;
        if (childNode instanceof LiteralCommandNode) {
            typedMap = get(LITERALS_FIELD, parentNode);
        } else if (childNode instanceof ArgumentCommandNode) {
            typedMap = get(ARGUMENTS_FIELD, parentNode);
        }
        if (typedMap != null) {
            typedMap.remove(childNode.getName());
        }

    }

    public static <E extends Throwable> void sneakyThrow(Throwable ex) throws E {
        throw (E) ex;
    }

    @FunctionalInterface
    public interface ThrowingSupplier<T> extends Supplier<T> {
        /**
         * Gets a result, potentially throwing an exception.
         *
         * @return a result
         */
        T getThrows() throws Throwable;

        /**
         * {@inheritDoc}
         *
         * @implSpec This calls {@link #getThrows()}, and rethrows any exception using {@link #sneakyThrow(Throwable)}.
         */
        @Override
        default T get() {
            try {
                return getThrows();
            } catch (Throwable e) {
                sneakyThrow(e);
                return null; // Never reached, as previous line always throws
            }
        }
    }
}
