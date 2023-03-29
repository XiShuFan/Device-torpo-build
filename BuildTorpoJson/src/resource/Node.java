package resource;

import jxl.Sheet;

//记录节点信息
public class Node {
    //坐标
    private Coordinate coordinate;
    //网元名称
    private String name;
    //颜色，blue或者yellow
    private String color;


    public Node(Sheet sheet, int row) {
        this.coordinate = new Coordinate(sheet.getCell(0, row).getContents());
        this.name = sheet.getCell(1, row).getContents();
        this.color = sheet.getCell(2, row).getContents();

        //解析颜色
        if (this.color.equals("黄色")) {
            this.color = "yellow";
        } else if (this.color.equals("蓝色")) {
            this.color = "blue";
        } else {
            System.out.println("颜色解析出错了");
            System.exit(-1);
        }

    }


    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public int getX() {
        return this.coordinate.getX();
    }

    public int getY() {
        return this.coordinate.getY();
    }

    public String getCoorStr() {
        return coordinate.getCoorStr();
    }
}
