import service.Torpo;

public class MainClass {
    public static void main(String[] args) {
        String file = "E:\\java demo in Idea\\BuildTorpoJson\\src\\files\\拓扑信息表.xls";
        String outputDirectory = "D:\\大三下\\跨层故障定位\\";
        Torpo torpo = new Torpo(file, outputDirectory);

        torpo.readFile();
        torpo.buildTorpoJson();
    }
}
