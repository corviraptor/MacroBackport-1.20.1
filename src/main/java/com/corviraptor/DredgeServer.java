package dev.corviraptor;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class DredgeServer implements ModInitializer {

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			HealthCommand.register(dispatcher);
			FoodbarCommand.register(dispatcher);
		});
	}
}