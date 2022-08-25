package com.bowencraft.blzeemachines.ItemList

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*

class ProducerList {
    fun producerItemList(plist: List<String>): List<String> {
        val itemlist = ArrayList<String>()
        
        for (s in plist) {
            val itemwithpercentage = s.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val itemname = itemwithpercentage[0]
            val num = itemwithpercentage[1].toInt()
            for (j in 0 until num) {
                itemlist.add(itemname)
            }
        }
        return itemlist
    }
    
    fun getAnItemFromList(itemlist: List<String?>): ItemStack {
        val r = Random(86802025)
        val name = itemlist[r.nextInt(itemlist.size)]
        return ItemStack(Material.getMaterial(name!!)!!)
    }
}