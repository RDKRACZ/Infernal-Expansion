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

package org.infernalstudios.infernalexp.events;

import com.google.common.base.Preconditions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class PostRightClickBlockEvent extends PlayerEvent {

    private final Hand hand;
    private final BlockPos pos;
    private final BlockRayTraceResult hitVec;

    /**
     * This event is only fired if there was no right click action for the targeted block.
     * This event won't fire if {@link net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock} has set the useItem {@link net.minecraftforge.eventbus.api.Event.Result} to DENY
     */
    public PostRightClickBlockEvent(PlayerEntity player, Hand hand, BlockPos pos, BlockRayTraceResult hitVec) {
        super(Preconditions.checkNotNull(player, "Null player in PostRightClickBlockEvent!"));
        this.hand = Preconditions.checkNotNull(hand, "Null hand in PostRightClickBlockEvent!");
        this.pos = Preconditions.checkNotNull(pos, "Null position in PostRightClickBlockEvent!");
        this.hitVec = hitVec;
    }

    public Hand getHand() {
        return hand;
    }

    public BlockPos getPos() {
        return pos;
    }

    public BlockRayTraceResult getHitVec() {
        return hitVec;
    }

    public Direction getFace() {
        return getHitVec().getFace();
    }

    public ItemStack getItemStack() {
        return getPlayer().getHeldItem(getHand());
    }

    public World getWorld() {
        return getPlayer().getEntityWorld();
    }
}
