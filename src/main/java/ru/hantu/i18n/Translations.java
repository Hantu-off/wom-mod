package ru.hantu.i18n;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Built-in translations for WoM messages.
 * Supports 12 languages with the ability to override any string via config.
 */
public class Translations {

    // Message keys
    public static final String KICK_TITLE = "kick_title";
    public static final String KICK_LINE1 = "kick_line1";
    public static final String KICK_LINE2 = "kick_line2";
    public static final String KICK_LINE3 = "kick_line3";
    public static final String WARN_PREFIX = "warn_prefix";
    public static final String ALERT_PREFIX = "alert_prefix";
    public static final String CMD_RELOAD_SUCCESS = "cmd_reload_success";
    public static final String CMD_WHITELIST_ADD_SUCCESS = "cmd_whitelist_add_success";
    public static final String CMD_WHITELIST_ADD_ALREADY = "cmd_whitelist_add_already";
    public static final String CMD_WHITELIST_REMOVE_SUCCESS = "cmd_whitelist_remove_success";
    public static final String CMD_WHITELIST_REMOVE_NOTFOUND = "cmd_whitelist_remove_notfound";
    public static final String CMD_BLACKLIST_ADD_SUCCESS = "cmd_blacklist_add_success";
    public static final String CMD_BLACKLIST_ADD_ALREADY = "cmd_blacklist_add_already";
    public static final String CMD_BLACKLIST_REMOVE_SUCCESS = "cmd_blacklist_remove_success";
    public static final String CMD_BLACKLIST_REMOVE_NOTFOUND = "cmd_blacklist_remove_notfound";
    public static final String VIOLATION_NOT_IN_WHITELIST = "violation_not_in_whitelist";
    public static final String VIOLATION_METADATA_MISMATCH = "violation_metadata_mismatch";
    public static final String VIOLATION_IN_BLACKLIST = "violation_in_blacklist";
    public static final String VIOLATION_HONEYPOT = "violation_honeypot";
    public static final String VIOLATION_VERSION_MISMATCH = "violation_version_mismatch";
    public static final String VIOLATION_TOO_FEW_MODS = "violation_too_few_mods";
    public static final String VIOLATION_WOM_MISSING = "violation_wom_missing";
    public static final String VIOLATION_TIMEOUT = "violation_timeout";
    public static final String VIOLATION_DUPLICATE = "violation_duplicate";

    private static final Map<String, Map<String, String>> TRANSLATIONS = new HashMap<>();

    static {
        // English
        Map<String, String> en = new HashMap<>();
        en.put(KICK_TITLE, "§c§l⚠ ACCESS DENIED ⚠");
        en.put(KICK_LINE1, "§7The §cWoM §7mod has blocked your connection.");
        en.put(KICK_LINE2, "§7Reason: §f%s");
        en.put(KICK_LINE3, "§7If you believe this is a mistake, please contact the administration.");
        en.put(WARN_PREFIX, "§e§l[⚠ WoM] §fWarning: §c%s");
        en.put(ALERT_PREFIX, "§c§l[WoM Alert] §fPlayer §e%s §f: §c%s");
        en.put(CMD_RELOAD_SUCCESS, "§a[§2WoM§a] §fConfig successfully reloaded!");
        en.put(CMD_WHITELIST_ADD_SUCCESS, "§a[§2WoM§a] §fMod §e%s §fadded to whitelist.");
        en.put(CMD_WHITELIST_ADD_ALREADY, "§c[§4WoM§c] §fThis mod is already in the whitelist.");
        en.put(CMD_WHITELIST_REMOVE_SUCCESS, "§a[§2WoM§a] §fMod §e%s §fremoved from whitelist.");
        en.put(CMD_WHITELIST_REMOVE_NOTFOUND, "§c[§4WoM§c] §fThis mod is not in the whitelist.");
        en.put(CMD_BLACKLIST_ADD_SUCCESS, "§a[§2WoM§a] §fMod §e%s §fadded to blacklist.");
        en.put(CMD_BLACKLIST_ADD_ALREADY, "§c[§4WoM§c] §fThis mod is already in the blacklist.");
        en.put(CMD_BLACKLIST_REMOVE_SUCCESS, "§a[§2WoM§a] §fMod §e%s §fremoved from blacklist.");
        en.put(CMD_BLACKLIST_REMOVE_NOTFOUND, "§c[§4WoM§c] §fThis mod is not in the blacklist.");
        en.put(VIOLATION_NOT_IN_WHITELIST, "not in whitelist");
        en.put(VIOLATION_METADATA_MISMATCH, "metadata mismatch");
        en.put(VIOLATION_IN_BLACKLIST, "in blacklist");
        en.put(VIOLATION_HONEYPOT, "§cDetected non-existent mod: %s (Anti-Cheat)");
        en.put(VIOLATION_VERSION_MISMATCH, "§cWoM version mismatch: server=%s, client=%s");
        en.put(VIOLATION_TOO_FEW_MODS, "§cSuspiciously low mod count: %s (Anti-Cheat)");
        en.put(VIOLATION_WOM_MISSING, "§cWoM mod not found in the list");
        en.put(VIOLATION_TIMEOUT, "§cWoM mod did not respond in time");
        en.put(VIOLATION_DUPLICATE, "§cDuplicate mod list submission (Anti-Cheat)");
        TRANSLATIONS.put("en_us", en);

        // Russian
        Map<String, String> ru = new HashMap<>();
        ru.put(KICK_TITLE, "§c§l⚠ ДОСТУП ЗАПРЕЩЁН ⚠");
        ru.put(KICK_LINE1, "§7Мод §cWoM §7заблокировал ваше подключение.");
        ru.put(KICK_LINE2, "§7Причина: §f%s");
        ru.put(KICK_LINE3, "§7Если вы считаете, что это ошибка, обратитесь к администрации.");
        ru.put(WARN_PREFIX, "§e§l[⚠ WoM] §fВнимание: §c%s");
        ru.put(ALERT_PREFIX, "§c§l[WoM Alert] §fИгрок §e%s §f: §c%s");
        ru.put(CMD_RELOAD_SUCCESS, "§a[§2WoM§a] §fКонфиг успешно перезагружен!");
        ru.put(CMD_WHITELIST_ADD_SUCCESS, "§a[§2WoM§a] §fМод §e%s §fдобавлен в белый список.");
        ru.put(CMD_WHITELIST_ADD_ALREADY, "§c[§4WoM§c] §fЭтот мод уже есть в белом списке.");
        ru.put(CMD_WHITELIST_REMOVE_SUCCESS, "§a[§2WoM§a] §fМод §e%s §fудалён из белого списка.");
        ru.put(CMD_WHITELIST_REMOVE_NOTFOUND, "§c[§4WoM§c] §fЭтого мода нет в белом списке.");
        ru.put(CMD_BLACKLIST_ADD_SUCCESS, "§a[§2WoM§a] §fМод §e%s §fдобавлен в чёрный список.");
        ru.put(CMD_BLACKLIST_ADD_ALREADY, "§c[§4WoM§c] §fЭтот мод уже есть в чёрном списке.");
        ru.put(CMD_BLACKLIST_REMOVE_SUCCESS, "§a[§2WoM§a] §fМод §e%s §fудалён из чёрного списка.");
        ru.put(CMD_BLACKLIST_REMOVE_NOTFOUND, "§c[§4WoM§c] §fЭтого мода нет в чёрном списке.");
        ru.put(VIOLATION_NOT_IN_WHITELIST, "не в белом списке");
        ru.put(VIOLATION_METADATA_MISMATCH, "несоответствие метаданных");
        ru.put(VIOLATION_IN_BLACKLIST, "в чёрном списке");
        ru.put(VIOLATION_HONEYPOT, "§cОбнаружен несуществующий мод: %s (Анти-Чит)");
        ru.put(VIOLATION_VERSION_MISMATCH, "§cНесоответствие версии WoM: сервер=%s, клиент=%s");
        ru.put(VIOLATION_TOO_FEW_MODS, "§cПодозрительно малое количество модов: %s (Анти-Чит)");
        ru.put(VIOLATION_WOM_MISSING, "§cМод WoM не обнаружен в списке");
        ru.put(VIOLATION_TIMEOUT, "§cМод WoM не ответил вовремя");
        ru.put(VIOLATION_DUPLICATE, "§cПовторная отправка списка модов (Анти-Чит)");
        TRANSLATIONS.put("ru_ru", ru);

        // German
        Map<String, String> de = new HashMap<>();
        de.put(KICK_TITLE, "§c§l⚠ ZUGANG VERWEIGERT ⚠");
        de.put(KICK_LINE1, "§7Der §cWoM §7Mod hat deine Verbindung blockiert.");
        de.put(KICK_LINE2, "§7Grund: §f%s");
        de.put(KICK_LINE3, "§7Wenn du glaubst, dass dies ein Fehler ist, wende dich an die Administration.");
        de.put(WARN_PREFIX, "§e§l[⚠ WoM] §fWarnung: §c%s");
        de.put(ALERT_PREFIX, "§c§l[WoM Alarm] §fSpieler §e%s §f: §c%s");
        de.put(CMD_RELOAD_SUCCESS, "§a[§2WoM§a] §fKonfiguration erfolgreich neu geladen!");
        de.put(CMD_WHITELIST_ADD_SUCCESS, "§a[§2WoM§a] §fMod §e%s §fzur Whitelist hinzugefügt.");
        de.put(CMD_WHITELIST_ADD_ALREADY, "§c[§4WoM§c] §fDieser Mod ist bereits in der Whitelist.");
        de.put(CMD_WHITELIST_REMOVE_SUCCESS, "§a[§2WoM§a] §fMod §e%s §faus der Whitelist entfernt.");
        de.put(CMD_WHITELIST_REMOVE_NOTFOUND, "§c[§4WoM§c] §fDieser Mod ist nicht in der Whitelist.");
        de.put(CMD_BLACKLIST_ADD_SUCCESS, "§a[§2WoM§a] §fMod §e%s §fzur Blacklist hinzugefügt.");
        de.put(CMD_BLACKLIST_ADD_ALREADY, "§c[§4WoM§c] §fDieser Mod ist bereits in der Blacklist.");
        de.put(CMD_BLACKLIST_REMOVE_SUCCESS, "§a[§2WoM§a] §fMod §e%s §faus der Blacklist entfernt.");
        de.put(CMD_BLACKLIST_REMOVE_NOTFOUND, "§c[§4WoM§c] §fDieser Mod ist nicht in der Blacklist.");
        de.put(VIOLATION_NOT_IN_WHITELIST, "nicht in der Whitelist");
        de.put(VIOLATION_METADATA_MISMATCH, "Metadaten stimmen nicht überein");
        de.put(VIOLATION_IN_BLACKLIST, "in der Blacklist");
        de.put(VIOLATION_HONEYPOT, "§cNicht existenter Mod erkannt: %s (Anti-Cheat)");
        de.put(VIOLATION_VERSION_MISMATCH, "§cWoM Versionskonflikt: Server=%s, Client=%s");
        de.put(VIOLATION_TOO_FEW_MODS, "§cVerdächtig niedrige Mod-Anzahl: %s (Anti-Cheat)");
        de.put(VIOLATION_WOM_MISSING, "§cWoM Mod nicht in der Liste gefunden");
        de.put(VIOLATION_TIMEOUT, "§cWoM Mod hat nicht rechtzeitig geantwortet");
        de.put(VIOLATION_DUPLICATE, "§cDoppelte Mod-Liste Übermittlung (Anti-Cheat)");
        TRANSLATIONS.put("de_de", de);

        // Spanish
        Map<String, String> es = new HashMap<>();
        es.put(KICK_TITLE, "§c§l⚠ ACCESO DENEGADO ⚠");
        es.put(KICK_LINE1, "§7El mod §cWoM §7ha bloqueado tu conexión.");
        es.put(KICK_LINE2, "§7Razón: §f%s");
        es.put(KICK_LINE3, "§7Si crees que esto es un error, contacta a la administración.");
        es.put(WARN_PREFIX, "§e§l[⚠ WoM] §fAdvertencia: §c%s");
        es.put(ALERT_PREFIX, "§c§l[WoM Alerta] §fJugador §e%s §f: §c%s");
        es.put(CMD_RELOAD_SUCCESS, "§a[§2WoM§a] §f¡Configuración recargada exitosamente!");
        es.put(CMD_WHITELIST_ADD_SUCCESS, "§a[§2WoM§a] §fMod §e%s §fagregado a la lista blanca.");
        es.put(CMD_WHITELIST_ADD_ALREADY, "§c[§4WoM§c] §fEste mod ya está en la lista blanca.");
        es.put(CMD_WHITELIST_REMOVE_SUCCESS, "§a[§2WoM§a] §fMod §e%s §feliminado de la lista blanca.");
        es.put(CMD_WHITELIST_REMOVE_NOTFOUND, "§c[§4WoM§c] §fEste mod no está en la lista blanca.");
        es.put(CMD_BLACKLIST_ADD_SUCCESS, "§a[§2WoM§a] §fMod §e%s §fagregado a la lista negra.");
        es.put(CMD_BLACKLIST_ADD_ALREADY, "§c[§4WoM§c] §fEste mod ya está en la lista negra.");
        es.put(CMD_BLACKLIST_REMOVE_SUCCESS, "§a[§2WoM§a] §fMod §e%s §feliminado de la lista negra.");
        es.put(CMD_BLACKLIST_REMOVE_NOTFOUND, "§c[§4WoM§c] §fEste mod no está en la lista negra.");
        es.put(VIOLATION_NOT_IN_WHITELIST, "no está en la lista blanca");
        es.put(VIOLATION_METADATA_MISMATCH, "discordancia de metadatos");
        es.put(VIOLATION_IN_BLACKLIST, "en la lista negra");
        es.put(VIOLATION_HONEYPOT, "§cMod inexistente detectado: %s (Anti-Cheat)");
        es.put(VIOLATION_VERSION_MISMATCH, "§cDiscordancia de versión WoM: servidor=%s, cliente=%s");
        es.put(VIOLATION_TOO_FEW_MODS, "§cCantidad de mods sospechosamente baja: %s (Anti-Cheat)");
        es.put(VIOLATION_WOM_MISSING, "§cMod WoM no encontrado en la lista");
        es.put(VIOLATION_TIMEOUT, "§cEl mod WoM no respondió a tiempo");
        es.put(VIOLATION_DUPLICATE, "§cEnvío duplicado de lista de mods (Anti-Cheat)");
        TRANSLATIONS.put("es_es", es);

        // French
        Map<String, String> fr = new HashMap<>();
        fr.put(KICK_TITLE, "§c§l⚠ ACCÈS REFUSÉ ⚠");
        fr.put(KICK_LINE1, "§7Le mod §cWoM §7a bloqué votre connexion.");
        fr.put(KICK_LINE2, "§7Raison: §f%s");
        fr.put(KICK_LINE3, "§7Si vous pensez que c'est une erreur, contactez l'administration.");
        fr.put(WARN_PREFIX, "§e§l[⚠ WoM] §fAvertissement: §c%s");
        fr.put(ALERT_PREFIX, "§c§l[WoM Alerte] §fJoueur §e%s §f: §c%s");
        fr.put(CMD_RELOAD_SUCCESS, "§a[§2WoM§a] §fConfiguration rechargée avec succès!");
        fr.put(CMD_WHITELIST_ADD_SUCCESS, "§a[§2WoM§a] §fMod §e%s §fajouté à la liste blanche.");
        fr.put(CMD_WHITELIST_ADD_ALREADY, "§c[§4WoM§c] §fCe mod est déjà dans la liste blanche.");
        fr.put(CMD_WHITELIST_REMOVE_SUCCESS, "§a[§2WoM§a] §fMod §e%s §fretiré de la liste blanche.");
        fr.put(CMD_WHITELIST_REMOVE_NOTFOUND, "§c[§4WoM§c] §fCe mod n'est pas dans la liste blanche.");
        fr.put(CMD_BLACKLIST_ADD_SUCCESS, "§a[§2WoM§a] §fMod §e%s §fajouté à la liste noire.");
        fr.put(CMD_BLACKLIST_ADD_ALREADY, "§c[§4WoM§c] §fCe mod est déjà dans la liste noire.");
        fr.put(CMD_BLACKLIST_REMOVE_SUCCESS, "§a[§2WoM§a] §fMod §e%s §fretiré de la liste noire.");
        fr.put(CMD_BLACKLIST_REMOVE_NOTFOUND, "§c[§4WoM§c] §fCe mod n'est pas dans la liste noire.");
        fr.put(VIOLATION_NOT_IN_WHITELIST, "pas dans la liste blanche");
        fr.put(VIOLATION_METADATA_MISMATCH, "discordance de métadonnées");
        fr.put(VIOLATION_IN_BLACKLIST, "dans la liste noire");
        fr.put(VIOLATION_HONEYPOT, "§cMod inexistant détecté: %s (Anti-Cheat)");
        fr.put(VIOLATION_VERSION_MISMATCH, "§cDiscordance de version WoM: serveur=%s, client=%s");
        fr.put(VIOLATION_TOO_FEW_MODS, "§cNombre de mods suspectement bas: %s (Anti-Cheat)");
        fr.put(VIOLATION_WOM_MISSING, "§cMod WoM non trouvé dans la liste");
        fr.put(VIOLATION_TIMEOUT, "§cLe mod WoM n'a pas répondu à temps");
        fr.put(VIOLATION_DUPLICATE, "§cSoumission dupliquée de la liste de mods (Anti-Cheat)");
        TRANSLATIONS.put("fr_fr", fr);

        // Portuguese
        Map<String, String> pt = new HashMap<>();
        pt.put(KICK_TITLE, "§c§l⚠ ACESSO NEGADO ⚠");
        pt.put(KICK_LINE1, "§7O mod §cWoM §7bloqueou sua conexão.");
        pt.put(KICK_LINE2, "§7Motivo: §f%s");
        pt.put(KICK_LINE3, "§7Se você acredita que isso é um erro, entre em contato com a administração.");
        pt.put(WARN_PREFIX, "§e§l[⚠ WoM] §fAviso: §c%s");
        pt.put(ALERT_PREFIX, "§c§l[WoM Alerta] §fJogador §e%s §f: §c%s");
        pt.put(CMD_RELOAD_SUCCESS, "§a[§2WoM§a] §fConfiguração recarregada com sucesso!");
        pt.put(CMD_WHITELIST_ADD_SUCCESS, "§a[§2WoM§a] §fMod §e%s §fadicionado à lista branca.");
        pt.put(CMD_WHITELIST_ADD_ALREADY, "§c[§4WoM§c] §fEste mod já está na lista branca.");
        pt.put(CMD_WHITELIST_REMOVE_SUCCESS, "§a[§2WoM§a] §fMod §e%s §fremovido da lista branca.");
        pt.put(CMD_WHITELIST_REMOVE_NOTFOUND, "§c[§4WoM§c] §fEste mod não está na lista branca.");
        pt.put(CMD_BLACKLIST_ADD_SUCCESS, "§a[§2WoM§a] §fMod §e%s §fadicionado à lista negra.");
        pt.put(CMD_BLACKLIST_ADD_ALREADY, "§c[§4WoM§c] §fEste mod já está na lista negra.");
        pt.put(CMD_BLACKLIST_REMOVE_SUCCESS, "§a[§2WoM§a] §fMod §e%s §fremovido da lista negra.");
        pt.put(CMD_BLACKLIST_REMOVE_NOTFOUND, "§c[§4WoM§c] §fEste mod não está na lista negra.");
        pt.put(VIOLATION_NOT_IN_WHITELIST, "não está na lista branca");
        pt.put(VIOLATION_METADATA_MISMATCH, "incompatibilidade de metadados");
        pt.put(VIOLATION_IN_BLACKLIST, "na lista negra");
        pt.put(VIOLATION_HONEYPOT, "§cMod inexistente detectado: %s (Anti-Cheat)");
        pt.put(VIOLATION_VERSION_MISMATCH, "§cIncompatibilidade de versão WoM: servidor=%s, cliente=%s");
        pt.put(VIOLATION_TOO_FEW_MODS, "§cQuantidade de mods suspeitamente baixa: %s (Anti-Cheat)");
        pt.put(VIOLATION_WOM_MISSING, "§cMod WoM não encontrado na lista");
        pt.put(VIOLATION_TIMEOUT, "§cO mod WoM não respondeu a tempo");
        pt.put(VIOLATION_DUPLICATE, "§cEnvio duplicado da lista de mods (Anti-Cheat)");
        TRANSLATIONS.put("pt_br", pt);

        // Japanese
        Map<String, String> ja = new HashMap<>();
        ja.put(KICK_TITLE, "§c§l⚠ アクセス拒否 ⚠");
        ja.put(KICK_LINE1, "§7Mod §cWoM §7が接続をブロックしました。");
        ja.put(KICK_LINE2, "§7理由: §f%s");
        ja.put(KICK_LINE3, "§7これが間違いだと思う場合は、管理者に連絡してください。");
        ja.put(WARN_PREFIX, "§e§l[⚠ WoM] §f警告: §c%s");
        ja.put(ALERT_PREFIX, "§c§l[WoM アラート] §fプレイヤー §e%s §f: §c%s");
        ja.put(CMD_RELOAD_SUCCESS, "§a[§2WoM§a] §f設定が正常にリロードされました！");
        ja.put(CMD_WHITELIST_ADD_SUCCESS, "§a[§2WoM§a] §fMod §e%s §fがホワイトリストに追加されました。");
        ja.put(CMD_WHITELIST_ADD_ALREADY, "§c[§4WoM§c] §fこのModはすでにホワイトリストにあります。");
        ja.put(CMD_WHITELIST_REMOVE_SUCCESS, "§a[§2WoM§a] §fMod §e%s §fがホワイトリストから削除されました。");
        ja.put(CMD_WHITELIST_REMOVE_NOTFOUND, "§c[§4WoM§c] §fこのModはホワイトリストにありません。");
        ja.put(CMD_BLACKLIST_ADD_SUCCESS, "§a[§2WoM§a] §fMod §e%s §fがブラックリストに追加されました。");
        ja.put(CMD_BLACKLIST_ADD_ALREADY, "§c[§4WoM§c] §fこのModはすでにブラックリストにあります。");
        ja.put(CMD_BLACKLIST_REMOVE_SUCCESS, "§a[§2WoM§a] §fMod §e%s §fがブラックリストから削除されました。");
        ja.put(CMD_BLACKLIST_REMOVE_NOTFOUND, "§c[§4WoM§c] §fこのModはブラックリストにありません。");
        ja.put(VIOLATION_NOT_IN_WHITELIST, "ホワイトリストにない");
        ja.put(VIOLATION_METADATA_MISMATCH, "メタデータの不一致");
        ja.put(VIOLATION_IN_BLACKLIST, "ブラックリストにある");
        ja.put(VIOLATION_HONEYPOT, "§c存在しないModを検出: %s (アンチチート)");
        ja.put(VIOLATION_VERSION_MISMATCH, "§cWoMバージョンの不一致: サーバー=%s, クライアント=%s");
        ja.put(VIOLATION_TOO_FEW_MODS, "§c疑わしいほどMod数が少ない: %s (アンチチート)");
        ja.put(VIOLATION_WOM_MISSING, "§cリストにWoM Modが見つかりません");
        ja.put(VIOLATION_TIMEOUT, "§cWoM Modが時間内に応答しませんでした");
        ja.put(VIOLATION_DUPLICATE, "§cModリストの重複送信 (アンチチート)");
        TRANSLATIONS.put("ja_jp", ja);

        // Chinese (Simplified)
        Map<String, String> zh = new HashMap<>();
        zh.put(KICK_TITLE, "§c§l⚠ 访问被拒绝 ⚠");
        zh.put(KICK_LINE1, "§7Mod §cWoM §7已阻止您的连接。");
        zh.put(KICK_LINE2, "§7原因: §f%s");
        zh.put(KICK_LINE3, "§7如果您认为这是错误，请联系管理员。");
        zh.put(WARN_PREFIX, "§e§l[⚠ WoM] §f警告: §c%s");
        zh.put(ALERT_PREFIX, "§c§l[WoM 警报] §f玩家 §e%s §f: §c%s");
        zh.put(CMD_RELOAD_SUCCESS, "§a[§2WoM§a] §f配置已成功重新加载！");
        zh.put(CMD_WHITELIST_ADD_SUCCESS, "§a[§2WoM§a] §fMod §e%s §f已添加到白名单。");
        zh.put(CMD_WHITELIST_ADD_ALREADY, "§c[§4WoM§c] §f此Mod已在白名单中。");
        zh.put(CMD_WHITELIST_REMOVE_SUCCESS, "§a[§2WoM§a] §fMod §e%s §f已从白名单中移除。");
        zh.put(CMD_WHITELIST_REMOVE_NOTFOUND, "§c[§4WoM§c] §f此Mod不在白名单中。");
        zh.put(CMD_BLACKLIST_ADD_SUCCESS, "§a[§2WoM§a] §fMod §e%s §f已添加到黑名单。");
        zh.put(CMD_BLACKLIST_ADD_ALREADY, "§c[§4WoM§c] §f此Mod已在黑名单中。");
        zh.put(CMD_BLACKLIST_REMOVE_SUCCESS, "§a[§2WoM§a] §fMod §e%s §f已从黑名单中移除。");
        zh.put(CMD_BLACKLIST_REMOVE_NOTFOUND, "§c[§4WoM§c] §f此Mod不在黑名单中。");
        zh.put(VIOLATION_NOT_IN_WHITELIST, "不在白名单中");
        zh.put(VIOLATION_METADATA_MISMATCH, "元数据不匹配");
        zh.put(VIOLATION_IN_BLACKLIST, "在黑名单中");
        zh.put(VIOLATION_HONEYPOT, "§c检测到不存在的Mod: %s (反作弊)");
        zh.put(VIOLATION_VERSION_MISMATCH, "§cWoM版本不匹配: 服务器=%s, 客户端=%s");
        zh.put(VIOLATION_TOO_FEW_MODS, "§c可疑的低Mod数量: %s (反作弊)");
        zh.put(VIOLATION_WOM_MISSING, "§c列表中未找到WoM Mod");
        zh.put(VIOLATION_TIMEOUT, "§cWoM Mod未及时响应");
        zh.put(VIOLATION_DUPLICATE, "§c重复提交Mod列表 (反作弊)");
        TRANSLATIONS.put("zh_cn", zh);

        // Korean
        Map<String, String> ko = new HashMap<>();
        ko.put(KICK_TITLE, "§c§l⚠ 접근 거부 ⚠");
        ko.put(KICK_LINE1, "§7Mod §cWoM§7이(가) 연결을 차단했습니다.");
        ko.put(KICK_LINE2, "§7사유: §f%s");
        ko.put(KICK_LINE3, "§7오류라고 생각되면 관리자에게 문의하세요.");
        ko.put(WARN_PREFIX, "§e§l[⚠ WoM] §f경고: §c%s");
        ko.put(ALERT_PREFIX, "§c§l[WoM 경고] §f플레이어 §e%s §f: §c%s");
        ko.put(CMD_RELOAD_SUCCESS, "§a[§2WoM§a] §f구성이 성공적으로 다시 로드되었습니다!");
        ko.put(CMD_WHITELIST_ADD_SUCCESS, "§a[§2WoM§a] §fMod §e%s §f이(가) 화이트리스트에 추가되었습니다.");
        ko.put(CMD_WHITELIST_ADD_ALREADY, "§c[§4WoM§c] §f이 Mod는 이미 화이트리스트에 있습니다.");
        ko.put(CMD_WHITELIST_REMOVE_SUCCESS, "§a[§2WoM§a] §fMod §e%s §f이(가) 화이트리스트에서 제거되었습니다.");
        ko.put(CMD_WHITELIST_REMOVE_NOTFOUND, "§c[§4WoM§c] §f이 Mod는 화이트리스트에 없습니다.");
        ko.put(CMD_BLACKLIST_ADD_SUCCESS, "§a[§2WoM§a] §fMod §e%s §f이(가) 블랙리스트에 추가되었습니다.");
        ko.put(CMD_BLACKLIST_ADD_ALREADY, "§c[§4WoM§c] §f이 Mod는 이미 블랙리스트에 있습니다.");
        ko.put(CMD_BLACKLIST_REMOVE_SUCCESS, "§a[§2WoM§a] §fMod §e%s §f이(가) 블랙리스트에서 제거되었습니다.");
        ko.put(CMD_BLACKLIST_REMOVE_NOTFOUND, "§c[§4WoM§c] §f이 Mod는 블랙리스트에 없습니다.");
        ko.put(VIOLATION_NOT_IN_WHITELIST, "화이트리스트에 없음");
        ko.put(VIOLATION_METADATA_MISMATCH, "메타데이터 불일치");
        ko.put(VIOLATION_IN_BLACKLIST, "블랙리스트에 있음");
        ko.put(VIOLATION_HONEYPOT, "§c존재하지 않는 Mod 감지: %s (안티치트)");
        ko.put(VIOLATION_VERSION_MISMATCH, "§cWoM 버전 불일치: 서버=%s, 클라이언트=%s");
        ko.put(VIOLATION_TOO_FEW_MODS, "§c의심스럽게 낮은 Mod 수: %s (안티치트)");
        ko.put(VIOLATION_WOM_MISSING, "§c목록에서 WoM Mod를 찾을 수 없습니다");
        ko.put(VIOLATION_TIMEOUT, "§cWoM Mod가 시간 내에 응답하지 않았습니다");
        ko.put(VIOLATION_DUPLICATE, "§c중복 Mod 목록 제출 (안티치트)");
        TRANSLATIONS.put("ko_kr", ko);

        // Turkish
        Map<String, String> tr = new HashMap<>();
        tr.put(KICK_TITLE, "§c§l⚠ ERİŞİM REDDEDİLDİ ⚠");
        tr.put(KICK_LINE1, "§cWoM §7modu bağlantınızı engelledi.");
        tr.put(KICK_LINE2, "§7Sebep: §f%s");
        tr.put(KICK_LINE3, "§7Bunun bir hata olduğunu düşünüyorsanız, lütfen yönetim ile iletişime geçin.");
        tr.put(WARN_PREFIX, "§e§l[⚠ WoM] §fUyarı: §c%s");
        tr.put(ALERT_PREFIX, "§c§l[WoM Uyarısı] §fOyuncu §e%s §f: §c%s");
        tr.put(CMD_RELOAD_SUCCESS, "§a[§2WoM§a] §fYapılandırma başarıyla yeniden yüklendi!");
        tr.put(CMD_WHITELIST_ADD_SUCCESS, "§a[§2WoM§a] §fMod §e%s §fbeyaz listeye eklendi.");
        tr.put(CMD_WHITELIST_ADD_ALREADY, "§c[§4WoM§c] §fBu mod zaten beyaz listede.");
        tr.put(CMD_WHITELIST_REMOVE_SUCCESS, "§a[§2WoM§a] §fMod §e%s §fbeyaz listeden kaldırıldı.");
        tr.put(CMD_WHITELIST_REMOVE_NOTFOUND, "§c[§4WoM§c] §fBu mod beyaz listede değil.");
        tr.put(CMD_BLACKLIST_ADD_SUCCESS, "§a[§2WoM§a] §fMod §e%s §fkara listeye eklendi.");
        tr.put(CMD_BLACKLIST_ADD_ALREADY, "§c[§4WoM§c] §fBu mod zaten kara listede.");
        tr.put(CMD_BLACKLIST_REMOVE_SUCCESS, "§a[§2WoM§a] §fMod §e%s §fkara listeden kaldırıldı.");
        tr.put(CMD_BLACKLIST_REMOVE_NOTFOUND, "§c[§4WoM§c] §fBu mod kara listede değil.");
        tr.put(VIOLATION_NOT_IN_WHITELIST, "beyaz listede değil");
        tr.put(VIOLATION_METADATA_MISMATCH, "meta veri uyuşmazlığı");
        tr.put(VIOLATION_IN_BLACKLIST, "kara listede");
        tr.put(VIOLATION_HONEYPOT, "§cVar olmayan mod tespit edildi: %s (Anti-Hile)");
        tr.put(VIOLATION_VERSION_MISMATCH, "§cWoM sürüm uyuşmazlığı: sunucu=%s, istemci=%s");
        tr.put(VIOLATION_TOO_FEW_MODS, "§cŞüpheli derecede düşük mod sayısı: %s (Anti-Hile)");
        tr.put(VIOLATION_WOM_MISSING, "§cListede WoM modu bulunamadı");
        tr.put(VIOLATION_TIMEOUT, "§cWoM modu zamanında yanıt vermedi");
        tr.put(VIOLATION_DUPLICATE, "§cTekrarlanan mod listesi gönderimi (Anti-Hile)");
        TRANSLATIONS.put("tr_tr", tr);

        // Italian
        Map<String, String> it = new HashMap<>();
        it.put(KICK_TITLE, "§c§l⚠ ACCESSO NEGATO ⚠");
        it.put(KICK_LINE1, "§7La mod §cWoM §7ha bloccato la tua connessione.");
        it.put(KICK_LINE2, "§7Motivo: §f%s");
        it.put(KICK_LINE3, "§7Se ritieni che sia un errore, contatta l'amministrazione.");
        it.put(WARN_PREFIX, "§e§l[⚠ WoM] §fAvviso: §c%s");
        it.put(ALERT_PREFIX, "§c§l[WoM Avviso] §fGiocatore §e%s §f: §c%s");
        it.put(CMD_RELOAD_SUCCESS, "§a[§2WoM§a] §fConfigurazione ricaricata con successo!");
        it.put(CMD_WHITELIST_ADD_SUCCESS, "§a[§2WoM§a] §fMod §e%s §faggiunto alla whitelist.");
        it.put(CMD_WHITELIST_ADD_ALREADY, "§c[§4WoM§c] §fQuesta mod è già nella whitelist.");
        it.put(CMD_WHITELIST_REMOVE_SUCCESS, "§a[§2WoM§a] §fMod §e%s §frimosso dalla whitelist.");
        it.put(CMD_WHITELIST_REMOVE_NOTFOUND, "§c[§4WoM§c] §fQuesta mod non è nella whitelist.");
        it.put(CMD_BLACKLIST_ADD_SUCCESS, "§a[§2WoM§a] §fMod §e%s §faggiunto alla blacklist.");
        it.put(CMD_BLACKLIST_ADD_ALREADY, "§c[§4WoM§c] §fQuesta mod è già nella blacklist.");
        it.put(CMD_BLACKLIST_REMOVE_SUCCESS, "§a[§2WoM§a] §fMod §e%s §frimosso dalla blacklist.");
        it.put(CMD_BLACKLIST_REMOVE_NOTFOUND, "§c[§4WoM§c] §fQuesta mod non è nella blacklist.");
        it.put(VIOLATION_NOT_IN_WHITELIST, "non nella whitelist");
        it.put(VIOLATION_METADATA_MISMATCH, "mancata corrispondenza dei metadati");
        it.put(VIOLATION_IN_BLACKLIST, "nella blacklist");
        it.put(VIOLATION_HONEYPOT, "§cRilevata mod inesistente: %s (Anti-Cheat)");
        it.put(VIOLATION_VERSION_MISMATCH, "§cMancata corrispondenza versione WoM: server=%s, client=%s");
        it.put(VIOLATION_TOO_FEW_MODS, "§cNumero di mod sospettosamente basso: %s (Anti-Cheat)");
        it.put(VIOLATION_WOM_MISSING, "§cMod WoM non trovato nell'elenco");
        it.put(VIOLATION_TIMEOUT, "§cLa mod WoM non ha risposto in tempo");
        it.put(VIOLATION_DUPLICATE, "§cInvio duplicato dell'elenco delle mod (Anti-Cheat)");
        TRANSLATIONS.put("it_it", it);

        // Polish
        Map<String, String> pl = new HashMap<>();
        pl.put(KICK_TITLE, "§c§l⚠ DOSTĘP ODRZUCONY ⚠");
        pl.put(KICK_LINE1, "§7Mod §cWoM §7zablokował Twoje połączenie.");
        pl.put(KICK_LINE2, "§7Powód: §f%s");
        pl.put(KICK_LINE3, "§7Jeśli uważasz, że to pomyłka, skontaktuj się z administracją.");
        pl.put(WARN_PREFIX, "§e§l[⚠ WoM] §fOstrzeżenie: §c%s");
        pl.put(ALERT_PREFIX, "§c§l[WoM Alert] §fGracz §e%s §f: §c%s");
        pl.put(CMD_RELOAD_SUCCESS, "§a[§2WoM§a] §fKonfiguracja została pomyślnie przeładowana!");
        pl.put(CMD_WHITELIST_ADD_SUCCESS, "§a[§2WoM§a] §fMod §e%s §fdodany do białej listy.");
        pl.put(CMD_WHITELIST_ADD_ALREADY, "§c[§4WoM§c] §fTen mod jest już na białej liście.");
        pl.put(CMD_WHITELIST_REMOVE_SUCCESS, "§a[§2WoM§a] §fMod §e%s §fusunięty z białej listy.");
        pl.put(CMD_WHITELIST_REMOVE_NOTFOUND, "§c[§4WoM§c] §fTego moda nie ma na białej liście.");
        pl.put(CMD_BLACKLIST_ADD_SUCCESS, "§a[§2WoM§a] §fMod §e%s §fdodany do czarnej listy.");
        pl.put(CMD_BLACKLIST_ADD_ALREADY, "§c[§4WoM§c] §fTen mod jest już na czarnej liście.");
        pl.put(CMD_BLACKLIST_REMOVE_SUCCESS, "§a[§2WoM§a] §fMod §e%s §fusunięty z czarnej listy.");
        pl.put(CMD_BLACKLIST_REMOVE_NOTFOUND, "§c[§4WoM§c] §fTego moda nie ma na czarnej liście.");
        pl.put(VIOLATION_NOT_IN_WHITELIST, "nie na białej liście");
        pl.put(VIOLATION_METADATA_MISMATCH, "niezgodność metadanych");
        pl.put(VIOLATION_IN_BLACKLIST, "na czarnej liście");
        pl.put(VIOLATION_HONEYPOT, "§cWykryto nieistniejący mod: %s (Anti-Cheat)");
        pl.put(VIOLATION_VERSION_MISMATCH, "§cNiezgodność wersji WoM: serwer=%s, klient=%s");
        pl.put(VIOLATION_TOO_FEW_MODS, "§cPodejrzanie niska liczba modów: %s (Anti-Cheat)");
        pl.put(VIOLATION_WOM_MISSING, "§cNie znaleziono moda WoM na liście");
        pl.put(VIOLATION_TIMEOUT, "§cMod WoM nie odpowiedział na czas");
        pl.put(VIOLATION_DUPLICATE, "§cZduplikowane przesłanie listy modów (Anti-Cheat)");
        TRANSLATIONS.put("pl_pl", pl);
    }

    /**
     * Gets a translated message for the given language and key.
     * Falls back to English if the language or key is not found.
     */
    public static String get(String language, String key) {
        Map<String, String> langMap = TRANSLATIONS.get(language);
        if (langMap == null) {
            langMap = TRANSLATIONS.get("en_us");
        }
        String value = langMap.get(key);
        if (value == null) {
            value = TRANSLATIONS.get("en_us").get(key);
        }
        return value != null ? value : key;
    }

    /**
     * Gets a translated message with format arguments.
     */
    public static String format(String language, String key, Object... args) {
        return String.format(get(language, key), args);
    }

    /**
     * Returns all available language codes.
     */
    public static Set<String> getAvailableLanguages() {
        return TRANSLATIONS.keySet();
    }
}