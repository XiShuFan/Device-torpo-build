package resource;

public class Coordinate {
    private String coorStr;
    private int x;
    private int y;

    public Coordinate(String coorStr) {
        this.coorStr = coorStr;
        parseCoordinate();
    }

    //解析坐标
    private void parseCoordinate() {
        String tmp = new String(coorStr);
        //替换空格
        tmp = tmp.replaceAll(" ", "");
        //替换制表符
        tmp = tmp.replaceAll("\t", "");
        //替换括号
        tmp = tmp.replaceAll("\\(", "");
        tmp = tmp.replaceAll("\\)", "");
        //注意可能的中文括号
        tmp = tmp.replaceAll("（", "");
        tmp = tmp.replaceAll("）", "");
        //注意可能的中文逗号
        String[] strs = new String[2];
        if (tmp.contains(",")) {
            strs = tmp.split(",");
        } else if (tmp.contains("，")) {
            strs = tmp.split("，");
        } else {

            System.out.println("坐标解析出错了");
            System.exit(-1);
        }

        this.x = Integer.valueOf(strs[0]);
        this.y = Integer.valueOf(strs[1]);
    }

    public int getX() {
        return x;
    }


    public int getY() {
        return y;
    }


    public String getCoorStr() {
        return coorStr;
    }
}
