package it.casadeipallets.util;

public class NetworkUtilities {
    
    /**
     * Return a byte array from a string containing a ip address
     * @param ip
     * @return byte[4]
     */
    public static byte[] fromStringIpToByteArray(String ip) {
        String[] ipArr = ip.split("\\.");
        byte[] result = new byte[4];
        
        result[0] = (byte)(Integer.parseInt(ipArr[0]) & 0xFF);
        result[1] = (byte)(Integer.parseInt(ipArr[1]) & 0xFF);
        result[2] = (byte)(Integer.parseInt(ipArr[2]) & 0xFF);
        result[3] = (byte)(Integer.parseInt(ipArr[3]) & 0xFF);
        
        return result;
    }
    
    /**
     * Returns a int rapresentation of a IP address in canonical (string) form
     * @param ip
     * @return
     */
    public static int fromStringIpToInt(String ip) {
        byte[] b = fromStringIpToByteArray(ip);
        return fromByteArrayIpToInt(b);
    }
    
    /**
     * Return a int representation of a IP address
     * @param ip
     * @return
     */
    public static int fromByteArrayIpToInt(byte[] ip) {
        int result = 0;
        result += ((((int)ip[0]) & 0xFF) << 24);
        result += ((((int)ip[1]) & 0xFF) << 16);
        result += ((((int)ip[2]) & 0xFF) << 8);
        result += (((int)ip[3]) & 0xFF);
        return result;
    }
    
    /**
     * Return a int representation of a IP address
     * @param ip
     * @return
     */
    public static String fromIntIpToString(int ip) {
        String ipAddr = "" + ((ip >>  24) & 0xFF);
        ipAddr += "." + ((ip >>  16) & 0xFF);
        ipAddr += "." + ((ip >>  8) & 0xFF);
        ipAddr += "." + (ip & 0xFF);
        return ipAddr;
    }
}
