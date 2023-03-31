package pers.harry_deng.yawnsuite.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    public static String fileDir = new String("D:\\FileStorage\\DevelopFile\\CodeField\\CodeField_JAVAFx\\YawnSuite\\YawnSuite\\src\\main\\java\\pers\\harry_deng\\yawnsuite\\dataStorage\\");

    public static List<Object> getData(String fileName, Class<?> C) {
        List<Object> ret = new ArrayList<>();
        String fd = fileDir + fileName;
        try {
            BufferedReader br = new BufferedReader(new FileReader(fd));
            String line;
            while ((line = br.readLine()) != null) {
                line = "[" + line + "]";
                Object object = pers.harry_deng.yawnsuite.utils.JsonUtil.toObject(line, C);
                ret.add(object);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static void writeData(String data, String fileName, boolean mode) throws IOException {
        String fd = fileDir + fileName;
        BufferedWriter bw = new BufferedWriter(new FileWriter(fd, mode));
        bw.write(data);
        bw.newLine();
        bw.flush();
        bw.close();
    }

    public static List<String> readFile(String fileName) {
        List<String> ret = new ArrayList<>();
        String fd = fileDir + fileName;
        int num = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(fd));
            String line;
            while ((line = br.readLine()) != null) {
                ret.add(line+"\n" );
                num++;
                if (num > 30) {
                    break;
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static String ListToStr(ArrayList<String> list) {
        StringBuilder sum = new StringBuilder();
        for (String s : list) {
            sum.append(s).append("\n");
        }
        return sum.toString();
    }

    public static void clearFile(String fileName) {
        File file = new File(fileDir + fileName);
        if (file.exists()) file.delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}