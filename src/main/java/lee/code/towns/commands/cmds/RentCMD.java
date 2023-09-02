package lee.code.towns.commands.cmds;

import lee.code.colors.ColorAPI;
import lee.code.economy.EcoAPI;
import lee.code.towns.Towns;
import lee.code.towns.commands.SubCommand;
import lee.code.towns.commands.SubSyntax;
import lee.code.towns.database.CacheManager;
import lee.code.towns.enums.ChunkRenderType;
import lee.code.towns.lang.Lang;
import lee.code.towns.managers.BorderParticleManager;
import lee.code.towns.utils.ChunkUtil;
import lee.code.towns.utils.CoreUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RentCMD extends SubCommand {
  private final Towns towns;

  public RentCMD(Towns towns) {
    this.towns = towns;
  }

  @Override
  public String getName() {
    return "rent";
  }

  @Override
  public String getDescription() {
    return "Chunk renting options.";
  }

  @Override
  public String getSyntax() {
    return "/t rent &f<options>";
  }

  @Override
  public String getPermission() {
    return "towns.command.rent";
  }

  @Override
  public boolean performAsync() {
    return true;
  }

  @Override
  public boolean performAsyncSynchronized() {
    return true;
  }

  @Override
  public void perform(Player player, String[] args) {
    if (args.length < 2) {
      player.sendMessage(Lang.USAGE.getComponent(new String[]{getSyntax()}));
      return;
    }
    final CacheManager cacheManager = towns.getCacheManager();
    final BorderParticleManager borderParticleManager = towns.getBorderParticleManager();
    final UUID uuid = player.getUniqueId();
    final String chunk = ChunkUtil.serializeChunkLocation(player.getLocation().getChunk());
    final String option = args[1].toLowerCase();

    if (!cacheManager.getCacheTowns().hasTownOrJoinedTown(uuid)) {
      player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NO_TOWN.getComponent(null)));
      return;
    }

    switch (option) {
      case "price" -> {
        if (args.length < 3) {
          player.sendMessage(Lang.USAGE.getComponent(new String[] {SubSyntax.COMMAND_RENT_PRICE.getString()}));
          return;
        }
        if (!cacheManager.getCacheChunks().isClaimed(chunk)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_RENT_NOT_CLAIMED.getComponent(null)));
          return;
        }
        if (!cacheManager.getCacheChunks().isChunkOwner(chunk, uuid)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_RENT_PRICE_NOT_OWNER.getComponent(null)));
          return;
        }
        final String priceString = args[2];
        if (!CoreUtil.isPositiveDoubleNumber(priceString)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_VALUE_INVALID.getComponent(new String[]{priceString})));
          return;
        }
        final double price = Double.parseDouble(priceString);
        final double max = 1000000;
        if (price > max) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_RENT_PRICE_MAX.getComponent(new String[]{Lang.VALUE_FORMAT.getString(new String[]{CoreUtil.parseValue(max)})})));
          return;
        }
        borderParticleManager.spawnParticleChunkBorder(player, player.getLocation().getChunk(), ChunkRenderType.INFO, true);
        cacheManager.getCacheRenters().setRentChunkPrice(uuid, chunk, price);
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_RENT_PRICE_SUCCESS.getComponent(new String[]{
          chunk,
          Lang.VALUE_FORMAT.getString(new String[]{CoreUtil.parseValue(price)})
        })));
      }

      case "remove" -> {
        if (!cacheManager.getCacheChunks().isClaimed(chunk)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_RENT_NOT_CLAIMED.getComponent(null)));
          return;
        }
        if (!cacheManager.getCacheChunks().isChunkOwner(chunk, uuid)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_RENT_REMOVE_NOT_OWNER.getComponent(null)));
          return;
        }
        if (!cacheManager.getCacheRenters().hasRentData(chunk)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_RENT_REMOVE_NOT_RENTABLE.getComponent(new String[]{chunk})));
          return;
        }
        if (cacheManager.getCacheRenters().isRented(chunk)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_RENT_REMOVE_BEING_RENTED.getComponent(new String[]{chunk, cacheManager.getCacheRenters().getRenterName(chunk)})));
          return;
        }
        borderParticleManager.spawnParticleChunkBorder(player, player.getLocation().getChunk(), ChunkRenderType.INFO, true);
        cacheManager.getCacheRenters().deleteRentableChunk(chunk);
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_RENT_REMOVE_SUCCESS.getComponent(new String[]{chunk})));
      }

      case "claim" -> {
        if (!cacheManager.getCacheChunks().isClaimed(chunk)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_RENT_NOT_CLAIMED.getComponent(null)));
          return;
        }
        final UUID owner = cacheManager.getCacheChunks().getChunkOwner(chunk);
        if (uuid.equals(owner)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_RENT_CLAIM_OWNER.getComponent(null)));
          return;
        }
        if (!cacheManager.getCacheTowns().getCitizenData().isCitizen(owner, uuid)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_RENT_CLAIM_NOT_CITIZEN.getComponent(new String[]{cacheManager.getChunkTownName(chunk)})));
          return;
        }
        if (!cacheManager.getCacheRenters().isRentable(chunk)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_RENT_CLAIM_NOT_RENTABLE.getComponent(new String[]{chunk})));
          return;
        }

        if (args.length > 2) {
          final String result = args[2].toLowerCase();
          switch (result) {
            case "confirm" -> {
              final double cost = cacheManager.getCacheRenters().getRentPrice(chunk);
              if (EcoAPI.getBalance(uuid) < cost) {
                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_RENT_INSUFFICIENT_FUNDS.getComponent(null)));
                return;
              }
              EcoAPI.removeBalance(uuid, cost);
              cacheManager.getCacheBank().getData().addTownBalance(owner, cost);
              cacheManager.getCacheRenters().setRenter(uuid, chunk);
              borderParticleManager.spawnParticleChunkBorder(player, player.getLocation().getChunk(), ChunkRenderType.CLAIM, false);
              player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_RENT_CLAIM_SUCCESS.getComponent(new String[]{chunk, Lang.VALUE_FORMAT.getString(new String[]{CoreUtil.parseValue(cost)})})));
            }
            case "deny" -> player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_RENT_CLAIM_DENY_SUCCESS.getComponent(new String[]{chunk})));
            default -> player.sendMessage(Lang.USAGE.getComponent(new String[]{SubSyntax.COMMAND_RENT_CLAIM.getString()}));
          }
        } else {
          CoreUtil.sendConfirmMessage(player, Lang.PREFIX.getComponent(null).append(Lang.COMMAND_RENT_CLAIM_WARNING.getComponent(new String[]{chunk,
              Lang.VALUE_FORMAT.getString(new String[]{CoreUtil.parseValue(cacheManager.getCacheRenters().getRentPrice(chunk))})})),
            "/towns rent claim",
            Lang.CONFIRM_RENT_CLAIM_HOVER.getComponent(null),
            Lang.DENY_RENT_CLAIM_HOVER.getComponent(null),
            true
          );
        }
      }

      case "unclaim" -> {
        if (cacheManager.getCacheTowns().hasTown(uuid)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_RENT_UNCLAIM_OWNER.getComponent(null)));
          return;
        }
        if (!cacheManager.getCacheRenters().isRented(chunk)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_RENT_UNCLAIM_NOT_RENTED.getComponent(new String[]{chunk})));
          return;
        }
        if (!cacheManager.getCacheRenters().getRenter(chunk).equals(uuid)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_RENT_UNCLAIM_NOT_RENTED_BY_PLAYER.getComponent(new String[]{chunk})));
          return;
        }
        if (args.length > 2) {
          final String result = args[2].toLowerCase();
          switch (result) {
            case "confirm" -> {
              cacheManager.getCacheRenters().removeRenter(chunk);
              borderParticleManager.spawnParticleChunkBorder(player, player.getLocation().getChunk(), ChunkRenderType.UNCLAIM, false);
              player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_RENT_UNCLAIM_SUCCESS.getComponent(new String[]{chunk})));
            }
            case "deny" -> player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_RENT_UNCLAIM_DENY_SUCCESS.getComponent(new String[]{chunk})));
            default -> player.sendMessage(Lang.USAGE.getComponent(new String[]{SubSyntax.COMMAND_RENT_UNCLAIM.getString()}));
          }
        } else {
          CoreUtil.sendConfirmMessage(player, Lang.PREFIX.getComponent(null).append(Lang.COMMAND_RENT_UNCLAIM_WARNING.getComponent(new String[]{chunk})),
            "/towns rent unclaim",
            Lang.CONFIRM_RENT_UNCLAIM_HOVER.getComponent(null),
            Lang.DENY_RENT_UNCLAIM_HOVER.getComponent(null),
            true
          );
        }
      }

      case "trust" -> {
        if (args.length < 3) {
          player.sendMessage(Lang.USAGE.getComponent(new String[]{SubSyntax.COMMAND_RENT_TRUST.getString()}));
          return;
        }
        final String action = args[2].toLowerCase();
        final String targetString = args[3];
        final OfflinePlayer offlineTarget = Bukkit.getOfflinePlayerIfCached(targetString);
        if (offlineTarget == null) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_PLAYER_NOT_FOUND.getComponent(new String[]{targetString})));
          return;
        }
        if (!cacheManager.getCacheTowns().hasTownsData(offlineTarget.getUniqueId())) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NO_PLAYER_DATA.getComponent(new String[]{targetString})));
          return;
        }
        final UUID targetID = offlineTarget.getUniqueId();
        final UUID ownerID = cacheManager.getCacheTowns().getTargetTownOwner(offlineTarget.getUniqueId());
        if (uuid.equals(ownerID)) {
          player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_RENT_TRUST_OWNER.getComponent(null)));
          return;
        }
        if (!cacheManager.getCacheTowns().getCitizenData().isCitizen(ownerID, targetID)) {
          if (!targetID.equals(ownerID)) {
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_RENT_TRUST_NOT_CITIZEN.getComponent(new String[]{ColorAPI.getNameColor(targetID, targetString)})));
            return;
          }
        }
        switch (action) {
          case "add" -> {
            if (uuid.equals(targetID)) {
              player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_RENT_TRUST_ADD_SELF.getComponent(null)));
              return;
            }
            if (cacheManager.getCacheTowns().getTrustData().isTrusted(uuid, targetID)) {
              player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_RENT_TRUST_ADD_ALREADY_ADDED.getComponent(new String[]{ColorAPI.getNameColor(targetID, targetString)})));
              return;
            }
            cacheManager.getCacheTowns().getTrustData().addTrusted(uuid, targetID);
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_RENT_TRUST_ADD_SUCCESS.getComponent(new String[]{ColorAPI.getNameColor(targetID, targetString)})));
          }
          case "remove" -> {
            if (!cacheManager.getCacheTowns().getTrustData().isTrusted(uuid, targetID)) {
              player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_RENT_TRUST_REMOVE_INVALID.getComponent(new String[]{ColorAPI.getNameColor(targetID, targetString)})));
              return;
            }
            cacheManager.getCacheTowns().getTrustData().removeTrusted(uuid, targetID);
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_RENT_TRUST_REMOVE_SUCCESS.getComponent(new String[]{ColorAPI.getNameColor(targetID, targetString)})));
          }
          default -> player.sendMessage(Lang.USAGE.getComponent(new String[]{SubSyntax.COMMAND_RENT_TRUST.getString()}));
        }
      }

      default -> player.sendMessage(Lang.USAGE.getComponent(new String[]{getSyntax()}));
    }
  }

  @Override
  public void performConsole(CommandSender console, String[] args) {
    console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_CONSOLE_COMMAND.getComponent(null)));
  }

  @Override
  public void performSender(CommandSender sender, String[] args) {
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String[] args) {
    if (args.length == 4 && args[1].equalsIgnoreCase("trust"))
      return StringUtil.copyPartialMatches(args[3], CoreUtil.getOnlinePlayers(), new ArrayList<>());
    else return new ArrayList<>();
  }
}
