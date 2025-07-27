package com.JDStudio.Engine.Sound;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class Sound {

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
    private static float musicVolume = 1.0f;
    private static float sfxVolume = 1.0f;

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

    public static void play(String path) {
        play(path, 1.0f);
    }

    public static void play(String path, float volumeScale) {
        AudioData audioData = loadAudioData(path);
        if (audioData == null) return;

        try {
            Clip clip = AudioSystem.getClip();
            
            InputStream inputStream = new ByteArrayInputStream(audioData.data);
            AudioInputStream audioStream = new AudioInputStream(inputStream, audioData.format, audioData.data.length / audioData.format.getFrameSize());
            
            clip.open(audioStream);
            
            float finalVolume = sfxVolume * volumeScale;
            setClipVolume(clip, finalVolume);
            
            clip.start();

        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void loop(String path) {
        stopMusic();
        
        if (musicClipCache.containsKey(path)) {
            currentMusic = musicClipCache.get(path);
        } else {
            try (InputStream rawIs = Sound.class.getResourceAsStream(path)) {
                if (rawIs == null) throw new IOException("Arquivo de música não encontrado: " + path);
                
                // A CORREÇÃO PRINCIPAL ESTÁ AQUI
                InputStream bufferedIs = new BufferedInputStream(rawIs);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIs);
                
                Clip musicClip = AudioSystem.getClip();
                musicClip.open(audioStream);
                musicClipCache.put(path, musicClip);
                currentMusic = musicClip;
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                // A mensagem de erro original apontava para esta linha
                System.err.println("Erro ao carregar a música de: " + path);
                e.printStackTrace();
                return;
            }
        }
        
        setClipVolume(currentMusic, musicVolume);
        currentMusic.setFramePosition(0);
        currentMusic.loop(Clip.LOOP_CONTINUOUSLY);
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