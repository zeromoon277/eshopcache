package com.bixkjwfnh.eshop.cache.service.impl;

import com.bixkjwfnh.eshop.cache.dao.RedisDao;
import com.bixkjwfnh.eshop.cache.mapper.ProductInventoryMapper;
import com.bixkjwfnh.eshop.cache.model.ProductInventory;
import com.bixkjwfnh.eshop.cache.service.ProductInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 商品库存Service实现类
 * 
 * @since 2018.04.26
 * @author SunQW
 *
 */
@Service
public class ProductInventoryServiceImpl implements ProductInventoryService {

    private static final String CACHE_KEY = "'product'";
    private static final String CACHE_NAME_B = "demo";

    //* @Cacheable : Spring在每次执行前都会检查Cache中是否存在相同key的缓存元素，如果存在就不再执行该方法，而是直接从缓存中获取结果进行返回，否则才会执行并将返回结果存入指定的缓存中。
    //* @CacheEvict : 清除缓存。
    //* @CachePut : @CachePut也可以声明一个方法支持缓存功能。使用@CachePut标注的方法在执行前不会去检查缓存中是否存在之前执行过的结果，而是每次都会执行该方法，并将执行结果以键值对的形式存入指定的缓存中。


	@Autowired
	private ProductInventoryMapper productInventoryMapper;
	
	@Autowired
	private RedisDao redisDao;
	
	@Override
    @CacheEvict(value = CACHE_NAME_B, key = "'product_'+ #productInventory.productId")
	public void updateProductInventory(ProductInventory productInventory) {
		productInventoryMapper.updateProductInventory(productInventory);
		System.out.println("===========日志===========: 已修改数据库中的库存，商品id=" + productInventory.getProductId() + ", 商品库存数量=" + productInventory.getInventoryCnt());
	}

    @Override
    @CachePut(value = CACHE_NAME_B, key = "'product_'+ #productInventory.productId")
    public ProductInventory saveProductInventory(ProductInventory productInventory) {
        productInventoryMapper.saveProductInventory(productInventory);
        return productInventory;
    }

    @Override
    @CacheEvict(value = CACHE_NAME_B, key = "'product_'+ #productId")
	public void removeProductInventoryCache(Integer productId) {
		String key = "product:inventory:"+String.valueOf(productId);
		redisDao.delete(key);
		System.out.println("===========日志===========: 已删除redis中的缓存，key=" + key); 
	}

	@Override
    @Cacheable(value = CACHE_NAME_B, key = "'product_'+ #productId")
	public ProductInventory findProductInventory(Integer productId) {
		ProductInventory productInventory = productInventoryMapper.findProductInventory(productId);
		return productInventory;
	}

	@Override
	public void setProductInventoryCache(ProductInventory productInventory) {
		String key = "product:inventory:"+String.valueOf(productInventory.getProductId());
		
		redisDao.set(key, String.valueOf(productInventory.getInventoryCnt()));
		System.out.println("===========日志===========: 已更新商品库存的缓存，商品id=" + productInventory.getProductId() + ", 商品库存数量=" + productInventory.getInventoryCnt() + ", key=" + key);  
	}

	/**
	 * 获取商品库存的缓存
	 * @param productId
	 * @return
	 */
	public ProductInventory getProductInventoryCache(Integer productId) {
		Long inventoryCnt = 0L;
		
		String key = "product:inventory:" + productId;
		String result = redisDao.get(key);
		
		if(result != null && !"".equals(result)) {
			try {
				inventoryCnt = Long.valueOf(result);
				return new ProductInventory(productId, inventoryCnt);
			} catch (Exception e) {
				e.printStackTrace(); 
			}
		}
		
		return null;
	}
	
	

}
