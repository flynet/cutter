import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Utils {

    static class Tuple<A, B> {
        public A a;
        public B b;

        public Tuple(A a, B b) {
            this.a = a;
            this.b = b;
        }
    }

    public static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    public static File[] getSourceFiles(String path, String extension) {
        File dir = new File(path);
        return dir.listFiles((dir1, name) -> name.toLowerCase().endsWith(extension));
    }

    public static void splitSourceFiles(File[] lst, String videoDir, String videoTmpDir, String extension, long outputDuration) {
        for (File file : lst)
        {
            String fname = file.getName();
            var idx = fname.lastIndexOf(extension);
            new Splinter().split(videoDir, videoTmpDir, fname.substring(0, idx), outputDuration);
        }
    }

    public static File[] getSplittedFolders(String videoTmpDir, String extension) {
        File tmpdir = new File ( videoTmpDir );
        return tmpdir.listFiles((dir, name) -> dir.isDirectory() || name.toLowerCase().endsWith(extension));
    }

    public static void concatenateResuls(int count, File[] tmplst) {
        var ca = new Concattener();
        ca.split(count, Arrays.asList(tmplst));
    }

    public static Tuple<Integer, Integer> getSplittedFilesMinMax(File[] tmplst, String extension) {
        Map<String, List<File>> sblstall = new HashMap<>();
        for (File file : tmplst)
        {
            File subtmpdir = new File(file.getPath());
            File[] subtmplst = subtmpdir.listFiles((dir, name) -> dir.isDirectory() || name.toLowerCase().endsWith(extension));
            assert subtmplst != null;
            List<File> sblst = List.of(subtmplst);
            sblstall.put(subtmpdir.getName(), sblst);
        }
        var largestCount = 0;
        var smallestCount = 999;
        for (var sbval : sblstall.values()
        ) {
            largestCount = Math.max(sbval.size(), largestCount);
            smallestCount = sbval.size() > 0 && smallestCount > sbval.size() ? sbval.size() : smallestCount;
        }

        return new Tuple<>(smallestCount, largestCount);
    }
}
