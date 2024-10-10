package com.dtstack.engine.common.util;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sishu.yss on 2018/3/1.
 */
public class AddressUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(AddressUtil.class);

    private static List<String> localAddrList = Lists.newArrayList("0.0.0.0", "127.0.0.1", "localhost");

    private static final Pattern DOMAIN_NAME_PATTERN = Pattern.compile("[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})*$");

    private static final Pattern IP_PATTERN = Pattern.compile("((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}");

    /**
     * 获取本地ip地址，有可能会有多个地址, 若有多个网卡则会搜集多个网卡的ip地址
     */
    public static Set<InetAddress> resolveLocalAddresses(boolean filterDockerIp) {
        Set<InetAddress> addrs = new HashSet<InetAddress>();
        Enumeration<NetworkInterface> ns = null;
        try {
            ns = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            LOGGER.error("getNetworkInterfaces error:{}",e.getMessage(),e);
        }
        while (ns != null && ns.hasMoreElements()) {
            NetworkInterface n = ns.nextElement();
            Enumeration<InetAddress> is = n.getInetAddresses();
            while (is.hasMoreElements()) {
                InetAddress i = is.nextElement();
                if (!i.isLoopbackAddress() && !i.isLinkLocalAddress() && !i.isMulticastAddress()
                        && !isSpecialIp(i.getHostAddress(), filterDockerIp)) {addrs.add(i);}
            }
        }
        return addrs;
    }

    /**
     * @param ip
     * @param filterDockerIp 是否过滤掉 dockerIp，兼容历史逻辑
     * @return
     */
    private static boolean isSpecialIp(String ip, boolean filterDockerIp) {
        if (ip.contains(":")) {return true;}
        if (ip.startsWith("127.")) {return true;}
        if (ip.startsWith("169.254.")) {return true;}
        if (filterDockerIp && ip.startsWith("172.17.")) {return true;}
        if ("255.255.255.255".equals(ip)) {return true;}
        return false;
    }

    public static List<String> resolveLocalIps(boolean filterDockerIp) {
        Set<InetAddress> addrs = resolveLocalAddresses(filterDockerIp);
        List<String> ret = Lists.newArrayList();
        for (InetAddress addr : addrs){
            String ar = addr.getHostAddress();
            if(!ret.contains(ar)){
                ret.add(ar);
            }
        }
        return ret;
    }

    public static String getOneIp(){
        return getOneIp(false);
    }

    public static String getOneIp(boolean filterDockerIp){
        List<String> ips =   resolveLocalIps(filterDockerIp);
        return ips.size()>0?ips.get(0):"0.0.0.0";
    }

    public static boolean ping(String ip){
        try{
            return InetAddress.getByName(ip).isReachable(3000);
        }catch(Exception e){
            return false;
        }
    }

    /**
     * 校验ip是否是'0.0.0.0', '127.0.0.1'
     * @param ip
     * @return
     */
    public static boolean checkAddrIsLocal(String ip){
        for(String localIp : localAddrList){
            if(localIp.equalsIgnoreCase(ip)){
                return true;
            }
        }

        return false;
    }

    /**
     * 检查服务是否相同：ip相同，端口相同
     */
    public static boolean checkServiceIsSame(String host1,int port1,String host2,int port2) throws Exception{
        InetAddress address1 = InetAddress.getByName(host1);
        InetAddress address2 = InetAddress.getByName(host2);

        return address1.getHostAddress().equals(address2.getHostAddress()) && port1 == port2;
    }

    public static boolean telnet(String ip,int port){
        TelnetClient client = null;
        try{
            client = new TelnetClient();
            client.setConnectTimeout(3000);
            client.connect(ip,port);
            return true;
        }catch(Exception e){
            return false;
        } finally {
            try {
                if (client != null){
                    client.disconnect();
                }
            } catch (Exception e){
                LOGGER.error("disconnect error: {}", e.getMessage(), e);
            }
        }
    }

    public static List<Pair<String,Integer>> parseUrlString(String urlString) {
        return parseUrlString(urlString, ",");
    }

    public static List<Pair<String,Integer>> parseUrlString(String urlString,String separator) {
        List<Pair<String,Integer>> result = new ArrayList<>();

        if (StringUtils.isBlank(urlString)) {
            return new ArrayList<>();
        }
        if (StringUtils.isEmpty(separator)) {
            Pair<String,Integer> pair = parseSingleUrl(urlString);
            return Optional.ofNullable(pair).map(e -> result).orElse(Lists.newArrayList());
        }

        String[] urls = urlString.split(separator);
        for (String url : urls) {
            Pair<String, Integer> pair = parseSingleUrl(url);
            Optional.ofNullable(pair).map(result::add);
        }

        return result;
    }

    public static Pair<String, Integer> parseSingleUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        String[] splits = url.split(":");
        String ip = splits[0];
        Integer port = splits.length >= 2 ? Integer.valueOf(splits[1]) : null;
        return Pair.of(ip,port);

    }

    public static boolean isDomain(String address) {
        if (StringUtils.isBlank(address)) {
            return false;
        }
        Matcher matcher = DOMAIN_NAME_PATTERN.matcher(address);
        return matcher.matches();
    }

    public static boolean isCorrectIp(String ipString) {
        if (StringUtils.isBlank(ipString)) {
            return false;
        }
        Matcher matcher = IP_PATTERN.matcher(ipString);
        return matcher.matches();
    }
}
