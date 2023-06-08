package com.mohistmc.entity;

import com.mohistmc.api.EntityAPI;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftMinecart;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class MohistModsMinecraft extends CraftMinecart {

    public String entityName;

    public MohistModsMinecraft(CraftServer server, AbstractMinecart entity) {
        super(server, entity);
        this.entityName = EntityAPI.entityName(entity);
    }


    @NotNull
    @Override
    public EntityType getType() {
        return EntityAPI.entityType(entityName);
    }

    @Override
    public String toString() {
        return "MohistModsMinecraft{" + entityName + '}';
    }
}
