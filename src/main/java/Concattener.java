import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;

import java.io.File;
import java.nio.file.FileSystems;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Concattener
    {

        void split(int count, List<File> fnames) {

            FFmpeg ffmpeg = FFmpeg.atPath(Config.pathToFFmpeg);

            int ttlParts = 0;
            System.out.println("\n\nConcattener Started" );
            System.out.println("Parts for final Clip :" );
            for (int j = 0; j <= count; j++) {
                for (File fname : fnames)
                {
                    var path = fname.getPath();
                    String partPath = path + "\\" + Config.tempPattern + j + Config.extension;

                    File partPathFile = new File(partPath);
                    if (partPathFile.exists())
                    {
                        ffmpeg
                                .addInput(
                                        UrlInput.fromUrl(partPath)
                                                .setDuration(Config.outputDurationMillisec, TimeUnit.MILLISECONDS)
                                );
                        ttlParts++;
                        System.out.println("#" + ttlParts + " @ path = " + partPath);
                    }
                }
            }

            StringBuilder concatBuilder = new StringBuilder("\"");
            for (int i = 0; i < ttlParts; ++i)
            {
                      concatBuilder
                            .append("[")
                            .append(i)
                            .append(":v:0]");
                    concatBuilder
                            .append("[")
                            .append(i)
                            .append(":a:0]");
            }
            concatBuilder.append("concat=n=")
                    .append ( ttlParts )
                    .append(":v=1:a=1[outv][outa]")
                    .append("\"");

            String filter_complex = concatBuilder.toString();
            System.out.println("filter_complex = " + filter_complex);
            ffmpeg.addArguments("-filter_complex", filter_complex );
            ffmpeg.addArguments("-map", "\"[outv]\"");
            ffmpeg.addArguments("-map", "\"[outa]\"");
            ffmpeg.addArguments("-loglevel", "debug");
            ffmpeg.addArguments("-v", "verbose");

            //Destination folder to save.
            File videoOutDirPath = new File( Config.videoOutDir );
            if (!videoOutDirPath.exists()) {
                if(!videoOutDirPath.mkdirs())
                    throw new Error("Can not create folders");
                System.out.println("Directory Created -> "+ videoOutDirPath.getAbsolutePath());
            }

            String outName = videoOutDirPath.getAbsolutePath() + File.separator + Config.outFilename + Config.extension;
            System.out.println("outName = " + outName);
            ffmpeg.addOutput(
                            UrlOutput.toPath(FileSystems.getDefault().getPath( outName ))
                    )
                    .setOverwriteOutput(true)
                    .setLogLevel(LogLevel.DEBUG)
                    .execute();
        }

    }