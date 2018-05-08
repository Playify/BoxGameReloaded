package at.playify.boxgamereloaded.level.compress;

public class CompressionHandler {
    private Compresser[] arr;
    private Compresser fallback;

    public CompressionHandler() {
        arr=new Compresser[]{new Compresser0()};
        fallback=new CompresserFallback();
    }

    public String compress(LevelData data) {
        Compresser compresser=arr[arr.length-1];
        String version=compresser.version();
        if (version==null) {
            return compresser.compress(data);
        } else {
            return version+"."+compresser.compress(data);
        }
    }

    public LevelData decompress(String s) {
        String version=s.contains(".") ? s.substring(0, s.indexOf('.')) : null;
        if (version==null) {
            return fallback.decompress(s);
        }
        for (Compresser compresser : arr) {
            if (version.equals(compresser.version())) {
                LevelData decompress=null;
                try {
                    decompress=compresser.decompress(s.substring(version.length()+1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (decompress==null) {
                    System.err.println("Error loading Level with Version \""+version+"\" in LevelData: "+s);
                }
                return decompress;
            }
        }
        System.err.println("Error loading Level: Unknown Version: \""+version+"\" in LevelData: "+s);
        return null;
    }

}
