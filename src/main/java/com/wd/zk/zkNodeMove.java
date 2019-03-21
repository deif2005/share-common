package com.wd.zk;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;

/**
 * 迁移zookeeper节点及节点数据
 * @author wgzh159@163.com
 */

public class zkNodeMove {
    public static void main(String[] args) throws Exception{
        //旧zk配置
        ZooKeeper oldzk = new ZooKeeper("192.168.10.42:2181", 60000, null);
        //新zk配置
        ZooKeeper newzk = new ZooKeeper("192.168.10.61:2181", 60000, null);
        //迁移的节点
        //String node = "/startconfigs/microschoolappsvr/microschoolappsvr-jk";
        String node = "/configs/microschoolappsvr/microschoolappsvr-kc/download";
        List<String> children = oldzk.getChildren(node, false);
        move(oldzk, newzk, children,node);
        oldzk.close();
        newzk.close();
    }

    private static void move(ZooKeeper oldzk, ZooKeeper newzk, List<String> children,String parent)
            throws KeeperException, InterruptedException {
        if(children==null || children.isEmpty()){
            return;
        }else{
            for(String child:children){
                String c = parent+"/"+child;
                System.out.println(c);
                byte[] data = oldzk.getData(c, false, null);
                if(newzk.exists(c, false)==null){
                    newzk.create(c, data, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }else{
                    newzk.setData(c, data, -1, null, null);
                }
                move(oldzk, newzk, oldzk.getChildren(c, false),c);
            }
        }
    }

}
