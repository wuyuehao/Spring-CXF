package ${package};

import org.springframework.stereotype.Component;
import com.paypal.types.Currency;
import com.paypal.types.Currency.Info;

import ${fromClass};
import ${toClass};

@Component
public class ${fromClassName}MapperGen{

    public ${toClassName} map(${fromClassName} ${fromClassVarName}){
        ${toClassName} ${toClassVarName}VO = new ${toClassName}();
        
        ${nestedObjects}
        
        ${mappingMethods}
        
        return ${toClassVarName}VO;
    }
    
    private Currency getCurrency(
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
    }

}
