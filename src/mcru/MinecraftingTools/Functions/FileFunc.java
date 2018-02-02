package mcru.MinecraftingTools.Functions;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import mcru.MinecraftingTools.MyApplication;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.CodeSource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileFunc
{
    private static Logger logger = Logger.getLogger(MyApplication.class.getName());
    
    /**
     * сгенерировать уникальное имя файла
     * @param prefix  элемент в выражении имени файла [prefix][уникальное имя][postfix]
     * @param postfix элемент в выражении имени файла [prefix][уникальное имя][postfix]
     */
    public static String generateUnicName(String prefix, String postfix)
    {
        String fileName = "";
        File file;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
        do
        {
            String dt = simpleDateFormat.format(calendar.getTime());
            fileName = prefix + dt + postfix;
            file = new File(fileName);
            // если файл уже есть, то добавляем к имени милисекунды
            simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HHmmssSSS");
        }
        while (file.exists());
        return fileName;
    }
    
    /**
     * Прочитать файл в String
     * @param path        путь до файла
     * @param charsetName кодировка
     */
    public static String fileToString(String path, String charsetName)
    {
        File file = new File(path);
        if (!file.exists())
        {
            logger.log(Level.SEVERE, String.format("Файл не найден: \"%1$s\"", path));
            return null;
        }
        
        try (FileInputStream fis = new FileInputStream(path);
             InputStreamReader isr = new InputStreamReader(fis, Charset.forName(charsetName));
             BufferedReader br = new BufferedReader(isr))
        {
            StringBuilder sb = new StringBuilder();
            String line;
            
            while ((line = br.readLine()) != null)
            {
                if (sb.length() > 0)
                    sb.append("\n").append(line);
                else
                    sb.append(line);
            }
            
            return sb.toString();
        }
        catch (FileNotFoundException e)
        {
            logger.log(Level.SEVERE, String.format("Проблемы при чтении файла \"%1$s\", файл не найден", path), e);
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE,
                       String.format("Проблемы при чтении файла \"%1$s\" из-за ошибки ввода-вывода", path),
                       e);
        }
        catch (SecurityException e)
        {
            logger.log(Level.SEVERE, "Файл прочитать не удалось. Недостаточно прав", e);
        }
        
        return null;
    }
    
    /**
     * определяем путь до рабочего каталога приложения
     */
    public static String getJarDir(CodeSource cs)
    {
        try
        {
            String path = new File(cs.getLocation().toURI().normalize().getPath()).getCanonicalPath();
            
            if (path.endsWith(File.separator))
                path = path.substring(0, path.length() - File.separator.length());
            
            if (path.endsWith(".jar"))
                path = path.substring(0, path.lastIndexOf(File.separator));
            
            logger.log(Level.INFO, String.format("Каталог запуска: \"%1$s\"", path));
            return path;
        }
        catch (URISyntaxException e)
        {
            logger.log(Level.SEVERE, "Каталог запуска определить не удалось. Синтаксическая ошибка", e);
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, "Каталог запуска определить не удалось. Ошибка ввода-вывода", e);
        }
        catch (SecurityException e)
        {
            logger.log(Level.SEVERE, "Каталог запуска определить не удалось. Недостаточно прав", e);
        }
        
        return "";
    }
    
    /**
     * Запись текста в файл
     * @param fname    путь до файла
     * @param text     текст для записи
     * @param encoding кодировка
     * @param append   режим записи (true - дописывать в конец файла)
     */
    public static boolean saveTextToFile(String fname, String text, String encoding, boolean append)
    {
        try (FileOutputStream fos = new FileOutputStream(fname, append);
             OutputStreamWriter osw = new OutputStreamWriter(fos, encoding);
             PrintWriter out = new PrintWriter(osw))
        {
            out.println(text);
            return true;
        }
        catch (FileNotFoundException e)
        {
            logger.log(Level.SEVERE, "Ошибка записи в файл. Файл не найден", e);
        }
        catch (UnsupportedEncodingException e)
        {
            logger.log(Level.SEVERE,
                       String.format("Ошибка записи в файл. Кодировка \"%1$s\" не поддерживается", encoding),
                       e);
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, "Ошибка записи в файл. Ошибка ввода-вывода", e);
        }
        catch (SecurityException e)
        {
            logger.log(Level.SEVERE, "Ошибка записи в файл. Недостаточно прав", e);
        }
        
        return false;
    }
    
    /**
     * выбрать файл используя  {@link FileChooser}
     * @param parent           окно-владелец
     * @param InitialDirectory стартовая директория
     * @param filters          фильтр файлов
     */
    public static String showOpenFileDialog(
            Window parent, String InitialDirectory, FileChooser.ExtensionFilter... filters)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(filters);
        
        File initialDirectory = new File(InitialDirectory);
        if (initialDirectory.exists())
            fileChooser.setInitialDirectory(initialDirectory);
        else
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        
        fileChooser.setTitle("Выбрать файл");
        
        File file = fileChooser.showOpenDialog(parent);
        if (file == null || !file.exists())
            return "";
        
        String value = file.getAbsolutePath();
        String ext = fileChooser.getSelectedExtensionFilter().getExtensions().get(0).toLowerCase();
        ext = ext.substring(ext.lastIndexOf("."), ext.length());
        
        if (!ext.contains("*"))
            value = file.getAbsolutePath().toLowerCase().endsWith(ext) ? value : value + ext;
        
        return value;
    }
    
    /**
     * выбрать файл используя  {@link FileChooser}
     * @param parent           окно-владелец
     * @param InitialDirectory стартовая директория
     * @param filters          фильтр файлов
     */
    public static String showSaveFileDialog(
            Window parent, String InitialDirectory, FileChooser.ExtensionFilter... filters)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(filters);
        
        File initialDirectory = new File(InitialDirectory);
        
        if (initialDirectory.exists())
            fileChooser.setInitialDirectory(initialDirectory);
        else
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        
        fileChooser.setTitle("Сохранить документ");
        File file = fileChooser.showSaveDialog(parent);
        
        String value = file.getAbsolutePath();
        String ext = fileChooser.getSelectedExtensionFilter().getExtensions().get(0).toLowerCase();
        ext = ext.substring(ext.lastIndexOf("."), ext.length());
        
        if (!ext.contains("*"))
            value = file.getAbsolutePath().toLowerCase().endsWith(ext) ? value : value + ext;
        
        return value;
    }
}
