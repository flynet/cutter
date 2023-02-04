import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.NullOutput;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;

import java.io.File;
import java.nio.file.FileSystems;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class Splinter
{

    void split(String videoDir, String videoTmpDir, String filename, long outputDuration)
    {
        long inputDuration;

        // Get input duration
        final AtomicLong durationMillis = new AtomicLong();

        FFmpeg.atPath ( Config.pathToFFmpeg )
        .addInput(UrlInput.fromUrl( videoDir + filename + Config.extension ))
        .addOutput(new NullOutput())
                .setProgressListener(
                        progress -> durationMillis.set(progress.getTimeMillis())
                )
        .execute();

        inputDuration = durationMillis.get();

        System.out.println ( "\n\nSplitting : " + filename + " duration: " + inputDuration + " milliseconds");

        // Split Video
        int nVideoOut = (int) Math.ceil( 1.0 * inputDuration / outputDuration );

        System.out.println("Output video count: " + nVideoOut);

        File splitFileDirPath = new File( videoTmpDir + File.separator + filename + File.separator );
        if (!splitFileDirPath.exists()) {
            if(!splitFileDirPath.mkdirs())
                throw new Error("Can not create folders");
            System.out.println("Directory Created -> "+ splitFileDirPath.getAbsolutePath());
        }


        long currPoint = 0;

        for(int n=0; n < nVideoOut; n++)
        {
            long remaining = inputDuration - ( outputDuration * n );

            long currOutputDuration = Math.min(remaining, outputDuration);

            String videoPartSourceName = videoDir + filename + Config.extension;
            String videoPartName = splitFileDirPath.getPath() + File.separator + Config.tempPattern + n + Config.extension;
            System.out.println( videoPartName + " : " + currPoint + " / " + currOutputDuration);

            FFmpeg.atPath(Config.pathToFFmpeg)
            .addInput(
                    UrlInput.fromUrl( videoPartSourceName )
                    .setPosition(currPoint, TimeUnit.MILLISECONDS)
                    .setDuration(currOutputDuration, TimeUnit.MILLISECONDS)
                    )
            .addOutput(
                    UrlOutput.toPath(FileSystems.getDefault().getPath( videoPartName ))
                    .setPosition(0, TimeUnit.MILLISECONDS)
                     .setFrameRate(Config.freamPerSeconds)
                     .setFrameSize ( Config.aspectWidth, Config.aspectHeight )
                    )
                    .addArguments("-aspect", Config.aspectWidth + ":" +Config.aspectHeight)
                    .addArguments("-b", Config.bitRate + "k")
                    .addArguments("-maxrate", Config.bitRate + "k")
                    .addArguments("-minrate", Config.bitRate + "k")
                    .addArguments("-bufsize", Config.bitRate + "k")
            .setOverwriteOutput(true)
            .execute();


            currPoint += outputDuration;
        }

        System.out.println ( "Splitting Completed !\n\n" );
    }
}