//package com.xhr.xDB.ds;
//
//import com.yonyou.ifbp.dynamicds.parser.BeanConfigEntity;
//import com.yonyou.ifbp.dynamicds.parser.DynamicDsConfigParser;
//import com.yonyou.ifbp.dynamicds.parser.ParseResult;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.sql.DataSource;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * 租户对应数据源查找服务,单数据库实例，多schema
// */
//public class DefaultDataSourceProvider implements IDataSourceProvider {
//
//	public static final Logger LOGGER = LoggerFactory.getLogger(DefaultDataSourceProvider.class);
//
//	private ConcurrentHashMap<String, String> dsNameMap = new ConcurrentHashMap<String, String>();
//
//	// 保存新创建的DataSource
//	private ConcurrentHashMap<String, DataSource> dsMap = new ConcurrentHashMap<String, DataSource>();
//
//	public Map<String, String> getDsNameMap() {
//		return dsNameMap;
//	}
//
//	public void setDsNameMap(Map<String, String> dsNameMap) {// TODO validate
//		this.dsNameMap = new ConcurrentHashMap<String, String>(dsNameMap);
//	}
//
//	public Map<String, DataSource> getDsMap() {
//		return dsMap;
//	}
//
//	public void setDsMap(HashMap<String, DataSource> dsMap) {
//		this.dsMap = new ConcurrentHashMap<>(dsMap);
//	}
//
//	@Override
//	public String findTenantDataSource(String tenant, String appId) {
//		String key = tenant + "_" + appId;
//		String dsName = dsNameMap.get(key);
//		LOGGER.info("租户{}应用{}对应的数据源名称为{}!", tenant, appId, dsName);
//		return dsName;
//	}
//
//	@Override
//	public DataSource fetchDataSource(String dsName) throws Exception {
//		if (dsMap.get(dsName) != null) {
//			return dsMap.get(dsName);
//		} else {
//			LOGGER.error("can not find datasource for dsName,please check the global ds config!!!!");
//			throw new RuntimeException();
//		}
//	}
//
//	// 中断之后的重启，在启动watcher的时候初始化
//	@Override
//	public synchronized void refreshByConfig(String conf) {
//		LOGGER.info(conf);
//
//		// 处理dom，和map中的key比对，找到没有的key则创建
//		try {
//			ParseResult result = DynamicDsConfigParser.findNeedRefreshDsConfig(conf, dsNameMap, dsMap);
//			dsNameMap.putAll(result.getAddedDsMap());
//
//			List<BeanConfigEntity> addList = result.getAddList();
//			for (int i = 0; i < addList.size(); i++) {
//				BeanConfigEntity entity = addList.get(i);
//
//				// 创建新的数据源并添加
//				DataSource dataSource = DataSourceUtils.getDataSource(entity);
//
//				dsMap.put(entity.getBeanName(), dataSource);
//			}
//
//			List<String> list1 = result.getDeleteDsNames();
//			for (int i = 0; i < list1.size(); i++) {
//				dsNameMap.remove(list1.get(i));
//			}
//
//			List<String> list2 = result.getDeleteDses();
//			for (int i = 0; i < list2.size(); i++) {
//				String deleteKey = list2.get(i);
//				DataSourceUtils.destoryDataSource(dsMap.get(deleteKey));
//				dsMap.remove(deleteKey);
//			}
//
//		} catch (Exception e) {
//			LOGGER.error("refresh dynamic datasources error!", e);
//		}
//	}
//
//	@Override
//	public String getInitConfig() {
//		return DynamicDsConfigParser.getInitConfig(getDsNameMap());
//	}
//}
