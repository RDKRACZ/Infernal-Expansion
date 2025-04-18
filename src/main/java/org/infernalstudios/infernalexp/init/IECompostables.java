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

package org.infernalstudios.infernalexp.init;

import net.minecraft.block.ComposterBlock;
import net.minecraft.util.IItemProvider;

public class IECompostables {

    public static void registerCompostables() {
        registerCompostable(1.0F, IEBlocks.CRIMSON_FUNGUS_CAP.get().asItem());
        registerCompostable(0.45F, IEBlocks.LUMINOUS_FUNGUS.get().asItem());
        registerCompostable(1.0F, IEBlocks.LUMINOUS_FUNGUS_CAP.get().asItem());
        registerCompostable(0.65F, IEBlocks.SHROOMLIGHT_FUNGUS.get().asItem());
        registerCompostable(1.0F, IEBlocks.WARPED_FUNGUS_CAP.get().asItem());
    }

    private static void registerCompostable(float chance, IItemProvider item) {
        ComposterBlock.CHANCES.put(item.asItem(), chance);
    }
}
