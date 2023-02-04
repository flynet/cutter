import java.nio.file.FileSystems;
import java.nio.file.Path;

public class Config {

    public static final Path pathToFFmpeg    = FileSystems.getDefault().getPath(".\\bin");
    public static final String  videoDir        = "C:\\tmp\\test\\";
    public static final String  videoTmpDir     = videoDir + "tmp";
    public static final String  videoOutDir     = videoTmpDir + "out";
    public static final long outputDurationMillisec = 4000;
    public static final int aspectHeight = 1080;
    public static final int aspectWidth = 1920;
    public static final int freamPerSeconds = 60;
    public static final int bitRate = 15000;

    public static final String extension = ".mp4";
    public static final String outFilename = "finalClip";
    public static final String tempPattern = "part_";
    public static boolean cleanUpTmp = true;
    public static boolean useAllParts = true;
}
