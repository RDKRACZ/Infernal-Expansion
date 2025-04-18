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

package org.infernalstudios.infernalexp.blocks;

import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.Direction;
import net.minecraft.world.IWorld;
import org.infernalstudios.infernalexp.entities.BlindsightEntity;
import org.infernalstudios.infernalexp.init.IEBlocks;
import org.infernalstudios.infernalexp.init.IEEffects;
import org.infernalstudios.infernalexp.init.IEItems;
import org.infernalstudios.infernalexp.init.IETags;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IForgeShearable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DullthornsBlock extends BushBlock implements IForgeShearable {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_0_15;
    public static final BooleanProperty TIP = BooleanProperty.create("tip");
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 15.0D, 11.0D);

    public DullthornsBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.getDefaultState().with(AGE, Integer.valueOf(0)).with(TIP, false));
    }

    @Override
    public boolean isShearable(@Nonnull ItemStack item, World world, BlockPos pos) {
        return true;
    }

    @Nonnull
    @Override
    public List<ItemStack> onSheared(@Nullable PlayerEntity player, @Nonnull ItemStack item, World world, BlockPos pos, int fortune) {
        return Arrays.asList(new ItemStack(IEItems.DULLTHORNS.get()));
    }

    @Override
    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return state.isIn(IETags.Blocks.DULLTHORNS_GROUND);
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        if (!worldIn.isAreaLoaded(pos, 1))
            return; // Forge: prevent growing cactus from loading unloaded chunks with block update
        if (!state.isValidPosition(worldIn, pos)) {
            worldIn.destroyBlock(pos, true);
        }
    }

    /**
     * Performs a random tick on a block.
     */

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        BlockPos blockpos = pos.up();
        if (worldIn.isAirBlock(blockpos)) {
            int i;
            i = 1;
            while (worldIn.getBlockState(pos.down(i)).matchesBlock(this)) {
                ++i;
            }

            if (i < 9) {
                int j = state.get(AGE);
                if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, blockpos, state, true)) {
                    if (j == 15) {
                        worldIn.setBlockState(blockpos, this.getDefaultState());
                        BlockState blockstate = state.with(AGE, 0);
                        worldIn.setBlockState(pos, blockstate, 4);
                        blockstate.neighborChanged(worldIn, blockpos, this, pos, false);
                    } else {
                        worldIn.setBlockState(pos, state.with(AGE, j + 1), 4);
                    }
                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state);
                }
            }
        }
    }

    public boolean bonemealGrow(BlockState state, World worldIn, BlockPos pos) {
        BlockPos posUp = pos.up();
        if (worldIn.isAirBlock(posUp)) {
            worldIn.setBlockState(posUp, this.getDefaultState());
            BlockState blockstate = state.with(AGE, 0);
            worldIn.setBlockState(pos, blockstate, 4);
            blockstate.neighborChanged(worldIn, posUp, this, pos, false);
            return true;
        } else if (worldIn.getBlockState(posUp).getBlock() == IEBlocks.DULLTHORNS.get()) {
            return ((DullthornsBlock) worldIn.getBlockState(posUp).getBlock()).bonemealGrow(state, worldIn, posUp);
        } else {
            return false;
        }
    }

    @Override
    public boolean isLadder(BlockState state, net.minecraft.world.IWorldReader world, BlockPos pos, net.minecraft.entity.LivingEntity entity) {
        return true;
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        if (!worldIn.isRemote()) {
            if (entityIn instanceof LivingEntity && entityIn.isAlive() && !(entityIn instanceof BlindsightEntity)) {
                LivingEntity livingEntity = (LivingEntity) entityIn;
                livingEntity.addPotionEffect(new EffectInstance(IEEffects.LUMINOUS.get(), 200, 0, true, true));
            }
            entityIn.attackEntityFrom(DamageSource.CACTUS, 1.0F);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState aboveBlockState = context.getWorld().getBlockState(context.getPos().up());
        boolean aboveIsDullthorns = aboveBlockState.matchesBlock(IEBlocks.DULLTHORNS.get());

        if (aboveIsDullthorns) {
            return this.getDefaultState();
        } else {
            return this.getDefaultState().with(TIP, true);
        }
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        BlockState aboveBlockState = worldIn.getBlockState(currentPos.up());
        boolean aboveIsDullthorns = aboveBlockState.matchesBlock(IEBlocks.DULLTHORNS.get());

        if (!stateIn.isValidPosition(worldIn, currentPos)) {
            return Blocks.AIR.getDefaultState();
        }

        if (!aboveIsDullthorns && !stateIn.get(TIP)) {
            return !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : stateIn.with(TIP, true);
        } else if (aboveIsDullthorns && stateIn.get(TIP)) {
            return !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : stateIn.with(TIP, false);
        } else {
            return stateIn;
        }
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        Vector3d vector3d = state.getOffset(worldIn, pos);
        return SHAPE.withOffset(vector3d.x, vector3d.y, vector3d.z);
    }

    @Override
    public OffsetType getOffsetType() {
        return OffsetType.XZ;
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AGE);
        builder.add(TIP);
    }
}
