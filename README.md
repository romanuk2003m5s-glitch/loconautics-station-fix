# Loconautics Train Fixes

Small NeoForge 1.21.1 addon for Create: Loconautics 0.1.3.

- Holding Space to approach a station no longer cancels halfway, and the train parks if it stops within 8 blocks of the station mark (Creators-of-Locomotion/Create-Loconautics#152, fixed upstream in indev).
- With two analog controllers on a car, the one you sit at (or the one running a schedule) is used instead of whichever is found first.
- While someone drives, only the driver's car reads A/D, left/right is taken from the controller's facing, and the other cars follow the lead car through turns like a Create train.
