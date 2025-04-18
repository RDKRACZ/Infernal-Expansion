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

import org.infernalstudios.infernalexp.access.FireTypeAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

@Mixin(Entity.class)
public abstract class MixinEntity implements FireTypeAccess {

    @Shadow
    @Final
    protected EntityDataManager dataManager;

    @Unique
    private static final DataParameter<String> FIRE_TYPE = EntityDataManager.createKey(Entity.class, DataSerializers.STRING);

    @Inject(method = "<init>", at = @At("TAIL"))
    private void IE_init(EntityType<?> entityTypeIn, World worldIn, CallbackInfo ci) {
        this.dataManager.register(FIRE_TYPE, KnownFireTypes.FIRE.getName());
    }

    @Inject(method = "writeWithoutTypeId", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundNBT;putShort(Ljava/lang/String;S)V", ordinal = 0, shift = Shift.AFTER))
    private void IE_writeCustomFires(CompoundNBT tag, CallbackInfoReturnable<CompoundNBT> ci) {
        tag.putString("fireType", this.getFireType().getName());
    }

    @Inject(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundNBT;getShort(Ljava/lang/String;)S", ordinal = 0, shift = Shift.AFTER))
    private void IE_readCustomFires(CompoundNBT tag, CallbackInfo ci) {
        this.setFireType(KnownFireTypes.byName(tag.getString("fireType")));
    }

    @Inject(method = "setOnFireFromLava", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setFire(I)V", shift = Shift.BEFORE))
    private void IE_setCustomFireFromLava(CallbackInfo ci) {
        this.setFireType(KnownFireTypes.FIRE);
    }

    @Override
    public KnownFireTypes getFireType() {
        return KnownFireTypes.byName(this.dataManager.get(FIRE_TYPE));
    }

    @Override
    public void setFireType(KnownFireTypes type) {
        this.dataManager.set(FIRE_TYPE, type.getName());
    }

}
