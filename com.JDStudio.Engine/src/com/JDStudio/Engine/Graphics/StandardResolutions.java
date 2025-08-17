package com.JDStudio.Engine.Graphics;

/**
 * Enum que fornece uma coleção de perfis de resolução padrão e recomendados,
 * inspirados em consoles clássicos e padrões de pixel art modernos.
 */
public enum StandardResolutions {

    // --- Para Tiles de 16x16 ---
    /** Resolução 240x160 (3:2), inspirada no Game Boy Advance. Clássico e muito popular. */
    GBA_STYLE(new ResolutionProfile(240, 160, 3)),
    
    /** Resolução 256x144 (16:9), ideal para monitores modernos. */
    MODERN_16_9_LOW(new ResolutionProfile(256, 144, 4)),
    
    /** Resolução 320x180 (16:9), estilo pixel art HD com mais espaço na tela. */
    MODERN_16_9_HIGH(new ResolutionProfile(320, 180, 3)),
    
    /** Resolução 256x224 (~8:7), inspirada no SNES/Mega Drive. Estilo retro. */
    SNES_STYLE(new ResolutionProfile(256, 224, 3)),

    // --- Para Tiles de 32x32 ---
    /** Resolução 320x180 (16:9), ideal para 10 tiles de 32px de largura. */
    HD_10_TILES_WIDE(new ResolutionProfile(320, 180, 3)),

    /** Resolução 384x224 (~16:9), clássico de jogos de PC dos anos 90. */
    PC_90S_STYLE(new ResolutionProfile(384, 224, 2));

    private final ResolutionProfile profile;

    StandardResolutions(ResolutionProfile profile) {
        this.profile = profile;
    }

    /**
     * Retorna o perfil de resolução associado a esta predefinição.
     * @return O objeto ResolutionProfile.
     */
    public ResolutionProfile getProfile() {
        return this.profile;
    }
}