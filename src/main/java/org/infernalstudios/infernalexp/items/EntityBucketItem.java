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

package org.infernalstudios.infernalexp.items;

import org.infernalstudios.infernalexp.entities.IBucketable;

import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class EntityBucketItem extends BucketItem {
    private final Supplier<? extends EntityType<?>> entityTypeSupplier;
    private final Supplier<SoundEvent> emptyingSoundSupplier;

    public EntityBucketItem(Supplier<EntityType<?>> entityType, Fluid fluid, Supplier<SoundEvent> emptyingSound, Properties builder) {
        super(fluid, builder);
        this.entityTypeSupplier = entityType;
        this.emptyingSoundSupplier = emptyingSound;
    }

    @Override
    public void onLiquidPlaced(World worldIn, ItemStack stack, BlockPos pos) {
        if (worldIn instanceof ServerWorld) {
            this.placeEntity((ServerWorld) worldIn, stack, pos);
        }
    }

    @Override
    protected void playEmptySound(@Nullable PlayerEntity player, IWorld worldIn, BlockPos pos) {
        worldIn.playSound(player, pos, this.emptyingSoundSupplier.get(), SoundCategory.NEUTRAL, 1.0F, 1.0F);
    }

    private void placeEntity(ServerWorld worldIn, ItemStack stack, BlockPos pos) {
        Entity entity = this.entityTypeSupplier.get().spawn(worldIn, stack, null, pos, SpawnReason.BUCKET, true, true);
        if (entity instanceof IBucketable) {
            IBucketable bucketable = (IBucketable) entity;
            bucketable.copyFromAdditional(stack.getOrCreateTag());
            bucketable.setFromBucket(true);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ActionResult<ItemStack> actionResult = super.onItemRightClick(worldIn, playerIn, handIn);
        ItemStack heldItem = playerIn.getHeldItem(handIn);
        BlockRayTraceResult rayTraceResult = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.SOURCE_ONLY);
        if (rayTraceResult.getType() != RayTraceResult.Type.BLOCK) {
            return ActionResult.resultPass(heldItem);
        } else if (!(worldIn instanceof ServerWorld)) {
            return ActionResult.resultSuccess(heldItem);
        } else {
            BlockPos pos = rayTraceResult.getPos();
            if (!(worldIn.getBlockState(pos).getBlock() instanceof FlowingFluidBlock)) {
                return ActionResult.resultPass(heldItem);
            } else {
                return actionResult;
            }
        }
    }
}
