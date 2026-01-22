package com.froobert.curvyrails.blocks;

import net.minecraft.core.Vec3i;
import net.minecraft.util.StringRepresentable;

public interface ICurveSection extends StringRepresentable {
    Vec3i getOffsetFromOrigin();
}
