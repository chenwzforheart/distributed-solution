package com.cwzsmile.distributed.base.file;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author csh9016
 * @date 2019/9/17
 */
@Slf4j
public class ExcelDemo {

    public static void saveCsv(List<Report> data, BufferedWriter outReader) {
        try {
            LinkedList<Report> ffList = new LinkedList<>();
            for (int i = 0; i < data.size(); i++) {
                if (ffList.isEmpty()) {
                    ffList.add(data.get(i));
                    continue;
                }
                Report now = data.get(i);
                if (now.getB().equals(ffList.getFirst().getB()) && now.getC().equals(ffList.getFirst().getC())) {
                    ffList.addLast(now);
                } else {

                    print2File(ffList, outReader);
                    ffList.addLast(now);
                }
            }
            //最后一行
            print2File(ffList, outReader);
        } catch (IOException e) {
            log.error("保存报错", e);
        }
    }

    /**
     * 判断是否是单独内容，需要分开写。短信数大于等于4条，判断合并
     *
     * @param reports
     * @param bw
     * @throws IOException
     */
    public static void print2File(LinkedList<Report> reports, BufferedWriter bw) throws IOException {
        ArrayList<Report> base = new ArrayList<>(reports);
        for (int j = 0; j < base.size(); j++) {
            Report temp = base.get(j);
            if ((temp.getD().startsWith("【") || temp.getD().startsWith("[")) && temp.getD().length() < 67) {
                bw.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\"", temp.getD(), 1, temp.getB(), temp.getC()));
                bw.newLine();
                reports.remove(temp);
            }
        }

        if (reports.size() != 0) {
            sort1(reports);
            bw.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\"", connect1(reports), reports.size(), reports.getFirst().getB(), reports.getFirst().getC()));
            bw.newLine();
            reports.clear();
        }
        bw.flush();
    }

    public static void main(String[] args) {
        try {
            String path = "D:\\opt\\223082.xlsx";
            Path out = Paths.get("D:\\opt\\223082-1.csv");
            if (!Files.exists(out)) {
                Files.createFile(out);
            }
            BufferedWriter outReader = Files.newBufferedWriter(out, Charset.forName("GBK"));
            List<List<String>> data = FileUtil.readDataFromFile(path, 5);
            LinkedList<List<String>> ffList = new LinkedList<>();
            for (int i = 0; i < data.size(); i++) {
                if (ffList.isEmpty()) {
                    ffList.add(data.get(i));
                    continue;
                }

                List<String> now = data.get(i);
                if (now.get(1).equals(ffList.getFirst().get(1)) && now.get(2).equals(ffList.getFirst().get(2))) {
                    ffList.addLast(now);
                } else {
                    //前后不同，进一步
                    //outReader.newLine();
                    //outReader.flush();
                    sort(ffList);
                    System.out.println(JSON.toJSONString(ffList));
                    outReader.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\"", connect(ffList), ffList.size(), ffList.getFirst().get(1), ffList.getFirst().get(2)));
                    outReader.newLine();
                    outReader.flush();
                    ffList.clear();
                    ffList.addLast(now);
                }

            }
            //最后一行
            sort(ffList);
            System.out.println(JSON.toJSONString(ffList));
            outReader.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\"", connect(ffList), ffList.size(), ffList.getFirst().get(1), ffList.getFirst().get(2)));
            outReader.newLine();
            outReader.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void sort(LinkedList<List<String>> aa) {
        for (int i = 0; i < aa.size(); i++) {
            List<String> now = aa.get(i);
            if (now.get(3).startsWith("【")) {
                aa.remove(now);
                aa.addFirst(now);
            }
            if (now.get(3).length() < 67) {
                aa.remove(now);
                aa.addLast(now);
            }
        }
    }

    public static void sort1(LinkedList<Report> aa) {
        ArrayList<Report> copy = new ArrayList<>(aa);
        for (int i = 0; i < copy.size(); i++) {
            Report now = copy.get(i);
            if (now.getD().startsWith("【") || now.getD().startsWith("[")) {
                aa.remove(now);
                aa.addFirst(now);
                continue;
            }
            if (now.getD().length() < 67) {
                aa.remove(now);
                aa.addLast(now);
                continue;
            }
        }
    }


    public static String connect(LinkedList<List<String>> aa) {
        return aa.stream().map(t -> t.get(3)).reduce(String::concat).get();
    }

    public static String connect1(LinkedList<Report> aa) {
        return aa.stream().map(t -> t.getD()).reduce(String::concat).get();
    }

    public static void main11(String[] args) {
        try {
            String path = "D:\\opt\\223013.xlsx";
            Path out = Paths.get("D:\\opt\\223013-1.csv");
            if (!Files.exists(out)) {
                Files.createFile(out);
            }
            BufferedWriter outReader = Files.newBufferedWriter(out, Charset.forName("GBK"));
            List<List<String>> data = FileUtil.readDataFromFile(path, 5);
            List<String> forward = null;
            for (int i = 0; i < data.size(); i++) {
                if (forward == null) {
                    forward = data.get(i);
                    continue;
                }

                List<String> now = data.get(i);
                if (now.get(1).equals(forward.get(1)) && now.get(2).equals(forward.get(2))) {
                    //前后相同，合并
                    String merge = "";
                    if (forward.get(3).charAt(0) == '【') {
                        merge = forward.get(3) + now.get(3);
                    } else {
                        merge = now.get(3) + forward.get(3);
                    }
                    //System.out.println(String.format("\"%s\",\"%s\",\"%s\",\"%s\"",forward.get(3),"2",forward.get(1),forward.get(2)));
                    outReader.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\"", merge, "2", forward.get(1), forward.get(2)));
                    outReader.newLine();
                    outReader.flush();
                    forward = null;
                } else {
                    //前后不同，进一步
                    outReader.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\"", forward.get(3), "1", forward.get(1), forward.get(2)));
                    outReader.newLine();
                    outReader.flush();
                    System.out.println(JSON.toJSONString(forward));
                    forward = now;
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
