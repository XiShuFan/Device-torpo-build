package resource;

import jxl.Sheet;

//连接信息
public class Link {
    private Coordinate coor1;
    private Coordinate coor2;
    //注意这个label，可能需要换行
    //并且可能为空
    private String label;

    public Link(Sheet sheet, int row) {
        this.coor1 = new Coordinate(sheet.getCell(0, row).getContents());
        this.coor2 = new Coordinate(sheet.getCell(1, row).getContents());

        this.label = sheet.getCell(2, row).getContents();
    }

    public String getCoor1Str() {
        return coor1.getCoorStr();
    }

    public String getCoor2Str() {
        return coor2.getCoorStr();
    }


    public String getLabel() {
        return label;
    }

    public boolean validLabel() {
        //非空标签
        return label != null && !label.equals("");
    }


    //判断标签是否是竖直放置的
    public boolean verticalLabel() {
        return coor1.getX() == coor2.getX() && coor1.getY() != coor2.getY();
    }


}
