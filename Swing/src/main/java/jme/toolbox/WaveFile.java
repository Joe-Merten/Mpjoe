package jme.toolbox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TODO: Klassenbeschreibung
 *
 * @author Angelo Balke
 */
public class WaveFile {

    static final Logger logger = LogManager.getLogger(WaveFile.class);

    private Chunk chunk = null;
    private List<Chunk> chunks = new ArrayList<Chunk>();

    public WaveFile(File file) throws FileNotFoundException, IOException {
        if (!file.exists())
            throw new FileNotFoundException("Die Datei " + file.getAbsolutePath() + " ist entweder nicht vorhanden");
        else if (!file.canRead())
            throw new FileNotFoundException("Die Datei " + file.getAbsolutePath() + " kann nicht gelesen werden");
        else
            parseFileToWave(file);
    }

    private String getFourCC(byte[] data, int index) {
        return new String((char)data[index+0] + "" +
                          (char)data[index+1] + "" +
                          (char)data[index+2] + "" +
                          (char)data[index+3]);
    }

    // int reicht hier als Rückgabewert nicht aus, da der im Riff enthaltene 32 Bit Wert unsigned ist
    private long getUInt32(byte[] data, int index) {
        long ret = 0;
        ret += data[index+3] & 0xFF; ret <<= 8;
        ret += data[index+2] & 0xFF; ret <<= 8;
        ret += data[index+1] & 0xFF; ret <<= 8;
        ret += data[index+0] & 0xFF;
        return ret;
    }

    private void parseFileToWave(File file) throws FileNotFoundException, IOException {
        byte[] filedata = null;
        long chunkDataSize = -1;

        try {
            if (logger.isTraceEnabled()) {
                Timeter t = new Timeter();
                filedata = Files.readAllBytes(file.toPath());  // Lesegeschwindigkeit auf dem Karo-Board ist ca. 30MB/s (getestet mit 'nem ca. 900kB Wav)
                logger.trace("Read " + file.getPath() + " needs " + t);
            } else {
                filedata = Files.readAllBytes(file.toPath());  // Lesegeschwindigkeit auf dem Karo-Board ist ca. 30MB/s (getestet mit 'nem ca. 900kB Wav)
            }
        } catch (IOException e) {
            logger.error("Die Datei " + file.getAbsolutePath() + " konnte nicht eingelesen werden", e);
        }

        // Checke auf RIFF und prüfe auf Gültigkeit
        if (filedata.length <= 16 || !getFourCC(filedata, 0).equals("RIFF"))
            throw new IOException("No RIFF Header detected!");

        chunkDataSize = getUInt32(filedata, 4);
        if ((filedata.length - 8) != chunkDataSize)
            throw new IOException("File is corrupt (" + "chunkDataSize=" + chunkDataSize+ " fileSize-8=" + (filedata.length-8) + ")");
//        logger.trace("Chunk Data Size: " + chunkDataSize);

        String riffType = getFourCC(filedata, 8);
        if (!riffType.equals("WAVE"))
            throw new IOException("No correct RIFF Type detected! Type = " + riffType);

        // Prüfung erfolgreich, erstelle "Haupt"-Chunk
        chunk = new Chunk("WAVE", chunkDataSize, Arrays.copyOfRange(filedata, 12, filedata.length));

        // Suche alle "Sub-Chunks"
        int zeiger = 0;
        while (chunk.getDataSize() >= zeiger+8) {
            byte[] data = chunk.getData();
            String subChunkName = getFourCC(data, zeiger);
            long subChunkDataSize = getUInt32(data, zeiger + 4);
            // logger.trace("Found Chunk ID/size:  " + subChunkName + "/" + subChunkDataSize + " Byte at index " + zeiger);
            byte[] subChunkData = Arrays.copyOfRange(data, zeiger+8, zeiger+8+(int)subChunkDataSize); // Kopieren der vielen Daten find' ich hier ja eher unschön, ist jetzt aber erstmal so
            chunks.add(new Chunk(subChunkName, subChunkDataSize, subChunkData));
            zeiger = zeiger + 8 + (int)subChunkDataSize;
        }
    }

    public byte[] getRawPcmData() {
        if (chunks != null) {
            for (Chunk chunk : chunks) {
                if (chunk.getId().contentEquals("data")) {
                    return chunk.getData();
                }
            }
        }
        return null;
    }

    /**
     * Wave-Dateien bestehen aus Chunks. Ein Basis Wave-Datei Layout ist wie folgt aufgebaut.
     *
     * Das RIFF-Chunk, mit der ID: RIFF, der Chunk-Data-Size: filesize-8, dem RIFF-Type: WAVE und dem WAVE-Chunk, der alle anderen Daten (auch raw-pcm-Daten)
     * enthält.
     *
     * siehe: http://www.sonicspot.com/guide/wavefiles.html
     *
     * @author Angelo Balke
     *
     */
    public class Chunk {
        private String id;
        private long dataSize;
        private byte[] data;

        public Chunk(String id, long dataSize, byte[] data){
            this.id = id;
            this.dataSize = dataSize;
            this.data = data;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public long getDataSize() {
            return dataSize;
        }

        public void setDataSize(long dataSize) {
            this.dataSize = dataSize;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }

        @Override public String toString() {
            return new String("ID/size:  \"" + id + "\"/" + dataSize + " Byte");
        }
    }

}
