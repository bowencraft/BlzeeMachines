package com.bowencraft.blzeemachines

// import com.bowencraft.blzeemachines.Machines.Producer
import com.bowencraft.blzeemachines.Machines.Producer
import org.bukkit.Material
import org.bukkit.Sound
import xyz.xenondevs.nova.data.world.block.property.Directional
import xyz.xenondevs.nova.material.BlockOptions
import xyz.xenondevs.nova.material.NovaMaterialRegistry
import xyz.xenondevs.nova.material.NovaMaterialRegistry.registerBlock
import xyz.xenondevs.nova.material.NovaMaterialRegistry.registerTileEntity
import xyz.xenondevs.nova.util.SoundEffect
import xyz.xenondevs.nova.util.item.ToolCategory
import xyz.xenondevs.nova.util.item.ToolLevel

object Blocks {
    
    private val STONE = BlockOptions(
        3.0, //
        ToolCategory.PICKAXE, //
        ToolLevel.STONE, //
        true, //
        SoundEffect(Sound.BLOCK_STONE_PLACE), //
        SoundEffect(Sound.BLOCK_STONE_BREAK), //
        Material.NETHERITE_BLOCK, //
        true //
    )
    

    
    // val RESOURCE_PRODUCER = NovaMaterialRegistry.registerTileEntity(blzeemachines, "resource_producer", STONE, ::Producer, properties = listOf(Directional.NORMAL))
    // val RESOURCE_PRODUCER = NovaMaterialRegistry.registerBlock(blzeemachines, "resource_producer", STONE)
    // val TEST_MACHINE = NovaMaterialRegistry.registerBlock(blzeemachines, "testmachine", STONE)
    val RESOURCE_PRODUCER = registerTileEntity(blzeemachines, "resource_producer", STONE, ::Producer, properties = listOf(Directional.NORMAL))
    fun init() = Unit
    
}