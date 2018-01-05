package com.ilummc.bugrepgui;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("report")) {
            if (sender instanceof Player) {
                if (sender.hasPermission("bugrepgui.report")) {
                    Bug bug = new Bug((Player) sender);
                    Storage.putMap(bug);
                    Storage.send(sender, "input-bug-info");
                    return true;
                } else {
                    Storage.send(sender, "no-perm");
                    return true;
                }
            } else {
                Storage.send(sender, "must-player-run");
                return true;
            }
        } else {
            if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
                Storage.getHelp(sender);
                return true;
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("view")) {
                if (sender instanceof Player) {
                    if (sender.hasPermission("bugrepgui.view")) {
                        GUI.open((Player) sender, 1);
                    } else {
                        Storage.send(sender, "no-perm");
                    }
                    return true;
                } else {
                    Storage.send(sender, "must-player-run");
                }
                return true;
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("view") && args[1].equalsIgnoreCase("history")) {
                if (sender instanceof Player) {
                    if (sender.hasPermission("bugrepgui.view")) {
                        GUI.openHistory((Player) sender, 1);
                    } else {
                        Storage.send(sender, "no-perm");
                    }
                    return true;
                } else {
                    Storage.send(sender, "must-player-run");
                }
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("report")) {
                if (sender instanceof Player) {
                    if (sender.hasPermission("bugrepgui.report")) {
                        Bug bug = new Bug((Player) sender);
                        Storage.putMap(bug);
                        Storage.send(sender, "input-bug-info");
                        return true;
                    } else {
                        Storage.send(sender, "no-perm");
                        return true;
                    }
                } else {
                    Storage.send(sender, "must-player-run");
                    return true;
                }
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("stats")) {
                if (args[1].equalsIgnoreCase("text")) {
                    if (sender.hasPermission("bugrepgui.stats.text")) {
                        final CommandSender send = sender;
                        Thread t = new Thread(() -> {
                            send.sendMessage(Stats.loadTextRep());
                            try {
                                Thread.sleep(3000);
                                Storage.send(send, "wait");
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                Storage.logExcept(e);
                            }
                            send.sendMessage(Stats.loadTextExe());
                        });
                        t.start();
                        return true;
                    } else {
                        Storage.send(sender, "no-perm");
                        return true;
                    }
                }
                if (args[1].equalsIgnoreCase("gui")) {
                    return true;
                }
                return true;
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("bugrepgui.reload")) {
                    Storage.reload();
                    Storage.send(sender, "reload-suc");
                    return true;
                } else {
                    Storage.send(sender, "no-perm");
                }
            }
            Storage.getHelp(sender);
            return true;
        }
    }
}
