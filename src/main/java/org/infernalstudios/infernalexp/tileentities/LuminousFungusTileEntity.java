/*
 * Copyright 2022 Infernal Studios
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.infernalstudios.infernalexp.tileentities;

import org.infernalstudios.infernalexp.blocks.LuminousFungusBlock;
import org.infernalstudios.infernalexp.config.InfernalExpansionConfig;
import org.infernalstudios.infernalexp.init.IETileEntityTypes;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;

public class LuminousFungusTileEntity extends TileEntity implements ITickableTileEntity {

    private int lightTime = 0;

    public LuminousFungusTileEntity() {
        super(IETileEntityTypes.LUMINOUS_FUNGUS_TILE_ENTITY.get());
    }

    @Override
    public void tick() {
        if (!this.world.isRemote()) {
            List<Entity> nearbyEntities = this.getWorld().getEntitiesWithinAABB(Entity.class,
                new AxisAlignedBB(this.getPos()).grow(InfernalExpansionConfig.Miscellaneous.LUMINOUS_FUNGUS_ACTIVATE_DISTANCE.getDouble()));
            Vector3d blockPos = new Vector3d(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
            nearbyEntities.removeIf((entity) -> {
                Vector3d entityPos = new Vector3d(entity.getPosX(), entity.getPosYEye(), entity.getPosZ());
                return this.world.rayTraceBlocks(new RayTraceContext(blockPos, entityPos, RayTraceContext.BlockMode.VISUAL, RayTraceContext.FluidMode.NONE, entity)).getType() != RayTraceResult.Type.MISS;
            });
            boolean shouldLight = false;
            for (Entity entity : nearbyEntities) {
                if (entity.lastTickPosX != entity.getPosX() || entity.lastTickPosY != entity.getPosY() || entity.lastTickPosZ != entity.getPosZ()) {
                    double velX = Math.abs(entity.getPosX() - entity.lastTickPosX);
                    double velY = Math.abs(entity.getPosY() - entity.lastTickPosY);
                    double velZ = Math.abs(entity.getPosZ() - entity.lastTickPosZ);
                    if (velX >= (double) 0.003F || velY >= (double) 0.003F || velZ >= (double) 0.003F) {
                        shouldLight = true;
                        break;
                    }
                } else if (
                    entity.distanceWalkedModified - entity.prevDistanceWalkedModified > 0 ||
                        entity.getMotion().length() > 0.1D
                ) {
                    shouldLight = true;
                    break;
                }
            }
            if (lightTime == 0) {
                this.world.setBlockState(this.pos, this.getBlockState().with(LuminousFungusBlock.LIT, shouldLight));
            } else {
                this.lightTime--;
            }
            if (shouldLight) this.lightTime = 60;
        }
    }
}
