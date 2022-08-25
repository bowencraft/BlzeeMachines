package com.bowencraft.blzeemachines.Machines

import com.bowencraft.blzeemachines.Blocks.RESOURCE_PRODUCER
import de.studiocode.invui.gui.GUI
import de.studiocode.invui.gui.builder.GUIBuilder
import de.studiocode.invui.gui.builder.guitype.GUIType
import de.studiocode.invui.virtualinventory.event.ItemUpdateEvent
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.inventory.ItemStack
import xyz.xenondevs.nova.data.config.NovaConfig
import xyz.xenondevs.nova.data.config.configReloadable
import xyz.xenondevs.nova.data.world.block.state.NovaTileEntityState
import xyz.xenondevs.nova.tileentity.NetworkedTileEntity
import xyz.xenondevs.nova.tileentity.network.NetworkConnectionType
import xyz.xenondevs.nova.tileentity.network.energy.holder.ConsumerEnergyHolder
import xyz.xenondevs.nova.tileentity.network.item.holder.NovaItemHolder
import xyz.xenondevs.nova.tileentity.upgrade.Upgradable
import xyz.xenondevs.nova.tileentity.upgrade.UpgradeType
import xyz.xenondevs.nova.ui.EnergyBar
import xyz.xenondevs.nova.ui.OpenUpgradesItem
import xyz.xenondevs.nova.ui.config.side.OpenSideConfigItem
import xyz.xenondevs.nova.ui.config.side.SideConfigGUI
import xyz.xenondevs.nova.util.BlockSide
import xyz.xenondevs.nova.util.advance
import xyz.xenondevs.nova.util.axis
import xyz.xenondevs.nova.util.particleBuilder
import xyz.xenondevs.particle.ParticleEffect
import java.util.ArrayList
import kotlin.math.max
import kotlin.random.Random


private val MAX_ENERGY = configReloadable { NovaConfig[RESOURCE_PRODUCER].getLong("capacity") }
private val ENERGY_PER_TICK = configReloadable { NovaConfig[RESOURCE_PRODUCER].getLong("energy_per_tick") }
private val PROCESS_SPEED by configReloadable { NovaConfig[RESOURCE_PRODUCER].getInt("speed") }
private val TIME_PER_ITEM by configReloadable { NovaConfig[RESOURCE_PRODUCER].getInt("tick_per_item") }
private val ITEM_LIST by configReloadable { NovaConfig[RESOURCE_PRODUCER].getStringList("item_list") }


class ResourceProducer(blockState: NovaTileEntityState) : NetworkedTileEntity(blockState), Upgradable {
    
    override val gui = lazy { ResourceProducerGUI() }
    
    private val outputInv = getInventory("output", 9, ::handleOutputUpdate)
    
    override val upgradeHolder = getUpgradeHolder(UpgradeType.SPEED, UpgradeType.EFFICIENCY, UpgradeType.ENERGY)
    override val energyHolder = ConsumerEnergyHolder(this, MAX_ENERGY, ENERGY_PER_TICK, null, upgradeHolder) { createSideConfig(NetworkConnectionType.INSERT, BlockSide.FRONT) }
    override val itemHolder = NovaItemHolder(this, outputInv to NetworkConnectionType.EXTRACT) { createSideConfig(NetworkConnectionType.EXTRACT, BlockSide.FRONT) }
    
    private var timeLeft: Int = retrieveData("processTime") { 0 }
    private var processSpeed = 0
    
    
    private val particleEffect = particleBuilder(ParticleEffect.SMOKE_LARGE) {
        location(centerLocation.advance(getFace(BlockSide.FRONT), 0.6).apply { y += 0.6 })
        offset(getFace(BlockSide.RIGHT).axis, 0.15f)
        amount(5)
        speed(0.03f)
    }
    
    private fun producerItemList(plist: List<String>): List<String> {
        val itemlist = ArrayList<String>()

        for (s in plist) {
            val itemwithpercentage = s.split("~".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    
            val itemname = itemwithpercentage[0]
            val num = itemwithpercentage[1].toInt()
            
            for (j in 0 until num) {
                itemlist.add(itemname)
            }
        }
        return itemlist
    }
    
    private var defualtItemlist = producerItemList(ITEM_LIST)
    
    private fun getAnItemFromList(itemlist: List<String?>): ItemStack {
        val r = java.util.Random()
        val name = itemlist[r.nextInt(itemlist.size)]
        // add itemsadder compability
        // format: minecraft:ITEM_NAME
        //         itemsadder:name
        return ItemStack(Material.getMaterial(name!!)!!)
    }
    
    private fun handleOutputUpdate(event: ItemUpdateEvent) {
        event.isCancelled = !event.isRemove && event.updateReason != SELF_UPDATE_REASON
    }
    
    override fun handleTick() {
        if (energyHolder.energy >= energyHolder.energyConsumption) {
            if (timeLeft == 0) {
                outputInv.addItem(SELF_UPDATE_REASON, getAnItemFromList(defualtItemlist))
                playSoundEffect(Sound.BLOCK_LAVA_EXTINGUISH, 0.1f, Random.nextDouble(0.5, 1.95).toFloat())
                particleEffect.display(getViewers())
                timeLeft = TIME_PER_ITEM
                
            }
            if (timeLeft != 0) { // is pressing
                timeLeft = max(timeLeft - processSpeed, 0)
            
            }
            energyHolder.energy -= energyHolder.energyConsumption
        }
        
    }
    
    init {
        reload()
        timeLeft = TIME_PER_ITEM
        defualtItemlist = producerItemList(ITEM_LIST)
    }
    
    override fun reload() {
        super.reload()
        processSpeed = (PROCESS_SPEED * upgradeHolder.getValue(UpgradeType.SPEED)).toInt()
    }
    
    override fun handleRemoved(unload: Boolean) {
        super.handleRemoved(unload)
    }
    
    override fun saveData() {
        super.saveData()
        storeData("processTime", timeLeft)
    }
    
    
    inner class ResourceProducerGUI : TileEntityGUI() {
        private val sideConfigGUI = SideConfigGUI(
            this@ResourceProducer,
            listOf(
                itemHolder.getNetworkedInventory(outputInv) to "inventory.nova.output",
            ),
            ::openWindow
        )
        
        override val gui: GUI = GUIBuilder(GUIType.NORMAL)
            .setStructure(
                "1 - - - - - - - 2",
                "| u # o o o # e |",
                "| s # o o o # e |",
                "| # # o o o # e |",
                "3 - - - - - - - 4")
            .addIngredient('o', outputInv)
            .addIngredient('u', OpenUpgradesItem(upgradeHolder))
            .addIngredient('s', OpenSideConfigItem(sideConfigGUI))
            .addIngredient('e', EnergyBar(3, energyHolder))
            .build()
        
    }
}