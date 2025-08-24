package com.jdstudio.engine.Graphics;

/**
 * Enum providing a collection of standard and recommended resolution profiles,
 * inspired by classic consoles and modern pixel art standards.
 * Each profile includes a base internal resolution and a recommended scaling factor
 * for the final display window.
 */
public enum StandardResolutions {

    // --- For 16x16 Tiles ---
    /** Resolution 240x160 (3:2 aspect ratio), inspired by the Game Boy Advance. Classic and very popular. */
    GBA_STYLE(new ResolutionProfile(240, 160, 3)),
    
    /** Resolution 256x144 (16:9 aspect ratio), ideal for modern monitors. */
    MODERN_16_9_LOW(new ResolutionProfile(256, 144, 4)),
    
    /** Resolution 320x180 (16:9 aspect ratio), a pixel art HD style with more screen space. */
    MODERN_16_9_HIGH(new ResolutionProfile(320, 180, 3)),
    
    /** Resolution 256x224 (~8:7 aspect ratio), inspired by SNES/Mega Drive. Retro style. */
    SNES_STYLE(new ResolutionProfile(256, 224, 3)),

    // --- For 32x32 Tiles ---
    /** Resolution 320x180 (16:9 aspect ratio), ideal for 10 tiles of 32px width. */
    HD_10_TILES_WIDE(new ResolutionProfile(320, 180, 3)),

    /** Resolution 384x224 (~16:9 aspect ratio), classic PC games of the 90s style. */
    PC_90S_STYLE(new ResolutionProfile(384, 224, 2));

    /** The ResolutionProfile associated with this standard resolution. */
    private final ResolutionProfile profile;

    /**
     * Constructs a StandardResolutions enum constant with the given ResolutionProfile.
     * @param profile The ResolutionProfile for this standard resolution.
     */
    StandardResolutions(ResolutionProfile profile) {
        this.profile = profile;
    }

    /**
     * Returns the resolution profile associated with this preset.
     * @return The ResolutionProfile object.
     */
    public ResolutionProfile getProfile() {
        return this.profile;
    }
}
