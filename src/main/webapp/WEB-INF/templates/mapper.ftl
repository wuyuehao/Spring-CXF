/* This is a generated class */

package ${package};

import org.springframework.stereotype.Component;

import ${fromClass};
import ${toClass};

@Component
public class ${fromClassName}MapperGen{

    public ${toClassName} map(${fromClassName} ${fromClassVarName}){
        ${toClassName} ${toClassVarName} = new ${toClassName}();
        
        ${nestedObjects}
        
        ${mappingMethods}
        
        return ${toClassVarName};
    }
    
    /*private Currency getCurrency(
            com.paypal.api.platform.riskprofileapi.Currency currency) {
        if (currency == null) {
            return null;
        }
        String isoCode = currency.getCurrency();
        String value = currency.getValue();
        if (isoCode == null || value == null) {
            return null;
        }
        return new Currency(Info.getInstance(isoCode),
                Double.parseDouble(value));
    }*/

}
