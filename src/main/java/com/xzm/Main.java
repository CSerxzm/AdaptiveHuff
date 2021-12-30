package com.xzm;

import com.xzm.untils.ParmUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.*;
import java.util.Map;

/**
 * @author xiangzhimin
 * @Description
 * @create 2021-12-30 10:45
 */
public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    private AdaptiveHuff adaptiveHuff = null;

    /**
     *
     * @param srcpath
     * @param distpath
     */
    public void code(String type, String srcpath, String distpath){
        adaptiveHuff = new AdaptiveHuff();
        File srcFile=new File(srcpath);
        File distFile=new File(distpath);
        if(!srcFile.exists()){
            logger.error("File does not exist.");
            return;
        }
        if(!distFile.exists()){
            try {
                distFile.createNewFile();
            } catch (IOException e) {
                logger.error("File creation error.");
            }
        }
        try {
            FileReader fileReader = new FileReader(srcFile);
            FileWriter fileWriter = new FileWriter(distFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            String str, output;
            while ((str = bufferedReader.readLine()) != null) {
                if(type.equals("encode")){
                    output = adaptiveHuff.encode(str);
                }else{
                    output = adaptiveHuff.decode(str);
                }
                bufferedWriter.write(output);
            }
            bufferedReader.close();
            bufferedWriter.close();
            fileReader.close();
            fileWriter.close();
        }catch (IOException e) {
            logger.error("File does not exist.");
        }

    }

    // java -jar huff.jar -type=encode/decode -srcpath=input.txt -distpath=dist.txt
    public static void main(String[] args) throws Exception {
        Map<String, String> parm = ParmUtil.getParm(args);
        String srcpath = parm.get("srcpath");
        String distpath = parm.get("distpath");
        String type = parm.get("type");
        if(StringUtils.isEmpty(srcpath) || StringUtils.isEmpty(distpath) || StringUtils.isEmpty(type) || (!"decode".equals(type) && !"encode".equals(type))){
            logger.error("Program parameter input error.");
            logger.error("Correct example:java -jar huff.jar -type=encode/decode -srcpath=input.txt -distpath=dist.txt");
            return;
        }
        Main main = new Main();
        main.code(type,srcpath,distpath);
    }

}
