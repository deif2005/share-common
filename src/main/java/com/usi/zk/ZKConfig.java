package com.usi.zk;

import org.apache.curator.framework.CuratorFramework;

public class ZKConfig {

	// ZK的根目录
	static String rootPath = "/configs/device_service";

	/**
	 * 获取ZK上的配置路径
	 * @param zk_path ZK的配置路径
	 * @return
	 */
	public static String getConfigInfo(String zk_path) {
		zk_path = rootPath + zk_path;
		// 默认上传地址
		String uploadPath = null;

		// 判断 ZK是否有配置，如果没有，使用默认
		CuratorFramework zk = ZKClient.getClient();
		try {
			if (zk.checkExists().forPath(zk_path) != null) {
				byte[] data = zk.getData().forPath(zk_path);
				uploadPath = new String(data);
			} else {
				throw new RuntimeException("读取zk配置失败，配置不存在：" + zk_path);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return uploadPath;
	}

	/**
	 * 获取静态资源展示地址
	 * @return 返回资源展示地址
	 * @return 例：http://192.168.10.42:8085/ices-static/upload
	 */
	public static String getStaticShowPath(){
		return getConfigInfo("/static/showPath");
	}

}
