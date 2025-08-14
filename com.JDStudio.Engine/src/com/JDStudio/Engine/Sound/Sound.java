package com.JDStudio.Engine.Sound;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.JDStudio.Engine.Engine;

public final class Sound {
	
	// --- NOVO ENUM PARA OS CANAIS DE ÁUDIO ---
    public enum SoundChannel {
        MUSIC, // Para a trilha sonora
        SFX,   // Para efeitos sonoros do jogo (tiros, passos, etc.)
        UI     // Para sons de interface (cliques de botão, etc.)
    }


    private static class AudioData {
        final byte[] data;
        final AudioFormat format;

        AudioData(byte[] data, AudioFormat format) {
            this.data = data;
            this.format = format;
        }
    }

    private static Map<String, AudioData> sfxDataCache = new HashMap<>();
    private static Map<String, Clip> musicClipCache = new HashMap<>();
    
    private static Clip currentMusic;
    
    private static final Map<SoundChannel, Float> channelVolumes = new EnumMap<>(SoundChannel.class);
    private static float musicVolume = 1.0f;
    private static float sfxVolume = 1.0f;
    
 // Bloco estático para inicializar os volumes padrão
    static {
        channelVolumes.put(SoundChannel.MUSIC, 1.0f);
        channelVolumes.put(SoundChannel.SFX, 1.0f);
        channelVolumes.put(SoundChannel.UI, 1.0f);
    }

    private Sound() {}

    private static AudioData loadAudioData(String path) {
        if (sfxDataCache.containsKey(path)) {
            return sfxDataCache.get(path);
        }

        try (InputStream rawIs = Sound.class.getResourceAsStream(path)) {
            if (rawIs == null) throw new IOException("Arquivo de som não encontrado: " + path);
            
            // CORREÇÃO APLICADA AQUI TAMBÉM
            InputStream bufferedIs = new BufferedInputStream(rawIs);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIs);
            AudioFormat format = audioStream.getFormat();
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int read;
            while ((read = audioStream.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, read);
            }
            byte[] audioBytes = baos.toByteArray();
            
            AudioData audioData = new AudioData(audioBytes, format);
            sfxDataCache.put(path, audioData);
            audioStream.close();
            return audioData;

        } catch (UnsupportedAudioFileException | IOException e) {
            System.err.println("Erro ao carregar os dados do som de: " + path);
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Toca um efeito sonoro em um canal específico.
     * @param path O caminho para o arquivo de som.
     * @param channel O canal no qual o som será tocado (MUSIC, SFX, ou UI).
     */
    public static void play(String path, SoundChannel channel) {
        play(path, channel, 1.0f); // Volume máximo individual
    }

    
    /**
     * Toca um efeito sonoro espacial (com distância e balanço) em um canal.
     * @param path O caminho para o arquivo de som.
     * @param channel O canal no qual o som será tocado (geralmente SFX).
     * @param emitterX A posição X do emissor do som no mundo.
     * @param emitterY A posição Y do emissor do som no mundo.
     */
    public static void play(String path, SoundChannel channel, int emitterX, int emitterY) {
        play(path, channel, 1.0f, true, emitterX, emitterY);
    }
    
    /**
     * Toca um efeito sonoro em um canal com um volume individual.
     * O volume final será (volume do canal * volume individual).
     */
    public static void play(String path, SoundChannel channel, float volumeScale) {
        AudioData audioData = loadAudioData(path);
        if (audioData == null) return;

        try {
            Clip clip = AudioSystem.getClip();
            InputStream inputStream = new ByteArrayInputStream(audioData.data);
            AudioInputStream audioStream = new AudioInputStream(inputStream, audioData.format, audioData.data.length / audioData.format.getFrameSize());
            clip.open(audioStream);
            
            // O volume final considera o volume do canal e o volume individual do som
            float finalVolume = channelVolumes.get(channel) * volumeScale;
            setClipControls(clip, finalVolume, 0.0f);
            
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Toca um som em loop no canal de MÚSICA.
     * @param path O caminho para o arquivo de música.
     */
    public static void loop(String path) {
        stopMusic();
        
        // A lógica de cache para carregar o clipe de música permanece a mesma
        if (musicClipCache.containsKey(path)) {
            currentMusic = musicClipCache.get(path);
        } else {
            try (InputStream rawIs = Sound.class.getResourceAsStream(path)) {
                if (rawIs == null) throw new IOException("Arquivo de música não encontrado: " + path);
                
                InputStream bufferedIs = new BufferedInputStream(rawIs);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIs);
                
                Clip musicClip = AudioSystem.getClip();
                musicClip.open(audioStream);
                musicClipCache.put(path, musicClip);
                currentMusic = musicClip;
            } catch (Exception e) {
                System.err.println("Erro ao carregar a música de: " + path);
                e.printStackTrace();
                return;
            }
        }
        
        // --- A CORREÇÃO ESTÁ AQUI ---
        // A música agora usa explicitamente o volume do canal MUSIC.
        // O balanço (pan) é definido como 0.0 (centro) para a música.
        setClipControls(currentMusic, channelVolumes.get(SoundChannel.MUSIC), 0.0f);
        
        currentMusic.setFramePosition(0);
        currentMusic.loop(Clip.LOOP_CONTINUOUSLY);
    }
     /**
      * Método base privado que lida com todas as formas de tocar som.
      */
     private static void play(String path, SoundChannel channel, float volumeScale, boolean isSpatial, int emitterX, int emitterY) {
         AudioData audioData = loadAudioData(path);
         if (audioData == null) return;

         float finalVolume = channelVolumes.get(channel) * volumeScale;
         float pan = 0.0f;

         if (isSpatial) {
             int listenerX = Engine.camera.getX() + Engine.getWIDTH() / 2;
             int listenerY = Engine.camera.getY() + Engine.getHEIGHT() / 2;
             
             double distance = Math.sqrt(Math.pow(emitterX - listenerX, 2) + Math.pow(emitterY - listenerY, 2));
             double maxHearingDistance = 200.0;
             
             float spatialVolume = (distance > maxHearingDistance) ? 0.0f : (float)(1.0 - (distance / maxHearingDistance));
             finalVolume *= spatialVolume; // Aplica a atenuação da distância

             double horizontalOffset = emitterX - listenerX;
             double panWidth = Engine.getWIDTH() * 1.5;
             pan = (float) Math.max(-1.0, Math.min(1.0, horizontalOffset / panWidth));
         }
         
         // Otimização: não tenta tocar um som que não pode ser ouvido.
         if (finalVolume <= 0.001f) {
             return;
         }

         try {
             Clip clip = AudioSystem.getClip();
             InputStream inputStream = new ByteArrayInputStream(audioData.data);
             AudioInputStream audioStream = new AudioInputStream(inputStream, audioData.format, audioData.data.length / audioData.format.getFrameSize());
             clip.open(audioStream);
             setClipControls(clip, finalVolume, pan);
             clip.start();
         } catch (Exception e) {
             e.printStackTrace();
         }
     }
    
    public static void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    private static void setClipVolume(Clip clip, float volume) {
        if (clip == null) return;
        volume = Math.max(0.0f, Math.min(1.0f, volume));
        try {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume == 0.0 ? 0.0001 : volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        } catch (IllegalArgumentException e) {
            // Silencioso
        }
    }
    /**
     * Método atualizado que agora controla Volume E Balanço (Panning).
     */
    private static void setClipControls(Clip clip, float volume, float pan) {
        if (clip == null) return;
        
        // Garante que os valores estão nos limites corretos
        volume = Math.max(0.0f, Math.min(1.0f, sfxVolume * volume));
        pan = Math.max(-1.0f, Math.min(1.0f, pan));
        
        // Define o volume (MASTER_GAIN)
        try {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume == 0.0 ? 0.0001 : volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        } catch (IllegalArgumentException e) { /* Ignora se o controle não existir */ }

        // Define o balanço (PAN)
        try {
            FloatControl panControl = (FloatControl) clip.getControl(FloatControl.Type.PAN);
            panControl.setValue(pan);
        } catch (IllegalArgumentException e) { /* Ignora se o controle não existir */ }
    }
    
    /**
     * Define o volume mestre para um canal de áudio específico.
     * @param channel O canal a ser modificado.
     * @param volume O novo volume (0.0 a 1.0).
     */
    public static void setChannelVolume(SoundChannel channel, float volume) {
        volume = Math.max(0.0f, Math.min(1.0f, volume));
        channelVolumes.put(channel, volume);

        // Se estivermos ajustando o volume da música, atualiza a música que está tocando
        if (channel == SoundChannel.MUSIC && currentMusic != null) {
            setClipControls(currentMusic, volume, 0.0f);
        }
    }

    /**
     * Obtém o volume atual de um canal.
     */
    public static float getChannelVolume(SoundChannel channel) {
        return channelVolumes.getOrDefault(channel, 1.0f);
    }


    public static void setMusicVolume(float volume) {
        musicVolume = Math.max(0.0f, Math.min(1.0f, volume));
        setClipVolume(currentMusic, musicVolume);
    }

    public static void setSfxVolume(float volume) {
        sfxVolume = Math.max(0.0f, Math.min(1.0f, volume));
    }
    
    public static float getMusicVolume() { return musicVolume; }
    public static float getSfxVolume() { return sfxVolume; }
}