/* This is a generated class */

package ${package};


import org.junit.Assert;
import java.math.*;
import org.junit.Test;
import java.util.*;
import ${fromClass};
import ${toClass};

public class ${fromClassName}MapperGenTest{


    @Test
    public void mapTest(){
        
        ${fromClassName} ${fromClassVarName} = new ${fromClassName}();
        
        ${nestedObjects}
        
        feed(${fromClassVarName});
        
        ${fromClassName}MapperGen mapper = new ${fromClassName}MapperGen();
        
        verify(${fromClassVarName} , mapper.map(${fromClassVarName}));
        
    }
    
    
    private void feed(${fromClassName} ${fromClassVarName}){
        <#list feeder?keys as key> 
        ${fromClassVarName}.${key}(${feeder[key]});
        
        </#list>
    }
    
    private void verify(${fromClassName} ${fromClassVarName}, ${toClassName} ${toClassVarName}){
        <#list assertions?keys as key> 
        assertEquals(${fromClassVarName}.${key}, ${toClassVarName}.${assertions[key]});
        
        </#list>
    }
    
    private void assertEquals(java.util.Date date, Long mills) {
        Assert.assertEquals((Long)date.getTime(), mills);
    }
    private void assertEquals(String s, BigInteger b) {
        Assert.assertEquals(s, b.toString());
    }
    private void assertEquals(Object o, String s) {
        Assert.assertEquals(o.toString(), s);
    }
    private void assertEquals(Object o1, Object o2) {
        Assert.assertEquals(o1, o2);
    }
    
    
}
