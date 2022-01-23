package net.frozenorb.potpvp.kt.menu

import net.frozenorb.potpvp.PotPvPND
import org.bukkit.event.Listener
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.ClickType
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.InventoryDragEvent

class ButtonListeners : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onButtonPress(event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        val openMenu = Menu.currentlyOpenedMenus[player.name]

        if (openMenu != null) {

            if (openMenu.buttons.containsKey(event.rawSlot)) {
                val button = openMenu.buttons[event.rawSlot]!!
                val cancel = button.shouldCancel(player, event.rawSlot, event.click)

                event.isCancelled = true
                if (!cancel && event.currentItem != null) {
                        player.inventory.addItem(event.currentItem)
                }

                button.clicked(player, event.rawSlot, event.click, event.view)

                if (Menu.currentlyOpenedMenus.containsKey(player.name)) {
                    val newMenu = Menu.currentlyOpenedMenus[player.name]
                    if (newMenu === openMenu && newMenu.updateAfterClick) {
                        newMenu.openMenu(player)
                    }
                }

                if (event.isCancelled) {
                    Bukkit.getScheduler().runTaskLater(PotPvPND.getInstance(), { player.updateInventory() }, 1L)
                }
            }

            if (openMenu.editable) {
                if (event.inventory == player.openInventory.bottomInventory) {
                    event.isCancelled = false;
                }
            }

            if (event.click == ClickType.SHIFT_LEFT || event.click == ClickType.SHIFT_RIGHT) {
                event.isCancelled = true
            }
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as Player
        val openMenu = Menu.currentlyOpenedMenus[player.name]

        if (openMenu != null) {
            if (event.view.cursor != null) {
                event.player.inventory.addItem(event.view.cursor)
                event.view.cursor = null
            }

            val manualClose = openMenu.manualClose
            openMenu.manualClose = true

            openMenu.onClose(player, manualClose)
            Menu.cancelCheck(player)
            Menu.currentlyOpenedMenus.remove(player.name)
        }
    }
}