
package com.jdstudio.engine.Sound;

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

import com.jdstudio.engine.Engine;

/**
 * A final utility class for handling all sound-related operations in the engine.
 * <p>
 * This class provides a centralized system for playing, looping, and managing sound effects and music.
 * Key features include:
 * <ul>
 *   <li>Caching of sound data to prevent repeated disk I/O.</li>
 *   <li>Multiple sound channels (MUSIC, SFX, UI) with independent volume control.</li>
 *   <li>Spatial (3D) sound for sound effects, with volume attenuation and panning based on distance.</li>
 *   <li>A clear separation between one-shot sound effects and looping music.</li>
 * </ul>
 * This is a static utility class and cannot be instantiated.
 */
public final class Sound {

    /**
     * Defines the audio channels for categorizing sounds.
     * This allows for independent volume control over different types of audio.
     */
    public enum SoundChannel {
        /** For background music and soundtracks. */
        MUSIC,
        /** For in-game sound effects like actions, impacts, etc. */
        SFX,
        /** For user interface sounds like button clicks and notifications. */
        UI
    }

    /**
     * A private inner class to hold pre-loaded audio data in memory.
     * This includes the raw byte data and the audio format, necessary for creating new clips quickly.
     */
    private static class AudioData {
        final byte[] data;
        final AudioFormat format;

        AudioData(byte[] data, AudioFormat format) {
            this.data = data;
            this.format = format;
        }
    }

    private static final Map<String, AudioData> sfxDataCache = new HashMap<>();
    private static final Map<String, Clip> musicClipCache = new HashMap<>();
    
    private static Clip currentMusic;
    
    private static final Map<SoundChannel, Float> channelVolumes = new EnumMap<>(SoundChannel.class);
    
    // Static initializer to set default volumes for each channel.
    static {
        channelVolumes.put(SoundChannel.MUSIC, 1.0f);
        channelVolumes.put(SoundChannel.SFX, 1.0f);
        channelVolumes.put(SoundChannel.UI, 1.0f);
    }

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Sound() {}

    /**
     * Loads audio data from a given path and caches it.
     * If the data is already in the cache, it returns the cached version.
     * Otherwise, it loads the audio file, converts it to a byte array, and stores it in the cache.
     *
     * @param path The resource path to the audio file.
     * @return An {@link AudioData} object containing the sound's bytes and format, or null if loading fails.
     */
    private static AudioData loadAudioData(String path) {
        if (sfxDataCache.containsKey(path)) {
            return sfxDataCache.get(path);
        }

        try (InputStream rawIs = Sound.class.getResourceAsStream(path)) {
            if (rawIs == null) throw new IOException("Sound file not found: " + path);
            
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
            System.err.println("Error loading sound data from: " + path);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Plays a sound effect on a specific channel with default volume.
     *
     * @param path    The resource path to the sound file.
     * @param channel The {@link SoundChannel} to play the sound on.
     */
    public static void play(String path, SoundChannel channel) {
        play(path, channel, 1.0f, false, 0, 0);
    }
    
    /**
     * Plays a sound effect with an individual volume scale.
     * The final volume is a product of the channel's volume and this scale.
     *
     * @param path        The resource path to the sound file.
     * @param channel     The {@link SoundChannel} to play the sound on.
     * @param volumeScale A multiplier for the sound's volume (0.0 to 1.0).
     */
    public static void play(String path, SoundChannel channel, float volumeScale) {
        play(path, channel, volumeScale, false, 0, 0);
    }

    /**
     * Plays a spatial sound effect that originates from a specific point in the world.
     * Its volume and panning will be adjusted based on the listener's position (camera center).
     *
     * @param path     The resource path to the sound file.
     * @param channel  The {@link SoundChannel} to play the sound on (usually SFX).
     * @param emitterX The world X-coordinate of the sound's origin.
     * @param emitterY The world Y-coordinate of the sound's origin.
     */
    public static void play(String path, SoundChannel channel, int emitterX, int emitterY) {
        play(path, channel, 1.0f, true, emitterX, emitterY);
    }
    
    /**
     * The master private method for playing any sound.
     * It handles loading, volume/pan calculation, and playback.
     *
     * @param path        The resource path to the sound file.
     * @param channel     The channel to play on.
     * @param volumeScale An individual volume multiplier.
     * @param isSpatial   Whether to calculate spatial audio properties.
     * @param emitterX    World X-coordinate (if spatial).
     * @param emitterY    World Y-coordinate (if spatial).
     */
     private static void play(String path, SoundChannel channel, float volumeScale, boolean isSpatial, int emitterX, int emitterY) {
         AudioData audioData = loadAudioData(path);
         if (audioData == null) return;

         float finalVolume = channelVolumes.get(channel) * volumeScale;
         float pan = 0.0f;

         if (isSpatial) {
             int listenerX = Engine.camera.getX() + Engine.getWIDTH() / 2;
             
             double distance = Math.abs(emitterX - listenerX);
             double maxHearingDistance = 400.0; // Increased hearing distance
             
             float spatialVolume = (distance > maxHearingDistance) ? 0.0f : (float)(1.0 - (distance / maxHearingDistance));
             finalVolume *= spatialVolume;

             double horizontalOffset = emitterX - listenerX;
             double panWidth = Engine.getWIDTH(); // Panning is full screen width
             pan = (float) Math.max(-1.0, Math.min(1.0, horizontalOffset / panWidth));
         }
         
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
    
    /**
     * Plays a music track in a continuous loop. If another track is playing, it is stopped first.
     * The music is always played on the {@link SoundChannel#MUSIC} channel.
     *
     * @param path The resource path to the music file.
     */
    public static void loop(String path) {
        stopMusic();
        
        if (musicClipCache.containsKey(path)) {
            currentMusic = musicClipCache.get(path);
        } else {
            try (InputStream rawIs = Sound.class.getResourceAsStream(path)) {
                if (rawIs == null) throw new IOException("Music file not found: " + path);
                
                InputStream bufferedIs = new BufferedInputStream(rawIs);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIs);
                
                Clip musicClip = AudioSystem.getClip();
                musicClip.open(audioStream);
                musicClipCache.put(path, musicClip);
                currentMusic = musicClip;
            } catch (Exception e) {
                System.err.println("Error loading music from: " + path);
                e.printStackTrace();
                return;
            }
        }
        
        setClipControls(currentMusic, channelVolumes.get(SoundChannel.MUSIC), 0.0f);
        
        currentMusic.setFramePosition(0);
        currentMusic.loop(Clip.LOOP_CONTINUOUSLY);
    }
    
    /**
     * Stops the currently playing music track, if any.
     */
    public static void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    /**
     * A private helper to set the volume (gain) and pan (balance) of a given clip.
     *
     * @param clip   The {@link Clip} to control.
     * @param volume The target volume (0.0 to 1.0).
     * @param pan    The target pan (-1.0 for left, 0.0 for center, 1.0 for right).
     */
    private static void setClipControls(Clip clip, float volume, float pan) {
        if (clip == null) return;
        
        volume = Math.max(0.0f, Math.min(1.0f, volume));
        pan = Math.max(-1.0f, Math.min(1.0f, pan));
        
        try {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume == 0.0 ? 0.0001 : volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        } catch (IllegalArgumentException e) { /* Ignore if control is not supported */ }

        try {
            FloatControl panControl = (FloatControl) clip.getControl(FloatControl.Type.PAN);
            panControl.setValue(pan);
        } catch (IllegalArgumentException e) { /* Ignore if control is not supported */ }
    }
    
    /**
     * Sets the master volume for a specific audio channel.
     *
     * @param channel The {@link SoundChannel} to modify.
     * @param volume  The new volume, from 0.0 (silent) to 1.0 (full).
     */
    public static void setChannelVolume(SoundChannel channel, float volume) {
        volume = Math.max(0.0f, Math.min(1.0f, volume));
        channelVolumes.put(channel, volume);

        if (channel == SoundChannel.MUSIC && currentMusic != null) {
            setClipControls(currentMusic, volume, 0.0f);
        }
    }

    /**
     * Gets the current master volume of a specific audio channel.
     *
     * @param channel The {@link SoundChannel} to query.
     * @return The current volume of the channel (0.0 to 1.0).
     */
    public static float getChannelVolume(SoundChannel channel) {
        return channelVolumes.getOrDefault(channel, 1.0f);
    }


}