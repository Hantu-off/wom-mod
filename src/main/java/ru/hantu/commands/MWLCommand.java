package ru.hantu.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import ru.hantu.MWL;

import java.util.concurrent.CompletableFuture;

public class MWLCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("mwl")
                .requires(source -> {
                    // Check OP status using the verified method
                    if (source.getEntity() instanceof ServerPlayer player) {
                        return player.level().getServer().getPlayerList().isOp(player.nameAndId());
                    }
                    return true; // Console and command blocks always have permission
                })
                .then(Commands.literal("reload")
                        .executes(ctx -> {
                            MWL.CONFIG = ru.hantu.config.MwlConfig.load();
                            ctx.getSource().sendSuccess(() -> Component.literal("§a[§2MWL§a] §fConfig successfully reloaded!"), false);
                            return 1;
                        })
                )
                .then(Commands.literal("whitelist")
                        .then(Commands.literal("add")
                                .then(Commands.argument("mod_id", StringArgumentType.string())
                                        .executes(ctx -> {
                                            String modId = StringArgumentType.getString(ctx, "mod_id").toLowerCase();
                                            if (!MWL.CONFIG.whitelist.contains(modId)) {
                                                MWL.CONFIG.whitelist.add(modId);
                                                MWL.CONFIG.save();
                                                ctx.getSource().sendSuccess(() -> Component.literal("§a[§2MWL§a] §fMod §e" + modId + " §fadded to whitelist."), false);
                                            } else {
                                                ctx.getSource().sendFailure(Component.literal("§c[§4MWL§c] §fThis mod is already in the whitelist."));
                                            }
                                            return 1;
                                        })
                                )
                        )
                        .then(Commands.literal("remove")
                                .then(Commands.argument("mod_id", StringArgumentType.string())
                                        // AUTO-COMPLETION FOR REMOVE
                                        .suggests((ctx, builder) -> suggestFromList(MWL.CONFIG.whitelist, builder))
                                        .executes(ctx -> {
                                            String modId = StringArgumentType.getString(ctx, "mod_id").toLowerCase();
                                            if (MWL.CONFIG.whitelist.remove(modId)) {
                                                MWL.CONFIG.save();
                                                ctx.getSource().sendSuccess(() -> Component.literal("§a[§2MWL§a] §fMod §e" + modId + " §fremoved from whitelist."), false);
                                            } else {
                                                ctx.getSource().sendFailure(Component.literal("§c[§4MWL§c] §fThis mod is not in the whitelist."));
                                            }
                                            return 1;
                                        })
                                )
                        )
                )
                .then(Commands.literal("blacklist")
                        .then(Commands.literal("add")
                                .then(Commands.argument("mod_id", StringArgumentType.string())
                                        .executes(ctx -> {
                                            String modId = StringArgumentType.getString(ctx, "mod_id").toLowerCase();
                                            if (!MWL.CONFIG.blacklist.contains(modId)) {
                                                MWL.CONFIG.blacklist.add(modId);
                                                MWL.CONFIG.save();
                                                ctx.getSource().sendSuccess(() -> Component.literal("§a[§2MWL§a] §fMod §e" + modId + " §fadded to blacklist."), false);
                                            } else {
                                                ctx.getSource().sendFailure(Component.literal("§c[§4MWL§c] §fThis mod is already in the blacklist."));
                                            }
                                            return 1;
                                        })
                                )
                        )
                        .then(Commands.literal("remove")
                                .then(Commands.argument("mod_id", StringArgumentType.string())
                                        // AUTO-COMPLETION FOR REMOVE
                                        .suggests((ctx, builder) -> suggestFromList(MWL.CONFIG.blacklist, builder))
                                        .executes(ctx -> {
                                            String modId = StringArgumentType.getString(ctx, "mod_id").toLowerCase();
                                            if (MWL.CONFIG.blacklist.remove(modId)) {
                                                MWL.CONFIG.save();
                                                ctx.getSource().sendSuccess(() -> Component.literal("§a[§2MWL§a] §fMod §e" + modId + " §fremoved from blacklist."), false);
                                            } else {
                                                ctx.getSource().sendFailure(Component.literal("§c[§4MWL§c] §fThis mod is not in the blacklist."));
                                            }
                                            return 1;
                                        })
                                )
                        )
                )
        );
    }

    // Helper method to provide suggestions from a list
    private static CompletableFuture<Suggestions> suggestFromList(java.util.List<String> list, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(list, builder);
    }
}