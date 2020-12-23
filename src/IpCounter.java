import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.BitSet;
import java.nio.ByteBuffer;

public class IpCounter {
    //путь к файлу, чтобы не таскать его по директориям
    private static String filePath = "C:\\Users\\recfordrem\\Downloads\\ip_addresses\\ip_addresses";
    
    private static long bitMaskCounter() {
        /*
            Максимальное десятичное число, соответствующее Ipv4 адресу (а равно и возможное кол-во этих адресов) - 4294967295, поэтому
            выделим для него два битовых массива общим размером в 4294967295 эл-тов, кот. будет иметь индексы (десят. представления IP адресов)
            в диапазоне от 0 до 4294967294

            Вычисляя из десятичного представления IP адреса единицу, мы получим соответствующий индекс для обращения к битовому массиву и установлению
            флага в единицу.

           long maskSize = 4294967295L;
         */
        int maxIntValue = 2147483647;

        //общий размер этих двух массивов = maskSize
        BitSet bitMaskOne = new BitSet(maxIntValue);
        BitSet bitMaskTwo = new BitSet(maxIntValue);

        try {
            File file = new File(filePath);
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String ipAddress = reader.readLine();
            while (ipAddress != null) {
                // считываем остальные строки в цикле
                long decIpAddress = ipToLong(ipAddress);
                if (decIpAddress <= maxIntValue) {
                    bitMaskOne.set((int)(decIpAddress - 1));
                } else {
                    int maskTwoInd = (int)(decIpAddress - maxIntValue - 1);
                    bitMaskTwo.set(maskTwoInd);
                }
                ipAddress = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitMaskOne.stream().count() + bitMaskTwo.stream().count();
    }

    private static long ipToLong(String ipAddress) {

        long result = 0;

        String[] ipAddressInArray = ipAddress.split("\\.");

        for (int i = 3; i >= 0; i--) {

            long ip = Long.parseLong(ipAddressInArray[3 - i]);

            //left shifting 24,16,8,0 and bitwise OR

            //1. 192 << 24
            //1. 168 << 16
            //1. 1   << 8
            //1. 2   << 0
            result |= ip << (i * 8);

        }

        return result;
    }

    public static void main(String[] args) {
        //пробуем метод битовых карт
        long beginTime = System.currentTimeMillis();
        long uniqIpCount = bitMaskCounter();
        long endTime = System.currentTimeMillis();
        //выясняем, сколько памяти потратили на этот метод
        long usedBytes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        try(FileWriter writer = new FileWriter("report.txt", false))
        {
            // запись всей строки
            writer.write("Метод подсчета: битовые маски\n");
            writer.write("Кол-во уникальных адресов: " + uniqIpCount + "\n");
            writer.write("Время выполнения метода (в мс): " + (endTime - beginTime) + "\n");
            writer.write("Потраченная ОЗУ (в МБ): " + (usedBytes/1048576) + "\n");
            writer.flush();
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }
}
