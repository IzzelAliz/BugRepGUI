package com.ilummc.bugrepgui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUI {
	static List<Inventory> invs = new ArrayList<>();
	static List<Inventory> his = new ArrayList<>();
	static Integer pages;

	public static void open(Player player, int page) {
		loadInv();
		player.closeInventory();
		player.openInventory(invs.get(page - 1));
	}

	public static void openHistory(Player player, int page) {
		loadHis(player);
		player.closeInventory();
		player.openInventory(his.get(page - 1));
	}

	public static void loadHis(Player player) {
		his = new ArrayList<>();
		List<Bug> set = Database.listHis();
		Iterator<Bug> it = set.iterator();
		List<ItemStack> nlist = new ArrayList<>();
		while (it.hasNext()) {
			nlist.add(it.next().toItemExecuted());
		}
		setItemHis(nlist, player);
	}

	public static boolean loadInv() {
		invs = new ArrayList<>();
		List<Bug> set = Database.list();
		Iterator<Bug> it = set.iterator();
		List<ItemStack> nlist = new ArrayList<>();
		while (it.hasNext()) {
			nlist.add(it.next().toItem());
		}
		setItem(nlist);
		return true;
	}

	private static void setItemHis(List<ItemStack> nlist, Player player) {
		ItemStack lp = new ItemStack(Material.REDSTONE);
		ItemMeta lm = lp.getItemMeta();
		lm.setDisplayName(Storage.getMsg("last-page"));
		lp.setItemMeta(lm);
		ItemStack np = new ItemStack(Material.EMERALD);
		ItemMeta nm = np.getItemMeta();
		nm.setDisplayName(Storage.getMsg("next-page"));
		np.setItemMeta(nm);
		ItemStack usage = new ItemStack(Material.PAPER);
		ItemMeta um = usage.getItemMeta();
		um.setDisplayName(Storage.getMsg("to-not-executed"));
		usage.setItemMeta(um);
		ItemStack re = new ItemStack(Material.BOOK);
		ItemMeta rm = re.getItemMeta();
		rm.setDisplayName(Storage.getMsg("refresh"));
		re.setItemMeta(rm);
		ItemStack ex = new ItemStack(Material.NETHER_STAR);
		ItemMeta em = ex.getItemMeta();
		em.setDisplayName(Storage.getMsg("exit"));
		ex.setItemMeta(em);
		Iterator<ItemStack> iter = nlist.iterator();
		int all = nlist.size();
		pages = (((all % 45) == 0) && (all != 0)) ? (all / 45) : (all / 45 + 1);
		for (Integer t = 1; t <= pages; t++) {
			Inventory inv = Bukkit.createInventory(player, 54,
					"¡ìd( " + t.toString() + " / " + pages.toString() + " )  " + Storage.getMsg("history-name"));
			for (int i = 0; i <= 44 && iter.hasNext(); i++) {
				ItemStack it = iter.next();
				inv.setItem(i, it);
			}
			inv.setItem(45, lp);
			inv.setItem(47, np);
			inv.setItem(51, usage);
			inv.setItem(49, re);
			inv.setItem(53, ex);
			his.add(t - 1, inv);
		}
	}

	public static void setItem(List<ItemStack> item) {
		ItemStack lp = new ItemStack(Material.REDSTONE);
		ItemMeta lm = lp.getItemMeta();
		lm.setDisplayName(Storage.getMsg("last-page"));
		lp.setItemMeta(lm);
		ItemStack np = new ItemStack(Material.EMERALD);
		ItemMeta nm = np.getItemMeta();
		nm.setDisplayName(Storage.getMsg("next-page"));
		np.setItemMeta(nm);
		ItemStack usage = new ItemStack(Material.PAPER);
		ItemMeta um = usage.getItemMeta();
		um.setDisplayName(Storage.getMsg("usage-name"));
		List<String> lore = Storage.getMsgs("usage-lore");
		um.setLore(lore);
		usage.setItemMeta(um);
		ItemStack re = new ItemStack(Material.BOOK);
		ItemMeta rm = re.getItemMeta();
		rm.setDisplayName(Storage.getMsg("refresh"));
		re.setItemMeta(rm);
		ItemStack ex = new ItemStack(Material.NETHER_STAR);
		ItemMeta em = ex.getItemMeta();
		em.setDisplayName(Storage.getMsg("exit"));
		ex.setItemMeta(em);
		// add items to bug view
		Iterator<ItemStack> iter = item.iterator();
		int all = item.size();
		pages = (((all % 45) == 0) && (all != 0)) ? (all / 45) : (all / 45 + 1);
		for (Integer t = 1; t <= pages; t++) {
			Inventory inv = Bukkit.createInventory(null, 54,
					"¡ìd[ " + t.toString() + " / " + pages.toString() + " ]  " + Storage.getMsg("gui-name"));
			for (int i = 0; i <= 44 && iter.hasNext(); i++) {
				ItemStack it = iter.next();
				inv.setItem(i, it);
			}
			inv.setItem(45, lp);
			inv.setItem(47, np);
			inv.setItem(51, usage);
			inv.setItem(49, re);
			inv.setItem(53, ex);
			invs.add(t - 1, inv);
		}
	}

	/**
	 * 
	 * @param inv
	 *            this inventory
	 * @param page
	 *            this page
	 * @param id
	 *            slot id
	 * @param click
	 *            click type
	 */
	public static void click(Inventory inv, Integer page, Integer id, ClickType click, Player player) {
		if (inv.getItem(id) != null) {
			ItemStack item = inv.getItem(id);
			String serial = item.getItemMeta().getDisplayName();
			if (id != 45 && id != 47 && id != 49 && id != 51 && id != 53) {
				if (click.isLeftClick() && (!click.isShiftClick())) {
					ItemMeta im = item.getItemMeta();
					List<String> lore = im.getLore();
					lore.add(Storage.getMsg("executed"));
					item.setItemMeta(im);
					inv.clear(id);
					inv.setItem(id, item);
					Database.qback(serial, player);
					Notify.notifyb(serial, Storage.getConfig().getString("auto-sendback-msg"), player);
				}
				if (click.isRightClick() && (!click.isShiftClick())) {
					// need to add a serial to this map
					Storage.back.put(player.getUniqueId().toString(), serial);
					Storage.send(player, "send-back");
					player.closeInventory();
				}
				if (click.isLeftClick() && (click.isShiftClick())) {
					ItemMeta im = item.getItemMeta();
					List<String> lore = im.getLore();
					lore.add(Storage.getMsg("executed"));
					item.setItemMeta(im);
					inv.clear(id);
					inv.setItem(id, item);
					Database.ignore(serial, player);
				}
			} else {
				if (id == 45) {
					if (page != 1) {
						open(player, page - 1);
					} else {
						Storage.send(player, "no-this-page");
					}
				}
				if (id == 47) {
					if (page != pages) {
						open(player, page + 1);
					} else {
						Storage.send(player, "no-this-page");
					}
				}
				if (id == 49) {
					loadInv();
					player.closeInventory();
					open(player, page);
				}
				if (id == 53) {
					player.closeInventory();
				}
			}
		}
	}

	public static void clickHis(Inventory inv, Integer page, Integer id, ClickType click, Player player) {
		if (id >= 0 && id <= 53 && inv.getItem(id) != null) {
			if (id == 45) {
				if (page != 1) {
					openHistory(player, page - 1);
				} else {
					Storage.send(player, "no-this-page");
				}
			}
			if (id == 47) {
				if (page != pages) {
					openHistory(player, page + 1);
				} else {
					Storage.send(player, "no-this-page");
				}
			}
			if (id == 49) {
				loadHis(player);
				player.closeInventory();
				openHistory(player, page);
			}
			if (id == 51) {
				player.closeInventory();
				open(player, 1);
			}
			if (id == 53) {
				player.closeInventory();
			}
		}
	}
}
