package com.roma.kreiatlocofix.mixin;

import com.lycoris.loconautics.content.analogcontroller.AnalogControllerBlockEntity;
import com.lycoris.loconautics.trainphysics.SableTrain;
import com.lycoris.loconautics.trainphysics.station.StationLanding;
import com.lycoris.loconautics.trainphysics.station.TrainStations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = TrainStations.class, remap = false)
public abstract class TrainStationsMixin {
    private static final double PARK_RANGE = 8.0;
    private static final double STOPPED_SPEED = 0.02;

    @ModifyVariable(method = "tickStationState", at = @At("STORE"), name = "engineTurning")
    private static boolean kreiat$keepApproach(boolean engineTurning, SableTrain train, AnalogControllerBlockEntity controller) {
        return engineTurning || (train != null && controller != null
                && train.stationState() == SableTrain.StationState.STOPPING
                && controller.isSpaceHeld());
    }

    @Redirect(method = "tickStationState", at = @At(value = "INVOKE",
            target = "Lcom/lycoris/loconautics/trainphysics/station/StationLanding;canSnap(Lcom/lycoris/loconautics/trainphysics/SableTrain;D)Z"))
    private static boolean kreiat$canPark(SableTrain train, double distance) {
        return StationLanding.canSnap(train, distance)
                || (Math.abs(train.speed()) <= STOPPED_SPEED && Math.abs(distance) <= PARK_RANGE);
    }
}
