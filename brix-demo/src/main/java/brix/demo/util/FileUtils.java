/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package brix.demo.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.wicket.util.io.Streams;

/**
 * Various file utilities
 * 
 * @author igor.vaynberg
 * 
 */
public class FileUtils
{
    /**
     * Constructor
     */
    private FileUtils()
    {

    }

    /**
     * Generates a temporary file name inside tmp directory
     * 
     * @return
     */
    public static String getDefaultRepositoryFileName()
    {
        String fileName = System.getProperty("java.io.tmpdir");
        if (!fileName.endsWith(File.separator))
        {
            fileName += File.separator;
        }
        fileName += "brix.demo.repository";
        return fileName;
    }
    
    /**
     * Generates a temporary file name inside tmp directory for use as a webapp staging area
     * 
     * @return
     */
    public static String getDefaultWebAppFileName()
    {
        String fileName = System.getProperty("java.io.tmpdir");
        if (!fileName.endsWith(File.separator))
        {
            fileName += File.separator;
        }
        fileName += "brix.demo.webapp";
        return fileName;
    }

    /**
     * {@link File#mkdirs()} that throws runtime exception if it fails
     * 
     * @param file
     */
    public static void mkdirs(File file)
    {
        if (!file.exists())
        {
            if (!file.mkdirs())
            {
                throw new RuntimeException("Could not create directory: " + file.getAbsolutePath());
            }
        }
    }

    /**
     * Copies a resource from classpath to a {@link File}
     * 
     * @param source
     *            classpath to resource
     * @param destination
     *            destination file
     */
    public static void copyClassResourceToFile(String source, File destination)
    {
        final InputStream in = FileUtils.class.getResourceAsStream(source);
        if (in == null)
        {
            throw new RuntimeException("Class resource: " + source + " does not exist");
        }

        try
        {
            final FileOutputStream fos = new FileOutputStream(destination);
            Streams.copy(in, fos);
            fos.close();
            in.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not copy class resource: " + source +
                " to destination: " + destination.getAbsolutePath());
        }
    }
}
