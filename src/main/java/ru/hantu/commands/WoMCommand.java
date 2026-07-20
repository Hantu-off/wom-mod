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
import ru.hantu.WoM;
import ru.hantu.config.WoMConfig;

import java.util.concurrent.CompletableFuture;

public class WoMCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("wom")
                .requires(source -> {
                    if (source.getEntity() instanceof ServerPlayer player) {
                        return player.level().getServer().getPlayerList().isOp(player.nameAndId());
                    }
                    return true;
                })
                .then(Commands.literal("reload")
                        .executes(ctx -> {
                            WoM.CONFIG = WoMConfig.load();
                            ctx.getSource().sendSuccess(() -> Component.literal("§a[§2WoM§a] §fConfig successfully reloaded!"), false);
                            return 1;
                        })
                )
                .then(Commands.literal("whitelist")
                        .then(Commands.literal("add")
                                .then(Commands.argument("mod_id", StringArgumentType.string())
                                        .executes(ctx -> {
                                            String modId = StringArgumentType.getString(ctx, "mod_id").toLowerCase();
                                            WoMConfig.ModRule rule = new WoMConfig.ModRule(modId);
                                            WoM.CONFIG.whitelist.add(rule);
                                            WoM.CONFIG.save();
                                            ctx.getSource().sendSuccess(() -> Component.literal("§a[§2WoM§a] §fMod §e" + modId + " §fadded to whitelist."), false);
                                            return 1;
                                        })
                                        .then(Commands.argument("version", StringArgumentType.string())
                                                .executes(ctx -> {
                                                    String modId = StringArgumentType.getString(ctx, "mod_id").toLowerCase();
                                                    String version = StringArgumentType.getString(ctx, "version");
                                                    WoMConfig.ModRule rule = new WoMConfig.ModRule(modId);
                                                    rule.version = version;
                                                    WoM.CONFIG.whitelist.add(rule);
                                                    WoM.CONFIG.save();
                                                    ctx.getSource().sendSuccess(() -> Component.literal("§a[§2WoM§a] §fMod §e" + modId + " §fadded with version §e" + version), false);
                                                    return 1;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("remove")
                                .then(Commands.argument("mod_id", StringArgumentType.string())
                                        .suggests((ctx, builder) -> suggestFromList(WoM.CONFIG.whitelist.stream().map(r -> r.id).toList(), builder))
                                        .executes(ctx -> {
                                            String modId = StringArgumentType.getString(ctx, "mod_id").toLowerCase();
                                            boolean removed = WoM.CONFIG.whitelist.removeIf(rule -> rule.id.equals(modId));
                                            if (removed) {
                                                WoM.CONFIG.save();
                                                ctx.getSource().sendSuccess(() -> Component.literal("§a[§2WoM§a] §fMod §e" + modId + " §fremoved from whitelist."), false);
                                            } else {
                                                ctx.getSource().sendFailure(Component.literal("§c[§4WoM§c] §fThis mod is not in the whitelist."));
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
                                            WoMConfig.ModRule rule = new WoMConfig.ModRule(modId);
                                            WoM.CONFIG.blacklist.add(rule);
                                            WoM.CONFIG.save();
                                            ctx.getSource().sendSuccess(() -> Component.literal("§a[§2WoM§a] §fMod §e" + modId + " §fadded to blacklist."), false);
                                            return 1;
                                        })
                                        .then(Commands.argument("version", StringArgumentType.string())
                                                .executes(ctx -> {
                                                    String modId = StringArgumentType.getString(ctx, "mod_id").toLowerCase();
                                                    String version = StringArgumentType.getString(ctx, "version");
                                                    WoMConfig.ModRule rule = new WoMConfig.ModRule(modId);
                                                    rule.version = version;
                                                    WoM.CONFIG.blacklist.add(rule);
                                                    WoM.CONFIG.save();
                                                    ctx.getSource().sendSuccess(() -> Component.literal("§a[§2WoM§a] §fMod §e" + modId + " §fadded with version §e" + version), false);
                                                    return 1;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("remove")
                                .then(Commands.argument("mod_id", StringArgumentType.string())
                                        .suggests((ctx, builder) -> suggestFromList(WoM.CONFIG.blacklist.stream().map(r -> r.id).toList(), builder))
                                        .executes(ctx -> {
                                            String modId = StringArgumentType.getString(ctx, "mod_id").toLowerCase();
                                            boolean removed = WoM.CONFIG.blacklist.removeIf(rule -> rule.id.equals(modId));
                                            if (removed) {
                                                WoM.CONFIG.save();
                                                ctx.getSource().sendSuccess(() -> Component.literal("§a[§2WoM§a] §fMod §e" + modId + " §fremoved from blacklist."), false);
                                            } else {
                                                ctx.getSource().sendFailure(Component.literal("§c[§4WoM§c] §fThis mod is not in the blacklist."));
                                            }
                                            return 1;
                                        })
                                )
                        )
                )
        );
    }

    private static CompletableFuture<Suggestions> suggestFromList(java.util.List<String> list, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(list, builder);
    }
}