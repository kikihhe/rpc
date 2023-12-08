package com.xiaohe.test.scanner;

import com.xiaohe.annotation.RpcReference;
import com.xiaohe.common.scanner.ClassScanner;
import com.xiaohe.common.scanner.reference.RpcReferenceScanner;
import com.xiaohe.common.scanner.server.RpcServiceScanner;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-12-08 09:40
 */
public class ScannerTest {
    /**
     * 拿到指定包下的所有类的全限定类名
     * @throws Exception
     */
    @Test
    public void scannerClassNameList() throws Exception {
        List<String> classNameList = ClassScanner.getClassNameList("com.xiaohe.test.scanner");
        for (int i = 0; i < classNameList.size(); i++) {
            System.out.println(classNameList.get(i));
        }
    }

    /**
     * 扫描带有 @RpcService 注解的类的名字
     * @throws Exception
     */
    @Test
    public void scannerClassNameWithRpcService() throws Exception {
        Map<String, Object> stringObjectMap =
                RpcServiceScanner.doScannerWithRpcServiceAnnotationFilterAndRegistryService("com.xiaohe.test.scanner");
        for (Map.Entry<String, Object> stringObjectEntry : stringObjectMap.entrySet()) {
            System.out.println(stringObjectEntry.getKey());
            System.out.println(stringObjectEntry.getValue());
        }
    }


    @Test
    public void scannerClassNameWithRpcReference() throws Exception {
        Map<String, Object> stringObjectMap = RpcReferenceScanner.doScannerWithRpcReferenceAnnotationFilter("com.xiaohe.test.scanner");
        for (Map.Entry<String, Object> stringObjectEntry : stringObjectMap.entrySet()) {
            System.out.println(stringObjectEntry.getKey());
            System.out.println(stringObjectEntry.getValue());
        }
    }
}
