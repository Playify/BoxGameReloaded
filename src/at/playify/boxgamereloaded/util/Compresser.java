package at.playify.boxgamereloaded.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import at.playify.boxgamereloaded.interfaces.Game;

//Zukünftig um Leveltext zu komprimieren
public class Compresser {
    public static String compress(String s) {
        return s;
        //return new String(Base64.encode(Compressor.compress(s),Base64.DEFAULT));
    }

    public static String decompress(String s) {
        return s;
    }

    private static class Compressor {

        private static byte[] compress(byte[] bytesToCompress) {
            Deflater deflater=new Deflater();
            deflater.setInput(bytesToCompress);
            deflater.finish();

            byte[] bytesCompressed=new byte[Short.MAX_VALUE];

            int numberOfBytesAfterCompression=deflater.deflate(bytesCompressed);

            byte[] returnValues=new byte[numberOfBytesAfterCompression];

            System.arraycopy
                    (
                            bytesCompressed,
                            0,
                            returnValues,
                            0,
                            numberOfBytesAfterCompression
                    );

            return returnValues;
        }

        private static byte[] compress(String stringToCompress) {
            byte[] returnValues=null;

            try {

                returnValues=Compressor.compress
                        (
                                stringToCompress.getBytes("UTF-8")
                        );
            } catch (UnsupportedEncodingException uee) {
                Game.report(uee);
            }

            return returnValues;
        }

        private static byte[] decompress(byte[] bytesToDecompress) {
            byte[] returnValues=null;

            Inflater inflater=new Inflater();

            int numberOfBytesToDecompress=bytesToDecompress.length;

            inflater.setInput
                    (
                            bytesToDecompress,
                            0,
                            numberOfBytesToDecompress
                    );

            int bufferSizeInBytes=numberOfBytesToDecompress;

            int numberOfBytesDecompressedSoFar=0;
            List<Byte> bytesDecompressedSoFar=new ArrayList<Byte>();

            try {
                while (inflater.needsInput()==false) {
                    byte[] bytesDecompressedBuffer=new byte[bufferSizeInBytes];

                    int numberOfBytesDecompressedThisTime=inflater.inflate
                            (
                                    bytesDecompressedBuffer
                            );

                    numberOfBytesDecompressedSoFar+=numberOfBytesDecompressedThisTime;

                    for(int b=0; b<numberOfBytesDecompressedThisTime; b++) {
                        bytesDecompressedSoFar.add(bytesDecompressedBuffer[b]);
                    }
                }

                returnValues=new byte[bytesDecompressedSoFar.size()];
                for(int b=0; b<returnValues.length; b++) {
                    returnValues[b]=(byte) (bytesDecompressedSoFar.get(b));
                }

            } catch (DataFormatException dfe) {
                Game.report(dfe);
            }

            inflater.end();

            return returnValues;
        }

        private static String decompressToString(byte[] bytesToDecompress) {
            byte[] bytesDecompressed=Compressor.decompress
                    (
                            bytesToDecompress
                    );

            String returnValue=null;

            try {
                returnValue=new String
                        (
                                bytesDecompressed,
                                0,
                                bytesDecompressed.length,
                                "UTF-8"
                        );
            } catch (UnsupportedEncodingException uee) {
                Game.report(uee);
            }

            return returnValue;
        }
    }
}
