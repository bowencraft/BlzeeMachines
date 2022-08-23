package com.bowencraft.blzeemachines.Machines

import com.bowencraft.blzeemachines.Blocks.RESOURCE_PRODUCER
import de.studiocode.invui.gui.GUI
import de.studiocode.invui.gui.builder.GUIBuilder
import de.studiocode.invui.gui.builder.guitype.GUIType
import xyz.xenondevs.nova.data.config.NovaConfig
import xyz.xenondevs.nova.data.config.configReloadable
import xyz.xenondevs.nova.data.world.block.state.NovaTileEntityState
import xyz.xenondevs.nova.tileentity.NetworkedTileEntity
import xyz.xenondevs.nova.tileentity.network.NetworkConnectionType
import xyz.xenondevs.nova.tileentity.network.energy.holder.ProviderEnergyHolder
import xyz.xenondevs.nova.ui.EnergyBar
import xyz.xenondevs.nova.util.BlockSide

private val MAX_ENERGY = configReloadable { NovaConfig[RESOURCE_PRODUCER].getLong("capacity") }
private val ENERGY_PER_TICK = configReloadable { NovaConfig[RESOURCE_PRODUCER].getLong("energy_per_tick") }


class Producer(blockState: NovaTileEntityState) : NetworkedTileEntity(blockState) {
    
    override val gui = lazy(::ProducerGUI)
    
    override val energyHolder = ProviderEnergyHolder(this, MAX_ENERGY, ENERGY_PER_TICK, null) {
        createExclusiveSideConfig(NetworkConnectionType.EXTRACT, BlockSide.BOTTOM)
    }
    
    
    inner class ProducerGUI : TileEntityGUI() {
        
        override val gui: GUI = GUIBuilder(GUIType.NORMAL)
            .setStructure(
                "1 - - - - - - - 2",
                "| # # # e # # # |",
                "| # # # e # # # |",
                "| # # # e # # # |",
                "3 - - - - - - - 4")
            .addIngredient('e', EnergyBar(3, energyHolder)) //
            
            .build()
        
    }
}