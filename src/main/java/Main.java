import java.io.File;
import java.util.Arrays;


public class Main {

    public static void main(String[] args) {

        File[] lst = Utils.getSourceFiles ( Config.videoDir, Config.extension);
        System.out.println("Video parts list = " + Arrays.deepToString(lst));

        Utils.splitSourceFiles ( lst, Config.videoDir, Config.videoTmpDir, Config.extension, Config.outputDurationMillisec);

        File[] tmplst = Utils.getSplittedFolders ( Config.videoTmpDir, Config.extension );
        System.out.println("Video parts sub folders = " + Arrays.deepToString(tmplst));

        var minMaxCount = Utils.getSplittedFilesMinMax ( tmplst, Config.extension);
        System.out.println("largestCount = " + minMaxCount.b);
        System.out.println("smallestCount = " + minMaxCount.a);

        if ( Config.useAllParts ) {
            Utils.concatenateResuls(minMaxCount.b, tmplst);
        } else {
            Utils.concatenateResuls(minMaxCount.a, tmplst);
        }

        if ( Config.cleanUpTmp ) {
            if(!Utils.deleteDirectory ( new File ( Config.videoTmpDir )))
                throw new Error("Can not delete folders");
        }

    }

}