package com.airedale.StationCreation.utils;

import javax.baja.file.BFileSystem;
import javax.baja.file.BIFile;
import javax.baja.file.FilePath;
import javax.baja.naming.BOrd;
import javax.baja.naming.OrdQuery;
import javax.baja.sys.Sys;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains file utility methods for use by Niagara 4 classes.
 *
 * @author Phil Holden
 * @version 4.0
 * @copyright Airedale International Air Conditioning Ltd.
 */
public class FileUtils
{

    /**
     *  Print a String as a line to the specified BIFile.
     */
    public static void printToFile(BIFile biFile, String text, boolean blankLineRequired)
    {
        try
        {
            writeTextToBIFile(biFile, text, blankLineRequired);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * Create a new file.
     */
    public static BIFile createNewFile(BOrd ord)
    {
        try
        {
            OrdQuery[] queries = ord.parse();

            FilePath filePath = (FilePath) queries[queries.length - 1];

            return BFileSystem.INSTANCE.makeFile(filePath);
        } catch (IOException e)
        {
            e.printStackTrace();

            return null;
        }
    }
    /**
     * Write the specified text to the specified BIFile.
     */
    private static void writeTextToBIFile(BIFile biFile, String text, boolean blankLineRequired) throws IOException
    {
        FilePath filePath = biFile.getFilePath();
        File file = BFileSystem.INSTANCE.pathToLocalFile(filePath);

        writeTextToFile(file, text, blankLineRequired);
    }

    /**
     * Write the specified text to the specified file.
     */
    private static void writeTextToFile(File file, String text, boolean blankLineRequired) throws IOException
    {
        try (FileWriter fileWriter = new FileWriter(file, true))
        {
            fileWriter.write(text + System.lineSeparator());

            if (blankLineRequired)
            {
                fileWriter.write(System.lineSeparator());
            }
        }
    }

    /**
     * Print a String as a line to the specified file in the station home directory.
     */
    public static void printToFile(String filename, String textToPrint, boolean blankLineRequired)
    {
        try
        {
            final File file = new File(Sys.getStationHome(), filename);

            final PrintWriter writer = new PrintWriter(new FileOutputStream(file, true));

            writer.println(textToPrint);

            if (blankLineRequired)
            {
                writer.println("");
            }

            writer.close();
        }
        catch (final FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Get the size, in kilobytes, of the specified file in the station home directory.
     */
    public static int getFileSizeInKb(String filename)
    {
        int kilobytes = -1;

        final File file = new File(Sys.getStationHome(), filename);

        if (file.exists())
        {
            double bytes = file.length();
            double kbytes = (bytes / 1024);
            kilobytes = (int) (NumericUtils.round(kbytes, 0));
        }

        return kilobytes;
    }

    /**
     * Get the size, in MB, of the specified file in the station home directory.
     */
    public static int getFileSizeInMb(String filename)
    {
        int result = -1;

        final File file = new File(Sys.getStationHome(), filename);

        if (file.exists())
        {
            double bytes = file.length();
            double mbytes = (bytes / 1024 / 1024);
            result = (int)(NumericUtils.round(mbytes, 0));
        }

        return result;
    }

    /**
     * Delete the specified file from the station home directory, if it exists.
     */
    public static void deleteFileIfExists(String filename)
    {
        final File file = new File(Sys.getStationHome(), filename);

        if (file.exists())
        {
            file.delete();
        }
    }

    /**
     * Read lines, as an ArrayList of Strings, from the specified file Ord.
     */
    public static List<String> readLinesFromFileAsArrayList(BOrd fileOrd)
    {
        ArrayList<String> lines;

        BIFile file = (BIFile) fileOrd.resolve(Sys.getStation()).get();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream())))
        {
            lines = new ArrayList<>();

            String line;

            while ((line = bufferedReader.readLine()) != null)
            {
                lines.add(line);
            }
        }
        catch (final Exception e)
        {
            lines = null;
        }

        return lines;
    }

    public static String readLinesFromFileAsSring(BOrd fileOrd) {
        BIFile file = (BIFile) fileOrd.resolve(Sys.getStation()).get();

        ArrayList lines;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            Throwable var4 = null;

            try {
                lines = new ArrayList();

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    lines.add(line);
                }
            } catch (Throwable var14) {
                var4 = var14;
                throw var14;
            } finally {
                if (bufferedReader != null) {
                    if (var4 != null) {
                        try {
                            bufferedReader.close();
                        } catch (Throwable var13) {
                            var4.addSuppressed(var13);
                        }
                    } else {
                        bufferedReader.close();
                    }
                }

            }
        } catch (Exception var16) {
            lines = null;
        }
        // turn ArrayList into a string
        String result = "";
        if (lines != null) {
            for (Object line : lines) {
                result += line + "\n";
            }
        }
        return result;
    }

}
