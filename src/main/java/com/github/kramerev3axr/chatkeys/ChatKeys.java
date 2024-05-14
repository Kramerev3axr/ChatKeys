package com.github.kramerev3axr.chatkeys;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.jmx.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ChatKeys implements ModInitializer {
	public static final String MOD_ID = "chatkeys";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, sender, parameters) -> {
			String stringMessage = message.getContent().getString();
			String namePrefix = "<" + sender.getName().getString() + "> ";

            switch (stringMessage) {
                case "{pos}" -> {
                    return broadcastToPlayers(sender, namePrefix + getPos(sender));
                }
                case "{biome}" -> {
                    return broadcastToPlayers(sender, namePrefix + getBiome(sender));
                }
                case "{dim}" -> {
					return broadcastToPlayers(sender, namePrefix + getDim(sender));
                }
				case "{loc}" -> {
					String all = namePrefix + getPos(sender) + " (" + getBiome(sender) + ") " + "(" + getDim(sender) + ")";
					return broadcastToPlayers(sender, all);
				}
            }
            return true;
		});
	}

	private boolean broadcastToPlayers(ServerPlayerEntity sender, String message) {
		List<ServerPlayerEntity> players =  sender.getServer().getPlayerManager().getPlayerList();
		for (ServerPlayerEntity player : players) // Send message to all players
			player.sendMessage(Text.literal(message));
		return false;
	}

	private String getPos(ServerPlayerEntity sender) {
		return (int) sender.getPos().getX() + ", " + (int) sender.getPos().getY() + ", " + (int) sender.getPos().getZ();
	}

	private String getBiome(ServerPlayerEntity sender) {
		String rawBiome = new Identifier(sender.getEntityWorld().getBiome(sender.getBlockPos()).getIdAsString()).getPath();
		return fixName(rawBiome);
	}

	private String getDim(ServerPlayerEntity sender) {
		String dim = sender.getEntityWorld().getRegistryKey().getValue().getPath();
		return fixName(dim);
	}

	private String fixName(String rawName) {
		String name = "";
		for (int i = 0; i < rawName.length(); i++) {
			if (i == 0 || rawName.charAt(i - 1) == '_') {
				name += Character.toUpperCase(rawName.charAt(i));
				continue;
			}

			if (rawName.charAt(i) == '_') {
				name += " ";
				continue;
			}

			name += rawName.charAt(i);
		}
		return name;
	}
}