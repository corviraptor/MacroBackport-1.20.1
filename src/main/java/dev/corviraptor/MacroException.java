package dev.corviraptor;

import net.minecraft.text.Text;

public class MacroException extends Exception {
    private final Text message;

    public MacroException(Text message) {
        super(message.getString());
        this.message = message;
    }

    public String getMessage() {
        return this.message.getString();
    }
}
