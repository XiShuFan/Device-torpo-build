package service;

import com.alibaba.fastjson.JSONObject;
import jxl.Sheet;
import jxl.Workbook;
import resource.Link;
import resource.Node;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

//拓扑图
public class Torpo {
    private HashMap<Integer, ArrayList<Node>> nodes;
    private ArrayList<Link> links;
    private String file;
    private String outputDirectory;

    //x和y坐标每单位对应画布的单位
    private static final int XUNIT = 10;
    private static final int YUNIT = 60;

    public Torpo(String file, String outputDirectory) {
        this.nodes = new HashMap<>();
        this.links = new ArrayList<>();
        this.file = file;
        this.outputDirectory = outputDirectory;
    }


    //读取拓扑文件
    public void readFile() {
        System.out.println("读取拓扑信息");
        try {
            Workbook book = Workbook.getWorkbook(new File(file));
            //首先读取20页的节点信息
            for (int i = 0; i < 20; i++) {
                Sheet sheet = book.getSheet(i + "");
                int row = sheet.getRows();
                nodes.put(i, new ArrayList<>());
                //从第二行开始读取
                for (int k = 1; k < row; k++) {
                    nodes.get(i).add(new Node(sheet, k));
                }
            }
            //然后读取连接信息
            Sheet sheet = book.getSheet("links");
            int row = sheet.getRows();
            for (int k = 1; k < row; k++) {
                links.add(new Link(sheet, k));
            }
            book.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //输出json文件
    public void buildTorpoJson() {
        System.out.println("输出json文件");

        //根据每个x坐标的节点的最大名称长度
        HashMap<Integer, Integer> x_width = new HashMap<>();
        int globalMinWidth = Integer.MAX_VALUE;
        for (int x : nodes.keySet()) {
            ArrayList<Node> sameXNodes = nodes.get(x);
            //节点名字的最大宽度
            int maxWidth = 0;
            for (Node node : sameXNodes) {
                if (maxWidth < node.getName().length()) {
                    maxWidth = node.getName().length();
                }
            }
            if (maxWidth != 0) {
                globalMinWidth = globalMinWidth > maxWidth ? maxWidth : globalMinWidth;
            }
            x_width.put(x, maxWidth);
        }
        //会出现maxWidth为0的情况，替换成globalMinWidth
        for (int x : x_width.keySet()) {
            if (x_width.get(x) == 0) {
                x_width.put(x, globalMinWidth);
            }
        }

        //准备完毕，可以开始生成节点的json文件
        //注意，同时需要做一个节点顺序表，节点坐标作为内容

        ArrayList<String> nodesOrder = new ArrayList<>();
        ArrayList<Object> nodesJson = new ArrayList<>();
        //记录坐标对应的节点的颜色
        HashMap<String, String> coor_color = new HashMap<>();
        //全局目前的x坐标
        int now_x = 0;
        for (int x : nodes.keySet()) {
            ArrayList<Node> sameXNodes = nodes.get(x);
            for (Node node : sameXNodes) {

                HashMap<String, Object> nodeJson = new HashMap<>();
                nodeJson.put("name", node.getName());
                nodeJson.put("x", (now_x + x_width.get(x) / 2) * XUNIT);
                nodeJson.put("y", node.getY() * YUNIT);
                nodeJson.put("symbol", node.getColor() + "_icon");

                nodesJson.add(nodeJson);
                //记录节点放入的顺序
                nodesOrder.add(node.getCoorStr());

                if (coor_color.containsKey(node.getCoorStr())) {
                    System.out.println("出现了相同的节点坐标，错误");
                    System.exit(-1);
                } else {
                    coor_color.put(node.getCoorStr(), node.getColor());
                }
            }
            //更新全局目前的x坐标
            now_x += x_width.get(x);
        }



        //到这里，开始生成link的json数据
        ArrayList<Object> linksJson = new ArrayList<>();
        for (Link link : links) {
            HashMap<String, Object> linkJson = new HashMap<>();

            linkJson.put("source", nodesOrder.indexOf(link.getCoor1Str()));
            linkJson.put("target", nodesOrder.indexOf(link.getCoor2Str()));
            //要确认连线的颜色
            //只要一个node颜色是黄色，连线的颜色就是黑色，否则是蓝色
            HashMap<String, Object> lineStyleJson = new HashMap<>();
            if (coor_color.get(link.getCoor1Str()).equals("yellow")
                    || coor_color.get(link.getCoor2Str()).equals("yellow")) {
                lineStyleJson.put("color", "black");
            } else {
                lineStyleJson.put("color", "blue");
            }
            linkJson.put("lineStyle", lineStyleJson);

            //要确认标签
            HashMap<String, Object> labelJson = new HashMap<>();
            //如果没有标签，不显示
            if (link.validLabel()) {
                labelJson.put("show", true);
                labelJson.put("formatter", link.getLabel());
                labelJson.put("verticalAlign", "middle");
                //标签水平放置
                labelJson.put("rotate", 0);
                //设置标签的大小，在前端也要动态调整大小
                labelJson.put("fontSize", 9);
            } else {
                labelJson.put("show", false);
                labelJson.put("fontSize", 9);
            }

            linkJson.put("label", labelJson);

            linksJson.add(linkJson);
        }

        //画布的长宽参数确定
        HashMap<String, Object> canvas = new HashMap<>();
        //宽度是可以固定的
        canvas.put("canvasHeight", 600);
        //长度需要分析确定，找到最大的长度，也就是now_x
        canvas.put("canvasWidth", now_x * XUNIT);


        //到这里，全部结束

        JSONObject torpoJson = new JSONObject();
        torpoJson.put("nodes", nodesJson);
        torpoJson.put("links", linksJson);
        torpoJson.put("canvas", canvas);

        //输出文件
        try {
            //必须保留原始输出
            PrintStream out = System.out;
            System.setOut(new PrintStream(new BufferedOutputStream(
                    new FileOutputStream(this.outputDirectory + "torpo.json")),true));
            System.out.println(torpoJson.toJSONString());
            System.setOut(out);
        } catch (Exception e) {
            System.out.println("输出文件到json错误");
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
