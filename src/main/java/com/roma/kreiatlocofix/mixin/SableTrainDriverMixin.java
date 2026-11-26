package com.roma.kreiatlocofix.mixin;

import com.lycoris.loconautics.common.sable.SubLevelBlocks;
import com.lycoris.loconautics.content.analogcontroller.AnalogControllerBlockEntity;
import com.lycoris.loconautics.trainphysics.drive.SableTrainDriver;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = SableTrainDriver.class, remap = false)
public abstract class SableTrainDriverMixin {

    @Inject(method = "analogControllerIn", at = @At("HEAD"), cancellable = true)
    private static void kreiat$pickController(ServerSubLevel sub, CallbackInfoReturnable<AnalogControllerBlockEntity> cir) {
        if (sub == null) return;
        List<AnalogControllerBlockEntity> all = SubLevelBlocks.findAll(sub, AnalogControllerBlockEntity.class);
        if (all == null || all.size() < 2) return;
        AnalogControllerBlockEntity scheduled = null;
        for (AnalogControllerBlockEntity c : all) {
            if (c == null || c.isRemoved()) continue;
            if (c.hasUser()) {
                cir.setReturnValue(c);
                return;
            }
            if (scheduled == null && c.isScheduleControlled()) scheduled = c;
        }
        if (scheduled != null) cir.setReturnValue(scheduled);
    }
}
