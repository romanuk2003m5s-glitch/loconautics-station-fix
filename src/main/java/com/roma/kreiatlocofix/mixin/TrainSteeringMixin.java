package com.roma.kreiatlocofix.mixin;

import com.lycoris.loconautics.content.analogcontroller.AnalogControllerBlockEntity;
import com.lycoris.loconautics.trainphysics.SableTrain;
import com.lycoris.loconautics.trainphysics.SableTrain.Car;
import com.lycoris.loconautics.trainphysics.drive.SableTrainDriver;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.lycoris.loconautics.trainphysics.drive.TrainSteering", remap = false)
public abstract class TrainSteeringMixin {

    @Shadow
    static SableTrain drivenLeader(SableTrain train) {
        throw new AssertionError();
    }

    @Shadow
    static SableTrain leaderAhead(SableTrain train) {
        throw new AssertionError();
    }

    @Shadow
    static boolean followAhead(SableTrain train, SableTrain ahead) {
        throw new AssertionError();
    }

    @Shadow
    static void applySteerTarget(Car car, Direction target) {
        throw new AssertionError();
    }

    @Shadow
    static void clearFollow(Car car) {
        throw new AssertionError();
    }

    @Inject(method = "tickSteering", at = @At("HEAD"), cancellable = true)
    private static void kreiat$steerFromController(SableTrain train, CallbackInfo ci) {
        if (train == null || train.isDerailed() || train.isPhysics()) return;
        Car car = train.car();
        if (car == null || car.subLevelId() == null) return;

        SableTrain leader = drivenLeader(train);
        SableTrain driver = leader != null ? leader : train;
        if (driver.car() == null || driver.car().subLevelId() == null) return;
        AnalogControllerBlockEntity controller = SableTrainDriver.operatedControllerIn(driver);
        if (controller == null) return;
        ci.cancel();

        if (car.subLevelId().equals(driver.car().subLevelId())) {
            Direction intent = kreiat$intent(train, controller.getSteerInput());
            if (intent != null) {
                train.setSteerIntent(intent);
                clearFollow(car);
                applySteerTarget(car, intent);
                return;
            }
            SableTrain ahead = leader == null ? null : leaderAhead(train);
            train.setSteerIntent(null);
            if (ahead == null || !followAhead(train, ahead)) clearFollow(car);
            applySteerTarget(car, null);
            return;
        }

        Direction intent = driver.steerIntent();
        SableTrain ahead = leaderAhead(train);
        if (ahead == null || !followAhead(train, ahead)) clearFollow(car);
        applySteerTarget(car, intent);
    }

    private static Direction kreiat$intent(SableTrain driver, int input) {
        if (input == 0) return null;
        Vec3 look = SableTrainDriver.driverLook(driver);
        if (look == null || look.x * look.x + look.z * look.z < 1.0e-9) return null;
        Direction ahead = Direction.getNearest(look.x, 0.0, look.z);
        return input < 0 ? ahead.getCounterClockWise() : ahead.getClockWise();
    }
}
