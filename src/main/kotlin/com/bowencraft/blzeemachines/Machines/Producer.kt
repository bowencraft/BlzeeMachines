package com.bowencraft.blzeemachines.Machines

import com.bowencraft.blzeemachines.Blocks.RESOURCE_PRODUCER
import de.studiocode.invui.gui.GUI
import de.studiocode.invui.gui.builder.GUIBuilder
import de.studiocode.invui.gui.builder.guitype.GUIType
import org.bukkit.Material
import xyz.xenondevs.nova.data.config.NovaConfig
import xyz.xenondevs.nova.data.config.configReloadable
import xyz.xenondevs.nova.data.world.block.state.NovaTileEntityState
import xyz.xenondevs.nova.tileentity.NetworkedTileEntity
import xyz.xenondevs.nova.tileentity.network.NetworkConnectionType
import xyz.xenondevs.nova.tileentity.network.energy.holder.ProviderEnergyHolder
import xyz.xenondevs.nova.tileentity.upgrade.UpgradeType
import xyz.xenondevs.nova.ui.EnergyBar
import xyz.xenondevs.nova.ui.OpenUpgradesItem
import xyz.xenondevs.nova.util.BlockSide
import xyz.xenondevs.nova.util.item.isGlass
import xyz.xenondevs.nova.util.runTaskTimer
import xyz.xenondevs.nova.util.untilHeightLimit
import java.lang.Math.abs
import kotlin.math.roundToInt

private val MAX_ENERGY = configReloadable { NovaConfig[RESOURCE_PRODUCER].getLong("capacity") }
private val ENERGY_PER_TICK = configReloadable { NovaConfig[RESOURCE_PRODUCER].getLong("energy_per_tick") }


class Producer(blockState: NovaTileEntityState) : NetworkedTileEntity(blockState) {
    override val gui = lazy { SolarPanelGUI() }
    val upgradeHolder = getUpgradeHolder(UpgradeType.EFFICIENCY, UpgradeType.ENERGY)
    override val energyHolder = ProviderEnergyHolder(this, MAX_ENERGY, ENERGY_PER_TICK, upgradeHolder) {
        createExclusiveSideConfig(NetworkConnectionType.EXTRACT, BlockSide.BOTTOM)
    }
    
    private val obstructionTask = runTaskTimer(0, 20 * 5, ::checkSkyObstruction)
    private var obstructed = true
    
    private fun checkSkyObstruction() {
        obstructed = false
        location.untilHeightLimit(false) {
            val material = it.block.type
            if (material != Material.AIR && !material.isGlass()) {
                obstructed = true
                return@untilHeightLimit false
            }
            return@untilHeightLimit true
        }
    }
    
    override fun handleTick() {
        energyHolder.energy += calculateCurrentEnergyOutput()
    }
    
    private fun calculateCurrentEnergyOutput(): Int {
    
//        val time = location.world!!.time
//        if (!obstructed && time < 13_000) {
//            val bestTime = 6_500
//            val multiplier = (bestTime - abs(bestTime - time)) / bestTime.toDouble()
//            return (energyHolder.energyGeneration * multiplier).roundToInt()
//        }
//        return 0
        
        return energyHolder.energyGeneration.toInt()
    }
    
    override fun handleRemoved(unload: Boolean) {
        super.handleRemoved(unload)
        obstructionTask.cancel()
    }
    
    inner class SolarPanelGUI : TileEntityGUI() {
        
        override val gui: GUI = GUIBuilder(GUIType.NORMAL)
            .setStructure(
                "1 - - - - - - - 2",
                "| u # # e # # # |",
                "| # # # e # # # |",
                "| # # # e # # # |",
                "3 - - - - - - - 4")
            .addIngredient('u', OpenUpgradesItem(upgradeHolder))
            .addIngredient('e', EnergyBar(3, energyHolder))
            .build()
        
    }
}