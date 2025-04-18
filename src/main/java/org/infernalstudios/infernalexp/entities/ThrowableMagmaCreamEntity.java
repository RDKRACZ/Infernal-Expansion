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

package org.infernalstudios.infernalexp.entities;

import org.infernalstudios.infernalexp.init.IEEntityTypes;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import net.minecraftforge.fml.network.NetworkHooks;

public class ThrowableMagmaCreamEntity extends ProjectileItemEntity {

    public ThrowableMagmaCreamEntity(EntityType<? extends ThrowableMagmaCreamEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public ThrowableMagmaCreamEntity(World world, LivingEntity livingEntity) {
        super(IEEntityTypes.THROWABLE_MAGMA_CREAM.get(), livingEntity, world);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.MAGMA_CREAM;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    /**
     * Called when the magma cream hits an entity
     */
    @Override
    protected void onEntityHit(EntityRayTraceResult entityRayTraceResult) {
        super.onEntityHit(entityRayTraceResult);
        Entity entity = entityRayTraceResult.getEntity();
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 60));
        }
    }

    /**
     * Called when this ThrowableMagmaCreamEntity hits a block or entity.
     */
    @Override
    protected void onImpact(RayTraceResult result) {
        super.onImpact(result);

        if (result.getType() == RayTraceResult.Type.BLOCK) {
            ItemEntity item = new ItemEntity(world, this.getPosX(), this.getPosY(), this.getPosZ(), Items.MAGMA_CREAM.getDefaultInstance());
            world.addEntity(item);
        }
        this.remove();
    }
}
