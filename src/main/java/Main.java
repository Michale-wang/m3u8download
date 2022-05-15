import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import org.junit.jupiter.api.Test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Author Michale-
 * @Date 2022/5/15 18:49
 * @Version 1.0
 */
public class Main {

    String m3U8Path = "classpath:1652183215-1652188882.m3u8";
    String videoPath = System.getProperty("user.dir")+"/video/";
    String urlPrefix = "https://voipfindervod2.wxqcloud.qq.com";

    @Test
    void downloadTs(){
        FileReader fileReader = new FileReader(m3U8Path);
        //stream 流只能运行（调用中间或终端流操作）一次
        List<String> urlList = fileReader.readLines().stream()
                .filter(line -> line.startsWith("/"))
                .map(url->urlPrefix+url)
                .collect(Collectors.toList());
        System.out.println(urlList.size());
        urlList.forEach(System.out::println);

        AtomicInteger idx = new AtomicInteger(1);
        urlList.forEach(url->{
            System.out.println(url);
            BufferedOutputStream out = FileUtil.getOutputStream(videoPath+idx+".ts");
            idx.getAndIncrement();
            HttpUtil.download(url,out,true);
        });
    }

    @Test
    void genList(){
        File[] files = FileUtil.ls(videoPath);
        FileWriter fileWriter = new FileWriter(videoPath + "file_list.txt");
        Arrays.stream(files)
                .map((file ->file.getName()))
                // hu-utils Convert.toInt() StrUtil.removeSuffix()
                .sorted((o1,o2)-> Convert.toInt(StrUtil.removeSuffix(o1,".ts"))-Convert.toInt(StrUtil.removeSuffix(o2,".ts")))
                //.sorted((o1,o2)->Integer.parseInt(o1.substring(0,o1.length()-3))-Integer.parseInt(o2.substring(0,o2.length()-3)))
                .forEach(fileName->fileWriter.append(String.format("file '%s'\n",fileName)));

    }

}
