package net.dustley.items

import net.dustley.Crystal
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry

object ModItems {

    val CONTRAPTION_CREATOR = registerItem( "contraption_creator", ContraptionCreator())

    private fun registerItem(name: String, item: Item): Item = Registry.register(Registries.ITEM, Crystal.identifier(name), item)

    fun registerModItems() {
        Crystal.LOGGER.info("Registering Mod Items for " + Crystal.MOD_ID)

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
            .register(ItemGroupEvents.ModifyEntries { entries: FabricItemGroupEntries ->
                entries.add(CONTRAPTION_CREATOR)
            })
    }
}