package tc.oc.pgm.goldenhead;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tc.oc.pgm.events.ListenerScope;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchModule;
import tc.oc.pgm.match.MatchScope;

import javax.inject.Inject;

@ListenerScope(MatchScope.RUNNING)
public class GoldenHeadMatchModule extends MatchModule implements Listener {

    @Inject private Server server;

    private static String GOLDEN_HEAD_DISPLAY = ChatColor.BOLD.toString() + ChatColor.AQUA + "Golden Head";

    public GoldenHeadMatchModule(Match match) {
        super(match);
    }

    @Override
    public void enable() {
        super.enable();

        ItemStack goldenHead = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta newMeta = goldenHead.getItemMeta();
        newMeta.setDisplayName(GOLDEN_HEAD_DISPLAY);
        goldenHead.setItemMeta(newMeta);

        ShapedRecipe recipe = new ShapedRecipe(goldenHead);

        recipe.shape("GGG", "GHG", "GGG");

        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('H', new MaterialData(Material.SKULL_ITEM, (byte)3));

        server.addRecipe(recipe);
    }

    @Override
    public void disable() {
        // Recipe changes affect all worlds on the server, so we make changes at match start/end
        // to avoid interfering with adjacent matches. If we wait until unload() to reset them,
        // the next match would already be loaded.
        server.resetRecipes();
        super.disable();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(GOLDEN_HEAD_DISPLAY)) {
            event.getActor().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 10, 1));
        }
    }
}
