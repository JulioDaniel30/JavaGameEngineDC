package com.JDStudio.Engine.Sound;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Classe utilitária estática para gerenciar todo o áudio do jogo.
 * <p>
 * É responsável por carregar, armazenar em cache e tocar sons. Diferencia
 * entre efeitos sonoros (SFX) de curta duração e músicas de fundo (em loop).
 * Esta classe não deve ser instanciada.
 *
 * @author JDStudio
 * @since 1.0
 */
public final class Sound {

    /** Cache para armazenar os clips de áudio já carregados, evitando leituras repetidas do disco. */
    private static Map<String, Clip> clipCache = new HashMap<>();
    
    /** Referência para a música que está tocando atualmente em loop. */
    private static Clip currentMusic;
    
    /** O nível de volume atual para as músicas (0.0 a 1.0). */
    private static float musicVolume = 1.0f;
    
    /** O nível de volume atual para os efeitos sonoros (0.0 a 1.0). */
    private static float sfxVolume = 1.0f;

    /**
     * Construtor privado para impedir a instanciação desta classe utilitária.
     */
    private Sound() {}

    /**
     * Carrega um clipe de áudio a partir do classpath e o armazena em cache.
     * Se o clipe já estiver no cache, retorna a instância existente.
     *
     * @param path O caminho para o arquivo de som (ex: "/sounds/music.wav").
     * @return O objeto {@link Clip} carregado, ou {@code null} se ocorrer um erro.
     */
    private static Clip loadClip(String path) {
        if (clipCache.containsKey(path)) {
            return clipCache.get(path);
        }

        try (InputStream rawIs = Sound.class.getResourceAsStream(path)) {
            if (rawIs == null) {
                throw new IOException("Arquivo de som não encontrado: " + path);
            }
            
            // Embrulha o InputStream em um BufferedInputStream para suportar a marcação (mark/reset).
            // Isso é necessário para que o AudioSystem possa ler o formato do arquivo corretamente.
            InputStream bufferedIs = new BufferedInputStream(rawIs);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIs);

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            
            clipCache.put(path, clip);
            return clip;

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Erro ao carregar o som de: " + path);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Aplica um nível de volume a um Clip, convertendo a escala linear (0.0-1.0) para decibéis.
     * @param clip O Clip ao qual o volume será aplicado.
     * @param volume O volume em uma escala de 0.0f (mudo) a 1.0f (máximo).
     */
    private static void setClipVolume(Clip clip, float volume) {
        if (clip == null) return;
        
        volume = Math.max(0.0f, Math.min(1.0f, volume));

        try {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            // A conversão para dB não é linear. Esta é uma fórmula comum para mapeamento.
            float dB = (float) (Math.log(volume == 0.0 ? 0.0001 : volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        } catch (IllegalArgumentException e) {
            System.err.println("Controle de volume não suportado para o clipe.");
        }
    }

    /**
     * Toca um efeito sonoro (SFX) uma única vez.
     *
     * @param path O caminho para o arquivo de som.
     */
    public static void play(String path) {
        Clip clip = loadClip(path);
        if (clip != null) {
            setClipVolume(clip, sfxVolume);
            clip.setFramePosition(0); // Garante que o som toque desde o início
            clip.start();
        }
    }

    /**
     * Toca uma música em loop contínuo.
     * Se outra música já estiver tocando, ela será parada primeiro.
     *
     * @param path O caminho para o arquivo de música.
     */
    public static void loop(String path) {
        stopMusic(); // Para a música anterior
        Clip clip = loadClip(path);
        if (clip != null) {
            currentMusic = clip;
            setClipVolume(currentMusic, musicVolume);
            currentMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    
    /**
     * Para a música que está tocando atualmente.
     * Se nenhuma música estiver tocando, este método não faz nada.
     */
    public static void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null;
        }
    }
    
    /**
     * Define o volume para todas as músicas (futuras e a que está tocando).
     * @param volume O volume em uma escala de 0.0f (mudo) a 1.0f (máximo).
     */
    public static void setMusicVolume(float volume) {
        musicVolume = Math.max(0.0f, Math.min(1.0f, volume));
        setClipVolume(currentMusic, musicVolume); // Ajusta o volume da música atual
    }

    /**
     * Define o volume para todos os efeitos sonoros futuros.
     * @param volume O volume em uma escala de 0.0f (mudo) a 1.0f (máximo).
     */
    public static void setSfxVolume(float volume) {
        sfxVolume = Math.max(0.0f, Math.min(1.0f, volume));
    }
    
    //<editor-fold desc="Getters">
    /** Retorna o nível de volume atual da música. */
    public static float getMusicVolume() { return musicVolume; }
    
    /** Retorna o nível de volume atual dos efeitos sonoros. */
    public static float getSfxVolume() { return sfxVolume; }
    //</editor-fold>
}