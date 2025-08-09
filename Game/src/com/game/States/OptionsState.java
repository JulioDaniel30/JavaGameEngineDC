package com.game.States;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.function.Consumer;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Graphics.UI.UISpriteKey;
import com.JDStudio.Engine.Graphics.UI.Elements.UIButton;
import com.JDStudio.Engine.Graphics.UI.Elements.UISlider;
import com.JDStudio.Engine.Graphics.UI.Elements.UIText;
import com.JDStudio.Engine.Graphics.UI.Managers.ThemeManager;
import com.JDStudio.Engine.Sound.Sound;
import com.JDStudio.Engine.Sound.Sound.SoundChannel;
import com.JDStudio.Engine.States.EngineMenuState;

public class OptionsState extends EngineMenuState {

	/**
     * Cria a tela de opções.
     * @param parentState O estado de jogo para o qual se deve voltar (ex: MenuState ou PauseState).
     */
    public OptionsState() {
        super();

        
    }
    
    @Override
    protected void buildUI() {
    	// --- TÍTULO ---
        uiManager.addElement(new UIText(Engine.getWIDTH() / 2 - 30, 30, new Font("Serif", Font.BOLD, 20), Color.WHITE, "Opções"));

        // --- SLIDER DE VOLUME DA MÚSICA ---
        uiManager.addElement(new UIText(20, 60, new Font("Arial", Font.PLAIN, 12), Color.WHITE, "Música:"));
        
        // Ação que será executada QUANDO o valor do slider mudar
        Consumer<Float> onMusicVolumeChange = (novoValor) -> {
            Sound.setChannelVolume(SoundChannel.MUSIC, novoValor);
        };
        
        int button_width = (int) ThemeManager.getInstance().get(UISpriteKey.SLIDER_TRACK).getWidth();
		int button_centerX_Position = Engine.getWIDTH() / 2 - button_width / 2;
        
        UISlider musicSlider = new UISlider(
            button_centerX_Position, 50,
            0.0f, 1.0f, // Valor mínimo e máximo
            Sound.getChannelVolume(SoundChannel.MUSIC), // Pega o volume ATUAL para definir a posição inicial
            onMusicVolumeChange // A ação a ser executada
        );
        uiManager.addElement(musicSlider);

        // --- SLIDER DE VOLUME DOS EFEITOS SONOROS (SFX) ---
        uiManager.addElement(new UIText(20, 90, new Font("Arial", Font.PLAIN, 12), Color.WHITE, "Efeitos:"));
        
        Consumer<Float> onSfxVolumeChange = (novoValor) -> {
            Sound.setChannelVolume(SoundChannel.SFX, novoValor);
        };
        
        UISlider sfxSlider = new UISlider(
        		button_centerX_Position, 80,
            ThemeManager.getInstance().get(UISpriteKey.SLIDER_TRACK),
            ThemeManager.getInstance().get(UISpriteKey.SLIDER_HANDLE),
            0.0f, 1.0f,
            Sound.getChannelVolume(SoundChannel.SFX),
            onSfxVolumeChange
        );
        uiManager.addElement(sfxSlider);

        // --- BOTÃO DE VOLTAR ---
        UIButton backButton = new UIButton(
                Engine.getWIDTH() / 2 - 40, 120,
                "Voltar",
                new Font("Arial", Font.BOLD, 12),
                () -> Engine.popState() // Volta para o estado que o abriu (Menu ou Pausa)
            );
        uiManager.addElement(backButton);
    }

    @Override
    public void render(Graphics g) {
        g.setColor(new Color(20, 20, 80));
        g.fillRect(0, 0, Engine.getWIDTH(), Engine.getHEIGHT());
        super.render(g);
    }
}