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

package org.infernalstudios.infernalexp.mixin.common;

import org.infernalstudios.infernalexp.blocks.GlowCampfireBlock;
import org.infernalstudios.infernalexp.init.IEBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NyliumBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShovelItem.class)
public class MixinShovelItem {

    @Inject(at = @At("HEAD"), method = "onItemUse", cancellable = true)
    private void IE_onItemUse(ItemUseContext context, CallbackInfoReturnable<ActionResultType> cir) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        BlockState state = world.getBlockState(pos);
        PlayerEntity playerentity = context.getPlayer();
        if (state.getBlock() instanceof GlowCampfireBlock && state.get(GlowCampfireBlock.LIT)) {
            if (!world.isRemote()) {
                world.playEvent(null, 1009, pos, 0);
            }
            GlowCampfireBlock.extinguish(world, pos, state);
            world.setBlockState(pos, state.with(GlowCampfireBlock.LIT, false));
            cir.setReturnValue(ActionResultType.SUCCESS);
        } else if (state.getBlock() instanceof NyliumBlock || state.getBlock().matchesBlock(Blocks.SOUL_SOIL)) {
            if (state.getBlock().matchesBlock(Blocks.CRIMSON_NYLIUM)) {
                state = IEBlocks.CRIMSON_NYLIUM_PATH.get().getDefaultState();
            } else if (state.getBlock().matchesBlock(Blocks.WARPED_NYLIUM)) {
                state = IEBlocks.WARPED_NYLIUM_PATH.get().getDefaultState();
            } else {
                state = IEBlocks.SOUL_SOIL_PATH.get().getDefaultState();
            }
            world.playSound(playerentity, pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (playerentity != null) {
                context.getItem().damageItem(1, playerentity, (player) -> player.sendBreakAnimation(context.getHand()));
            }
            world.setBlockState(pos, state);
            cir.setReturnValue(ActionResultType.SUCCESS);
        }
    }

}
